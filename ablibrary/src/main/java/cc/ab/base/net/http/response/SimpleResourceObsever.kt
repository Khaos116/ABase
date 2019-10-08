package cc.ab.base.net.http.response

import io.reactivex.observers.ResourceObserver

/**
 * Description:只关心成功失败的请求观察者
 * @author: caiyoufei
 * @date: 2019/9/22 18:56
 */
class SimpleResourceObsever<T>(
  private val success: ((T) -> Unit)?,
  private val failure: ((Throwable) -> Unit)? = null
) : ResourceObserver<T>() {
  override fun onComplete() {
  }

  override fun onNext(t: T) {
    success?.invoke(t)
  }

  override fun onError(e: Throwable) {
    failure?.invoke(e)
  }
}