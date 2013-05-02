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
 * Represents a key value pair for web requests.
 */
public class RequestProperty {	
	/** User agent key. */
	public static final String USER_AGENT = "User-Agent";

	/** Content type key. */
	public static final String CONTENT_TYPE = "Content-Type";

	/** Content type for URL encoded forms. */
	public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";

	/** Content type for JSON. */
	public static final String CONTENT_TYPE_JSON = "application/json";

	private final String key;
	private final String value;


	/**
	 * Creates a new RequestProperty with the specified key and value.
	 * @param key
	 * @param value
	 */
	public RequestProperty(String key, String value) {
		this.key = key;
		this.value = value;
	}


	/**
	 * Gets the key of the request property.
	 * @return
	 */
	public String getKey() {
		return key;
	}


	/**
	 * Gets the value of the request property.
	 * @return
	 */
	public String getValue() {
		return value;
	}
}