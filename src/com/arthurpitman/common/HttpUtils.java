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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


/**
 * A collection of HTTP related utilities.
 */
public class HttpUtils {
	/**
	 * Name of the UTF-8 character set.
	 */
	public static final String UTF8_CHARACTER_SET = "UTF-8";

	private static final String GET_METHOD = "GET";
	private static final String POST_METHOD = "POST";
	private static final int BUFFER_SIZE = 4096;

	// implement retries to try and prevent HttpURLConnections failing for silly reasons
	// see: http://code.google.com/p/android/issues/detail?id=41576
	private static final int CONNECTION_RETRIES = 5;
	private static final int CONNECTION_RETRY_SLEEP = 200;


	/**
	 * Calls a URL as JSON.
	 * @param url
	 * @param requestProperties optional request properties, <code>null</code> if not required.
	 * @param post optional post data, <code>null</code> if not required.
	 * @return a {@link JSONObject} representing the response.
	 * @throws IOException
	 * @throws JSONException
	 */
	public static JSONObject retrieveUrlAsJson(final String url, final RequestProperty[] requestProperties, final String post) throws IOException, JSONException {
		return (JSONObject) new JSONTokener(retrieveUrlAsString(url, requestProperties, post.getBytes())).nextValue();
	}


	/**
	 * Retrieves a URL as a string.
	 * @param url
	 * @param requestProperties optional request properties, <code>null</code> if not required.
	 * @param postData optional post data, <code>null</code> if not required.
	 * @return a {@link String} representing the response.
	 * @throws IOException
	 */
	public static String retrieveUrlAsString(final String url, final RequestProperty[] requestProperties, final byte[] postData) throws IOException {
		for (int i = 0;; i++) {
			HttpURLConnection connection = null;
			try {
				if (postData != null) {
					connection = connectPost(url, requestProperties, postData);
				} else {
					connection = connectGet(url, requestProperties);
				}
				return StreamUtils.readStreamIntoString(connection.getInputStream(), UTF8_CHARACTER_SET, BUFFER_SIZE);
			}  catch (IOException e) {
				if (i == CONNECTION_RETRIES) {
					throw e;
				}
			} finally {
				// always close the connection
				if (connection != null) {
					connection.disconnect();
				}
			}

			// allow recovery time
			try {
				Thread.sleep(CONNECTION_RETRY_SLEEP);
			} catch (InterruptedException e) {
			}
		}
	}


	/**
	 * Downloads a file from a URL and saves it to the local file system.
	 * @param url the URL to connect to.
	 * @param requestProperties optional request properties, <code>null</code> if not required.
	 * @param outputFile local file to write to.
	 * @throws IOException
	 */
	public static void downloadFile(final String url, final RequestProperty[] requestProperties, final File outputFile) throws IOException {
		for (int i = 0;; i++) {
			HttpURLConnection connection = null;
			try {
				connection = connectGet(url, requestProperties);
				StreamUtils.readStreamIntoFile(connection.getInputStream(), outputFile, BUFFER_SIZE, true);
				return;
			} catch (IOException e) {
				if (i == CONNECTION_RETRIES) {
					throw e;
				}
			} finally {
				// always close the connection
				if (connection != null) {
					connection.disconnect();
				}
			}

			// allow recovery time
			try {
				Thread.sleep(CONNECTION_RETRY_SLEEP);
			} catch (InterruptedException e) {
			}
		}
	}


	/**
	 * Connects to an HTTP resource using the post method.
	 * @param url the URL to connect to.
	 * @param requestProperties optional request properties, <code>null</code> if not required.
	 * @param postData the data to post.
	 * @return the resulting {@link HttpURLConnection}.
	 * @throws IOException
	 */
	public static HttpURLConnection connectPost(final String url, final RequestProperty[] requestProperties, final byte[] postData) throws IOException {
		// setup connection
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setDoOutput(true);
		connection.setUseCaches(false);
		connection.setRequestMethod(POST_METHOD);

		// send the post form
		connection.setFixedLengthStreamingMode(postData.length);
		addRequestProperties(connection, requestProperties);
		OutputStream outStream = connection.getOutputStream();
		outStream.write(postData);
		outStream.close();

		return connection;
	}


	/**
	 * Connects to an HTTP resource using the get method.
	 * @param url the URL to connect to.
	 * @param requestProperties optional request properties, <code>null</code> if not required.
	 * @return the resulting {@link HttpURLConnection}.
	 * @throws IOException
	 */
	public static HttpURLConnection connectGet(final String url, final RequestProperty[] requestProperties) throws IOException {
		// setup connection
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setUseCaches(false);
		connection.setRequestMethod(GET_METHOD);
		addRequestProperties(connection, requestProperties);
		return connection;
	}


	/**
	 * Adds request properties to an HttpURLConnection.
	 * @param connection
	 * @param requestProperties
	 */
	private static void addRequestProperties(final HttpURLConnection connection, final RequestProperty[] requestProperties) {
		if (requestProperties != null) {
			for(RequestProperty requestProperty : requestProperties) {
				connection.addRequestProperty(requestProperty.getKey(), requestProperty.getValue());
			}
		}
	}


	/**
	 * Encodes a string as UTF-8.
	 * @param s the {@link String} to encode.
	 * @return the encoded string.
	 * @throws UnsupportedEncodingException
	 */
	public static String encodeUtf8(String s) throws UnsupportedEncodingException {
		return URLEncoder.encode(s, UTF8_CHARACTER_SET);
	}
}