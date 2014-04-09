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


/**
 * Base and wrapper class for exceptions.
 */
public class CoreException extends Exception {
	private static final long serialVersionUID = -8883783396176907984L;


	/**
	 * Creates a new CoreException wrapping the specified Throwable.
	 * @param throwable
	 */
	public CoreException(Throwable throwable) {
		super(throwable);
	}

	/**
	 * Creates a new CoreException with the specified message.
	 * @param message
	 */
	public CoreException(String message) {
		super(message);
	}
}