/*
 * Copyright (C) 2012, 2013 Arthur Pitman
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
 * Wrapper of standard Log class to make it easy to enable / disable log messages.
 */
public class Log {

	/**
	 * Change this flag and recompile to turn logging on or off.
	 */
	public static final boolean LOG = true;


	/**
	 * Emits a information log message.
	 * @param tag
	 * @param message
	 */
	public static void i(String tag, String message) {
		if (LOG)
			android.util.Log.i(tag, message);
	}


	/**
	 * Emits a error log message.
	 * @param tag
	 * @param message
	 */
	public static void e(String tag, String message) {
		if (LOG)
			android.util.Log.e(tag, message);
	}


	/**
	 * Emits a debug log message.
	 * @param tag
	 * @param message
	 */
	public static void d(String tag, String message) {
		if (LOG)
			android.util.Log.d(tag, message);
	}


	/**
	 * Emits a verbose log message.
	 * @param tag
	 * @param message
	 */
	public static void v(String tag, String message) {
		if (LOG)
			android.util.Log.v(tag, message);
	}


	/**
	 * Emits a warning log message.
	 * @param tag
	 * @param message
	 */
	public static void w(String tag, String message) {
		if (LOG)
			Log.w(tag, message);
	}
}
