/*
 * Copyright (C) 2012 tamtam180
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.abase.demo.utils.hash;

import java.math.BigInteger;

/**
 * byte配列を整数に変換するユーティリティクラス。
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class EncodeUtils {
	
	/**
	 * byte配列のi番目から4バイト読み取り、BigEndianとみなした整数を返す。
	 * @param b データ
	 * @param i オフセット
	 * @return BigEndianとみなした整数
	 */
	public static int toIntBE(byte[] b, int i) {
		return (((b[i+0] & 255) << 24) + ((b[i+1] & 255) << 16) + ((b[i+2] & 255) << 8) + ((b[i+3] & 255) << 0));
	}

	/**
	 * byte配列のi番目から4バイト読み取り、LittleEndianとみなした整数を返す。
	 * @param b データ
	 * @param i オフセット
	 * @return LittleEndianとみなした整数
	 */
	public static int toIntLE(byte[] b, int i) {
		return (((b[i+3] & 255) << 24) + ((b[i+2] & 255) << 16) + ((b[i+1] & 255) << 8) + ((b[i+0] & 255) << 0));
	}
	
	/**
	 * byte配列のi番目から8バイト読み取り、BigEndianとみなした整数を返す。
	 * @param b データ
	 * @param i オフセット
	 * @return BigEndianとみなした整数
	 */
	public static long toLongBE(byte[] b, int i) {
		
		return (((long)b[i+0] << 56) +
				((long)(b[i+1] & 255) << 48) +
				((long)(b[i+2] & 255) << 40) +
				((long)(b[i+3] & 255) << 32) +
				((long)(b[i+4] & 255) << 24) +
				((b[i+5] & 255) << 16) +
				((b[i+6] & 255) <<  8) +
				((b[i+7] & 255) <<  0));
		
	}

	/**
	 * byte配列のi番目から8バイト読み取り、LittleEndianとみなした整数を返す。
	 * @param b データ
	 * @param i オフセット
	 * @return LittleEndianとみなした整数
	 */
	public static long toLongLE(byte[] b, int i) {
		
		return (((long)b[i+7] << 56) +
				((long)(b[i+6] & 255) << 48) +
				((long)(b[i+5] & 255) << 40) +
				((long)(b[i+4] & 255) << 32) +
				((long)(b[i+3] & 255) << 24) +
				((b[i+2] & 255) << 16) +
				((b[i+1] & 255) <<  8) +
				((b[i+0] & 255) <<  0));
		
	}
	
	/**
	 * 8バイト整数をビッグエンディアンのバイト配列に変換。
	 * @param v
	 * @return
	 */
	public static byte[] toBytesBE(long v) {
		return new byte[]{
				(byte) (v >>> 56),
				(byte) (v >>> 48),
				(byte) (v >>> 40),
				(byte) (v >>> 32),
				(byte) (v >>> 24),
				(byte) (v >>> 16),
				(byte) (v >>> 8),
				(byte) (v >>> 0),
		};
	}
	
	/**
	 * 4バイト整数をビッグエンディアンのバイト配列に変換。
	 * @param v
	 * @return
	 */
	public static byte[] toBytesBE(int v) {
		return new byte[] {
				(byte) ((v >>> 24) & 0xFF),
				(byte) ((v >>> 16) & 0xFF),
				(byte) ((v >>>  8) & 0xFF),
				(byte) ((v >>>  0) & 0xFF)
		};
	}
	
	/**
	 * unsignedな値に変換する。
	 * @param value signed-int値
	 * @return unsigned-int値
	 */
	public static long toUnsigned(int value) {
		return 0xffffffffL & value;
	}
	
	/**
	 * unsignedな値に変換する。
	 * @param value
	 * @return
	 */
	public static BigInteger toUnsigned(long value) {
		byte[] v = toBytesBE(value);
		byte[] vv = new byte[v.length+1];
		System.arraycopy(v, 0, vv, 1, v.length);
		return new BigInteger(vv);
	}

	/**
	 * unsignedな値に変換する。
	 * @param values
	 * @return
	 */
	public static BigInteger toUnsigned(int[] values) {
		
		byte[] buffer = new byte[values.length * 4+1];
		for (int i = 0; i < values.length; i++) {
			byte[] ival = toBytesBE(values[i]);
			System.arraycopy(ival, 0, buffer, i * 4 + 1, 4);
		}
		
		return new BigInteger(buffer);
		
	}
	
	/**
	 * unsignedな値に変換する。
	 * @param values
	 * @return
	 */
	public static BigInteger toUnsigned(long[] values) {
		
		byte[] buffer = new byte[values.length * 8 + 1];
		for (int i = 0; i < values.length; i++) {
			byte[] ival = toBytesBE(values[i]);
			System.arraycopy(ival, 0, buffer, i * 8 + 1, 8);
		}
		
		return new BigInteger(buffer);
		
	}
	
}
