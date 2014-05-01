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
import java.util.List;

import com.arthurpitman.common.CoreException;
import android.support.v4.util.LruCache;


/**
 * Base class for remote providers of IdObjects.
 * @param <T>
 */
public abstract class RemoteProvider <T extends IdObject> {

	public static final int SCOPE_ALL = 1;
	public static final int SCOPE_LOCAL = 2;

	protected LruCache<Long, T> cache;


	/**
	 * Creates a new RemoteProvider with the specified cache size.
	 * @param cacheSize
	 */
	public RemoteProvider(int cacheSize) {
		cache = new LruCache<Long, T>(cacheSize);
	}


	/**
	 * Gets a object by ID.
	 * @param id
	 * @param scope
	 * @return
	 * @throws CoreException
	 */
	public T get(long id, int scope) throws CoreException {
		T o = cache.get(id);
		if (o == null) {
			o = getLocal(id);
			if ((o == null) && (scope == SCOPE_ALL)) {
				o = getRemote(id);
				if (o != null) {
					updateLocal(o);
				}
			}
			if (o != null) {
				cache.put(id, o);
			}
		}

		if ((o != null) && o.isStale() && (scope == SCOPE_ALL)) {
			T or = getRemote(id);
			if (or != null) {
				updateLocal(or);
				cache.put(id, or);
				o = or;
			}
		}

		return o;
	}


	/**
	 * Gets a set of objects specified by an {@link IdSet}.
	 * @param ids
	 * @param scope
	 * @return
	 * @throws CoreException
	 */
	public ResultSet<T> get(IdSet ids, int scope) throws CoreException {
		long[] sortedIds = ids.toArray();
		Arrays.sort(sortedIds);
		ResultSet<T> result = new ResultSet<T>(sortedIds.length);
		IdSet missingIds = null;
		for (long id : sortedIds) {
			T o = cache.get(id);
			if (o == null) {
				o = getLocal(id);
				if ((o == null) && (scope == SCOPE_ALL)) {
					if (missingIds == null) {
						missingIds = new IdSet();
					}
					missingIds.add(id);
				} else {
					cache.put(id, o);
					result.append(o);
				}
			} else {
				result.append(o);
			}
		}

		pullRemote(missingIds, result);

		IdSet staleIds = null;
		int size = result.size();
		for (int i = 0; i < size; i++) {
			T o = result.valueAt(i);
			if (o.isStale()) {
				if (staleIds == null) {
					staleIds = new IdSet();
				}
				staleIds.add(o.getId());
			}
		}

		pullRemote(staleIds, result);

		return result;
	}


	/**
	 * Gets objects from remote storage, updates caches and adds them to the specified result.
	 * @param ids
	 * @param result
	 * @throws CoreException
	 */
	private void pullRemote(IdSet ids, ResultSet<T> result) throws CoreException {
		if ((ids != null) && !ids.isEmpty()) {
			List<T> bulkObjects = getRemoteBulk(ids);
			updateLocalBulk(bulkObjects);
			for(T o : bulkObjects) {
				cache.put(o.getId(), o);
				result.put(o);
			}
		}

	}


	/**
	 * Refreshes a single object.
	 * @param id
	 * @param defer
	 * @throws CoreException
	 */
	public void refresh(long id, boolean defer) throws CoreException {
		if (defer) {
			markStaleLocal(id);
			T o = cache.get(id);
			if (o != null) {
				o.setStale(true);
			}
		} else {
			T o = getRemote(id);
			if (o != null) {
				updateLocal(o);
				if (cache.get(id) != null) {
					cache.put(o.getId(), o);
				}
			}
		}
	}


	/**
	 * Refreshes a set of objects.
	 * @param ids
	 * @param defer
	 * @throws CoreException
	 */
	public void refresh(IdSet ids, boolean defer) throws CoreException {
		if (defer) {
			markStaleLocalBulk(ids);
			int size = ids.size();
			for (int i = 0; i < size; i++) {
				long id = ids.get(i);
				T o = cache.get(id);
				if (o != null) {
					o.setStale(true);
				}
			}
		} else {
			List<T> bulkObjects = getRemoteBulk(ids);
			updateLocalBulk(bulkObjects);
			for(T o : bulkObjects) {
				long id = o.getId();
				if (cache.get(id) != null) {
					cache.put(id, o);
				}
			}
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


	/**
	 * Updates an object in local storage.
	 * <p/>
	 * Override this in derived classes.
	 * @param o
	 * @throws CoreException
	 */
	protected abstract void updateLocal(T o) throws CoreException;


	/**
	 * Marks an object as stale in local storage, if it exists.
	 * <p/>
	 * Override this in derived classes.
	 * @param id
	 * @throws CoreException
	 */
	protected abstract void markStaleLocal(long id) throws CoreException;


	/**
	 * Marks an object as stale in local storage, if it exists.
	 * <p/>
	 * Override this in derived classes.
	 * @param ids
	 * @throws CoreException
	 */
	protected abstract void markStaleLocalBulk(IdSet ids) throws CoreException;


	/**
	 * Updates a set of objects in local storage.
	 * <p/>
	 * Override this in derived classes.
	 * @param set
	 * @throws CoreException
	 */
	protected abstract void updateLocalBulk(List<T> set) throws CoreException;


	/**
	 * Gets an object from remote storage.
	 * <p/>
	 * Override this in derived classes.
	 * @param id
	 * @return
	 * @throws CoreException
	 */
	protected abstract T getRemote(long id) throws CoreException;


	/**
	 * Gets a set of objects from remote storage.
	 * <p/>
	 * Override this in derived classes.
	 * @param ids
	 * @return
	 * @throws CoreException
	 */
	protected abstract List<T> getRemoteBulk(IdSet ids) throws CoreException;
}