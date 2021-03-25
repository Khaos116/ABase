package cc.abase.demo.widget.smart

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import cc.abase.demo.R
import com.airbnb.lottie.LottieAnimationView
import com.scwang.smart.refresh.layout.api.*
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.simple.SimpleComponent

/**
 * @description: 小黑人动画的下拉刷新的头(艾美咪哒项目)
 * @Author:CASE
 * @Date:2021年1月5日
 * @Time:18:54:13
 */
open class MidaMusicHeader @kotlin.jvm.JvmOverloads constructor(c: Context, a: AttributeSet? = null, d: Int = 0
) : SimpleComponent(c, a, d), RefreshComponent, RefreshHeader {

  //<editor-fold defaultstate="collapsed" desc="变量+XML">
  //头部布局
  @SuppressLint("InflateParams")
  private val mRootView: View = LayoutInflater.from(c).inflate(R.layout.layout_refresh_header, null)

  //动画
  private var headerLav: LottieAnimationView? = null

  //提示文字
  private var headerTv: TextView? = null
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  init {
    headerLav = mRootView.findViewById(R.id.headerLav)
    headerTv = mRootView.findViewById(R.id.headerTv)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="父类调用头部布局">
  override fun getView() = mRootView
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="下拉过程中的状态变化">
  override fun onStateChanged(refreshLayout: RefreshLayout, oldState: RefreshState, newState: RefreshState) {
    when (newState) {
      RefreshState.None -> { //恢复初始状态
        headerTv?.text = "下拉刷新"
        headerLav?.cancelAnimation()
        headerLav?.progress = 0f
      }
      RefreshState.PullDownToRefresh -> headerTv?.text = "下拉刷新"
      RefreshState.ReleaseToRefresh -> headerTv?.text = "释放刷新"
      RefreshState.Refreshing -> headerTv?.text = "刷新中..."
      else -> {
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="开始动画">
  override fun onStartAnimator(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {
    headerLav?.playAnimation()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="禁止父类方法">
  override fun onFinish(refreshLayout: RefreshLayout, success: Boolean) = 500 //延迟500毫秒之后再弹回
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Header外部背景设置">
  private var mRefreshKernel: RefreshKernel? = null
  private var mBackgroundColor: Int = 0

  override fun onInitialized(kernel: RefreshKernel, height: Int, maxDragHeight: Int) {
    super.onInitialized(kernel, height, maxDragHeight)
    mRefreshKernel = kernel
    kernel.requestDrawBackgroundFor(this, mBackgroundColor)
  }

  private var mSetPrimaryColor = false
  private var mSetAccentColor = false
  override fun setPrimaryColors(vararg colors: Int) {
    if (colors.isNotEmpty()) {
      val thisView: View = this
      if (thisView.background !is BitmapDrawable && !mSetPrimaryColor) {
        setPrimaryColor(colors[0])
        mSetPrimaryColor = false
      }
      if (!mSetAccentColor) {
        if (colors.size > 1) {
          setAccentColor(colors[1])
        }
        mSetAccentColor = false
      }
    }
  }

  private fun setPrimaryColor(@ColorInt primaryColor: Int) {
    mSetPrimaryColor = true
    mBackgroundColor = primaryColor
    mRefreshKernel?.requestDrawBackgroundFor(this, primaryColor)
  }

  private fun setAccentColor(@ColorInt accentColor: Int) {
    mSetAccentColor = true
    headerTv?.setTextColor(accentColor)
  }
  //</editor-fold>
}