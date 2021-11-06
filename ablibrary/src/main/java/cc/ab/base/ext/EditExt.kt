package cc.ab.base.ext

import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.EditText
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

/**
 * Description: 防抖动搜索
 * @author: Khaos
 * @date: 2019/10/10 22:35
 */
fun EditText.onDebounceTextChanges(life: Lifecycle, time: Long = 600, onStart: Boolean = false, afterChange: (String) -> Unit) {
  //防止搜索一样的内容
  var lastSearchStr: String? = null
  val call: Flow<CharSequence?> = callbackFlow {
    val listener = object : TextWatcher {
      override fun afterTextChanged(s: Editable?) = Unit
      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        trySend(s)
      }
    }
    addTextChangedListener(listener)
    awaitClose { removeTextChangedListener(listener) }
  }
  if (onStart) call.onStart { emit(text) } //添加监听时发送控件的文本信息(否则只有变化时才会回调)
  call.debounce(time).onEach {
    val result = it?.toString() ?: ""
    if (lastSearchStr != result) {
      lastSearchStr = result
      "搜索内容:$result".logI()
      afterChange.invoke(result)
    }
  }.launchIn(life.coroutineScope)
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