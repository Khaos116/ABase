package cc.abase.demo.component.playlist.viewmoel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.rxLifeScope
import cc.ab.base.ui.viewmodel.DataState
import cc.ab.base.ui.viewmodel.DataState.FailMore
import cc.ab.base.ui.viewmodel.DataState.FailRefresh
import cc.ab.base.ui.viewmodel.DataState.Start
import cc.ab.base.ui.viewmodel.DataState.SuccessMore
import cc.ab.base.ui.viewmodel.DataState.SuccessRefresh
import cc.abase.demo.bean.local.VideoBean
import cc.abase.demo.component.comm.CommViewModel
import cc.abase.demo.utils.VideoRandomUtils
import kotlinx.coroutines.delay

/**
 * Description:
 * @author: Khaos
 * @date: 2019/12/12 11:37
 */
class VerticalPagerViewModel : CommViewModel() {
  val videoLiveData = MutableLiveData<DataState<MutableList<VideoBean>>?>()

  //加载数据
  fun loadData() {
    if (videoLiveData.value is Start) return
    val old = videoLiveData.value?.data //加载前的旧数据
    rxLifeScope.launch({
      delay(2000)
      //协程代码块
      val result = VideoRandomUtils.getVideoList()
      //可以直接更新UI
      videoLiveData.value = SuccessRefresh(newData = result, hasMore = result.size > 0)
    }, { e -> //异常回调，这里可以拿到Throwable对象
      videoLiveData.value = FailRefresh(oldData = old, exc = e)
    }, { //开始回调，可以开启等待弹窗
      videoLiveData.value = Start(oldData = old)
    })
  }

  //加载更多
  fun loadMore() {
    val value = videoLiveData.value
    if (value is Start) return //正在请求
    val old = videoLiveData.value?.data //加载前的旧数据
    rxLifeScope.launch({
      delay(1000)
      //协程代码块
      val result = VideoRandomUtils.getVideoList(idStart = videoLiveData.value?.data?.size?.toLong() ?: 0L, count = 4)
      //可以直接更新UI
      videoLiveData.value = SuccessMore(
        newData = result,
        totalData = if (old.isNullOrEmpty()) result else (old + result).toMutableList(),
        hasMore = result.size >= 4
      )
    }, { e -> //异常回调，这里可以拿到Throwable对象
      videoLiveData.value = FailMore(oldData = old, exc = e)
    }, { //开始回调，可以开启等待弹窗
      videoLiveData.value = Start(oldData = old)
    })
  }
}