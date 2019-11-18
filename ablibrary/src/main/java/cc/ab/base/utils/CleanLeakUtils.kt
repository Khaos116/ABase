package cc.ab.base.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.lang.reflect.Field

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/11/18 10:09
 */
class CleanLeakUtils private constructor() {
  private object SingletonHolder {
    val holder = CleanLeakUtils()
  }

  companion object {
    val instance = SingletonHolder.holder
  }

  //修复输入法导致的内存泄漏
  fun fixInputMethodManagerLeak(destContext: Context?) {
    if (destContext == null) return
    val manager =
      destContext.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
          ?: return
    val viewArray = arrayOf("mCurRootView", "mServedView", "mNextServedView")
    var filed: Field
    var filedObject: Any?
    for (view in viewArray) {
      try {
        filed = manager.javaClass.getDeclaredField(view)
        if (!filed.isAccessible) filed.isAccessible = true
        filedObject = filed.get(manager)
        if (filedObject != null && filedObject is View) {
          if (filedObject.context === destContext) { // 被InputMethodManager持有引用的context是想要目标销毁的
            filed.set(manager, null) // 置空，破坏掉path to gc节点
          } else {
            break// 不是想要目标销毁的，即为又进了另一层界面了，不要处理，避免影响原逻辑,也就不用继续for循环了
          }
        }
      } catch (t: Throwable) {
        t.printStackTrace()
      }
    }
  }
}