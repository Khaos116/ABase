package cc.ab.base.utils;

/**
 * Description:https://blog.csdn.net/haiyoushui123456/article/details/84338494
 *
 * @author: CASE
 * @date: 2020/3/28 17:44
 */
public class PaiLieZuHeUtils {
  private PaiLieZuHeUtils() {
  }

  public static PaiLieZuHeUtils getInstance() {
    return SingleTonHolder.INSTANCE;
  }

  //计算C(n,k)的排列组合值
  public long nchoosek(int n, int k) throws Throwable {
    if (n > 70 || (n == 70 && k > 25 && k < 45)) {
      throw new IllegalArgumentException(
          "N(" + n + ") and k(" + k + ") don't meet the requirements.");
    }
    checknk(n, k);
    k = Math.min(k, (n - k));
    if (k <= 1) {  // C(n, 0) = 1, C(n, 1) = n
      return k == 0 ? 1 : n;
    }
    int cacheLen = k + 1;
    long[] befores = new long[cacheLen];
    befores[0] = 1;
    long[] afters = new long[cacheLen];
    afters[0] = 1;
    for (int i = 1; i <= n; i++) {
      for (int j = 1; j <= k; j++) {
        afters[j] = befores[j - 1] + befores[j];
      }
      System.arraycopy(afters, 1, befores, 1, k);
    }
    return befores[k];
  }

  //检查是否越界
  private void checknk(int n, int k) {
    if (k < 0 || k > n) { // N must be a positive integer.
      throw new IllegalArgumentException("K must be an integer between 0 and N.");
    }
  }

  private static class SingleTonHolder {
    private static final PaiLieZuHeUtils INSTANCE = new PaiLieZuHeUtils();
  }
}
