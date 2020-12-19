package cc.ab.base.ext

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

/**
 * Description:
 * @author: CASE
 * @date: 2019/10/10 22:35
 */
fun EditText.addTextWatcher(after: (s: Editable?) -> Unit) {
  this.addTextChangedListener(object : TextWatcher {
    override fun afterTextChanged(s: Editable?) {
      after.invoke(s)
    }

    override fun beforeTextChanged(
      s: CharSequence?,
      start: Int,
      count: Int,
      after: Int
    ) {
    }

    override fun onTextChanged(
      s: CharSequence?,
      start: Int,
      before: Int,
      count: Int
    ) {
    }

  })
}