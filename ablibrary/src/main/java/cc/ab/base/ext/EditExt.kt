package cc.ab.base.ext

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

/**
 * Description: 防抖动搜索
 * @author: Khaos
 * @date: 2019/10/10 22:35
 */
@FlowPreview
@ExperimentalCoroutinesApi
fun EditText.onDebounceTextChanges(lifecycle: Lifecycle, debounceTime: Long = 600, afterChange: (String) -> Unit) {
  callbackFlow {
    val listener = object : TextWatcher {
      override fun afterTextChanged(s: Editable?) = Unit
      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        trySend(s)
      }
    }
    addTextChangedListener(listener)
    awaitClose { removeTextChangedListener(listener) }
  }.onStart { emit(text) }
    .distinctUntilChanged()
    .filterNot { it.isNullOrBlank() }
    .debounce(debounceTime)
    .mapLatest { afterChange.invoke(it?.toString() ?: "") }
    .launchIn(lifecycle.coroutineScope)
}