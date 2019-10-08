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
 * @author: caiyoufei
 * @date: 2019/10/8 10:47
 */
abstract class CommFragment : BaseFragment() {
  override fun invalidate() {
  }

  //lottie的加载动画
  lateinit var loadingView: LottieAnimationView

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    loadingView = LottieAnimationView(mContext)
    loadingView.setAnimation("loading.json")
    loadingView.imageAssetsFolder = "images/"
    loadingView.setRenderMode(RenderMode.HARDWARE)
    loadingView.repeatCount = LottieDrawable.INFINITE
    loadingView.repeatMode = LottieDrawable.RESTART
    loadingView.setOnClickListener { }
    return super.onCreateView(inflater, container, savedInstanceState)
  }

  //#################################镶嵌在页面中的loading->Start#################################//
  //显示json动画的loading
  fun showLoadingView() {
    //防止获取不到高度
    rootView?.post { startShowLoadingView() }
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
    mContext.runOnUiThread {
      loadingView.pauseAnimation()
      loadingView.cancelAnimation()
      loadingView.removeParent()
    }
  }

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
  //#################################镶嵌在页面中的loading<-END#################################//
}