package cc.abase.demo.component.playlist.viewmoel

import cc.ab.base.mvrx.MvRxViewModel
import cc.abase.demo.repository.bean.local.VideoBean
import com.airbnb.mvrx.*

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/12/12 11:37
 */

data class PlayPagerState(
  var videoList: MutableList<VideoBean> = mutableListOf(),
  var hasMore: Boolean = true,
  var request: Async<Any> = Uninitialized
) : MvRxState

class PlayPagerViewModel(
  state: PlayPagerState = PlayPagerState()
) : MvRxViewModel<PlayPagerState>(state) {

}