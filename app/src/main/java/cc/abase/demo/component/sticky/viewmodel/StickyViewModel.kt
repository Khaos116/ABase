package cc.abase.demo.component.sticky.viewmodel

import cc.ab.base.mvrx.MvRxViewModel
import cc.ab.base.utils.RxUtils
import cc.abase.demo.bean.local.CityBean
import cc.abase.demo.bean.local.ProvinceBean
import cc.abase.demo.utils.CountryUtils
import com.airbnb.mvrx.*
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ResourceUtils
import com.github.promeg.pinyinhelper.Pinyin
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable

/**
 * Description:
 * @author: CASE
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
    //读取城市数据
    Observable.just(ResourceUtils.readAssets2String("city.json"))
        //转换为Bean
        .map {
          GsonUtils.fromJson<MutableList<ProvinceBean>>(
              it, object : TypeToken<MutableList<ProvinceBean>>() {}.type
          )
        }
        //按照拼音首字母排序
        .map {
          it.sortBy { pb -> pb.pinYinFirst }
          it
        }
        .compose(RxUtils.instance.rx2SchedulerHelperODelay())
        .execute {
          copy(provinces = it.invoke() ?: mutableListOf(), request = it)
        }
  }

  //加载国家
  fun loadCountry() {
    Observable.just(
        CountryUtils.countries.filter {
          !it.country_phone_code.startsWith("-")
        }.toMutableList()
    )
        //获取拼音
        .map {
          for (entity in it) {
            val pinyin = Pinyin.toPinyin(entity.name_zh, "")
            entity.pinyinFull = pinyin
            entity.pinyinFirst = pinyin.substring(0, 1)
          }
          it
        }
        //按照拼音排序
        .map {
          it.sortBy { pb -> pb.pinyinFull }
          it
        }
        //类型转换
        .map { list ->
          val result = mutableListOf<ProvinceBean>()
          var province = ProvinceBean()
          list.forEachIndexed { index, country ->
            //新的header
            if (province.regionName != country.pinyinFirst) {
              province = province.copy(
                  id = index.toLong(),
                  pinYinFirst = country.pinyinFirst,
                  regionName = country.pinyinFirst,
                  cmsRegionDtoList = mutableListOf()
              )
              result.add(province)
            }
            //子列表
            province.cmsRegionDtoList?.add(
                CityBean(
                    id = index.toLong(),
                    pinYinFirst = country.pinyinFirst,
                    regionFullName = country.name_zh,
                    regionCode = "+${country.country_phone_code}"
                )
            )
          }
          result
        }
        .compose(RxUtils.instance.rx2SchedulerHelperODelay())
        .execute {
          copy(provinces = it.invoke() ?: mutableListOf(), request = it)
        }
  }
}