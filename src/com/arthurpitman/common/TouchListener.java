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


/**
 * Interface for receiving single and dual touch events emitted by a {@link TouchFilter}.
 * <p>
 * Events are passed in a consistent order, for example if the user adds a second finger after touching with just one,
 * a <code>onSingleTouchEnd()</code> event will be sent before the <code>onDualTouchStart()</code> event.
 */
public interface TouchListener {

	/**
	 * Override this method to handle the start of a single touch.
	 * @param x x coordinate relative to the View, in pixels.
	 * @param y y coordinate relative to the View, in pixels.
	 * @param eventTime the time this event occurred, in the
	 * {@link android.os.SystemClock#uptimeMillis() uptimeMillis()} time base.
	 */
	void onSingleTouchStart(float x, float y, long eventTime);

	/**
	 * Override this method to handle the movement of a single touch.
	 * @param x x coordinate relative to the View, in pixels.
	 * @param y y coordinate relative to the View, in pixels.
	 * @param eventTime the time this event occurred, in the
	 * {@link android.os.SystemClock#uptimeMillis() uptimeMillis()} time base.
	 */
	void onSingleTouchMove(float x, float y, long eventTime);

	/**
	 * Override this method to handle the end of a single touch.
	 * @param eventTime the time this event occurred, in the
	 * {@link android.os.SystemClock#uptimeMillis() uptimeMillis()} time base.
	 */
	void onSingleTouchEnd(long eventTime);

	/**
	 * Override this method to handle the start of a dual touch.
	 * @param x1 x coordinate of the first finger relative to the View, in pixels.
	 * @param y1 y coordinate of the first finger relative to the View, in pixels.
	 * @param x2 x coordinate of the second finger relative to the View, in pixels.
	 * @param y2 y coordinate of the second finger relative to the View, in pixels.
	 * @param eventTime the time this event occurred, in the
	 * {@link android.os.SystemClock#uptimeMillis() uptimeMillis()} time base.
	 */
	void onDualTouchStart(float x1, float y1, float x2, float y2, long eventTime);

	/**
	 * Override this method to handle the movement of a dual touch.
	 * @param x1 x coordinate of the first finger relative to the View, in pixels.
	 * @param y1 y coordinate of the first finger relative to the View, in pixels.
	 * @param x2 x coordinate of the second finger relative to the View, in pixels.
	 * @param y2 y coordinate of the second finger relative to the View, in pixels.
	 * @param eventTime the time this event occurred, in the
	 * {@link android.os.SystemClock#uptimeMillis() uptimeMillis()} time base.
	 */
	void onDualTouchMove(float x1, float y1, float x2, float y2, long eventTime);

	/**
	 * Override this method to handle the end of a dual touch.
	 * @param eventTime the time this event occurred, in the
	 * {@link android.os.SystemClock#uptimeMillis() uptimeMillis()} time base.
	 */
	void onDualTouchEnd(long eventTime);
}
