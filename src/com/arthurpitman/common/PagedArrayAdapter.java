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

	/**
	 * Caches loading view.
	 */
	private View loadingView;


	/**
	 * Indicates if loading is in progress.
	 */
	private boolean loading = false;


	/**
	 * Indicates if more items are available, i.e. loading view should be displayed.
	 */
	private boolean moreAvailable = true;


	/**
	 * Backing store for items.
	 */
	protected ArrayList<T> items  = new ArrayList<T>();


	/** Item view type constant */
	public static final int ITEM_VIEW_TYPE = 0;


	/** Loading view type constant */
	public static final int LOADING_VIEW_TYPE = 1;


	/*
	 * ========================================
	 * IMPLEMENTATION FOR BASE ADAPTER
	 * ========================================
	 */


	@Override
	public int getItemViewType(int position) {
		if (position == items.size()) {
			return LOADING_VIEW_TYPE;
		} else {
			return 0;
		}
	}


	@Override
	public int getCount() {
		if (moreAvailable) {
			return items.size() + 1;
		} else {
			return items.size();
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
		 if (getItemViewType(position) == LOADING_VIEW_TYPE) {
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
		 } else {
			 // note: cannot convert a loading view to an item view
			 if (convertView == loadingView) {
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
	 * Get a view for an item at the specified position.
	 * @param position position of the item.
	 * @param convertView view to recycle if possible.
	 * @return the view.
	 */
	protected abstract View getItemView(int position, View convertView);


	/**
	 * Get a loading view.
	 * <p>
	 * This view is never recycled as it is cached.
	 * @return
	 */
	protected abstract View getLoadingView();


	/*
	 * ========================================
	 * OTHER METHODS
	 * ========================================
	 */


	/**
	 * Adds new items, typically loaded on another thread.
	 * @param newItems the items to add, <code>null</code> indicates no more items are available.
	 */
	public void addNewItems(List<T> newItems) {
		if (newItems == null) {
			moreAvailable = false;
		} else {
			items.addAll(newItems);
			moreAvailable = true;
		}
		loading = false;
		notifyDataSetChanged();
	}


	/**
	 * Manually sets the flag indicating if more items are available.
	 * @param moreAvailable
	 */
	public void setMoreAvailable(boolean moreAvailable) {
		this.moreAvailable = moreAvailable;
		notifyDataSetChanged();
	}
}