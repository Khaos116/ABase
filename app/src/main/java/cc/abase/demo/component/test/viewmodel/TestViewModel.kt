package cc.abase.demo.component.test.viewmodel

import cc.ab.base.mvrx.MvRxViewModel
import cc.ab.base.utils.RxUtils
import com.airbnb.mvrx.*
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

/**
 * Description:
 * @author: CASE
 * @date: 2020/5/7 9:46
 */
data class TestState(
    val time1: Long = 0,
    val time2: Long = 0,
    val request: Async<Any> = Uninitialized) : MvRxState

class TestViewModel(state: TestState = TestState()) : MvRxViewModel<TestState>(state) {

  //<editor-fold defaultstate="collapsed" desc="外部调用">
  fun startGetTime() {
    startTime1()
    startTime2()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="内部方法">

  //运行次数
  private var count = 20L

  //内部调用withState
  private fun startTime1() {
    disposable1?.dispose()
    disposable1 = Flowable.intervalRange(0, count, 0, 200, TimeUnit.MILLISECONDS)
      .compose(RxUtils.instance.rx2SchedulerHelperF())
      .doOnNext { withState { state -> setState { copy(time1 = state.time1 + 1) } } }
      .doOnComplete { }
      .subscribe()
  }

  //外部调用withState(同时调用会导致time1的数据恢复到第一次获取到的状态)
  private fun startTime2() = withState { state ->
    disposable2?.dispose()
    disposable2 = Flowable.intervalRange(0, count, 0, 300, TimeUnit.MILLISECONDS)
      .compose(RxUtils.instance.rx2SchedulerHelperF())
      .doOnNext { setState { copy(time2 = state.time2 + 100) } }
      .doOnComplete { }
      .subscribe()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="释放">
  private var disposable1: Disposable? = null
  private var disposable2: Disposable? = null
  fun release() {
    disposable1?.dispose()
    disposable2?.dispose()
  }
  //</editor-fold>
}