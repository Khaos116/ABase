package cc.abase.demo.component.playlist.view

import android.content.Context
import android.util.AttributeSet
import com.dueeeke.videoplayer.controller.BaseVideoController

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/12/13 13:24
 */
class PagerController(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : BaseVideoController(context, attrs, defStyleAttr) {
  override fun getLayoutId() = 0
  //不显示移动网络播放警告
  override fun showNetWarning() = false
}