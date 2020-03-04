package cc.abase.demo.component.playlist.viewmoel

import cc.ab.base.mvrx.MvRxViewModel
import cc.ab.base.utils.RxUtils
import cc.abase.demo.bean.local.VideoBean
import cc.abase.demo.utils.VideoRandomUtils
import com.airbnb.mvrx.*
import io.reactivex.Observable

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/12/12 11:37
 */
data class PlayListState(
  val videoList: MutableList<VideoBean> = mutableListOf(),
  val hasMore: Boolean = false,
  val request: Async<Any> = Uninitialized
) : MvRxState

class PlayListViewModel(
  state: PlayListState = PlayListState()
) : MvRxViewModel<PlayListState>(state) {

  //加载列表
  fun loadData() {
    Observable.just(VideoRandomUtils.instance.getVideoList())
      .compose(RxUtils.instance.rx2SchedulerHelperODelay())
      .execute {
        copy(videoList = it.invoke() ?: mutableListOf(), request = it)
      }
  }
}