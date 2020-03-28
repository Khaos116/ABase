package cc.abase.demo.rxhttp.parser;

import com.blankj.utilcode.util.GsonUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import cc.ab.base.net.http.response.BaseResponse;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import rxhttp.wrapper.annotation.Parser;
import rxhttp.wrapper.entity.ParameterizedTypeImpl;
import rxhttp.wrapper.exception.ParseException;
import rxhttp.wrapper.parse.AbstractParser;

/**
 * Description:https://github.com/liujingxing/okhttp-RxHttp/blob/781a0dc440d3981956ca66a2b909655ceadf6471/app/src/main/java/com/example/httpsender/parser/java/ResponseParser.java
 *
 * @author: caiyoufei
 * @date: 2020/3/28 21:01
 */
//通过@Parser注解，为解析器取别名为Response，此时就会在RxHttp类生成asResponse(Class<T>)方法
@Parser(name = "ResponseWan")
public class ResponseWanParser<T> extends AbstractParser<T> {

    //注意，以下两个构造方法是必须的
    protected ResponseWanParser() {
        super();
    }

    public ResponseWanParser(Class<T> type) {
        super(type);
    }

    @Override
    public T onParse(okhttp3.Response response) throws IOException {
        final Type type = ParameterizedTypeImpl.get(BaseResponse.class, mType); //获取泛型类型
        String result = null;
        ResponseBody body = response.body();
        if (body != null) {
            BufferedSource source = body.source();
            if (source != null) {
                source.request(Long.MAX_VALUE);
                Buffer buffer = source.getBuffer();
                result = buffer.clone()
                        .readString(StandardCharsets.UTF_8);
            }
        }
        if (result == null || result.length() == 0) {
            throw new ParseException("500", "服务器没有数据", response);
        }
        BaseResponse<T> data = GsonUtils.fromJson(result, type);
        T t = data.getData(); //获取data字段
        if (t == null && mType == String.class) {
            /*
             * 考虑到有些时候服务端会返回：{"errorCode":0,"errorMsg":"关注成功"}  类似没有data的数据
             * 此时code正确，但是data字段为空，直接返回data的话，会报空指针错误，
             * 所以，判断泛型为String类型时，重新赋值，并确保赋值不为null
             */
            t = (T) data.getErrorMsg();
        }
        if (data.getErrorCode() != 0 || t == null) {//code不等于0，说明数据不正确，抛出异常
            throw new ParseException(String.valueOf(data.getErrorCode()), data.getErrorMsg(), response);
        }
        return t;
    }
}
