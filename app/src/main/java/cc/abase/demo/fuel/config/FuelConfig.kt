package cc.abase.demo.fuel.config

import cc.ab.base.fuel.FuelHelper
import cc.abase.demo.constants.WanUrls

/**
 * Description:初始化调用
 * @author: caiyoufei
 * @date: 2020/3/4 15:21
 */
class FuelConfig private constructor() {
  private object SingletonHolder {
    val holder = FuelConfig()
  }

  companion object {
    val instance = SingletonHolder.holder
  }

  private var hasInit = false

  fun init() {
    if (hasInit) return
    hasInit = true
    FuelHelper.initFuel(
        WanUrls.BASE,
        requestInterceptor = FuelRequestInterceptor.instance.fuelHeader(),
        responseInterceptor = FuelResponseManager.instance.fuelResponse()
    )
  }
}