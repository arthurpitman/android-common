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

import android.support.v4.util.LruCache;

import com.arthurpitman.common.CoreException;


/**
 * Base class for local providers of IdObjects.
 * @param <T>
 */
public abstract class LocalProvider<T extends IdObject>{
	private LruCache<Long, T> cache;


	/**
	 * Creates a LocalProvider with the specified cache size.
	 * @param cacheSize
	 */
	public LocalProvider(int cacheSize) {
		cache = new LruCache<Long, T>(cacheSize);
	}


	/**
	 * Gets an object by ID.
	 * @param id
	 * @return
	 * @throws CoreException
	 */
	public T get(long id) throws CoreException {
		T o = cache.get(id);
		if (o == null) {
			o = getLocal(id);
		}
		return o;
	}


	/**
	 * Gets a set of objects specified by an {@link IdSet}.
	 * @param ids
	 * @return
	 * @throws CoreException
	 */
	public ResultSet<T> get(IdSet ids) throws CoreException {
		ids.sort();
		int size = ids.size();
		ResultSet<T> result = new ResultSet<T>(size);
		for (int i = 0; i < size; i++) {
			long id = ids.get(i);
			T o = cache.get(id);
			if (o == null) {
				o = getLocal(id);
				if (o != null) {
					cache.put(id, o);
					result.append(o);
				}
			} else {
				result.append(o);
			}
		}
		return result;
	}


	/**
	 * Refreshes a single object.
	 * @param id
	 * @throws CoreException
	 */
	public void refresh(long id) throws CoreException {
		if (cache.get(id) != null) {
			T o = getLocal(id);
			cache.put(o.getId(), o);
		}
	}


	/**
	 * Refreshes a set of objects.
	 * @param ids
	 * @throws CoreException
	 */
	public void refresh(IdSet ids) throws CoreException {
		int size = ids.size();
		for (int i = 0; i < size; i++) {
			long id = ids.get(i);
			refresh(id);
		}
	}


	/**
	 * Retrieves an object from local storage.
	 * <p/>
	 * Override this in derived classes.
	 * @param id The id of the object
	 * @return The object or null if unavailable
	 * @throws CoreException
	 */
	protected abstract T getLocal(long id) throws CoreException;
}