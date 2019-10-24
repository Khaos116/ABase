package cc.abase.demo.widget.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.fragment.app.FragmentManager
import cc.ab.base.ui.dialog.BaseFragmentDialog
import cc.abase.demo.R
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.Utils
import kotlinx.android.synthetic.main.dialog_action.*

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/9/24 13:03
 */
class ActionDialog : BaseFragmentDialog() {
  //默认提示语
  var hintText: String = Utils.getApp().getString(R.string.action_loading)

  override fun contentLayout() = R.layout.dialog_action

  override fun initView(view: View, savedInstanceState: Bundle?) {
    dialogActionHint?.text = hintText
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
    dialogActionHint?.text = hintText
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