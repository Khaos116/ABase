package cc.abase.demo.component.chat.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cc.ab.base.ui.viewmodel.DataState
import cc.abase.demo.component.comm.CommViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Description:
 * @author: Khaos
 * @date: 2019/10/17 16:42
 */
class ChatViewModel : CommViewModel() {
  //监听数据
  val chatLiveData = MutableLiveData<DataState<MutableList<String>>?>()

  //发送消息
  fun sendMsg(msg: String, call: ((suc: Boolean) -> Unit)? = null) {
    if (chatLiveData.value is DataState.Start) return
    val old = chatLiveData.value?.data //加载前的旧数据
    viewModelScope.launch {
      chatLiveData.value = DataState.Start(oldData = old)
      delay(50)
      //协程代码块
      val result = mutableListOf(msg)
      //可以直接更新UI
      chatLiveData.value = DataState.SuccessMore(
        newData = result,
        totalData = if (old.isNullOrEmpty()) result else (old + result).toMutableList(),
        hasMore = false
      )
      call?.invoke(true)
    }
  }
}