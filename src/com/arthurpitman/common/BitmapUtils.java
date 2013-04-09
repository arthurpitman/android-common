/*
 * Copyright (C) 2012, 2013 Arthur Pitman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.arthurpitman.common;

import android.graphics.Bitmap;


/**
 * Some utility functions for working with {@link Bitmap Bitmaps}.
 */
public class BitmapUtils {

	/**
	 * Resizes a {@link Bitmap} to fit within certain dimensions.
	 * @param bitmap the Bitmap to resize.
	 * @param width maximum width.
	 * @param height maximum height.
	 * @return the resized Bitmap.
	 */
	public static Bitmap resizeBitmapToFit(Bitmap bitmap, int width, int height) {

		boolean filter = false;
		int originalWidth = bitmap.getWidth();
		int originalHeight = bitmap.getHeight();
		double multiplier = 0;

		// don't do anything if it already small enough
		if ((originalWidth <= width) && (originalHeight <= height))
			return bitmap;

		// determine how to scale it
		if ((originalWidth >= width) || (originalHeight > height)) {
			if (originalWidth > originalHeight) {
				multiplier = (double)(originalHeight) / originalWidth;
				height = (int)(Math.min(originalHeight, height) * multiplier);
			} else {
				multiplier = (double)(originalWidth) / originalHeight;
				width = (int)(Math.min(originalWidth, width) * multiplier);
			}
		} else {
			if (originalWidth > originalHeight) {
				multiplier = (double)(width) / originalWidth;
			} else {
				multiplier = (double)(height) / originalHeight;
			}
			width = (int)(originalWidth * multiplier);
			height = (int)(originalHeight * multiplier);
		}

		// actually rescale
		return Bitmap.createScaledBitmap(bitmap, width, height, filter);
	}
}
