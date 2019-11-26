package cc.abase.demo.component.sticky.viewmodel

import cc.ab.base.mvrx.MvRxViewModel
import cc.ab.base.utils.RxUtils
import cc.abase.demo.repository.bean.local.ProvinceBean
import com.airbnb.mvrx.*
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ResourceUtils
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/11/26 18:13
 */
data class StickState(
  val provinces: MutableList<ProvinceBean> = mutableListOf(),
  val request: Async<Any> = Uninitialized
) : MvRxState

class StickyViewModel(
  state: StickState = StickState()
) : MvRxViewModel<StickState>(state) {

  //加载城市
  fun loadProvince() {
    Observable.just(ResourceUtils.readAssets2String("city.json"))
        .map {
          GsonUtils.fromJson<MutableList<ProvinceBean>>(
              it, object : TypeToken<MutableList<ProvinceBean>>() {}.type
          )
        }
        .compose(RxUtils.instance.rx2SchedulerHelperODelay())
        .execute {
          copy(provinces = it.invoke() ?: mutableListOf(), request = it)
        }
  }
}