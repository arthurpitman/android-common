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

package com.arthurpitman.common.preference;

import android.content.Context;
import android.preference.ListPreference;
import android.preference.Preference;
import android.util.AttributeSet;

/**
 * A {@link ListPreference} that displays the current selection in its summary.
 */
public class VisibleListPreference extends ListPreference {

	/**
	 * Creates a new {@code VisibleListPreference}.
	 * @param context
	 */
	public VisibleListPreference(Context context) {
		super(context);
		initializePreference();
	}


	/**
	 * Creates a new {@code VisibleListPreference}.
	 * @param context
	 * @param attrs
	 */
	public VisibleListPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		initializePreference();
	}


	/**
	 * Performs shared initialization.
	 */
	private void initializePreference() {
		setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				preference.setSummary(getEntry());
				return true;
			}
		});
	}


	@Override
	public CharSequence getSummary() {
		return super.getEntry();
	}
}