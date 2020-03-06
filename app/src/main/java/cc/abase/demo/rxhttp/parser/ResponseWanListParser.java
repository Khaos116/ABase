package cc.abase.demo.rxhttp.parser;

import cc.ab.base.net.http.response.BaseResponse;
import com.blankj.utilcode.util.GsonUtils;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import rxhttp.wrapper.annotation.Parser;
import rxhttp.wrapper.entity.ParameterizedTypeImpl;
import rxhttp.wrapper.exception.ParseException;
import rxhttp.wrapper.parse.AbstractParser;

/**
 * 改为kotlin代码会导致无法编译通过
 * Description:https://github.com/liujingxing/okhttp-RxHttp/blob/e7bb610782/app/src/main/java/com/example/httpsender/parser/ResponseListParser.java
 *
 * @author: caiyoufei
 * @date: 2020/3/5 20:52
 */
@Parser(name = "ResponseWanList")
public class ResponseWanListParser<T> extends AbstractParser<List<T>> {
  /**
   * 此构造方法适用于任意Class对象，但更多用于带泛型的Class对象，如：List<Student>
   * <p>
   * 用法:
   * Java: .asParser(new ResponseListParser<List<Student>>(){})
   * Kotlin: .asParser(object : ResponseListParser<List<Student>>() {})
   * <p>
   * 注：此构造方法一定要用protected关键字修饰，否则调用此构造方法将拿不到泛型类型
   */
  protected ResponseWanListParser() {
    super();
  }

  /**
   * 此构造方法仅适用于解析不带泛型的Class对象，如: Student.class
   * <p>
   * 用法
   * Java: .asParser(new ResponseListParser<>(Student.class))   或者  .asResponseList(Student.class)
   * Kotlin: .asParser(ResponseListParser(Student::class.java)) 或者  .asResponseList(Student::class.java)
   */
  public ResponseWanListParser(Class<T> type) {
    super(type);
  }

  @Override
  public List<T> onParse(okhttp3.Response response) throws IOException {
    final Type type = ParameterizedTypeImpl.get(BaseResponse.class, List.class, mType); //获取泛型类型
    String result = null;
    ResponseBody body = response.body();
    if (body != null) {
      BufferedSource source = body.source();
      source.request(Long.MAX_VALUE);
      result = source.getBuffer().clone()
          .readString(StandardCharsets.UTF_8);
    }
    if (result == null || result.isEmpty()) {
      throw new ParseException("500", "服务器没有数据", response);
    }
    BaseResponse<List<T>> data = GsonUtils.fromJson(result, type);
    List<T> list = data.getData(); //获取data字段
    if (data.getErrorCode() != 0 || list == null) {  //code不等于0，说明数据不正确，抛出异常
      throw new ParseException(String.valueOf(data.getErrorCode()), data.getErrorMsg(), response);
    }
    return list;
  }
}