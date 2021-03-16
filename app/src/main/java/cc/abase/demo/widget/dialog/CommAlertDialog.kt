package cc.abase.demo.widget.dialog

import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import androidx.fragment.app.FragmentManager
import cc.ab.base.ext.*
import cc.ab.base.ui.dialog.BaseBindFragmentDialog
import cc.abase.demo.databinding.DialogCommBinding
import com.blankj.utilcode.util.ScreenUtils

/**
 * Author:CASE
 * Date:2020-10-13
 * Time:09:51
 */
class CommAlertDialog : BaseBindFragmentDialog<DialogCommBinding>() {
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
  var confirmCallback: (() -> Unit)? = null
  var contentHorizontal: Boolean = false
  var titleHorizontal: Boolean = true

  //加粗效果
  var boldContent: Boolean? = null
  var boldCancel: Boolean? = null
  var boldConfirm: Boolean? = null
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun loadViewBinding(inflater: LayoutInflater) = DialogCommBinding.inflate(inflater)
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  override fun initView() {
    //按压效果
    viewBinding.commAlertCancel?.pressEffectAlpha()
    viewBinding.commAlertConfirm?.pressEffectAlpha()
    //标题
    viewBinding.commAlertTitle?.text = title
    viewBinding.commAlertTitle?.visibleGone(!title.isNullOrBlank())
    if (!titleHorizontal) viewBinding.commAlertTitle?.gravity = Gravity.START
    //内容
    viewBinding.commAlertContent?.text = content
    if (contentHorizontal) viewBinding.commAlertContent?.gravity = Gravity.CENTER_HORIZONTAL
    else viewBinding.commAlertContent?.post {
      if (viewBinding.commAlertContent?.lineCount ?: 0 == 1) viewBinding.commAlertContent?.gravity = Gravity.CENTER_HORIZONTAL
    }
    if (boldContent == true) viewBinding.commAlertContent?.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
    viewBinding.commContentLl?.visibleGone(!content.isNullOrBlank())
    //取消
    cancelText?.let { viewBinding.commAlertCancel.text = it }
    cancelTextColor?.let { viewBinding.commAlertCancel?.setTextColor(it) }
    if (boldCancel == true) viewBinding.commAlertCancel?.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
    //确定
    confirmText?.let { viewBinding.commAlertConfirm?.text = it }
    confirmTextColor?.let { viewBinding.commAlertConfirm?.setTextColor(it) }
    if (boldConfirm == true) viewBinding.commAlertConfirm?.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
    //点击事件
    viewBinding.commAlertCancel?.click {
      cancelCallback?.invoke()
      dismissAllowingStateLoss()
    }
    viewBinding.commAlertConfirm.click {
      confirmCallback?.invoke()
      dismissAllowingStateLoss()
    }
    //按钮类型
    when (type) {
      AlertDialogType.SINGLE_BUTTON -> {
        viewBinding.commAlertConfirm?.visible()
        viewBinding.commAlertContent?.visible()
      }
      AlertDialogType.DOUBLE_BUTTON -> {
        viewBinding.commAlertCancel.visible()
        viewBinding.commBtnLine.visible()
        viewBinding.commAlertContent.visible()
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