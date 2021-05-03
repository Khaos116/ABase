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

/**
 * MurmurHash3を求めるクラス。
 * 32ビットか128ビットのハッシュを求める。
 * オリジナル版はEndian依存のため、このクラスではEndianを指定できるようにしてある。
 * x86とx64のメソッドは異なる値を返す。
 *
 * @author tamtam180 - kirscheless at gmail.com
 * @see https://sites.google.com/site/murmurhash/
 * @see http://code.google.com/p/smhasher/
 *
 */
public class MurmurHash3 {

	/**
	 * 指定したデータから32ビットのハッシュを求める。
	 * @param data データ
	 * @param seed シード値
	 * @param bigendian ビッグエンディアンの場合はtrue
	 * @return 32ビットハッシュ値
	 */
	public static int digest32_x86(byte[] data, int seed, boolean bigendian) {

		final int len = data.length;
		final int block_remain = len % 4;
		final int block_size = len - block_remain;

		int h1 = seed;
		int c1 = 0xcc9e2d51;
		int c2 = 0x1b873593;

		int i = 0;
		for (i = 0; i < block_size; i += 4) {
			int k1 = bigendian ? EncodeUtils.toIntBE(data, i) : EncodeUtils.toIntLE(data, i);

			k1 *= c1;
			k1 = Integer.rotateLeft(k1, 15);
			k1 *= c2;

			h1 ^= k1;
			h1 = Integer.rotateLeft(h1, 13);
			h1 = h1 * 5 + 0xe6546b64;
		}

		int k1 = 0;
		switch (block_remain) {
		case 3: k1 ^= data[i+2] << 16;
		case 2: k1 ^= data[i+1] << 8;
		case 1: k1 ^= data[i+0];
			k1 *= c1;
			k1 = Integer.rotateLeft(k1, 15);
			k1 *= c2;
			h1 ^=k1;
		}

		h1 ^= len;
		h1 = fmix(h1);

		return h1;
	}

	/**
	 * 指定したデータから128ビットのハッシュを求める。
	 * 戻り値は長さ4のint配列。
	 *
	 * @param data データ
	 * @param seed シード値
	 * @param bigendian ビッグエンディアンの場合はtrue
	 * @return 128ビットのハッシュ値(長さ4のint配列)
	 */
	public static int[] digest128_x86(byte[] data, int seed, boolean bigendian) {

		final int c1 = 0x239b961b;
		final int c2 = 0xab0e9789;
		final int c3 = 0x38b34ae5;
		final int c4 = 0xa1e38b93;

		final int len = data.length;
		final int block_remain = len % 16;
		final int block_size = len - block_remain;

		int h1 = seed;
		int h2 = seed;
		int h3 = seed;
		int h4 = seed;

		int i = 0;
		for (i = 0; i < block_size; i += 16) {
			int k1 = bigendian ? EncodeUtils.toIntBE(data, i+0)  : EncodeUtils.toIntLE(data, i+0);
			int k2 = bigendian ? EncodeUtils.toIntBE(data, i+4)  : EncodeUtils.toIntLE(data, i+4);
			int k3 = bigendian ? EncodeUtils.toIntBE(data, i+8)  : EncodeUtils.toIntLE(data, i+8);
			int k4 = bigendian ? EncodeUtils.toIntBE(data, i+12) : EncodeUtils.toIntLE(data, i+12);

			k1 = updateK(k1, c1, 15, c2);
			h1 = updateH(h1, k1, 19, h2, 0x561ccd1b);

			k2 = updateK(k2, c2, 16, c3);
			h2 = updateH(h2, k2, 17, h3, 0x0bcaa747);

			k3 = updateK(k3, c3, 17, c4);
			h3 = updateH(h3, k3, 15, h4, 0x96cd1c35);

			k4 = updateK(k4, c4, 18, c1);
			h4 = updateH(h4, k4, 13, h1, 0x32ac3b17);
		}

		int k1 = 0;
		int k2 = 0;
		int k3 = 0;
		int k4 = 0;

		switch (block_remain) {
		case 15: k4 ^= data[i+14] << 16;
		case 14: k4 ^= data[i+13] << 8;
		case 13: k4 ^= data[i+12] << 0;
			k4 = updateK(k4, c4, 18, c1);
			h4 ^= k4;
		case 12: k3 ^= data[i+11] << 24;
		case 11: k3 ^= data[i+10] << 16;
		case 10: k3 ^= data[i+9] << 8;
		case 9: k3 ^= data[i+8] << 0;
			k3 = updateK(k3, c3, 17, c4);
			h3 ^= k3;
		case 8: k2 ^= data[i+7] << 24;
		case 7: k2 ^= data[i+6] << 16;
		case 6: k2 ^= data[i+5] << 8;
		case 5: k2 ^= data[i+4] << 0;
			k2 = updateK(k2, c2, 16, c3);
			h2 ^= k2;
		case 4: k1 ^= data[i+3] << 24;
		case 3: k1 ^= data[i+2] << 16;
		case 2: k1 ^= data[i+1] << 8;
		case 1: k1 ^= data[i+0] << 0;
			k1 = updateK(k1, c1, 15, c2);
			h1 ^= k1;
		}

		// finalization
		h1 ^= len;
		h2 ^= len;
		h3 ^= len;
		h4 ^= len;

		h1 += h2; h1 += h3; h1 += h4;
		h2 += h1; h3 += h1; h4 += h1;

		h1 = fmix(h1);
		h2 = fmix(h2);
		h3 = fmix(h3);
		h4 = fmix(h4);

		h1 += h2; h1 += h3; h1 += h4;
		h2 += h1; h3 += h1; h4 += h1;

		return new int[]{ h1, h2, h3, h4 };

	}

	/**
	 * 指定したデータから128ビットのハッシュを求める。
	 * 戻り値は長さ2のlong配列。
	 *
	 * @param data データ
	 * @param seed シード値
	 * @param bigendian ビッグエンディアンの場合はtrue
	 * @return 128ビットのハッシュ値(長さ2のlong配列)
	 */
	public static long[] digest128_x64(byte[] data, int seed, boolean bigendian) {

		final long c1 = 0x87c37b91114253d5L;
		final long c2 = 0x4cf5ad432745937fL;

		final int len = data.length;
		final int block_remain = len % 16;
		final int block_size = len - block_remain;

		long h1 = seed;
		long h2 = seed;

		// body
		int i = 0;
		for (i = 0; i < block_size; i += 16) {
			long k1 = bigendian ? EncodeUtils.toLongBE(data, i + 0) : EncodeUtils.toLongLE(data, i + 0);
			long k2 = bigendian ? EncodeUtils.toLongBE(data, i + 8) : EncodeUtils.toLongLE(data, i + 8);

			k1 = updateK(k1, c1, 31, c2);
			h1 = updateH(h1, k1, 27, h2, 0x52dce729L);

			k2 = updateK(k2, c2, 33, c1);
			h2 = updateH(h2, k2, 31, h1, 0x38495ab5L);
		}

		// tail
		long k1 = 0;
		long k2 = 0;

		switch (block_remain) {
		case 15: k2 ^= (long)data[i+14] << 48;
		case 14: k2 ^= (long)data[i+13] << 40;
		case 13: k2 ^= (long)data[i+12] << 32;
		case 12: k2 ^= (long)data[i+11] << 24;
		case 11: k2 ^=       data[i+10] << 16;
		case 10: k2 ^=       data[i+9] << 8;
		case 9:  k2 ^=       data[i+8] << 0;
			k2 = updateK(k2, c2, 33, c1);
			h2 ^= k2;
		case 8: k1 ^= (long)data[i+7] << 56;
		case 7: k1 ^= (long)data[i+6] << 48;
		case 6: k1 ^= (long)data[i+5] << 40;
		case 5: k1 ^= (long)data[i+4] << 32;
		case 4: k1 ^= (long)data[i+3] << 24;
		case 3: k1 ^=       data[i+2] << 16;
		case 2: k1 ^=       data[i+1] << 8;
		case 1: k1 ^=       data[i+0] << 0;
			k1 = updateK(k1, c1, 31, c2);
			h1 ^= k1;
		}

		// finalization
		h1 ^= len;
		h2 ^= len;

		h1 += h2;
		h2 += h1;

		h1 = fmix(h1);
		h2 = fmix(h2);

		h1 += h2;
		h2 += h1;

		return new long[]{ h1, h2 };

	}

	private static int fmix(int h) {
		h ^= h >>> 16;
		h *= 0x85ebca6b;
		h ^= h >>> 13;
		h *= 0xc2b2ae35;
		h ^= h >>> 16;
		return h;
	}

	private static long fmix(long k) {
		k ^= k >>> 33;
		k *= 0xff51afd7ed558ccdL;
		k ^= k >>> 33;
		k *= 0xc4ceb9fe1a85ec53L;
		k ^= k >>> 33;
		return k;
	}


	private static int updateK(int k, int cx1, int rnum, int cx2) {
		k *= cx1;
		k = Integer.rotateLeft(k, rnum);
		k *= cx2;
		return k;
	}

	private static int updateH(int h, int kx, int rnum, int hx, int cc) {
		h ^= kx;
		h = Integer.rotateLeft(h, rnum);
		h += hx;
		h = h * 5 + cc;
		return h;
	}

	private static long updateK(long k, long cx1, int rnum, long cx2) {
		k *= cx1;
		k = Long.rotateLeft(k, rnum);
		k *= cx2;
		return k;
	}

	private static long updateH(long h, long kx, int rnum, long hx, long cc) {
		h ^= kx;
		h = Long.rotateLeft(h, rnum);
		h += hx;
		h = h * 5 + cc;
		return h;
	}

}
