/*
 * Copyright (C) 2012 - 2014 Arthur Pitman
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

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


/**
 * Paged adapted backed by an {@link ArrayList}.
 *
 * @param <T> the item type.
 */
public abstract class PagedArrayAdapter<T> extends BaseAdapter {


	public interface OnFinishedLoadingListener {
		void onFinishedLoading();
	}


	/** Caches loading view. */
	private View loadingView;


	/** Caches empty view. */
	private View emptyView;


	/** Indicates if loading is in progress. */
	private boolean loading = false;


	/** Indicates if more items are available, i.e. loading view should be displayed. */
	private boolean moreAvailable = true;


	/** Backing store for items. */
	protected ArrayList<T> items  = new ArrayList<T>();


	/** Item view type constant */
	public static final int ITEM_VIEW_TYPE = 0;


	/** Loading view type constant */
	public static final int LOADING_VIEW_TYPE = 1;


	/** Empty view type constant */
	public static final int EMPTY_VIEW_TYPE = 2;



	/** Listener for finished loading event. */
	private OnFinishedLoadingListener onFinishedLoadingListener;


	/*
	 * ========================================
	 * IMPLEMENTATION FOR BASE ADAPTER
	 * ========================================
	 */


	@Override
	public int getItemViewType(int position) {
		if (position == items.size()) {
			if (moreAvailable) {
				return LOADING_VIEW_TYPE;
			} else {
				return EMPTY_VIEW_TYPE;
			}
		} else {
			return 0;
		}
	}


	@Override
	public int getCount() {
		if (moreAvailable) {
			return items.size() + 1;
		} else {
			return Math.max(items.size(), 1);
		}
	}


	@Override
	public Object getItem(int position) {
		return items.get(position);
	}


	@Override
	public long getItemId(int position) {
		return position;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int itemViewType = getItemViewType(position);
		if (itemViewType == LOADING_VIEW_TYPE) {
			// if loading view is now visible and not loading, start loading
			if (!loading) {
				loading = true;
				loadItems();
			}

			// cache loading view
			if (loadingView == null) {
				loadingView = getLoadingView();
			}
			return loadingView;
		} else if (itemViewType == EMPTY_VIEW_TYPE) {
			// cache loading view
			if (emptyView == null) {
				emptyView = getEmptyView();
			}
			return emptyView;
		} else {

			 // note: cannot convert a loading or empty view to an item view
			 if ((convertView == loadingView) || (convertView == emptyView)){
				 return getItemView(position, null);
			 } else {
				 return getItemView(position, convertView);
			 }
		 }
	}


	/*
	 * ========================================
	 * ABSTRACT METHODS
	 * ========================================
	 */


	/**
	 * Loads more items, usually on a separate thread.
	 */
	protected abstract void loadItems();


	/**
	 * Gets a view for an item at the specified position.
	 * @param position position of the item.
	 * @param convertView view to recycle if possible.
	 * @return the view.
	 */
	protected abstract View getItemView(int position, View convertView);


	/**
	 * Gets a loading view.
	 * <p>
	 * This view is never recycled as it is cached.
	 * @return
	 */
	protected abstract View getLoadingView();


	/**
	 * Gets a view to display when the adapter is empty.
	 * <p>
	 * This view is never recycled as it is cached.
	 * @return
	 */
	protected abstract View getEmptyView();


	/*
	 * ========================================
	 * OTHER METHODS
	 * ========================================
	 */


	/**
	 * Adds new items, typically loaded on another thread.
	 * @param newItems the items to add.
	 * @param moreAvailable
	 */
	public void addNewItems(List<T> newItems, boolean moreAvailable) {
		if (newItems != null) {
			items.addAll(newItems);
		}

		this.moreAvailable = moreAvailable;
		loading = false;
		notifyDataSetChanged();

		if (!moreAvailable && (onFinishedLoadingListener != null)) {
			onFinishedLoadingListener.onFinishedLoading();
		}
	}


	/**
	 * Manually sets the flag indicating if more items are available.
	 * @param moreAvailable
	 */
	public void setMoreAvailable(boolean moreAvailable) {
		this.moreAvailable = moreAvailable;
		notifyDataSetChanged();
	}


	/**
	 * Refreshes the adapter by reloading the data.
	 */
	public void refresh() {
		loading = false;
		moreAvailable = true;
		items.clear();
		onRefresh();
		notifyDataSetChanged();
	}


	/**
	 * Override this to perform custom reinitialization upon refresh.
	 */
	protected void onRefresh() {};


	/**
	 * Gets the {@code OnFinishedLoadingListener}.
	 * @return
	 */
	public OnFinishedLoadingListener getOnFinishedLoadingListener() {
		return onFinishedLoadingListener;
	}


	/**
	 * Sets the {@code OnFinishedLoadingListener}.
	 * @param onFinishedLoadingListener
	 */
	public void setOnFinishedLoadingListener(
			OnFinishedLoadingListener onFinishedLoadingListener) {
		this.onFinishedLoadingListener = onFinishedLoadingListener;
	}
}