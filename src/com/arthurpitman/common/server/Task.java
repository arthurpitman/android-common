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

	/**
	 * Callback interface for {@code Tasks}.
	 */
	public interface Callback {
		void run(Task task, boolean success);
	}

	private Callback callback;
	private volatile boolean success = false;
	private volatile boolean canceled = false;


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
	 * @return
	 * @throws CoreException
	 */
	public abstract boolean run(SharedContext sharedContext) throws CoreException;


	/**
	 * Gets the callback for this {@code Task}.
	 * @return
	 */
	public Callback getCallback() {
		return callback;
	}


	/**
	 * Sets the state of the success flag.
	 * @param success
	 */
	public void setSuccess(boolean success) {
		this.success = success;
	}


	/**
	 * Gets the success flag.
	 * @return
	 */
	public boolean isSuccess() {
		return success;
	}


	/**
	 * Sets the canceled flag.
	 */
	public void cancel() {
		canceled = true;
	}


	/**
	 * Gets the canceled flag.
	 * @return
	 */
	public boolean isCanceled() {
		return canceled;
	}
}