package cc.abase.demo.component.comm

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import cc.ab.base.ext.removeParent
import cc.ab.base.ui.fragment.BaseFragment
import com.airbnb.lottie.*

/**
 * Description:
 * @author: CASE
 * @date: 2019/10/8 10:47
 */
abstract class CommFragment : BaseFragment() {
  //<editor-fold defaultstate="collapsed" desc="MVRX相关">
  override fun invalidate() {}
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="变量">
  //lottie的加载动画
  lateinit var loadingView: LottieAnimationView
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    loadingView = LottieAnimationView(mContext)
    loadingView.setAnimation("loading.json")
    loadingView.imageAssetsFolder = "images/"
    loadingView.setRenderMode(RenderMode.HARDWARE)
    loadingView.repeatCount = LottieDrawable.INFINITE
    loadingView.repeatMode = LottieDrawable.RESTART
    loadingView.setOnClickListener { }
    return super.onCreateView(inflater, container, savedInstanceState)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Loading显示和隐藏">
  //显示json动画的loading
  fun showLoadingView() {
    removeAllCallbacks()
    runShowLoading = Runnable { startShowLoadingView() }
    //防止获取不到高度
    rootView?.post(runShowLoading)
  }

  //显示loadingView
  private fun startShowLoadingView(
      transY: Float = getLoadingViewTransY(),
      height: Int = getLoadingViewHeight(),
      gravity: Int = getLoadingViewGravity(),
      bgColor: Int = getLoadingViewBgColor()
  ) {
    val parent = rootView
    if (loadingView.parent == null && parent != null) {
      val prams = FrameLayout.LayoutParams(-1, height)
      prams.gravity = gravity
      loadingView.translationY = transY
      loadingView.setBackgroundColor(bgColor)
      parent.addView(loadingView, prams)
    }
    loadingView.playAnimation()
  }

  //关闭loadingView
  fun dismissLoadingView() {
    removeAllCallbacks()
    runDismissLoading = Runnable {
      loadingView.pauseAnimation()
      loadingView.cancelAnimation()
      loadingView.removeParent()
    }
    //防止获取不到高度
    rootView?.post(runDismissLoading)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="延迟执行(为了获取View尺寸)">
  private var runShowLoading: Runnable? = null
  private var runDismissLoading: Runnable? = null

  //移除所有监听
  private fun removeAllCallbacks() {
    rootView?.removeCallbacks(runShowLoading)
    rootView?.removeCallbacks(runDismissLoading)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Loading相关偏移量">

  //设置偏移量
  open fun getLoadingViewTransY(): Float {
    return 0f
  }

  //设置动画高度
  open fun getLoadingViewHeight(): Int {
    return rootView?.width ?: 680
  }

  //设置动画的位置，默认居中
  open fun getLoadingViewGravity(): Int {
    return Gravity.CENTER
  }

  //设置动画背景
  open fun getLoadingViewBgColor(): Int {
    return Color.WHITE
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="防止内存泄漏">
  override fun onDestroy() {
    removeAllCallbacks()
    super.onDestroy()
  }
  //</editor-fold>
}