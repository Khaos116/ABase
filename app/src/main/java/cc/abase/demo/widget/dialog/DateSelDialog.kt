package cc.abase.demo.widget.dialog

import android.view.*
import androidx.fragment.app.FragmentManager
import cc.ab.base.ext.click
import cc.ab.base.ui.dialog.BaseBindFragmentDialog
import cc.abase.demo.R
import cc.abase.demo.databinding.DialogDateSelBinding
import com.blankj.utilcode.util.TimeUtils
import java.util.Date

/**
 * Description:
 * @author: Khaos
 * @date: 2020/2/20 18:43
 */
class DateSelDialog : BaseBindFragmentDialog<DialogDateSelBinding>() {
  //选择的结果
  private var result: Triple<Int, Int, Int>? = null

  //回调
  var call: ((result: Triple<Int, Int, Int>) -> Unit)? = null

  //默认选中的日期
  var mDefaultDate = "1997-7-1"

  override fun initView() {
    viewBinding.dialogDateView.let {
      it.listener = { array -> result = Triple(array[0], array[1], array[2]) }
      it.setDate("1900-1-1", TimeUtils.date2String(Date(), "yyyy-MM-dd"), mDefaultDate)
    }
    viewBinding.dialogDateCancel.click { dismissAllowingStateLoss() }
    viewBinding.dialogDateConfirm.click {
      dismissAllowingStateLoss()
      result?.let { r -> call?.invoke(r) }
    }
  }

  companion object {
    fun newInstance(touchCancel: Boolean = true): DateSelDialog {
      val dialog = DateSelDialog()
      dialog.mGravity = Gravity.BOTTOM
      dialog.canTouchOutside = touchCancel
      dialog.mAnimation = R.style.BottomDialogAnimation
      dialog.mWidth = ViewGroup.LayoutParams.MATCH_PARENT
      return dialog
    }
  }

  fun show(fragmentManager: FragmentManager) {
    show(fragmentManager, "DateSelDialog")
  }
}

//DSL style
inline fun dateSelDialog(fragmentManager: FragmentManager, defaultDate: String? = null, dsl: DateSelDialog.() -> Unit) {
  val dialog = DateSelDialog.newInstance().apply(dsl)
  if (defaultDate != null) dialog.mDefaultDate = defaultDate
  dialog.show(fragmentManager)
}