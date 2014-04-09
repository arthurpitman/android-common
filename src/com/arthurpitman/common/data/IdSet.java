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

package com.arthurpitman.common.data;

import java.util.Arrays;


/**
 * Simple class for a set of long IDs.
 */
public class IdSet {

	/** Default capacity of the IdSet. */
	public static final int DEFAULT_CAPACITY = 10;

	private int size = 0;
	private long[] ids;


	/**
	 * Creates a new IdSet with DEFAULT_CAPACITY.
	 */
	public IdSet() {
		this(DEFAULT_CAPACITY);
	}


	/**
	 * Creates a new IdSet.
	 * @param capacity initial capacity.
	 */
	public IdSet(int capacity) {
		capacity = getIdealSize(capacity * 8) / 8;
		ids = new long[capacity];
		size = 0;
	}


	/**
	 * Adds an ID.
	 * @param id
	 */
	public void add(long id) {
		if (size == ids.length) {
			long[] newIds= new long[getIdealSize(size+1)];
			System.arraycopy(ids, 0, newIds, 0, ids.length);
			ids = newIds;
		}

		ids[size] = id;
		size++;
	}


	/**
	 * Gets an ID by index.
	 * @param index
	 * @return
	 */
	public long get(int index) {
		return ids[index];
	}


	/**
	 * Gets the current size of the IdSet.
	 * @return
	 */
	public int size() {
		return size;
	}


	/**
	 * Sorts the IdSet.
	 */
	public void sort() {
		Arrays.sort(ids);
	}


	/**
	 * Determines if the IdSet is empty.
	 * @return
	 */
	public boolean isEmpty() {
		return size == 0;
	}


	/**
	 * Determines the ideal size of the IdSet with the knowledge that it uses a long array.
	 * @param size the requested size.
	 * @return ideal size, greater than the requested size.
	 */
	public static int getIdealSize(int size) {
		size = size * 8;
		for (int i = 4; i < 32; i++) {
			if (size <= (1 << i) - 12) {
				return ((1 << i) - 12) / 8;
			}
		}
		return size / 8;
	}
}