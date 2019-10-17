package cc.abase.demo.component.chat.viewmodel

import cc.ab.base.mvrx.MvRxViewModel
import com.airbnb.mvrx.*

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/17 16:42
 */
data class ChatState(
  var chatList: MutableList<String> = mutableListOf(),
  var request: Async<Any> = Uninitialized
) : MvRxState

class ChatViewModel(
  state: ChatState = ChatState()
) : MvRxViewModel<ChatState>(state) {

  fun sendMsg(
    msg: String,
    call: ((suc: Boolean) -> Unit)? = null
  ) = withState { state ->
    setState {
      val newList = mutableListOf<String>()
      newList.addAll(state.chatList)
      newList.add(msg)
      call?.invoke(true)
      copy(chatList = newList, request = Success(""))
    }
  }
}