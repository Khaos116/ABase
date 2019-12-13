package cc.abase.demo.component.playlist.viewmoel

import cc.ab.base.mvrx.MvRxViewModel
import cc.ab.base.utils.RxUtils
import cc.abase.demo.repository.bean.local.VideoBean
import cc.abase.demo.utils.VideoRandomUtils
import com.airbnb.mvrx.*
import io.reactivex.Observable

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


  fun loadData(){
    Observable.just(VideoRandomUtils.instance.getVideoList())
        .compose(RxUtils.instance.rx2SchedulerHelperODelay())
        .execute {
          copy(videoList = it.invoke() ?: mutableListOf(), request = it)
        }
  }

  fun loadMore(){

  }
}