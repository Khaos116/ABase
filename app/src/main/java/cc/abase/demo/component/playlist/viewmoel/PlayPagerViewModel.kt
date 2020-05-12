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

data class PlayPagerState(
  val videoList: MutableList<VideoBean> = mutableListOf(),
  val hasMore: Boolean = true,
  val request: Async<Any> = Uninitialized
) : MvRxState

class PlayPagerViewModel(
  state: PlayPagerState = PlayPagerState()
) : MvRxViewModel<PlayPagerState>(state) {

  //加载数据
  fun loadData(){
    Observable.just(VideoRandomUtils.instance.getVideoList())
        .compose(RxUtils.instance.rx2SchedulerHelperODelay())
        .execute {
          copy(videoList = it.invoke() ?: mutableListOf(), request = it)
        }
  }

  //加载更多
  fun loadMore() = withState { state ->
    if (state.request is Loading || !state.hasMore) return@withState
    Observable.just(
        VideoRandomUtils.instance.getVideoList(
            idStart = state.videoList.size.toLong(),
            count = 4
        )
    )
        .compose(RxUtils.instance.rx2SchedulerHelperO())
        .execute {
          val suc = it is Success
          copy(
              videoList = if (suc) (state.videoList + (it.invoke()
                  ?: mutableListOf())).toMutableList()
              else state.videoList,
              hasMore = if (suc) false else state.hasMore,
              request = it
          )
        }
  }
}