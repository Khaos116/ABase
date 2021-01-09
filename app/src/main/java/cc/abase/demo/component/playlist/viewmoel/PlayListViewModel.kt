package cc.abase.demo.component.playlist.viewmoel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.rxLifeScope
import cc.ab.base.ui.viewmodel.DataState
import cc.abase.demo.bean.local.VideoBean
import cc.abase.demo.component.comm.CommViewModel
import cc.abase.demo.utils.VideoRandomUtils
import kotlinx.coroutines.delay

/**
 * Description:
 * @author: CASE
 * @date: 2019/12/12 11:37
 */
class PlayListViewModel : CommViewModel() {
  //监听数据
  val videoLiveData = MutableLiveData<DataState<MutableList<VideoBean>>?>()

  //加载列表
  fun loadData() {
    if (videoLiveData.value is DataState.Start) return
    val old = videoLiveData.value?.data //加载前的旧数据
    rxLifeScope.launch({
      delay(2000)
      //协程代码块
      val result = VideoRandomUtils.getVideoList()
      //可以直接更新UI
      videoLiveData.value = DataState.SuccessRefresh(newData = result)
    }, { e -> //异常回调，这里可以拿到Throwable对象
      videoLiveData.value = DataState.FailMore(oldData = old, exc = e)
    }, { //开始回调，可以开启等待弹窗
      videoLiveData.value = DataState.Start(oldData = old)
    }, { //结束回调，可以销毁等待弹窗
      val data = videoLiveData.value?.data
      videoLiveData.value = DataState.Complete(totalData = data, hasMore = false)
    })
  }
}