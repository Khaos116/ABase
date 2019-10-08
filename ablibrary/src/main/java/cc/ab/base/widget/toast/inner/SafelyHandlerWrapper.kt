package cc.ab.base.widget.toast.inner

import android.os.Handler
import android.os.Message

/**
 *description: catch了异常的Handler.
 *@date 2018/12/21 17:23.
 *@author: YangYang.
 */
class SafelyHandlerWrapper(val impl: Handler) : Handler() {

  override fun dispatchMessage(msg: Message) {
    try {
      impl.dispatchMessage(msg)
    } catch (e: Exception) {
    }
  }

  override fun handleMessage(msg: Message) {
    impl.handleMessage(msg)//需要委托给原Handler执行
  }
}