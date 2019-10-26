package cc.abase.demo.repository.parse;

import cc.abase.demo.repository.bean.gank.GankResponse;
import com.google.gson.Gson;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * Description: https://www.jianshu.com/p/936a76198b57
 *
 * @author: caiyoufei
 * @date: 2019/10/26 19:28
 */
public class GankParameterizedTypeImpl implements ParameterizedType {
  private final Class raw;
  private final Type[] args;

  public GankParameterizedTypeImpl(Class raw, Type[] args) {
    this.raw = raw;
    this.args = args != null ? args : new Type[0];
  }

  public static <T> GankResponse<T> fromJsonObject(String reader, Class<T> clazz) throws Exception {
    Type type = new GankParameterizedTypeImpl(GankResponse.class, new Class[] { clazz });
    return new Gson().fromJson(reader, type);
  }

  public static <T> GankResponse<List<T>> fromJsonArray(String reader, Class<T> clazz)
      throws Exception {
    // 生成List<T> 中的 List<T>
    Type listType = new GankParameterizedTypeImpl(List.class, new Class[] { clazz });
    // 根据List<T>生成完整的Response<List<T>>
    Type type = new GankParameterizedTypeImpl(GankResponse.class, new Type[] { listType });
    return new Gson().fromJson(reader, type);
  }

  @NotNull @Override
  public Type[] getActualTypeArguments() {
    return args;
  }

  @NotNull @Override
  public Type getRawType() {
    return raw;
  }

  @Override
  public Type getOwnerType() {
    return null;
  }
}