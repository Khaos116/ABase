package cc.abase.demo.component.pinyin;

import com.blankj.utilcode.util.Utils;
import com.github.promeg.pinyinhelper.PinyinMapDict;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * https://github.com/promeG/TinyPinyin/blob/master/tinypinyin-android-asset-lexicons/src/main/java/com/github/promeg/tinypinyin/android/asset/lexicons/AndroidAssetDict.java
 * 从Asset中的文本文件构建词典的辅助类
 * <p>
 * 词典格式为：每行一个词和对应的拼音，词在前，拼音在后，:分隔，拼音间以空格分隔
 * 例： 将进酒: 'qiāng jìn jiǔ'
 */
public abstract class PinYinDict1 extends PinyinMapDict {

  /**
   * 返回Asset中存储词典信息的文本文档的路径，必须非空
   */
  protected abstract String assetFileName();

  final Map<String, String[]> mDict;

  public PinYinDict1() {
    mDict = new HashMap<>();
    init();
  }

  @Override
  public Map<String, String[]> mapping() {
    return mDict;
  }

  private void init() {
    BufferedReader reader = null;
    InputStream inputStream = null;
    try {
      String name = assetFileName();
      // 打开文件并创建 BufferedReader 对象以逐行读取文件
      inputStream = Utils.getApp().getAssets().open(name);
      reader = new BufferedReader(new InputStreamReader(inputStream));
      // 读取文件内容
      String line;
      while ((line = reader.readLine()) != null) {
        String[] keyAndValue = line.split(":");
        if (keyAndValue.length == 2) {
          String s1 = keyAndValue[0].trim();
          String s2 = keyAndValue[1].replace("'", "").trim();
          String[] pinyinStrs = s2.split("\\s+");
          if (pinyinStrs.length > s1.length()) {
            String[] pinyinStrs2 = new String[s1.length()];
            System.arraycopy(pinyinStrs, 0, pinyinStrs2, 0, s1.length());
            mDict.put(s1, pinyinStrs2);
          } else {
            mDict.put(s1, pinyinStrs);
          }
        }
      }
      // 关闭 BufferedReader 和 InputStream
      reader.close();
      inputStream.close();
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
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }
}