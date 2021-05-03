package cc.abase.demo.utils.hash;

import android.annotation.SuppressLint;
import android.util.Log;

import java.math.BigInteger;

/**
 * @Description MurMur3 加密工具 https://github.com/tamtam180/MurmurHash-For-Java
 * @Author：CASE
 * @Date：2021-05-03
 * @Time：14:17
 */
public class MurMur3Utils {
  //不足的补齐
  private static final String fillTxt = "0000000000000000";

  //校验地址：http://murmurhash.shorelabs.com/
  @SuppressLint("LogNotTimber")
  public static void simpleTest() {
    String key = "case";
    int speed = 24;
    //x86_32    ->    3359281890
    //x86_128   ->    b5c544bd54d27c3254d27c3254d27c32
    //x64_128   ->    c1f90cad4751109f2fee1674aaf8fceb
    //参考地址：https://github.com/tamtam180/MurmurHash-For-Java/blob/e36bee9f4e58d535d398d6055c13963060c28e75/src/main/java/at/orz/hash/MurmurHash3.java
    //x86_32
    int x86_32Temp = MurmurHash3.digest32_x86(key.getBytes(), speed, false);
    long x86_32 = new BigInteger(Integer.toHexString(x86_32Temp), 16).longValue();
    //x86_128
    int[] x86_128Temp = MurmurHash3.digest128_x86(key.getBytes(), speed, false);
    long[] x86_128Array = new long[x86_128Temp.length];
    for (int i = 0; i < x86_128Temp.length; i++) {
      x86_128Array[i] = x86_128Temp[i];
    }
    String x86_128 = fixLength(x86_128Array, 8);
    //x64_128
    long[] x64_128Array = MurmurHash3.digest128_x64(key.getBytes(), speed, false);
    String x64_128 = fixLength(x64_128Array, 16);
    //打印结果
    Log.e("CASE", "\n============================================================================================\n");
    Log.e("CASE", "MurMur3_X86_32位计算结果:" + x86_32);
    Log.e("CASE", "MurMur3_X86_128位计算结果:" + x86_128);
    Log.e("CASE", "MurMur3_X64_128位计算结果:" + x64_128);
    Log.e("CASE", "\n============================================================================================\n");
  }

  //不足用0补齐，太多保留低8位
  private static String fixLength(long[] array, int len) {
    StringBuilder sb = new StringBuilder();
    for (long i : array) {
      String r = Long.toHexString(i);
      if (r.length() < len) {
        sb.append(fillTxt.substring(0, len - r.length()));
        sb.append(r);
      } else if (r.length() > len) {
        sb.append(r.substring(r.length() - len));
      } else {
        sb.append(r);
      }
    }
    return sb.toString();
  }
}
