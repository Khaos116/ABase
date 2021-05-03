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
  //<editor-fold defaultstate="collapsed" desc="测试使用">
  //校验地址：http://murmurhash.shorelabs.com/
  @SuppressLint("LogNotTimber")
  public static void simpleTest() {
    String key = "android";
    int speed = 24;
    //x86_32    ->    3115586689
    //x86_128   ->    c3bf0c3d77fa6a950a0eba700a0eba70
    //x64_128   ->    5d248e0847c1f036c0baf2505e490eb3
    //参考地址：https://github.com/tamtam180/MurmurHash-For-Java/blob/e36bee9f4e58d535d398d6055c13963060c28e75/src/main/java/at/orz/hash/MurmurHash3.java
    //x86_32
    int x86_32Temp = MurmurHash3.digest32_x86(key.getBytes(), speed, false);
    long x86_32 = new BigInteger(Integer.toHexString(x86_32Temp), 16).longValue();
    //x86_128
    int[] x86_128Array = MurmurHash3.digest128_x86(key.getBytes(), speed, false);
    StringBuilder x86_128Sb = new StringBuilder();
    for (int value : x86_128Array) x86_128Sb.append(String.format("%08x", value));//https://www.cnblogs.com/wlv1314/p/12188545.html
    String x86_128 = x86_128Sb.toString();
    //x64_128
    long[] x64_128Array = MurmurHash3.digest128_x64(key.getBytes(), speed, false);
    StringBuilder x64_128Sb = new StringBuilder();
    for (long value : x64_128Array) x64_128Sb.append(String.format("%016x", value));//https://www.cnblogs.com/wlv1314/p/12188545.html
    String x64_128 = x64_128Sb.toString();
    //打印结果
    Log.e("CASE", "\n============================================================================================\n");
    Log.e("CASE", "MurMur3_X86_32位计算结果:" + x86_32);
    Log.e("CASE", "MurMur3_X86_128位计算结果:" + x86_128);
    Log.e("CASE", "MurMur3_X64_128位计算结果:" + x64_128);
    Log.e("CASE", "\n============================================================================================\n");
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="正常使用方法">
  //计算Hash3_x86_32
  public static String MurmurHash3_x86_32(String key, int speed) {
    int x86_32Temp = MurmurHash3.digest32_x86(key.getBytes(), speed, false);
    long x86_32 = new BigInteger(Integer.toHexString(x86_32Temp), 16).longValue();//https://www.cnblogs.com/wlv1314/p/12188545.html
    return String.valueOf(x86_32);
  }

  //计算Hash3_x86_128
  public static String MurmurHash3_x86_128(String key, int speed) {
    int[] x86_128Array = MurmurHash3.digest128_x86(key.getBytes(), speed, false);
    StringBuilder x86_128Sb = new StringBuilder();
    for (int value : x86_128Array) x86_128Sb.append(String.format("%08x", value));//https://www.cnblogs.com/wlv1314/p/12188545.html
    return x86_128Sb.toString();
  }

  //计算Hash3_x64_128
  public static String MurmurHash3_x64_128(String key, int speed) {
    long[] x64_128Array = MurmurHash3.digest128_x64(key.getBytes(), speed, false);
    StringBuilder x64_128Sb = new StringBuilder();
    for (long value : x64_128Array) x64_128Sb.append(String.format("%016x", value));//https://www.cnblogs.com/wlv1314/p/12188545.html
    return x64_128Sb.toString();
  }
  //</editor-fold>
}
