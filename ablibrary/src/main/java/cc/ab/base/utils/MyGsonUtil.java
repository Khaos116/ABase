package cc.ab.base.utils;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.text.DecimalFormat;

/**
 * 参考：优化参考 https://wenku.baidu.com/view/7bdaa4dbfa0f76c66137ee06eff9aef8941e4866.html
 * 主要解决调用.toJson()时Int变为Double问题
 *
 * @see rxhttp.wrapper.utils.GsonUtil
 * @see com.blankj.utilcode.util.GsonUtils
 * User: ljx
 * Date: 2018/01/19
 * Time: 10:46
 */
public class MyGsonUtil {

  private static Gson gson;

  /**
   * json字符串转对象，解析失败，不会抛出异常，会直接返回null
   *
   * @param json json字符串
   * @param type 对象类类型
   * @param <T>  返回类型
   * @return T，返回对象有可能为空
   */
  @Nullable
  public static <T> T getObject(String json, Type type) {
    try {
      return fromJson(json, type);
    } catch (Exception ignore) {
      return null;
    }
  }

  /**
   * json字符串转对象，解析失败，将抛出对应的{@link JsonSyntaxException}异常，根据异常可查找原因
   *
   * @param json json字符串
   * @param type 对象类类型
   * @param <T>  返回类型
   * @return T，返回对象不为空
   */
  @NonNull
  public static <T> T fromJson(String json, Type type) {
    Gson gson = buildGson();
    return gson.fromJson(json, type);
  }

  public static String toJson(Object object) {
    return buildGson().toJson(object);
  }

  public static Gson buildGson() {
    if (gson == null) {
      gson = newGson();
    }
    return gson;
  }

  //安卓9.0以下在Bean中防止View等对象会造成解析异常闪退，需要自定义ExclusionStrategy进行过滤(测试时使用Expose进行过滤会导致整个解析没有内容，所以改为自定义)
  public static Gson newGson() {
    return new GsonBuilder().disableHtmlEscaping()
        .registerTypeAdapter(String.class, new StringAdapter())
        .registerTypeAdapter(int.class, new IntegerDefault0Adapter())
        .registerTypeAdapter(Integer.class, new IntegerDefault0Adapter())
        .registerTypeAdapter(double.class, new DoubleDefault0Adapter())
        .registerTypeAdapter(Double.class, new DoubleDefault0Adapter())
        .registerTypeAdapter(long.class, new LongDefault0Adapter())
        .registerTypeAdapter(Long.class, new LongDefault0Adapter())
        .setExclusionStrategies(new ExclusionStrategy() {
          @Override
          public boolean shouldSkipField(FieldAttributes f) {
            //return f.getName().contains("_");
            return false;//处理不需要序列化和反序列化的字段
          }

          @Override
          public boolean shouldSkipClass(Class<?> clazz) {
            return clazz == View.class || clazz == DecimalFormat.class;//处理不需要序列化和反序列化的字段类型
          }
        })
        .create();
  }

  private static class StringAdapter implements JsonSerializer<String>, JsonDeserializer<String> {
    @Override
    public String deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
      if (json instanceof JsonPrimitive) {
        return json.getAsString();
      } else {
        return json.toString();
      }
    }

    @Override
    public JsonElement serialize(String src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(src);
    }
  }

  private static class IntegerDefault0Adapter implements JsonSerializer<Integer>, JsonDeserializer<Integer> {
    @Override
    public Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
      try {
        String str = json.getAsString();
        if ("".equals(str) || "null".equals(str)) {//定义为int类型,如果后台返回""或者null,则返回0
          return 0;
        }
      } catch (Exception ignore) {
      }
      return json.getAsInt();
    }

    @Override
    public JsonElement serialize(Integer src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(src);
    }
  }

  private static class DoubleDefault0Adapter implements JsonSerializer<Double>, JsonDeserializer<Double> {
    @Override
    public Double deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      try {
        String str = json.getAsString();
        if ("".equals(str) || "null".equals(str)) {//定义为double类型,如果后台返回""或者null,则返回0.00
          return 0.00;
        }
      } catch (Exception ignore) {
      }
      return json.getAsDouble();
    }

    @Override
    public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
      //<editor-fold defaultstate="collapsed" desc="Khaos116修改的代码">
      if (src == src.longValue()) {
        return new JsonPrimitive(src.longValue());
      }
      //</editor-fold>
      return new JsonPrimitive(src);
    }
  }

  private static class LongDefault0Adapter implements JsonSerializer<Long>, JsonDeserializer<Long> {
    @Override
    public Long deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
      try {
        String str = json.getAsString();
        if ("".equals(str) || "null".equals(str)) { //定义为long类型,如果后台返回""或者null,则返回0
          return 0L;
        }
      } catch (Exception ignore) {
      }
      return json.getAsLong();
    }

    @Override
    public JsonElement serialize(Long src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(src);
    }
  }
}