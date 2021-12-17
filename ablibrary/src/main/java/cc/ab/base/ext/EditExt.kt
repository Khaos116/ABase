package cc.ab.base.ext

import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

/**
 * @OptIn 暂时是实验性的，有可能在后续版本会修改，需要做好兼容：https://kotlinlang.org/docs/opt-in-requirements.html
 * https://juejin.cn/post/6925304772383735822
 * Description: 防抖动搜索
 * @param life activity.lifecycle
 * @param time 抖动间隔时间，毫秒
 * @param onStart 是否添加马上回调(主要用于一进入页面就有搜索内容的情况)
 * @param afterChange 输入内容定期回调
 * @author: CASE
 * @date: 2019/10/10 22:35
 */
@OptIn(InternalCoroutinesApi::class, FlowPreview::class)
fun EditText.onDebounceTextChanges(life: Lifecycle, time: Long = 600, onStart: Boolean = false, afterChange: (String) -> Unit) {
    //防止搜索一样的内容
    var lastSearchStr = ""
    val etState = MutableStateFlow("")
    this.doAfterTextChanged { text -> etState.value = (text ?: "").toString() }// 往流里写数据
    if (onStart) {//添加监听时发送控件的文本信息(否则只有变化时才会回调)
        lastSearchStr = (text ?: "").toString()
        afterChange.invoke(lastSearchStr)
    }
    life.coroutineScope.launch {
        etState.debounce(time) // 限流，单位毫秒
            //.filter { it.isNotBlank() } // 空文本过滤掉
            .collect { s ->
                if (lastSearchStr != s) {
                    lastSearchStr = s
                    afterChange.invoke(s)
                }
            }
    }
}

fun EditText?.showPwd() {
    this?.transformationMethod = HideReturnsTransformationMethod.getInstance()
}

fun EditText?.hidePwd() {
    this?.transformationMethod = PasswordTransformationMethod.getInstance()
}

fun EditText?.isShowPwd(): Boolean {
    return this?.transformationMethod == HideReturnsTransformationMethod.getInstance()
}

fun EditText?.selectionEnd() {
    this?.setSelection(this.text.length)
}