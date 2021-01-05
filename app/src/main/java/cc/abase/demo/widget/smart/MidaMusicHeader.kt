package cc.abase.demo.widget.smart

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import cc.abase.demo.R
import com.airbnb.lottie.LottieAnimationView
import com.scwang.smart.refresh.classics.ClassicsAbstract
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState

/**
 * @description: 小黑人动画的下拉刷新的头(艾美咪哒项目)
 * @Author:CASE
 * @Date:2021年1月5日
 * @Time:18:54:13
 */
class MidaMusicHeader @JvmOverloads constructor(c: Context, a: AttributeSet? = null, d: Int = 0
) : ClassicsAbstract<MidaMusicHeader>(c, a, d), RefreshHeader {

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
  override fun onFinish(refreshLayout: RefreshLayout, success: Boolean) = mFinishDuration //延迟500毫秒之后再弹回
  //</editor-fold>
}