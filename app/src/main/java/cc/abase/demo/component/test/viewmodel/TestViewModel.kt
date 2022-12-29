package cc.abase.demo.component.test.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cc.ab.base.ui.viewmodel.DataState
import cc.abase.demo.component.comm.CommViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

/**
 * 并发请求处理：https://juejin.cn/post/7175854671603433528
 * Description:
 * @author: Khaos
 * @date: 2020/5/7 9:46
 */
class TestViewModel : CommViewModel() {
  //<editor-fold defaultstate="collapsed" desc="外部调用">
  val timeLiveData1 = MutableLiveData<DataState<String>?>()
  val timeLiveData2 = MutableLiveData<DataState<String>?>()
  fun startGetTime() {
    startTime1()
    startTime2()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="内部方法">
  //运行次数
  private var count = 20L

  //Rxjava倒计时
  private fun startTime1() {
    disposable1?.dispose()
    disposable1 = Flowable.intervalRange(1, count, 0, 500, TimeUnit.MILLISECONDS)
      .subscribeOn(Schedulers.io())
      .unsubscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .doOnNext { timeLiveData1.value = DataState.SuccessRefresh(newData = it.toString(), hasMore = false) }
      .doOnComplete { }
      .subscribe()
  }

  //协程倒计时
  private fun startTime2() {
    disposable2?.cancel()
    disposable2 = viewModelScope.launch {
      var count = 1
      timeLiveData2.value = DataState.SuccessRefresh(newData = count.toString(),hasMore = false)
      while (count < 20) {
        delay(500)
        count++
        timeLiveData2.value = DataState.SuccessRefresh(newData = count.toString(), hasMore = false)
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="释放">
  private var disposable1: Disposable? = null
  private var disposable2: Job? = null
  fun release() {
    disposable1?.dispose()
    disposable2?.cancel()
  }
  //</editor-fold>
}