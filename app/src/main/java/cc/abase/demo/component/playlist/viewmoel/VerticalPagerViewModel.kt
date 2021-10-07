package cc.abase.demo.component.playlist.viewmoel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cc.ab.base.ui.viewmodel.DataState
import cc.ab.base.ui.viewmodel.DataState.*
import cc.abase.demo.bean.local.VideoBean
import cc.abase.demo.component.comm.CommViewModel
import cc.abase.demo.utils.VideoRandomUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    viewModelScope.launch(getMainContext { _, e ->
      videoLiveData.value = FailRefresh(oldData = old, exc = e)
    }) {
      videoLiveData.value = Start(oldData = old)
      delay(2000)
      val result = VideoRandomUtils.getVideoList()
      //可以直接更新UI
      videoLiveData.value = SuccessRefresh(newData = result, hasMore = result.size > 0)
    }
  }

  //加载更多
  fun loadMore() {
    val value = videoLiveData.value
    if (value is Start) return //正在请求
    val old = videoLiveData.value?.data //加载前的旧数据
    viewModelScope.launch(getMainContext { _, e ->
      videoLiveData.value = FailMore(oldData = old, exc = e)
    }) {
      videoLiveData.value = Start(oldData = old)
      delay(1000)
      val result = VideoRandomUtils.getVideoList(idStart = videoLiveData.value?.data?.size?.toLong() ?: 0L, count = 4)
      videoLiveData.value = SuccessMore(
        newData = result,
        totalData = if (old.isNullOrEmpty()) result else (old + result).toMutableList(),
        hasMore = old?.size ?: 0 + result.size >= 24
      )
    }
  }
}