package cc.abase.demo.widget.dialog

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.fragment.app.FragmentManager
import cc.ab.base.ext.*
import cc.ab.base.ui.dialog.BaseFragmentDialog
import cc.abase.demo.R
import com.blankj.utilcode.util.ScreenUtils
import kotlinx.android.synthetic.main.dialog_comm.*

/**
 * Author:CASE
 * Date:2020-10-13
 * Time:09:51
 */
class CommAlertDialog : BaseFragmentDialog() {
  //<editor-fold defaultstate="collapsed" desc="变量">
  //提示框的类型
  var type = AlertDialogType.DOUBLE_BUTTON
  var title: String? = null
  var content: CharSequence? = null

  //默认 取消
  var cancelText: String? = null
  var cancelTextColor: Int? = null

  //默认 确定
  var confirmText: String? = null
  var confirmTextColor: Int? = null
  var cancelCallback: (() -> Unit)? = null
  var confirmCallback: (() -> Boolean)? = null
  var contentHorizontal: Boolean = false
  var titleHorizontal: Boolean = true

  //加粗效果
  var boldContent: Boolean? = null
  var boldCancel: Boolean? = null
  var boldConfirm: Boolean? = null
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun contentLayout() = R.layout.dialog_comm
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  override fun initView(view: View, savedInstanceState: Bundle?) {
    //按压效果
    commAlertCancel?.pressEffectAlpha()
    commAlertConfirm?.pressEffectAlpha()
    //标题
    commAlertTitle?.text = title
    commAlertTitle?.visibleGone(!title.isNullOrBlank())
    if (!titleHorizontal) commAlertTitle?.gravity = Gravity.START
    //内容
    commAlertContent?.text = content
    if (contentHorizontal) commAlertContent?.gravity = Gravity.CENTER_HORIZONTAL
    else commAlertContent?.post { if (commAlertContent?.lineCount ?: 0 == 1) commAlertContent?.gravity = Gravity.CENTER_HORIZONTAL }
    if (boldContent == true) commAlertContent?.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
    commContentLl?.visibleGone(!content.isNullOrBlank())
    //取消
    cancelText?.let { commAlertCancel.text = it }
    cancelTextColor?.let { commAlertCancel?.setTextColor(it) }
    if (boldCancel == true) commAlertCancel?.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
    //确定
    confirmText?.let { commAlertConfirm?.text = it }
    confirmTextColor?.let { commAlertConfirm?.setTextColor(it) }
    if (boldConfirm == true) commAlertConfirm?.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
    //点击事件
    commAlertCancel?.click {
      cancelCallback?.invoke()
      dismissAllowingStateLoss()
    }
    commAlertConfirm.click {
      val dis = confirmCallback?.invoke() ?: true
      if (dis) dismissAllowingStateLoss()
    }
    //按钮类型
    when (type) {
      AlertDialogType.SINGLE_BUTTON -> {
        commAlertConfirm?.visible()
        commAlertContent?.visible()
      }
      AlertDialogType.DOUBLE_BUTTON -> {
        commAlertCancel.visible()
        commBtnLine.visible()
        commAlertContent.visible()
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="外部调用弹窗">
  //开始弹窗
  fun show(fragmentManager: FragmentManager) {
    show(fragmentManager, "CommAlertDialog")
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="外部调用实例">
  companion object {
    fun newInstance(outside: Boolean = true) = CommAlertDialog().apply { touchOutside = outside }
  }
  //</editor-fold>
}

//<editor-fold defaultstate="collapsed" desc="按钮单双类型">
//按钮单双类型
enum class AlertDialogType {
  SINGLE_BUTTON,
  DOUBLE_BUTTON,
}
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="DSL调用">
//  DSL style
inline fun commAlertDialog(
    fragmentManager: FragmentManager,
    cancelable: Boolean = true,
    outside: Boolean = true,
    dsl: CommAlertDialog .() -> Unit
): CommAlertDialog {
  val dialog = CommAlertDialog.newInstance().apply(dsl)
  dialog.mGravity = Gravity.CENTER
  dialog.mWidth = (0.77f * ScreenUtils.getScreenWidth()).toInt()
  dialog.touchOutside = outside
  dialog.isCancelable = cancelable
  dialog.show(fragmentManager)
  return dialog
}
//</editor-fold>