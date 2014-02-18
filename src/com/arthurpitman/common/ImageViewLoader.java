/*
 * Copyright (C) 2012, 2013 Arthur Pitman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.arthurpitman.common;

import java.io.File;
import java.lang.ref.WeakReference;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;


/**
 * Asynchronously loads images into an {@link ImageView}.
 */
public class ImageViewLoader {

	/**
	 * Constant representing an unlimited dimension.
	 */
	public static final int UNLIMITED_DIMENSION = 100000;


	/**
	 * Custom tag used to track state.
	 */
	private static class ImageViewTag {

		public static final int IMAGE_ID_NONE = -1;

		public int imageId;
		public int targetImageId;
		public String url;
		public ImageView imageView;
		public Bitmap bitmap;

		public ImageViewTag() {
			imageId = IMAGE_ID_NONE;
			targetImageId = IMAGE_ID_NONE;
			url = null;
			imageView = null;
			bitmap = null;
		}
	}


	/**
	 * A least-recently-used cache of {@link BitmapDrawable BitmapDrawables}.
	 */
	private static class BitmapDrawableLruCache extends LruCache<Integer, BitmapDrawable> {

		public BitmapDrawableLruCache(int maxSizeBytes) {
			super(maxSizeBytes);
		}

		@Override
		protected int sizeOf(Integer key, BitmapDrawable value) {
			return value.getBitmap().getByteCount();
		}
	}


	/**
	 * Performs the actual work of loading images in the background.
	 */
	private static class BackgroundHandler extends Handler {

		private static final String TAG = "BackgroundHandler";
		private WeakReference<ImageViewLoader> outer;
		private File cacheFolder;


		public BackgroundHandler(Looper looper, File cacheFolder, ImageViewLoader imageViewLoader) {
			super(looper);
			outer = new WeakReference<ImageViewLoader>(imageViewLoader);
			this.cacheFolder = cacheFolder;
		}


		@Override
		public void handleMessage(Message msg) {
			try {
				ImageViewLoader provider = outer.get();
				ImageViewTag tag = (ImageViewTag) msg.obj;

				int targetImageId;
				String url;

				synchronized (tag) {
					if (tag.targetImageId == tag.imageId) {
						return;
					}
					targetImageId = tag.targetImageId;
					url = tag.url;
				}

				// synchronously download the file if it does not already exist
				File imageFile = new File(cacheFolder, Integer.toString(targetImageId));
				if (!imageFile.exists()) {
					WebUtils.downloadFile(url, null, imageFile);
				}

				// load an resize the image
				if (imageFile.exists()) {
					Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
					bitmap = BitmapUtils.resizeBitmapToFit(bitmap, provider.maxWidth, provider.maxHeight);

					synchronized (tag) {
						if (tag.targetImageId == targetImageId) {
							tag.bitmap = bitmap;
							provider.callbackHandler.sendMessage(Message.obtain(provider.callbackHandler, 0, tag));
						}
					}
				}
			} catch (Exception e) {
				Log.d(TAG, "exception while executing in background: " + e.toString());
			}
		}
	}


	/**
	 * Runs on the UI thread to actually set the {@link ImageView ImageView's}
	 * {@link android.graphics.drawable.Drawable Drawable}.
	 */
	private static class CallbackHandler extends Handler {

		private static final String TAG = "CallbackHandler";
		private WeakReference<ImageViewLoader> outer;

		public CallbackHandler(ImageViewLoader provider) {
			super();
			outer = new WeakReference<ImageViewLoader>(provider);
		}

		@Override
		public void handleMessage(Message msg) {
			try {
				ImageViewLoader provider = outer.get();
				ImageViewTag tag = (ImageViewTag) msg.obj;

				synchronized (tag) {
					if (tag.targetImageId == tag.imageId) {
						return;
					}

					if (tag.bitmap != null) {
						BitmapDrawable drawable = new BitmapDrawable(provider.context.getResources(), tag.bitmap);
						provider.cache.put(tag.targetImageId, drawable);
						tag.imageView.setImageDrawable(drawable);
						tag.imageId = tag.targetImageId;
					}
				}

			} catch (Exception e) {
				Log.d(TAG, "exception while executing in background: " + e.toString());
			}
		}
	}


	private final int maxWidth;
	private final int maxHeight;
	private final Context context;
	private final BitmapDrawableLruCache cache;
	private final BackgroundHandler backgroundHandler;
	private final CallbackHandler callbackHandler;


	/**
	 * Creates a new ImageViewLoader.
	 * @param context the associated context.
	 * @param maxWidth maximum image width in pixels, <code>UNLIMITED_DIMENSION</code> if unrestricted.
	 * @param maxHeight maximum image height in pixels, <code>UNLIMITED_DIMENSION</code> if unrestricted.
	 * @param cacheFolder cache location on local file system.
	 * @param cacheSize cache size in bytes.
	 * @param handlerThread {@link HandlerThread} to use for loading images.
	 */
	public ImageViewLoader(Context context, int maxWidth, int maxHeight, File cacheFolder, int cacheSize,
			HandlerThread handlerThread) {
		this.context = context;
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;

		cache = new BitmapDrawableLruCache(cacheSize);
		backgroundHandler = new BackgroundHandler(handlerThread.getLooper(), cacheFolder, this);
		callbackHandler = new CallbackHandler(this);
	}


	/**
	 * Applies a custom tag to an {@link ImageView} so that the ImageViewLoader can load images into it.
	 * @param imageView the ImageView to tag.
	 */
	public void tagImageView(ImageView imageView) {
		ImageViewTag tag = new ImageViewTag();
		tag.imageView = imageView;
		imageView.setTag(tag);
	}


	/**
	 * Loads an image into an {@link ImageView}.
	 * @param id the image's id, used to query the cache. This should be unique to the image.
	 * @param url the image's url.
	 * @param imageView the target ImageView.
	 */
	public void load(int id, String url, ImageView imageView) {
		ImageViewTag tag = (ImageViewTag) imageView.getTag();

		synchronized (tag) {
			if (id != tag.targetImageId) {
				backgroundHandler.removeMessages(0, tag);
				callbackHandler.removeMessages(0, tag);
				tag.targetImageId = id;
				tag.url = url;
				tag.bitmap = null;
			}

			if (id != tag.imageId) {
				BitmapDrawable drawable = null;
				drawable = cache.get(id);

				if (drawable == null) {
					backgroundHandler.sendMessage(Message.obtain(backgroundHandler, 0, tag));
				} else {
					tag.imageId = id;
					imageView.setImageDrawable(drawable);
				}
			}
		}
	}


	/**
	 * Cancels loading an image.
	 * @param imageView
	 */
	public void cancelLoading(ImageView imageView) {
		backgroundHandler.removeMessages(0, imageView.getTag());
		callbackHandler.removeMessages(0, imageView.getTag());
	}


	/**
	 * Removes the image in an {@link ImageView}, leaving it blank.
	 * @param imageView
	 */
	public void removeImage(ImageView imageView) {
		ImageViewTag tag = (ImageViewTag) imageView.getTag();

		synchronized (tag) {
			cancelLoading(imageView);
			imageView.setImageDrawable(null);
			tag.imageId = ImageViewTag.IMAGE_ID_NONE;
			tag.url = null;
			tag.targetImageId = ImageViewTag.IMAGE_ID_NONE;
		}
	}


	/**
	 * Retrieves the id of the image currently visible in the {@link ImageView}.
	 * @param imageView
	 * @return the id of the image.
	 */
	public int getId(ImageView imageView) {
		ImageViewTag tag = (ImageViewTag) imageView.getTag();
		int id = 0;
		synchronized (tag) {
			id = tag.imageId;
		}
		return id;
	}
}
