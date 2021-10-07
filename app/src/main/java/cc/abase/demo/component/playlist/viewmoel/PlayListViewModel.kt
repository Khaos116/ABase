package cc.abase.demo.component.playlist.viewmoel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cc.ab.base.ui.viewmodel.DataState
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
class PlayListViewModel : CommViewModel() {
  //监听数据
  val videoLiveData = MutableLiveData<DataState<MutableList<VideoBean>>?>()

  //加载列表
  fun loadData() {
    if (videoLiveData.value is DataState.Start) return
    val old = videoLiveData.value?.data //加载前的旧数据
    viewModelScope.launch {
      videoLiveData.value = DataState.Start(oldData = old)
      delay(2000)
      //协程代码块
      val result = VideoRandomUtils.getVideoList()
      //可以直接更新UI
      videoLiveData.value = DataState.SuccessRefresh(newData = result, hasMore = false)
    }
  }
}