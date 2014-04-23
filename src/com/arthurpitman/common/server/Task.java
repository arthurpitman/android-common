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

package com.arthurpitman.common.server;

import com.arthurpitman.common.CoreException;


/**
 * Base class for tasks.
 */
public abstract class Task {

	public static final int STATUS_NONE = 0;
	public static final int STATUS_READY = 1;
	public static final int STATUS_SUCCESS = 2;
	public static final int STATUS_ERROR = 3;
	public static final int STATUS_CANCELED = 4;

	/**
	 * Callback interface for {@code Tasks}.
	 */
	public interface Callback {
		void run(Task task, boolean success);
	}

	private Callback callback;
	private volatile int status = STATUS_NONE;


	/**
	 * Creates a new Task with the specified callback.
	 * @param callback
	 */
	public Task(Callback callback) {
		this.callback = callback;
	}


	/**
	 * Performs the actual work of the Task. <p/>
	 * Override this in derived classes.
	 * @param sharedContext
	 * @return new {@code Task} status.
	 * @throws CoreException
	 */
	public abstract int run(SharedContext sharedContext) throws CoreException;


	/**
	 * Gets the callback for this {@code Task}.
	 * @return
	 */
	public Callback getCallback() {
		return callback;
	}


	/**
	 * Gets the {@code Task} status.
	 * @return
	 */
	public int getStatus() {
		return status;
	}


	/**
	 * Sets the {@code Task} status.
	 * @param status
	 */
	public void setStatus(int status) {
		this.status = status;
	}
}