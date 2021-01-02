package cc.abase.demo.component.chat.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.rxLifeScope
import cc.ab.base.ui.viewmodel.DataState
import cc.abase.demo.component.comm.CommViewModel

/**
 * Description:
 * @author: CASE
 * @date: 2019/10/17 16:42
 */
class ChatViewModel : CommViewModel() {
  //监听数据
  val chatLiveData = MutableLiveData<DataState<MutableList<String>>?>()

  //发送消息
  fun sendMsg(msg: String, call: ((suc: Boolean) -> Unit)? = null) {
    if (chatLiveData.value is DataState.Start) return
    val old = chatLiveData.value?.data //加载前的旧数据
    rxLifeScope.launch({
      //协程代码块
      val result = mutableListOf(msg)
      //可以直接更新UI
      chatLiveData.value = DataState.SuccessMore(newData = result, totalData = if (old.isNullOrEmpty()) result else (old + result).toMutableList())
      call?.invoke(true)
    }, { e -> //异常回调，这里可以拿到Throwable对象
      chatLiveData.value = DataState.FailMore(oldData = old, exc = e)
      call?.invoke(false)
    }, { //开始回调，可以开启等待弹窗
      chatLiveData.value = DataState.Start(oldData = old)
    }, { //结束回调，可以销毁等待弹窗
      val data = chatLiveData.value?.data
      chatLiveData.value = DataState.Complete(totalData = data, hasMore = false)
    })
  }
}