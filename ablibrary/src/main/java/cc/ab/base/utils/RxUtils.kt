package cc.ab.base.utils

import androidx.lifecycle.Lifecycle
import com.trello.rxlifecycle3.LifecycleProvider
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/9/29 22:24
 */
class RxUtils private constructor() {
  private object SingletonHolder {
    val holder = RxUtils()
  }

  companion object {
    val instance = SingletonHolder.holder
  }

  /**
   * 统一线程处理(Rx 2.x)
   */
  fun <T> rx2SchedulerHelperF(provider: LifecycleProvider<Lifecycle.Event>? = null): FlowableTransformer<T, T> {//compose简化线程
    return FlowableTransformer { observable ->
      (if (provider != null) observable.compose(provider.bindUntilEvent(Lifecycle.Event.ON_DESTROY))
      else observable)
        .subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
    }
  }

  /**
   * 延迟出结果，防止请求结果出现太快
   * 统一线程处理(Rx 2.x)
   */
  fun <T> rx2SchedulerHelperFDelay(
    delay: Long = 500,
    provider: LifecycleProvider<Lifecycle.Event>? = null
  ): FlowableTransformer<T, T> {    //compose简化线程
    return FlowableTransformer { observable ->
      (if (provider != null) observable.zipWith(
        Flowable.timer(delay, TimeUnit.MILLISECONDS),
        BiFunction<T, Long, T> { t1, t2 -> t1 })
        .compose(provider.bindUntilEvent(Lifecycle.Event.ON_DESTROY))
      else observable.zipWith(Flowable.timer(delay, TimeUnit.MILLISECONDS),
        BiFunction<T, Long, T> { t1, t2 -> t1 }))
        .subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
    }
  }

  /**
   * 统一线程处理(Rx 2.x)
   */
  fun <T> rx2SchedulerHelperO(provider: LifecycleProvider<Lifecycle.Event>? = null): ObservableTransformer<T, T> {//compose简化线程
    return ObservableTransformer { observable ->
      (if (provider != null) observable.compose(provider.bindUntilEvent(Lifecycle.Event.ON_DESTROY))
      else observable)
        .subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
    }
  }

  /**
   * 延迟出结果，防止请求结果出现太快
   * 统一线程处理(Rx 2.x)
   */
  fun <T> rx2SchedulerHelperODelay(
    delay: Long = 500,
    provider: LifecycleProvider<Lifecycle.Event>? = null
  ): ObservableTransformer<T, T> {//compose简化线程
    return ObservableTransformer { observable ->
      (if (provider != null) observable.zipWith(
        Observable.timer(delay, TimeUnit.MILLISECONDS),
        BiFunction<T, Long, T> { t1, t2 -> t1 })
        .compose(provider.bindUntilEvent(Lifecycle.Event.ON_DESTROY))
      else observable.zipWith(Observable.timer(delay, TimeUnit.MILLISECONDS),
        BiFunction<T, Long, T> { t1, t2 -> t1 }))
        .subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
    }
  }

  /**
   * 统一线程处理(Rx 2.x)
   */
  fun <T> rx2SchedulerHelperS(provider: LifecycleProvider<Lifecycle.Event>? = null): SingleTransformer<T, T> {//compose简化线程
    return SingleTransformer { observable ->
      (if (provider != null) observable.compose(provider.bindUntilEvent(Lifecycle.Event.ON_DESTROY))
      else observable)
        .subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
    }
  }

  /**
   * 延迟出结果，防止请求结果出现太快
   * 统一线程处理(Rx 2.x)
   */
  fun <T> rx2SchedulerHelperSDelay(
    delay: Long = 500,
    provider: LifecycleProvider<Lifecycle.Event>? = null
  ): SingleTransformer<T, T> {//compose简化线程
    return SingleTransformer { observable ->
      (if (provider != null) observable.zipWith(
        Single.timer(delay, TimeUnit.MILLISECONDS),
        BiFunction<T, Long, T> { t1, t2 -> t1 })
        .compose(provider.bindUntilEvent(Lifecycle.Event.ON_DESTROY))
      else observable.zipWith(Single.timer(delay, TimeUnit.MILLISECONDS),
        BiFunction<T, Long, T> { t1, t2 -> t1 }))
        .subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
    }
  }

  /**
   * 生成Flowable
   */
  fun <T> createFlowable(t: T): Flowable<T> {
    return Flowable.create({ e ->
      try {
        e.onNext(t)
        e.onComplete()
      } catch (e1: Exception) {
        e.onError(e1)
        e.onComplete()
      }
    }, BackpressureStrategy.LATEST)
  }

  /*原型参考
    public  <T> FlowableTransformer<T, T> rx2SchedulerHelperFDelay(final long delay) {
    return new FlowableTransformer<T, T>() {
      @Override
      public Flowable<T> apply(Flowable<T> observable) {
        return
            observable.zipWith(Flowable.timer(delay, TimeUnit.MILLISECONDS),
                new BiFunction<T, Long, T>() {
                  @Override public T apply(T t, Long aLong) throws Exception {
                    return t;
                  }
                })
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
      }
    };
  }
   */
}