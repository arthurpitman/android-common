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


/**
 * Base class for objects identified by a long id.
 */
public class IdObject {

	/** Unique identifier for objects of this type. */
	protected final long id;

	/** Stale flag. Do not manipulate this directly, it will be managed by the provider. */
	protected boolean stale;


	/**
	 * Creates a new IdObject.
	 * @param id
	 */
	public IdObject(long id) {
		this.id = id;
	}


	/**
	 * Gets the id.
	 * @return
	 */
	public long getId() {
		return id;
	}


	/**
	 * Sets the stale flag.
	 * <p/>
	 * Do not call this directly, it will be managed by the provider.
	 * @param stale
	 */
	public void setStale(boolean stale) {
		this.stale = stale;
	}


	/**
	 * Gets the stale flag.
	 * @return
	 */
	public boolean isStale() {
		return stale;
	}
}