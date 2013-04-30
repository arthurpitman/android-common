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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


/**
 * A collection of web related utilities.
 */
public class WebUtils {
	/**
	 * Name of the UTF-8 character set.
	 */
	public static final String UTF8_CHARACTER_SET = "UTF-8";

	private static final String GET_METHOD = "GET";
	private static final String POST_METHOD = "POST";
	private static final String CONTENT_TYPE_FIELD = "Content-Type";
	private static final String FORM_MIME_TYPE = "application/x-www-form-urlencoded";
	private static final int BUFFER_SIZE = 4096;

	// implement retries to try and prevent HttpURLConnections failing for silly reasons
	// see: http://code.google.com/p/android/issues/detail?id=41576
	private static final int CONNECTION_RETRIES = 5;
	private static final int CONNECTION_RETRY_SLEEP = 200;


	/**
	 * Calls a JSON web service.
	 * @param url the url of the call.
	 * @param formData optional form data, <code>null</code> if not required.
	 * @return a {@link JSONObject} representing the response.
	 * @throws IOException
	 * @throws JSONException
	 */
	public static JSONObject callJsonWebService(final String url, final String formData) throws IOException, JSONException {
		return (JSONObject) new JSONTokener(retrieveUrlAsString(url, formData)).nextValue();
	}


	/**
	 * Retrieves a URL as a string.
	 * @param url
	 * @param formData optional form data, <code>null</code> if not required.
	 * @return a {@link String} representing the response.
	 * @throws IOException
	 */
	public static String retrieveUrlAsString(final String url, final String formData) throws IOException {
		for (int i = 0;; i++) {
			HttpURLConnection connection = null;
			try {
				if (formData != null) {
					connection = connectPost(url, formData);
				} else {
					connection = connectGet(url);
				}
				return readStreamIntoString(connection.getInputStream(), UTF8_CHARACTER_SET,  BUFFER_SIZE);
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
	 * @param url the url to connect to.
	 * @param outputFile local file to write to.
	 * @throws IOException
	 */
	public static void downloadFile(final String url, final File outputFile) throws IOException {
		for (int i = 0;; i++) {
			HttpURLConnection connection = null;
			try {
				connection = connectGet(url);
				readStreamIntoFile(connection.getInputStream(),outputFile, BUFFER_SIZE );
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
	 * @param url the url to connect to.
	 * @param formData the form data to post.
	 * @returnthe resulting {@link HttpURLConnection}.
	 * @throws IOException
	 */
	public static HttpURLConnection connectPost(final String url, final String formData) throws IOException {
		// setup connection
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setDoOutput(true);
		connection.setUseCaches(false);
		connection.setRequestMethod(POST_METHOD);

		// send the post form
		byte[] formDataBytes = formData.getBytes();
		connection.setFixedLengthStreamingMode(formDataBytes.length);
		connection.setRequestProperty(CONTENT_TYPE_FIELD, FORM_MIME_TYPE);
		OutputStream outStream = connection.getOutputStream();
		outStream.write(formDataBytes);
		outStream.close();

		return connection;
	}


	/**
	 * Connects to an HTTP resource using the get method.
	 * @param url the url to connect to.
	 * @return the resulting {@link HttpURLConnection}.
	 * @throws IOException
	 */
	public static HttpURLConnection connectGet(final String url) throws IOException {
		// setup connection
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setUseCaches(false);
		connection.setRequestMethod(GET_METHOD);

		return connection;
	}


	/**
	 * Reads a {@link InputStream} into a {@link String}.
	 * @param inputStream the InputStream to read.
	 * @param characterSet the character set to use, such as "UTF-8".
	 * @param bufferSize the size of the buffer in bytes.
	 * @return the resulting String.
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	public static String readStreamIntoString(final InputStream inputStream, final String characterSet, final int bufferSize) throws IOException, UnsupportedEncodingException {
		final char[] buffer = new char[bufferSize];
		final StringBuilder stringBuilder = new StringBuilder();
		final Reader reader = new InputStreamReader(inputStream, characterSet);
		try {
			while (true) {
				int charactersRead = reader.read(buffer, 0, bufferSize);
				if (charactersRead < 0)
					break;
				stringBuilder.append(buffer, 0, charactersRead);
			}
		} finally {
			reader.close();
		}
		return stringBuilder.toString();
	}


	/**
	 * Reads an {@link InputStream} into a {@link File}, useful for saving files from the web.
	 * @param inputStream the InputStream to read.
	 * @param outputFile the File in which to save the contents of the stream.
	 * @param bufferSize the size of the buffer in bytes.
	 * @throws IOException
	 */
	public static void readStreamIntoFile(final InputStream inputStream, final File outputFile, final int bufferSize) throws IOException {
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(outputFile);
			byte[] buffer = new byte[bufferSize];
			int bytesRead = 0;

			// transfer complete stream
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
		} finally {
			if (outputStream != null) {
				outputStream.close();
			}
			inputStream.close();
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