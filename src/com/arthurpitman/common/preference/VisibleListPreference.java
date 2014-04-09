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

package com.arthurpitman.common.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.ListPreference;
import android.util.AttributeSet;

import com.arthurpitman.common.R;


/**
 * A {@link ListPreference} that displays the current selection in its summary.
 */
public class VisibleListPreference extends ListPreference {

	private boolean autoActivate;


	/**
	 * Creates a new {@code VisibleListPreference}.
	 * @param context
	 */
	public VisibleListPreference(Context context) {
		super(context);
	}


	/**
	 * Creates a new {@code VisibleListPreference}.
	 * @param context
	 * @param attrs
	 */
	public VisibleListPreference(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.VisibleListPreference);
		autoActivate = array.getBoolean(R.styleable.VisibleListPreference_autoActivate, true);
		array.recycle();
	}


	/**
	 * Gets the auto activate flag.
	 * @return
	 */
	public boolean getAutoActivate() {
		return autoActivate;
	}


	/**
	 * Sets the auto activate flag.
	 * @param autoActivate
	 */
	public void setAutoActivate(boolean autoActivate) {
		this.autoActivate = autoActivate;
	}


	/**
	 * Manually activates the preference.
	 */
	public void activate() {
		super.onClick();
	}


	@Override
	protected void onClick() {
		if (autoActivate) {
			activate();
		}
	}


	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		setSummary(getEntry());
	}


	@Override
	public CharSequence getSummary() {
		return super.getEntry();
	}
}