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

import android.view.MotionEvent;


/**
 * Filters touch events to track single and dual touch.
 * <p>
 * A TouchFilter sends events to a {@link TouchListener}.
 */
public class TouchFilter {

	private static final int INVALID_POINTER_ID = -1;

	private int pointerCount = 0;
	private int pointerId1 = INVALID_POINTER_ID;
	private int pointerId2 = INVALID_POINTER_ID;

	private TouchListener listener;


	/**
	 * Creates a new TouchFilter with the associated {@link TouchListener}.
	 * @param listener
	 */
	public TouchFilter(TouchListener listener) {
		this.listener = listener;
	}


	/**
	 * Processes touch events and calls appropriate methods of the associated {@link TouchListener}.
	 * @param ev the event to process.
	 * @return <code>true</code> if the event was handled, otherwise <code>false</code>.
	 */
	public boolean onTouchEvent(final MotionEvent ev) {
		final int actionRaw = ev.getAction();
		final int action = actionRaw & MotionEvent.ACTION_MASK;
		switch (action) {
			case MotionEvent.ACTION_POINTER_DOWN:
			case MotionEvent.ACTION_DOWN:
				if (pointerCount < 2) {
					final int pointerIndex = (actionRaw & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
					final int pointerId = ev.getPointerId(pointerIndex);
					if (pointerCount == 0) {
						pointerId1 = pointerId;
						listener.onSingleTouchStart(ev.getX(pointerIndex), ev.getY(pointerIndex), ev.getEventTime());
					} else if (pointerCount == 1) {
						pointerId2 = pointerId;
						final int pointerIndex1 = ev.findPointerIndex(pointerId1);
						listener.onSingleTouchEnd(ev.getEventTime());
						listener.onDualTouchStart(ev.getX(pointerIndex1), ev.getY(pointerIndex1), ev.getX(pointerIndex), ev.getY(pointerIndex), ev.getEventTime());
					}
					pointerCount++;
				}
				break;

			case MotionEvent.ACTION_MOVE:
				if (pointerCount == 1) {
					final int pointerIndex1 = ev.findPointerIndex(pointerId1);
					listener.onSingleTouchMove(ev.getX(pointerIndex1), ev.getY(pointerIndex1), ev.getEventTime());
				} else if (pointerCount == 2) {
					final int pointerIndex1 = ev.findPointerIndex(pointerId1);
					final int pointerIndex2 = ev.findPointerIndex(pointerId2);
					listener.onDualTouchMove(ev.getX(pointerIndex1), ev.getY(pointerIndex1), ev.getX(pointerIndex2), ev.getY(pointerIndex2), ev.getEventTime());
				}
				break;

			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_POINTER_UP:
			case MotionEvent.ACTION_UP:
				int pointerIndex = (actionRaw & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
				final int pointerId = ev.getPointerId(pointerIndex);
				if (pointerId == pointerId1) {
					if (pointerCount == 1) {
						pointerId1 = INVALID_POINTER_ID;
						pointerCount--;
						listener.onSingleTouchEnd(ev.getEventTime());
					} else if (pointerCount == 2) {
						pointerId1 = pointerId2;
						pointerId2 = INVALID_POINTER_ID;
						pointerIndex = ev.findPointerIndex(pointerId1);
						pointerCount--;
						listener.onDualTouchEnd(ev.getEventTime());
						listener.onSingleTouchStart(ev.getX(pointerIndex), ev.getY(pointerIndex), ev.getEventTime());
					}
				} else if (pointerId == pointerId2) {
					if (pointerCount == 2) {
						pointerId2 = INVALID_POINTER_ID;
						pointerIndex = ev.findPointerIndex(pointerId1);
						pointerCount--;
						listener.onDualTouchEnd(ev.getEventTime());
						listener.onSingleTouchStart(ev.getX(pointerIndex), ev.getY(pointerIndex), ev.getEventTime());
					}
				}
				break;
		}
		return true;
	}
}
