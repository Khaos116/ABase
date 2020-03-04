package cc.abase.demo.fuel.repository.parse;

import cc.ab.base.net.http.response.BaseResponse;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * Description:https://www.jianshu.com/p/936a76198b57
 *
 * @author: caiyoufei
 * @date: 2019/10/26 19:44
 */
public class BaseParameterizedTypeImpl implements ParameterizedType {
  private final Class raw;
  private final Type[] args;

  private BaseParameterizedTypeImpl(Class raw, Type[] args) {
    this.raw = raw;
    this.args = args != null ? args : new Type[0];
  }

  public static Type typeOne(Class<?> clazz) {
    return new BaseParameterizedTypeImpl(BaseResponse.class, new Class[] { clazz });
  }

  public static Type typeList(Class<?> clazz) {
    // 生成List<T> 中的 List<T>
    Type listType = new BaseParameterizedTypeImpl(List.class, new Class[] { clazz });
    // 根据List<T>生成完整的Response<List<T>>
    return new BaseParameterizedTypeImpl(BaseResponse.class, new Type[] { listType });
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