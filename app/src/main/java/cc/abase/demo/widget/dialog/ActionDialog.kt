package cc.abase.demo.widget.dialog

import android.view.Gravity
import android.view.LayoutInflater
import androidx.fragment.app.FragmentManager
import cc.ab.base.ui.dialog.BaseBindFragmentDialog
import cc.abase.demo.R
import cc.abase.demo.databinding.DialogActionBinding
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.StringUtils

/**
 * Description:
 * @author: CASE
 * @date: 2019/9/24 13:03
 */
class ActionDialog : BaseBindFragmentDialog<DialogActionBinding>() {
  //默认提示语
  var hintText: String = StringUtils.getString(R.string.action_loading)

  override fun loadViewBinding(inflater: LayoutInflater) = DialogActionBinding.inflate(inflater)

  override fun initView() {
    viewBinding.dialogActionHint.text = hintText
  }

  companion object {
    fun newInstance(
        touchCancel: Boolean = true
    ): ActionDialog {
      val dialog = ActionDialog()
      dialog.mGravity = Gravity.CENTER
      dialog.touchOutside = touchCancel
      dialog.mWidth = ScreenUtils.getScreenWidth() / 3
      return dialog
    }
  }

  fun show(fragmentManager: FragmentManager) {
    show(fragmentManager, "ActionDialog")
  }
}

//  DSL style
inline fun actionDialog(
    fragmentManager: FragmentManager,
    dsl: ActionDialog.() -> Unit
) {
  val dialog = ActionDialog.newInstance()
      .apply(dsl)
  dialog.show(fragmentManager)
}