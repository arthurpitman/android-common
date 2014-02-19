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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;


/**
 * A collection of stream related utilities.
 */
public class StreamUtils {

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
	 * <p>
	 * @param inputStream the InputStream to read.
	 * @param outputFile the File in which to save the contents of the stream.
	 * @param bufferSize the size of the buffer in bytes.
	 * @param closeInputStream true if the input stream should be closed after reading.
	 * @throws IOException
	 */
	public static void readStreamIntoFile(final InputStream inputStream, final File outputFile, final int bufferSize, final boolean closeInputStream) throws IOException {
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
			if (closeInputStream) {
				inputStream.close();
			}
		}
	}
}
