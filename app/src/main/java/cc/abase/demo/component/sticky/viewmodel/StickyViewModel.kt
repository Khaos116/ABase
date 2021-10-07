package cc.abase.demo.component.sticky.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.rxLifeScope
import cc.ab.base.ui.viewmodel.DataState
import cc.ab.base.ui.viewmodel.DataState.FailRefresh
import cc.ab.base.ui.viewmodel.DataState.Start
import cc.ab.base.ui.viewmodel.DataState.SuccessRefresh
import cc.abase.demo.bean.local.CityBean
import cc.abase.demo.bean.local.ProvinceBean
import cc.abase.demo.component.comm.CommViewModel
import cc.abase.demo.utils.CountryUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ResourceUtils
import com.github.promeg.pinyinhelper.Pinyin
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * Description:
 * @author: Khaos
 * @date: 2019/11/26 18:13
 */
class StickyViewModel : CommViewModel() {
  //监听数据
  val cityLiveData = MutableLiveData<DataState<MutableList<ProvinceBean>>?>()
  val countryLiveData = MutableLiveData<DataState<MutableList<ProvinceBean>>?>()

  //加载城市
  fun loadProvince(delay: Long = 0) {
    val old = cityLiveData.value?.data
    //读取城市数据
    if (cityLiveData.value is Start || !old.isNullOrEmpty()) return
    rxLifeScope.launch({
      withContext(Dispatchers.IO) {
        delay(delay)
        val json = ResourceUtils.readAssets2String("city.json")
        val list = GsonUtils.fromJson<MutableList<ProvinceBean>>(json, object : TypeToken<MutableList<ProvinceBean>>() {}.type)
        //按照拼音排序
        list.sortBy { pb -> pb.pinYinFirst }
        list
      }.let { cityLiveData.value = SuccessRefresh(newData = it, hasMore = false) }
    }, { e ->
      cityLiveData.value = FailRefresh(oldData = old, exc = e)
    }, {
      cityLiveData.value = Start(oldData = old)
    })
  }

  //加载国家
  fun loadCountry(delay: Long = 0) {
    val old = countryLiveData.value?.data
    //读取城市数据
    if (countryLiveData.value is Start || !old.isNullOrEmpty()) return
    rxLifeScope.launch({
      withContext(Dispatchers.IO) {
        delay(delay)
        val list = CountryUtils.countries.filter { !it.country_phone_code.startsWith("-") }.toMutableList()
        for (entity in list) {
          val pinyin = Pinyin.toPinyin(entity.name_zh, "")
          entity.pinyinFull = pinyin
          entity.pinyinFirst = pinyin.substring(0, 1)
        }
        //按照拼音排序
        list.sortBy { pb -> pb.pinyinFull }
        //类型转换
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
      }.let { countryLiveData.value = SuccessRefresh(newData = it, hasMore = false) }
    }, { e ->
      countryLiveData.value = FailRefresh(oldData = old, exc = e)
    }, {
      countryLiveData.value = Start(oldData = old)
    })
  }
}