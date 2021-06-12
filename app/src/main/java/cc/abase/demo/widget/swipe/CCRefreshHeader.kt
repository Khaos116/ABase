package cc.abase.demo.widget.swipe

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import cc.abase.demo.R
import com.airbnb.lottie.LottieAnimationView
import com.billy.android.swipe.SmartSwipeRefresh.SmartSwipeRefreshHeader
import com.blankj.utilcode.util.StringUtils

/**
 * Description:https://qibilly.com/SmartSwipe-tutorial/pages/SmartSwipeRefresh.html
 * @author: Khaos
 * @date: 2019/10/1 20:48
 */
class CCRefreshHeader(context: Context) : SmartSwipeRefreshHeader {
  private val rootView: View =
    LayoutInflater.from(context)
        .inflate(R.layout.layout_refresh_header, null)

  private var headerLav: LottieAnimationView? = null
  private var headerTv: TextView? = null

  init {
    headerLav = rootView.findViewById(R.id.headerLav)
    headerTv = rootView.findViewById(R.id.headerTv)
  }

  override fun onInit(horizontal: Boolean) {
  }

  override fun onFinish(success: Boolean): Long {
    headerLav?.cancelAnimation()
    headerLav?.progress = 0f
    return 200
  }

  override fun onStartDragging() {
    headerTv?.text = StringUtils.getString(R.string.pull_to_refresh)
  }

  override fun onDataLoading() {
    headerLav?.playAnimation()
    headerTv?.text = StringUtils.getString(R.string.refresh_loading_data)
  }

  override fun getView(): View {
    return rootView
  }

  override fun onProgress(
    dragging: Boolean,
    progress: Float
  ) {
    headerTv?.text = StringUtils.getString(
            if (progress < 1) {
              R.string.pull_to_refresh
            } else {
              R.string.release_to_refresh
            }
        )
  }

  override fun onReset() {
  }
}