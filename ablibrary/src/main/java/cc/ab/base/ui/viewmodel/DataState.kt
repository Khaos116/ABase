package cc.ab.base.ui.viewmodel

/**
 * https://www.cnblogs.com/Jetictors/p/8157969.html
 * 密封类 用来表示受限的类继承结构
 * Author:Khaos
 * Date:2020-11-30
 * Time:15:46
 */
sealed class DataState<T>(val data: T? = null) {
  //请求开始
  class Start<T>(oldData: T?) : DataState<T>(data = oldData) {}

  //刷新成功
  class SuccessRefresh<T>(newData: T?, val hasMore: Boolean) : DataState<T>(data = newData) {}

  //加载更多成功
  class SuccessMore<T>(val newData: T?, totalData: T?, val hasMore: Boolean) : DataState<T>(data = totalData) {}

  //刷新失败
  class FailRefresh<T>(oldData: T?, val exc: Throwable?) : DataState<T>(data = oldData) {}

  //加载更多失败
  class FailMore<T>(oldData: T?, val exc: Throwable?) : DataState<T>(data = oldData) {}

  //是否请求结束
  fun isComplete() = this is SuccessRefresh || this is SuccessMore || this is FailRefresh || this is FailMore

  //是否有更多
  fun hasMore(): Boolean? {
    return when (this) {
      is SuccessRefresh -> this.hasMore
      is SuccessMore -> this.hasMore
      else -> null
    }
  }
}