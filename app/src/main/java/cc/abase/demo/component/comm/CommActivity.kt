package cc.abase.demo.component.comm

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.get
import cc.ab.base.ext.*
import cc.ab.base.ui.activity.BaseActivity
import cc.abase.demo.R
import cc.abase.demo.utils.NetUtils
import cc.abase.demo.widget.dialog.ActionDialog
import com.airbnb.lottie.*
import com.blankj.utilcode.util.NetworkUtils
import com.dueeeke.videocontroller.StandardVideoController

/**
 * Description:
 * @author: CASE
 * @date: 2019/10/8 10:03
 */
abstract class CommActivity : BaseActivity() {
  //<editor-fold defaultstate="collapsed" desc="清理奔溃前的fragment">
  override fun onCreate(savedInstanceState: Bundle?) {
    for (fragment in supportFragmentManager.fragments) {
      supportFragmentManager.beginTransaction()
          .remove(fragment)
          .commitAllowingStateLoss()
    }
    super.onCreate(savedInstanceState)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="loading和重试外部调用">
  //private var loadingView: FlashingTextView? = null
  private var loadingView: LottieAnimationView? = null
  private var errorView: TextView? = null

  protected fun showLoadingView(msg: String = "") {
    removeAllCallbacks()
    runShowLoading = Runnable { startLoadingView(msg) }
    mContentView.post(runShowLoading)
  }

  protected fun showNoDataView() = showErrorView(R.string.no_data.xmlToString())

  protected fun showErrorView(msg: String? = "", reTry: (() -> Unit)? = null) {
    removeAllCallbacks()
    runShowError = Runnable { startErrorView(msg, retry = reTry) }
    mContentView.post(runShowError)
  }

  protected fun dismissLoadingView() {
    removeAllCallbacks()
    runDismissLoading = Runnable {
      loadingView?.pauseAnimation()
      loadingView?.cancelAnimation()
      loadingView?.removeParent()
    }
    mContentView.post(runDismissLoading)
  }

  protected fun dismissErrorView() {
    removeAllCallbacks()
    runDismissError = Runnable { errorView?.removeParent() }
    mContentView.post(runDismissError)
  }

  protected fun dismissLoadingAndErrorView() {
    removeAllCallbacks()
    runDismissLoading = Runnable {
      loadingView?.pauseAnimation()
      loadingView?.pauseAnimation()
      loadingView?.removeParent()
    }
    mContentView.post(runDismissLoading)
    runDismissError = Runnable { errorView?.removeParent() }
    mContentView.post(runDismissError)
  }

  //移除所有监听
  private fun removeAllCallbacks() {
    mContentView.removeCallbacks(runShowLoading)
    mContentView.removeCallbacks(runShowError)
    mContentView.removeCallbacks(runDismissLoading)
    mContentView.removeCallbacks(runDismissError)
  }

  private var runShowLoading: Runnable? = null
  private var runShowError: Runnable? = null
  private var runDismissLoading: Runnable? = null
  private var runDismissError: Runnable? = null
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="loading和重试内部调用">
  private fun startLoadingView(
      msg: String = "",
      transY: Float = getHoldViewTransY(),
      height: Int = getHoldViewHeight(),
      gravity: Int = getHoldViewGravity(),
      bgColor: Int = getHoldViewBgColor()
  ) {
    dismissErrorView()
    if (loadingView == null) {
      loadingView = LottieAnimationView(mContext)
      loadingView?.let { lav ->
        lav.setAnimation("loading.json")
        lav.imageAssetsFolder = "images/"
        lav.setRenderMode(RenderMode.HARDWARE)
        lav.repeatCount = LottieDrawable.INFINITE
        lav.repeatMode = LottieDrawable.RESTART
        lav.setOnClickListener { }
      }
    }
    //if (msg.isNotBlank()) loadingView?.text = msg
    if (loadingView?.parent == null) {
      val prams = FrameLayout.LayoutParams(-1, height)
      prams.gravity = gravity
      loadingView?.translationY = transY
      loadingView?.setBackgroundColor(bgColor)
      mContentView.addView(loadingView, prams)
    }
    loadingView?.playAnimation()
  }

  private fun startErrorView(msg: String? = "",
      transY: Float = getHoldViewTransY(),
      height: Int = getHoldViewHeight(),
      gravity: Int = getHoldViewGravity(),
      bgColor: Int = getHoldViewBgColor(),
      retry: (() -> Unit)? = null) {
    dismissLoadingView()
    if (errorView == null) {
      errorView = TextView(mContext)
      errorView?.run {
        this.isClickable = true
        this.text = if (NetworkUtils.isConnected()) R.string.net_fail_retry.xmlToString() else R.string.net_error_retry.xmlToString()
        this.gravity = Gravity.CENTER
        this.setTextColor(R.color.gray.xmlToColor())
      }
    }
    if (!msg.isNullOrBlank()) errorView?.text = msg
    if (retry != null) errorView?.click { if (NetUtils.checkNetToast()) retry.invoke() } else errorView?.setOnClickListener(null)
    if (errorView?.parent == null) {
      val prams = FrameLayout.LayoutParams(-1, height)
      prams.gravity = gravity
      errorView?.translationY = transY
      errorView?.setBackgroundColor(bgColor)
      mContentView.addView(errorView, prams)
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="loading偏移量相关">
  //设置Loading和Error偏移量
  open fun getHoldViewTransY(): Float {
    return 0f
  }

  //设置Loading和Error高度
  open fun getHoldViewHeight(): Int {
    return mContentView.width
  }

  //设置Loading和Error的位置，默认居中
  open fun getHoldViewGravity(): Int {
    return Gravity.CENTER
  }

  //设置Loading和Error的背景色
  open fun getHoldViewBgColor(): Int {
    return Color.WHITE
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="执行动作的Dialog弹窗">
  private var mActionDialog: ActionDialog? = null

  fun showActionLoading(text: String? = null) {
    if (mActionDialog == null) {
      mActionDialog = ActionDialog.newInstance(true)
    }
    mActionDialog?.onDismiss {
      mActionDialog = null
    }
    mActionDialog?.let {
      if (!text.isNullOrBlank()) it.hintText = text
      it.show(supportFragmentManager)
    }
  }

  fun dismissActionLoading() {
    mActionDialog?.dismissAllowingStateLoss()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="退出全屏">
  override fun onBackPressed() {
    (window.decorView as? FrameLayout)?.let { fl ->
      if (fl.childCount > 0) {
        val child = fl.getChildAt(fl.childCount - 1)
        if (child is FrameLayout && child.childCount > 0) {
          val cc = child[child.childCount - 1]
          if (cc is StandardVideoController && cc.onBackPressed()) {
            return
          }
        }
      }
    }
    super.onBackPressed()
  }
  //</editor-fold>
}