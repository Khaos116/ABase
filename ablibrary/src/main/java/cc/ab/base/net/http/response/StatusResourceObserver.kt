package cc.ab.base.net.http.response

import io.reactivex.observers.ResourceObserver

/**
 * Description:关心各种状态的请求观察者
 * @author: caiyoufei
 * @date: 2019/9/22 18:59
 */
class StatusResourceObserver<T>(
  private val liveData: StatusDataLiveData<T>
) : ResourceObserver<T>() {

  override fun onStart() {
    super.onStart()
  }

  override fun onComplete() {
  }

  override fun onNext(t: T) {
    liveData.value = StatusData.Success(t)
  }

  override fun onError(e: Throwable) {
    liveData.value = StatusData.Failure(e)
  }
}