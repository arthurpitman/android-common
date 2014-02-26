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

package com.arthurpitman.common.preference;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.Preference;
import android.text.format.Time;
import android.util.AttributeSet;


/**
 * A {@link Preference} that displays version information in its summary.
 */
public class VersionPreference extends Preference {

	/**
	 * Creates a new {@code VersionPreference}.
	 * @param context
	 */
	public VersionPreference(Context context) {
		super(context);
		setSummary(getVersionString(context));
	}


	/**
	 * Creates a new {@code VersionPreference}.
	 * @param context
	 * @param attrs
	 */
	public VersionPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setSummary(getVersionString(context));
	}


	/**
	 * Creates a new {@code VersionPreference}.
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public VersionPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setSummary(getVersionString(context));
	}


	/**
	 * Gets a version string for the current package.
	 * @param context
	 * @return
	 */
	private String getVersionString(Context context) {
		final PackageManager packageManager = context.getPackageManager();
		if (packageManager == null) {
			return "";
		}

		StringBuilder versionBuilder = new StringBuilder();
		ZipFile applicationZipFile = null;
		try {

			PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			ApplicationInfo applicationInfo = packageInfo.applicationInfo;

			// append name and version
			versionBuilder.append(applicationInfo.packageName);
			versionBuilder.append("\n");
			versionBuilder.append(packageInfo.versionName);

			// extract and append build time stamp
			applicationZipFile = new ZipFile(applicationInfo.sourceDir);
			ZipEntry ze = applicationZipFile.getEntry("classes.dex");
			Time t = new Time();
			t.set(ze.getTime());
			versionBuilder.append(" / ");
			versionBuilder.append(t.format2445());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (applicationZipFile != null) {
					applicationZipFile.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return versionBuilder.toString();
	}
}