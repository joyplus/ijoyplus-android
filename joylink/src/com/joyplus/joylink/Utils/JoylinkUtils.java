/*
 * Copyright (C) 2010 The Android Open Source Project
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

package com.joyplus.joylink.Utils;

import java.io.Closeable;
import java.io.File;
import java.io.InterruptedIOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.os.Build;
import android.os.Environment;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;

import com.androidquery.util.AQUtility;

public class JoylinkUtils {
	private static final String TAG = "Utils";
	private static final String DEBUG_TAG = "Debug";

	private static final long POLY64REV = 0x95AC9329AC4BC9B5L;
	private static final long INITIALCRC = 0xFFFFFFFFFFFFFFFFL;

	private static long[] sCrcTable = new long[256];

	private static final boolean IS_DEBUG_BUILD = Build.TYPE.equals("eng")
			|| Build.TYPE.equals("userdebug");

	private static final String MASK_STRING = "********************************";

	// Throws AssertionError if the input is false.
	public static void assertTrue(boolean cond) {
		if (!cond) {
			throw new AssertionError();
		}
	}

	// Throws AssertionError if the input is false.
	public static void assertTrue(boolean cond, String message, Object... args) {
		if (!cond) {
			throw new AssertionError(args.length == 0 ? message
					: String.format(message, args));
		}
	}

	// Throws NullPointerException if the input is null.
	public static <T> T checkNotNull(T object) {
		if (object == null)
			throw new NullPointerException();
		return object;
	}

	// Returns true if two input Object are both null or equal
	// to each other.
	public static boolean equals(Object a, Object b) {
		return (a == b) || (a == null ? false : a.equals(b));
	}

	// Returns true if the input is power of 2.
	// Throws IllegalArgumentException if the input is <= 0.
	public static boolean isPowerOf2(int n) {
		if (n <= 0)
			throw new IllegalArgumentException();
		return (n & -n) == n;
	}

	// Returns the next power of two.
	// Returns the input if it is already power of 2.
	// Throws IllegalArgumentException if the input is <= 0 or
	// the answer overflows.
	public static int nextPowerOf2(int n) {
		if (n <= 0 || n > (1 << 30))
			throw new IllegalArgumentException();
		n -= 1;
		n |= n >> 16;
		n |= n >> 8;
		n |= n >> 4;
		n |= n >> 2;
		n |= n >> 1;
		return n + 1;
	}

	// Returns the previous power of two.
	// Returns the input if it is already power of 2.
	// Throws IllegalArgumentException if the input is <= 0
	public static int prevPowerOf2(int n) {
		if (n <= 0)
			throw new IllegalArgumentException();
		return Integer.highestOneBit(n);
	}

	// Returns the euclidean distance between (x, y) and (sx, sy).
	public static float distance(float x, float y, float sx, float sy) {
		float dx = x - sx;
		float dy = y - sy;
		return (float) Math.hypot(dx, dy);
	}

	// Returns the input value x clamped to the range [min, max].
	public static int clamp(int x, int min, int max) {
		if (x > max)
			return max;
		if (x < min)
			return min;
		return x;
	}

	// Returns the input value x clamped to the range [min, max].
	public static float clamp(float x, float min, float max) {
		if (x > max)
			return max;
		if (x < min)
			return min;
		return x;
	}

	// Returns the input value x clamped to the range [min, max].
	public static long clamp(long x, long min, long max) {
		if (x > max)
			return max;
		if (x < min)
			return min;
		return x;
	}

	public static boolean isOpaque(int color) {
		return color >>> 24 == 0xFF;
	}

	public static <T> void swap(T[] array, int i, int j) {
		T temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}

	public static void swap(int[] array, int i, int j) {
		int temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}

	/**
	 * A function thats returns a 64-bit crc for string
	 * 
	 * @param in
	 *            input string
	 * @return a 64-bit crc value
	 */
	public static final long crc64Long(String in) {
		if (in == null || in.length() == 0) {
			return 0;
		}
		return crc64Long(getBytes(in));
	}

	static {
		// http://bioinf.cs.ucl.ac.uk/downloads/crc64/crc64.c
		long part;
		for (int i = 0; i < 256; i++) {
			part = i;
			for (int j = 0; j < 8; j++) {
				long x = ((int) part & 1) != 0 ? POLY64REV : 0;
				part = (part >> 1) ^ x;
			}
			sCrcTable[i] = part;
		}
	}

	public static final long crc64Long(byte[] buffer) {
		long crc = INITIALCRC;
		for (int k = 0, n = buffer.length; k < n; ++k) {
			crc = sCrcTable[(((int) crc) ^ buffer[k]) & 0xff] ^ (crc >> 8);
		}
		return crc;
	}

	public static byte[] getBytes(String in) {
		byte[] result = new byte[in.length() * 2];
		int output = 0;
		for (char ch : in.toCharArray()) {
			result[output++] = (byte) (ch & 0xFF);
			result[output++] = (byte) (ch >> 8);
		}
		return result;
	}

	public static void closeSilently(Closeable c) {
		if (c == null)
			return;
		try {
			c.close();
		} catch (Throwable t) {
			Log.w(TAG, "close fail", t);
		}
	}

	public static int compare(long a, long b) {
		return a < b ? -1 : a == b ? 0 : 1;
	}

	public static int ceilLog2(float value) {
		int i;
		for (i = 0; i < 31; i++) {
			if ((1 << i) >= value)
				break;
		}
		return i;
	}

	public static int floorLog2(float value) {
		int i;
		for (i = 0; i < 31; i++) {
			if ((1 << i) > value)
				break;
		}
		return i - 1;
	}

	public static void closeSilently(ParcelFileDescriptor fd) {
		try {
			if (fd != null)
				fd.close();
		} catch (Throwable t) {
			Log.w(TAG, "fail to close", t);
		}
	}

	public static void closeSilently(Cursor cursor) {
		try {
			if (cursor != null)
				cursor.close();
		} catch (Throwable t) {
			Log.w(TAG, "fail to close", t);
		}
	}

	public static float interpolateAngle(float source, float target,
			float progress) {
		// interpolate the angle from source to target
		// We make the difference in the range of [-179, 180], this is the
		// shortest path to change source to target.
		float diff = target - source;
		if (diff < 0)
			diff += 360f;
		if (diff > 180)
			diff -= 360f;

		float result = source + diff * progress;
		return result < 0 ? result + 360f : result;
	}

	public static float interpolateScale(float source, float target,
			float progress) {
		return source + progress * (target - source);
	}

	public static String ensureNotNull(String value) {
		return value == null ? "" : value;
	}

	// Used for debugging. Should be removed before submitting.
	public static void debug(String format, Object... args) {
		if (args.length == 0) {
			Log.d(DEBUG_TAG, format);
		} else {
			Log.d(DEBUG_TAG, String.format(format, args));
		}
	}

	public static float parseFloatSafely(String content, float defaultValue) {
		if (content == null)
			return defaultValue;
		try {
			return Float.parseFloat(content);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public static int parseIntSafely(String content, int defaultValue) {
		if (content == null)
			return defaultValue;
		try {
			return Integer.parseInt(content);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public static boolean isNullOrEmpty(String exifMake) {
		return TextUtils.isEmpty(exifMake);
	}

	public static boolean hasSpaceForSize(long size) {
		String state = Environment.getExternalStorageState();
		if (!Environment.MEDIA_MOUNTED.equals(state)) {
			return false;
		}

		String path = Environment.getExternalStorageDirectory().getPath();
		try {
			StatFs stat = new StatFs(path);
			return stat.getAvailableBlocks() * (long) stat.getBlockSize() > size;
		} catch (Exception e) {
			Log.i(TAG, "Fail to access external storage", e);
		}
		return false;
	}

	public static void waitWithoutInterrupt(Object object) {
		try {
			object.wait();
		} catch (InterruptedException e) {
			Log.w(TAG, "unexpected interrupt: " + object);
		}
	}

	public static void shuffle(int array[], Random random) {
		for (int i = array.length; i > 0; --i) {
			int t = random.nextInt(i);
			if (t == i - 1)
				continue;
			int tmp = array[i - 1];
			array[i - 1] = array[t];
			array[t] = tmp;
		}
	}

	public static boolean handleInterrruptedException(Throwable e) {
		// A helper to deal with the interrupt exception
		// If an interrupt detected, we will setup the bit again.
		if (e instanceof InterruptedIOException
				|| e instanceof InterruptedException) {
			Thread.currentThread().interrupt();
			return true;
		}
		return false;
	}

	/**
	 * @return String with special XML characters escaped.
	 */
	public static String escapeXml(String s) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0, len = s.length(); i < len; ++i) {
			char c = s.charAt(i);
			switch (c) {
			case '<':
				sb.append("&lt;");
				break;
			case '>':
				sb.append("&gt;");
				break;
			case '\"':
				sb.append("&quot;");
				break;
			case '\'':
				sb.append("&#039;");
				break;
			case '&':
				sb.append("&amp;");
				break;
			default:
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static String getUserAgent(Context context) {
		PackageInfo packageInfo;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			throw new IllegalStateException("getPackageInfo failed");
		}
		return String.format("%s/%s; %s/%s/%s/%s; %s/%s/%s",
				packageInfo.packageName, packageInfo.versionName, Build.BRAND,
				Build.DEVICE, Build.MODEL, Build.ID, Build.VERSION.SDK,
				Build.VERSION.RELEASE, Build.VERSION.INCREMENTAL);
	}

	public static String[] copyOf(String[] source, int newSize) {
		String[] result = new String[newSize];
		newSize = Math.min(source.length, newSize);
		System.arraycopy(source, 0, result, 0, newSize);
		return result;
	}

	public static PendingIntent deserializePendingIntent(byte[] rawPendingIntent) {
		Parcel parcel = null;
		try {
			if (rawPendingIntent != null) {
				parcel = Parcel.obtain();
				parcel.unmarshall(rawPendingIntent, 0, rawPendingIntent.length);
				return PendingIntent.readPendingIntentOrNullFromParcel(parcel);
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("error parsing PendingIntent");
		} finally {
			if (parcel != null)
				parcel.recycle();
		}
	}

	public static byte[] serializePendingIntent(PendingIntent pendingIntent) {
		Parcel parcel = null;
		try {
			parcel = Parcel.obtain();
			PendingIntent.writePendingIntentOrNullToParcel(pendingIntent,
					parcel);
			return parcel.marshall();
		} finally {
			if (parcel != null)
				parcel.recycle();
		}
	}

	// Mask information for debugging only. It returns
	// <code>info.toString()</code> directly
	// for debugging build (i.e., 'eng' and 'userdebug') and returns a mask
	// ("****")
	// in release build to protect the information (e.g. for privacy issue).
	public static String maskDebugInfo(Object info) {
		if (info == null)
			return null;
		String s = info.toString();
		int length = Math.min(s.length(), MASK_STRING.length());
		return IS_DEBUG_BUILD ? s : MASK_STRING.substring(0, length);
	}

	// Returns a (localized) string for the given duration (in seconds).
	public static String formatDuration(int duration) {
		duration = duration / 1000;
		int h = duration / 3600;
		int m = (duration - h * 3600) / 60;
		int s = duration - (h * 3600 + m * 60);
		String durationValue;
		if (h == 0) {
			durationValue = String.format("%1$02d:%2$02d", m, s);
		} else {
			durationValue = String.format("%1$d:%2$02d:%3$02d", h, m, s);
		}
		return durationValue;
	}

	public static String getCacheFileName(String url) {

		String hash = getMD5Hex(url);
		return hash;
	}

	private static String getMD5Hex(String str) {
		byte[] data = getMD5(str.getBytes());

		BigInteger bi = new BigInteger(data).abs();

		String result = bi.toString(36);
		return result;
	}

	private static byte[] getMD5(byte[] data) {

		MessageDigest digest;
		try {
			digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(data);
			byte[] hash = digest.digest();
			return hash;
		} catch (NoSuchAlgorithmException e) {
			AQUtility.report(e);
		}

		return null;

	}

	public static File getCacheFile(File dir, String url) {
		if (url == null)
			return null;
		// if(url.startsWith(File.separator)){
		// return new File(url);
		// }

		String name = getCacheFileName(url);
		File file = makeCacheFile(dir, name);
		return file;
	}

	private static File makeCacheFile(File dir, String name) {

		File result = new File(dir, name);
		return result;
	}

	public interface Defs {
		public final static int OPEN_URL = 0;
		public final static int ADD_TO_PLAYLIST = 1;
		public final static int USE_AS_RINGTONE = 2;
		public final static int PLAYLIST_SELECTED = 3;
		public final static int NEW_PLAYLIST = 4;
		public final static int PLAY_SELECTION = 5;
		public final static int GOTO_START = 6;
		public final static int GOTO_PLAYBACK = 7;
		public final static int PARTY_SHUFFLE = 8;
		public final static int SHUFFLE_ALL = 9;
		public final static int DELETE_ITEM = 10;
		public final static int SCAN_DONE = 11;
		public final static int QUEUE = 12;
		public final static int EFFECTS_PANEL = 13;
		public final static int CHILD_MENU_BASE = 14; // this should be the last
														// item
	}

}
