package cc.ab.base.widget.toast.inner

import android.app.Activity
import android.os.*
import android.view.*
import cc.ab.base.ext.removeParent

/**
 *description: 描述.
 *@date 2018/12/21 17:39.
 *@author: YangYang.
 */
class CcTN : Handler(Looper.getMainLooper()) {

  private val REMOVE = 2
  private var toastQueue: DPriorityQueue<CcToast>//列表中成员要求非空

  init {
    toastQueue = DPriorityQueue(Comparator<CcToast> { x, y ->
      //往队列中add元素时，x为新增，y为原队列中元素
      // skip showing DToast
      if (y.isShowing()) return@Comparator 1
      if (x.getTimestamp() == y.getTimestamp()) return@Comparator 0
      if (x.getTimestamp() < y.getTimestamp()) -1 else 1//值小的排队首
    })
  }

  /**
   * 新增Toast任务加入队列
   */
  fun add(toast: CcToast?) {
    if (toast == null) return
    val mToast = toast.clone()

    notifyNewToastComeIn(mToast)
  }

  //当前有toast在展示
  private fun isShowing(): Boolean {
    return toastQueue.size > 0
  }

  private fun notifyNewToastComeIn(mToast: CcToast) {
    val isShowing = isShowing()
    //检查有没有时间戳，没有则一定要打上时间戳
    if (mToast.getTimestamp() <= 0) {
      mToast.setTimestamp(System.currentTimeMillis())
    }
    //然后加入队列
    toastQueue.add(mToast)

    //如果有toast正在展示
    if (isShowing) {
      if (toastQueue.size == 2) {
        //获取当前正在展示的toast
        val showing = toastQueue.peek()
        //允许新加入的toast终止当前的展示
        if (mToast.getPriority() >= showing!!.getPriority()) {
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

  private fun remove(toast: CcToast) {
    toastQueue.remove(toast)
    removeInternal(toast)
  }

  fun cancelAll() {
    removeMessages(REMOVE)
    if (!toastQueue.isEmpty()) {
      removeInternal(toastQueue.peek())
    }
    toastQueue.clear()
  }

  fun cancelActivityToast(activity: Activity?) {
    if (activity == null) return
    for (t in toastQueue) {
      if (t is ActivityToast && t.getContext() === activity) {
        remove(t)
      }
    }
  }

  private fun removeInternal(toast: CcToast?) {
    if (toast != null && toast.isShowing()) {
      // 2018/11/26 逻辑存在问题：队列中多个Toast使用相同ContentView时可能造成混乱。
      // 不过，不同时展示多个Toast的话，也不会出现此问题.因为next.show()在last.removeView()动作之后。
      // DToast不会同时展示多个Toast，因此成功避免了此问题
      val windowManager = toast.getWMManager()
      if (windowManager != null) {
        try {
          windowManager.removeView(toast.getView())
        } catch (e: Exception) {
          e.printStackTrace()
        }

      }
      toast.setShowing(false)
    }
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
        val next = toastQueue.get(1)
        if (next!!.getPriority() >= toast.getPriority()) {
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

  private fun sendRemoveMsgDelay(toast: CcToast) {
    removeMessages(REMOVE)
    val message = obtainMessage(REMOVE)
    message.obj = toast
    sendMessageDelayed(message, toast.getDuration().toLong())
  }

  private fun sendRemoveMsg(toast: CcToast) {
    removeMessages(REMOVE)
    val message = obtainMessage(REMOVE)
    message.obj = toast
    sendMessage(message)
  }

  private fun displayToast(toast: CcToast) {
    val windowManager = toast.getWMManager() ?: return
    val toastView: View? = toast.getView()
    if (toastView == null) {
      //没有ContentView时直接移除
      toastQueue.remove(toast)
      //移除一个未在展示的Toast任务后，主动唤起下一个Toast任务
      showNextToast()
      return
    }
    //从父容器中移除contentView
    toastView.removeParent()
    //再将contentView添加到WindowManager
    try {
      windowManager.addView(toastView, toast.getWMParams())

      //确定展示成功后
      toast.setShowing(true)
      //展示到时间后移除
      sendRemoveMsgDelay(toast)
    } catch (e: Exception) {
      if (e is WindowManager.BadTokenException &&
          e.message != null && e.message?.contains("token null is not valid") == true
      ) {
        if (toast is ActivityToast) {
          //如果ActivityToast也无法展示的话，暂时只能选择放弃治疗了，难受...
          CcToast.Count4BadTokenException = 0
        } else {
          CcToast.Count4BadTokenException++
          //尝试使用ActivityToast
          if (toast.getContext() is Activity) {
            //因为AimyToast未展示成功，需要主动移除,然后再尝试使用ActivityToast
            toastQueue.remove(toast)//从队列移除
            removeMessages(REMOVE)//清除已发送的延时消息
            toast.setShowing(false) //更新toast状态
            try {
              //尝试从窗口移除toastView，虽然windowManager.addView()抛出异常，但toastView仍然可能已经被添加到窗口父容器中(具体看ROM实现)，所以需要主动移除
              //因为toastView也可能没有被添加到窗口父容器，所以需要增加try-catch
              windowManager.removeViewImmediate(toastView)
            } catch (me: Exception) {
            }
            toast.getContext()
                ?.let {
                  ActivityToast(it)
                      .setTimestamp(toast.getTimestamp())
                      .setView(toastView)
                      .setDuration(toast.getDuration())
                      .setGravity(toast.getGravity(), toast.getXOffset(), toast.getYOffset())
                      .show()
                }
            return
          }
        }
      }
      e.printStackTrace()
    }

  }

  override fun handleMessage(message: Message?) {
    if (message == null) return
    when (message.what) {
      REMOVE -> {
        //移除当前
        remove(message.obj as CcToast)
        // 展示下一个Toast
        showNextToast()
      }
      else -> {
      }
    }
  }

  companion object {
    fun instance(): CcTN {
      return SingletonHolder.mTn
    }
  }

  private object SingletonHolder {
    val mTn = CcTN()
  }
}