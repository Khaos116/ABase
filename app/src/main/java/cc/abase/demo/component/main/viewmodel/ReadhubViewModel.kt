package cc.abase.demo.component.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cc.ab.base.ui.viewmodel.DataState
import cc.ab.base.ui.viewmodel.DataState.*
import cc.abase.demo.bean.readhub.TopicBean
import cc.abase.demo.component.comm.CommViewModel
import cc.abase.demo.rxhttp.repository.ReadhubRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import rxhttp.awaitResult
import rxhttp.onStart

/**
 * Author:Khaos
 * Date:2022年3月8日18:18:10
 * Time:9:52
 */
class ReadhubViewModel : CommViewModel() {
  //<editor-fold defaultstate="collapsed" desc="外部访问">
  val topicLiveData = MutableLiveData<DataState<MutableList<TopicBean>>?>()

  //刷新
  fun refresh(readCache: Boolean) = requestTopicList("", readCache)

  //加载更多
  fun loadMore() {
    val lastOne = topicLiveData.value?.data?.lastOrNull()
    if (lastOne == null) {
      viewModelScope.launch {
        delay(200)
        topicLiveData.value = FailMore(oldData = topicLiveData.value?.data, exc = Throwable(""))
      }
    } else {
      requestTopicList(lastOne.uid ?: "", false)
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="内部处理">
  private var pageSize = 20
  private fun requestTopicList(lastTopicId: String, readCache: Boolean) {
    if (topicLiveData.value is Start) return
    val old = topicLiveData.value?.data //加载前的旧数据
    viewModelScope.launch {
      ReadhubRepository.getTopicList(lastTopicId = lastTopicId, pageSize = pageSize, readCache = readCache)
        .onStart {
          topicLiveData.value = Start(oldData = old)
        }
        .awaitResult { r ->
          val result = r.items ?: mutableListOf()
          val more = result.isNotEmpty() && result.size % pageSize == 0
          //可以直接更新UI
          topicLiveData.value = if (lastTopicId.isBlank()) SuccessRefresh(
            newData = result,
            hasMore = more
          )
          else SuccessMore(
            newData = result,
            totalData = if (old.isNullOrEmpty()) result else (old + result).toMutableList(),
            hasMore = more
          )
        }
        .onFailure { e ->
          topicLiveData.value = if (lastTopicId.isBlank()) FailRefresh(oldData = old, exc = e) else FailMore(oldData = old, exc = e)
        }
    }
  }
  //</editor-fold>
}