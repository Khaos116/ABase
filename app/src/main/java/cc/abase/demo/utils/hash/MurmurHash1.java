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
 * MurmurHash1を求めるクラス。
 * 32ビットのハッシュ値を求める。
 * オリジナル版はEndian依存のため、このクラスではEndianを指定できるようにしてある。
 * 
 * @author tamtam180 - kirscheless at gmail.com
 * @see https://sites.google.com/site/murmurhash/
 * @see http://code.google.com/p/smhasher/
 */
public class MurmurHash1 {
	
	/**
	 * 指定したデータから32ビットのハッシュ値を求める。
	 * @param data データ
	 * @param seed シード値
	 * @param bigendian ビッグエンディアンの場合はtrue
	 * @return 32ビットのハッシュ値
	 */
	public static int digest32(byte[] data, int seed, boolean bigendian) {

		final int m = 0xc6a4a793;
		final int r = 16;
		final int len = data.length;

		int h = seed ^ (len * m);

		int block_remain = len % 4;
		int block_size = len - block_remain;

		int i = 0;
		for (i = 0; i < block_size; i += 4) {
			int k = bigendian ? EncodeUtils.toIntBE(data, i) : EncodeUtils.toIntLE(data, i);
			h += k;
			h *= m;
			h ^= h >>> 16;
		}

		switch (block_remain) {
		case 3:
			h += data[i + 2] << 16;
		case 2:
			h += data[i + 1] << 8;
		case 1:
			h += data[i];
			h *= m;
			h ^= h >>> r;
		}

		h *= m;
		h ^= h >>> 10;
		h *= m;
		h ^= h >>> 17;

		return h;
	}
	
}
