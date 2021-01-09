package cc.ab.base.widget.toast.inner

import android.os.*
import java.util.LinkedList

/**
 *description: 系统Toast队列工具类.
 *@date 2018/12/21 17:25.
 *@author: YangYang.
 */
object SystemTN : Handler(Looper.getMainLooper()) {
  private val REMOVE = 2
  private var toastQueue: LinkedList<SystemToast> = LinkedList() //列表中成员要求非空

  /**
   * 新增Toast任务加入队列
   */
  fun add(toast: SystemToast?) {
    if (toast == null) return
    val mToast = toast.clone()
    notifyNewToastComeIn(mToast)
  }

  //当前有toast在展示
  private fun isShowing(): Boolean {
    return toastQueue.size > 0
  }

  private fun notifyNewToastComeIn(mToast: SystemToast) {
    val isShowing = isShowing()
    //加入队列
    toastQueue.add(mToast)

    //如果有toast正在展示
    if (isShowing) {
      if (toastQueue.size == 2) {
        //获取当前正在展示的toast
        val showing = toastQueue.peek()
        //允许新加入的toast终止当前的展示
        if (mToast.getPriority() >= showing.getPriority()) {
          //立即终止当前正在展示toast,并开始展示下一个
          sendRemoveMsg(showing)
        } else {
          //do nothing ...
          return
        }
      } else {
        //do nothing ...
        return
      }
    } else {
      showNextToast()
    }
  }

  private fun remove(toast: SystemToast) {
    toastQueue.remove(toast)
    toast.cancelInternal()
    // 展示下一个Toast
    showNextToast()
  }

  fun cancelAll() {
    removeMessages(REMOVE)
    if (!toastQueue.isEmpty()) {
      toastQueue.peek().cancelInternal()
    }
    toastQueue.clear()
  }

  /**
   * 多个弹窗连续出现时：
   * 1.相同优先级时，会终止上一个，直接展示后一个；
   * 2.不同优先级时，如果后一个的优先级更高则会终止上一个，直接展示后一个。
   */
  private fun showNextToast() {
    if (toastQueue.isEmpty()) return
    val toast = toastQueue.peek()
    if (null == toast) {
      toastQueue.poll()
      showNextToast()
    } else {
      if (toastQueue.size > 1) {
        val next = toastQueue[1]
        if (next.getPriority() >= toast.getPriority()) {
          toastQueue.remove(toast)
          showNextToast()
        } else {
          displayToast(toast)
        }
      } else {
        displayToast(toast)
      }
    }
  }

  private fun sendRemoveMsgDelay(toast: SystemToast) {
    removeMessages(REMOVE)
    val message = obtainMessage(REMOVE)
    message.obj = toast
    sendMessageDelayed(message, toast.getDuration().toLong())
  }

  private fun sendRemoveMsg(toast: SystemToast) {
    removeMessages(REMOVE)
    val message = obtainMessage(REMOVE)
    message.obj = toast
    sendMessage(message)
  }

  private fun displayToast(toast: SystemToast) {
    toast.showInternal()
    //展示到时间后移除
    sendRemoveMsgDelay(toast)
  }

  override fun handleMessage(message: Message) {
    if (message == null)
      return
    when (message.what) {
      REMOVE -> remove(message.obj as SystemToast)
      else -> {
      }
    }
  }
}