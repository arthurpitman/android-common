package com.arthurpitman.common;

import java.nio.ByteBuffer;

public class ByteArrayUtils {

	/**
	 * Converts a byte array to a double array.
	 * @param bytes
	 * @return
	 */
	public static double[] convertByteArrayToDoubleArray(byte[] bytes) {
		if (bytes == null)
			return null;

		int count = bytes.length / 8;
		double[] doubleArray = new double[count];
		ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
		for (int i = 0; i < count; i++) {
			doubleArray[i] = byteBuffer.getDouble();
		}
		return doubleArray;
	}


	/**
	 * Converts a double array to a byte array.
	 * @param doubleArray
	 * @return
	 */
	public static byte[] convertDoubleArrayToByteArray(double[] doubleArray) {
		if (doubleArray == null)
			return null;
		byte[] bytes = new byte[doubleArray.length * 8];
		ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
		for (double v : doubleArray) {
			byteBuffer.putDouble(v);
		}
		return bytes;
	}


	/**
	 * Converts a byte array to a long array.
	 * @param bytes
	 * @return
	 */
	public static long[] convertByteArrayToLongArray(byte[] bytes) {
		if (bytes == null)
			return null;

		int count = bytes.length / 8;
		long[] longArray = new long[count];
		ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
		for (int i = 0; i < count; i++) {
			longArray[i] = byteBuffer.getLong();
		}
		return longArray;
	}


	/**
	 * Converts a long array to a byte array.
	 * @param longArray
	 * @return
	 */
	public static byte[] convertLongArrayToByteArray(long[] longArray) {
		if (longArray == null)
			return null;
		byte[] bytes = new byte[longArray.length * 8];
		ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
		for (long v : longArray) {
			byteBuffer.putLong(v);
		}
		return bytes;
	}


	/**
	 * Converts a byte array to an int array.
	 * @param bytes
	 * @return
	 */
	public static int[] convertByteArrayToIntArray(byte[] bytes) {
		if (bytes == null)
			return null;

		int count = bytes.length / 4;
		int[] intArray = new int[count];
		ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
		for (int i = 0; i < count; i++) {
			intArray[i] = byteBuffer.getInt();
		}
		return intArray;
	}


	/**
	 * Converts an int array to a byte array.
	 * @param intArray
	 * @return
	 */
	public static byte[] convertIntArrayToByteArray(int[] intArray) {
		if (intArray == null)
			return null;
		byte[] bytes = new byte[intArray.length * 4];
		ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
		for (int v : intArray) {
			byteBuffer.putInt(v);
		}
		return bytes;
	}

}