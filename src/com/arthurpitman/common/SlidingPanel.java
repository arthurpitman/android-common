/*
 * Copyright (C) 2014 Arthur Pitman
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

import android.content.Context;
import android.support.v4.view.VelocityTrackerCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;


/**
 * A panel that can be pulled up to show additional content.
 * <p>
 * Implemented as a {@link ViewGroup} with exactly two children.
 */
public class SlidingPanel extends ViewGroup {

	/** The panel is collapsed. */
	public static final int COLLAPSED = 0;

	/** The view is expanded. */
	public static final int EXPANDED = 1;

	/** The view is in transition. */
	public static final int TRANSITION = 2;


	/** No touch is in progress. */
	private static final int TOUCH_NONE = 0;

	/** Touch has started and being tracked by the view.  */
	private static final int TOUCH_STARTED = 1;


	/** {@link VelocityTracker} for the view. */
	private VelocityTracker velocityTracker = null;

	/** {@link Scroller} for the view. */
	private Scroller scroller;


	/** Amount of movement before sliding starts. */
	private int touchSlop;

	/** Minimum velocity for fling gesture.	 */
	private int minimumflingVelocity = 0;


	/** Current touch state. */
	private int touchState = TOUCH_NONE;

	/** Scroll position when touch started. */
	private int startTouchScrollY;

	/** Y position where touch started */
	private float startTouchY;


	/** Current view. */
	private int currentView = COLLAPSED;

	/** Last known collapsed height. */
	private int collapsedHeight = 0;

	/** Last known view height. */
	private int viewHeight = 0;


	/**
	 * Creates a new {@code SlidingPanel}.
	 * @param context
	 */
	public SlidingPanel(Context context) {
		super(context);
		initializeView();
	}


	/**
	 * Creates a new {@code SlidingPanel}.
	 * @param context
	 * @param attrs
	 */
	public SlidingPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		initializeView();
	}


	/**
	 * Performs shared initialization of the {@code SlidingPanel}.
	 */
	private final void initializeView() {
		scroller = new Scroller(getContext());
		final ViewConfiguration configuration = ViewConfiguration.get(getContext());
		touchSlop = configuration.getScaledTouchSlop();
		minimumflingVelocity = configuration.getScaledMinimumFlingVelocity() * 4;
	}


	/**
	 * Sets the current view of the {@code SlidingPanel}.
	 * @param view {@code HEADER} or {@code BODY}.
	 * @param animate {@code true} to animate to the new view.
	 */
	public void setView(int view, boolean animate) {
		int targetScrollY = 0;
		if (view == EXPANDED) {
			targetScrollY = viewHeight;
		}

		if (animate) {
			final int currentScrollY = getScrollY();
			scroller.startScroll(0, currentScrollY, 0, targetScrollY - currentScrollY);
		} else {
			scrollTo(0, targetScrollY);
		}
		invalidate();
	}


	/**
	 * Gets the current view of the {@code SlidingPanel}.
	 * @return {@code HEADER}, {@code BODY} or {@code TRANSITION}.
	 */
	public int getView() {
		return currentView;
	}


	/*
	 * ========================================
	 * OVERRIDING VIEW METHODS
	 * ========================================
	 */


	@Override
	public void computeScroll() {
		// work out correct scroll position
		int currentScrollY = 0;
		if (scroller.computeScrollOffset()) {
			currentScrollY = scroller.getCurrY();
			scrollTo(0, currentScrollY);
			invalidate();
		} else {
			currentScrollY = getScrollY();
		}

		// set alpha of views
		float alpha = (float)currentScrollY / viewHeight;
		View view1 = getChildAt(0);
		if (view1 != null) {
			view1.setAlpha(1 - alpha);
		}
		View view2 = getChildAt(1);
		if (view2 != null) {
			view2.setAlpha(alpha);
		}

		// update current view
		if (currentScrollY == 0) {
			currentView = COLLAPSED;
		} else  if (currentScrollY == viewHeight){
			currentView = EXPANDED;
		} else {
			currentView = TRANSITION;
		}
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float y = event.getY();
		int action = event.getActionMasked();
		int pointerId = event.getPointerId(event.getActionIndex());

		switch (action) {
			case MotionEvent.ACTION_POINTER_DOWN:
			case MotionEvent.ACTION_DOWN:
				startTouchY = y;
				startTouchScrollY = getScrollY();

				// if touch didn't occur on the actual control, ignore it
				float touchBoundary = viewHeight - collapsedHeight - startTouchScrollY;
				if (y < touchBoundary) {
					return false;
				}

				// start tracking velocity
				if (velocityTracker == null) {
					velocityTracker = VelocityTracker.obtain();
				} else {
					velocityTracker.clear();
				}
				velocityTracker.addMovement(event);
				break;

			case MotionEvent.ACTION_MOVE:
				// determine if a valid touch has started
				if (Math.abs(y - startTouchY) > touchSlop) {
					touchState = TOUCH_STARTED;
				}
				if (velocityTracker != null) {
					velocityTracker.addMovement(event);
				}

				// scroll as appropriate
				if (touchState == TOUCH_STARTED) {
					final int scrollDelta = (int)(startTouchY - y);
					scrollTo(0, Math.max(0,  Math.min(viewHeight, startTouchScrollY + scrollDelta)));
				}
				break;

			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_POINTER_UP:
			case MotionEvent.ACTION_UP:
				if (touchState == TOUCH_STARTED) {
					final int currentScrollY = getScrollY();

					// get velocity
					float velocity = 0;
					if (velocityTracker != null) {
						velocityTracker.computeCurrentVelocity(1000);
						velocity = VelocityTrackerCompat.getYVelocity(velocityTracker, pointerId);
						velocityTracker.recycle();
						velocityTracker = null;
					}

					// snap to final scroll position
					int target = startTouchScrollY;
					if ((Math.abs(velocity) > minimumflingVelocity) || (Math.abs(y - startTouchY) > (viewHeight / 2))) {
						if (velocity < 0) {
							target = viewHeight;
						} else {
							target = 0;
						}
					}
					scroller.startScroll(0, currentScrollY, 0, target - currentScrollY);
					invalidate();
					touchState = TOUCH_NONE;
				}
				break;
		}
		return true;
	}


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		View view1 = getChildAt(0);
		if (view1 != null) {
			view1.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.AT_MOST));
		}

		View view2 = getChildAt(1);
		if (view2 != null) {
			view2.measure(widthMeasureSpec, heightMeasureSpec);
		}

		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
	}


	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int width = getMeasuredWidth();
		viewHeight = getMeasuredHeight();

		View view1 = getChildAt(0);
		if (view1 != null) {
			collapsedHeight = view1.getMeasuredHeight();
			view1.layout(0, viewHeight - collapsedHeight, width, viewHeight);
		}

		View view2 = getChildAt(1);
		if (view2 != null) {
			view2.layout(0, viewHeight, width, viewHeight * 2);
		}
	}
}
