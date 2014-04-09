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
 * An efficient result set for IdObjects, optimized for small sets.
 * @param <T>
 */
public class ResultSet<T extends IdObject> {

	private static final int DEFAULT_CAPACITY = 10;
	private int size = 0;
	private long[] ids;
	private Object[] values;


	/**
	 * Creates a new ResultSet with default capacity.
	 */
	public ResultSet() {
		this(DEFAULT_CAPACITY);
	}


	/**
	 * Creates a new ResultSet with the specified capacity.
	 * @param capacity
	 */
	public ResultSet(int capacity) {
		capacity = IdSet.getIdealSize(capacity);
		ids = new long[capacity];
		values = new Object[capacity];
		size = 0;
	}


	/**
	 * Puts (inserts) a object into the ResultSet.
	 * @param value
	 */
	public void put(T value) {
		long id = value.getId();
		int i = Arrays.binarySearch(ids, 0, size, id);

		if (i >= 0) {
			values[i] = value;
		} else {
			i = ~i;

			if (size == ids.length) {
				grow();
			}

			int m = size - i;
			if (m != 0) {
				System.arraycopy(ids, i, ids, i + 1, m);
				System.arraycopy(values, i, values, i + 1, m);
			}

			ids[i] = id;
			values[i] = value;
			size++;
		}
	}


	/**
	 * Appends an object to the end of the ResultSet.
	 * @param value
	 */
	public void append(T value) {
		long id = value.getId();
		if ((size > 0) && (id <= ids[size - 1])) {
			put(value);
			return;
		}

		if (size == ids.length) {
			grow();
		}

		ids[size] = id;
		values[size] = value;
		size++;
	}


	/**
	 * Gets an object by ID.
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T get(long id) {
		int i = Arrays.binarySearch(ids, 0, size, id);
		if (i < 0) {
			return null;
		} else {
			return (T) values[i];
		}
	}


	/**
	 * Gets an ID by index.
	 * @param index
	 * @return
	 */
	public long idAt(int index) {
		return ids[index];
	}


	/**
	 * Gets an object by index.
	 * @param index
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T valueAt(int index) {
		return (T) values[index];
	}


	/**
	 * Gets the size of the ResultSet.
	 * @return
	 */
	public int size() {
		return size;
	}


	/**
	 * Increases the storage allocated for the ResultSet.
	 */
	private void grow()	{
		int n = IdSet.getIdealSize(size + 1);

		long[] newIds = new long[n];
		Object[] newValues = new Object[n];

		System.arraycopy(ids, 0, newIds, 0, ids.length);
		System.arraycopy(values, 0, newValues, 0, values.length);

		ids = newIds;
		values = newValues;
	}
}