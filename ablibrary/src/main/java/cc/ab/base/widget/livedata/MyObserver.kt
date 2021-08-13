package cc.ab.base.widget.livedata

import androidx.lifecycle.Observer

/**
 * @Description 解决协程调用LiveData异常后LiveData监听失效BUG
 * @link https://www.jianshu.com/p/d9af046be392
 * @Author：Khaos
 * @Date：2021-08-13
 * @Time：13:56
 */
class MyObserver<T>(
  private val error: ((e: Throwable) -> Unit)? = null,
  private val changed: (t: T) -> Unit,
) : Observer<T> {
  override fun onChanged(t: T) {
    try {
      changed.invoke(t)
    } catch (e: Throwable) {
      error?.invoke(e)
    }
  }
}