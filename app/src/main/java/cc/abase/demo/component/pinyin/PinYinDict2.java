package cc.abase.demo.component.pinyin;

import com.blankj.utilcode.util.Utils;
import com.github.promeg.pinyinhelper.PinyinMapDict;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * https://github.com/promeG/TinyPinyin/blob/master/tinypinyin-android-asset-lexicons/src/main/java/com/github/promeg/tinypinyin/android/asset/lexicons/AndroidAssetDict.java
 * 从Asset中的文本文件构建词典的辅助类
 * <p>
 * 词典格式为：每行一个词和对应的拼音，词在前，拼音在后，=分隔，拼音间以,分隔
 * 例： 和稀泥=huo4,xi1,ni2
 */
public abstract class PinYinDict2 extends PinyinMapDict {

  /**
   * 返回Asset中存储词典信息的文本文档的路径，必须非空
   */
  protected abstract String assetFileName();

  final Map<String, String[]> mDict;

  public PinYinDict2() {
    mDict = new HashMap<>();
    init();
  }

  @Override
  public Map<String, String[]> mapping() {
    return mDict;
  }

  private void init() {
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new InputStreamReader(Utils.getApp().getAssets().open(assetFileName()), StandardCharsets.UTF_8));
      String line;
      while ((line = reader.readLine()) != null) {
        String[] keyAndValue = line.split("=");
        if (keyAndValue.length == 2) {
          String s1 = keyAndValue[0].trim();
          String s2 = keyAndValue[1].trim();
          String[] pinyinStrs = s2.split(",");
          for (int i = 0; i < pinyinStrs.length; i++) {//处理音调数字
            pinyinStrs[i] = pinyinStrs[i].substring(0, pinyinStrs[i].length() - 1);
          }
          if (pinyinStrs.length > s1.length()) {
            String[] pinyinStrs2 = new String[s1.length()];
            System.arraycopy(pinyinStrs, 0, pinyinStrs2, 0, s1.length());
            mDict.put(s1, pinyinStrs2);
          } else {
            mDict.put(s1, pinyinStrs);
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }
}