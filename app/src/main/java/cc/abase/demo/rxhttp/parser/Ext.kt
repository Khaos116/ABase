package cc.abase.demo.rxhttp.parser

import okhttp3.*
import okio.Buffer
import java.nio.charset.Charset

/**
 * 是否可以解析
 * @return `true` 可以解析
 */
fun MediaType.isParsable(): Boolean {
    return (isText() || isPlain() || isJson() || isForm() || isHtml() || isXml())
}

fun MediaType.isText(): Boolean {
    return subtype.lowercase().contains("text")
}

fun MediaType.isPlain(): Boolean {
    return subtype.lowercase().contains("plain")
}

fun MediaType.isJson(): Boolean {
    return subtype.lowercase().contains("json")
}

fun MediaType.isXml(): Boolean {
    return subtype.lowercase().contains("xml")
}

fun MediaType.isHtml(): Boolean {
    return subtype.lowercase().contains("html")
}

fun MediaType.isForm(): Boolean {
    return subtype.lowercase().contains("x-www-form-urlencoded")
}

fun RequestBody.bodyString(): String {
    val charset: Charset = contentType()?.charset(Charsets.UTF_8) ?: Charsets.UTF_8
    val buffer = Buffer()
    writeTo(buffer)
    return buffer.readString(charset)
}

fun ResponseBody.bodyString(): String {
    val charset: Charset = contentType()?.charset(Charsets.UTF_8) ?: Charsets.UTF_8
    val buffer = source().buffer.clone()
    return buffer.readString(charset)
}

fun String.isGuessJson(): Boolean {
    val trim = trim()
    return (trim.startsWith("{") && trim.endsWith("}")) ||
            ((trim.startsWith("[") && trim.endsWith("]")))
}