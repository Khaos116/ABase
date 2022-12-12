package cc.abase.demo.component.comm

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.viewbinding.ViewBinding
import cc.ab.base.ext.removeParent
import cc.ab.base.ui.fragment.BaseBindFragment
import com.airbnb.lottie.*

/**
 * @Description
 * @Author：Khaos
 * @Date：2021/3/15
 * @Time：18:57
 */
abstract class CommBindFragment<T : ViewBinding> : BaseBindFragment<T>() {
  //<editor-fold defaultstate="collapsed" desc="变量">
  //lottie的加载动画
  private var loadingView: LottieAnimationView? = null
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View? {
    if (loadingView == null) loadingView = LottieAnimationView(mContext).also {
      it.setAnimation("loading.json")
      it.imageAssetsFolder = "images/"
      it.setRenderMode(RenderMode.HARDWARE)
      it.repeatCount = LottieDrawable.INFINITE
      it.repeatMode = LottieDrawable.RESTART
      it.setOnClickListener { }
    }
    return super.onCreateView(i, c, s)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Loading显示和隐藏">
  //显示json动画的loading
  fun showLoadingView() {
    removeAllCallbacks()
    runShowLoading = Runnable { startShowLoadingView() }
    //防止获取不到高度
    viewBinding.root.post(runShowLoading)
  }

  //显示loadingView
  private fun startShowLoadingView(
    transY: Float = getLoadingViewTransY(),
    height: Int = getLoadingViewHeight(),
    gravity: Int = getLoadingViewGravity(),
    bgColor: Int = getLoadingViewBgColor()
  ) {
    val parent = mRootLayout
    if (loadingView?.parent == null && parent != null) {
      val prams = FrameLayout.LayoutParams(-1, height)
      prams.gravity = gravity
      loadingView?.translationY = transY
      loadingView?.setBackgroundColor(bgColor)
      parent.addView(loadingView, prams)
    }
    loadingView?.playAnimation()
  }

  //关闭loadingView
  fun dismissLoadingView() {
    removeAllCallbacks()
    runDismissLoading = Runnable {
      loadingView?.pauseAnimation()
      loadingView?.cancelAnimation()
      loadingView?.removeParent()
    }
    //防止获取不到高度
    viewBinding.root.post(runDismissLoading)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="延迟执行(为了获取View尺寸)">
  private var runShowLoading: Runnable? = null
  private var runDismissLoading: Runnable? = null

  //移除所有监听
  private fun removeAllCallbacks() {
    if (_binding != null) {
      viewBinding.root.removeCallbacks(runShowLoading)
      viewBinding.root.removeCallbacks(runDismissLoading)
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Loading相关偏移量">

  //设置偏移量
  open fun getLoadingViewTransY() = 0f

  //设置动画高度
  open fun getLoadingViewHeight() = mRootLayout?.height ?: viewBinding.root.height

  //设置动画的位置，默认居中
  open fun getLoadingViewGravity() = Gravity.CENTER

  //设置动画背景
  open fun getLoadingViewBgColor() = Color.WHITE
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="防止内存泄漏">
  override fun onDestroyView() {
    removeAllCallbacks()
    super.onDestroyView()
  }
  //</editor-fold>
}