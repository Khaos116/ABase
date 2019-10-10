package cc.abase.demo.widget.dialog

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import cc.ab.base.ui.dialog.BaseFragmentDialog
import cc.abase.demo.R
import com.blankj.utilcode.util.ScreenUtils
import kotlinx.android.synthetic.main.dialog_action.view.dialogActionHint

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/9/24 13:03
 */
class ActionDialog : BaseFragmentDialog() {
  var hintText: String = "加载中..."
  private var textHint: TextView? = null
  override fun setView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val view = inflater.inflate(R.layout.dialog_action, container, false)
    initView(view)
    return view
  }

  private fun initView(view: View) {
    textHint = view.dialogActionHint
    textHint?.text = hintText
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
    textHint?.text = hintText
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