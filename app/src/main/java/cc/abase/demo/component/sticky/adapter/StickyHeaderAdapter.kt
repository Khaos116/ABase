package cc.abase.demo.component.sticky.adapter

import cc.abase.demo.component.sticky.widget.HasStickyHeader
import cc.abase.demo.epoxy.base.DividerItem_
import cc.abase.demo.epoxy.item.*
import cc.abase.demo.repository.bean.local.CityBean
import cc.abase.demo.repository.bean.local.ProvinceBean
import com.airbnb.epoxy.EpoxyAdapter

/**
 * Showcases [EpoxyAdapter] with sticky header support
 */
class StickyHeaderAdapter(
  provinces: MutableList<ProvinceBean>,
  onProvinceClick: ((province: ProvinceBean) -> Unit)? = null,
  onCityClick: ((city: CityBean) -> Unit)? = null
) : EpoxyAdapter(), HasStickyHeader {

  init {
    enableDiffing()
    provinces.forEachIndexed { index, provinceBean ->
      //省份
      addModel(
          StickyTopItem_().apply {
            id("sticky_province_${provinceBean.id}")
            province(provinceBean)
            onItemClick { p -> onProvinceClick?.invoke(p) }
          })
      provinceBean.cmsRegionDtoList?.forEachIndexed { index2, cityBean ->
        //城市
        addModel(
            StickyNormalItem_().apply {
              id("sticky_city_${cityBean.id}")
              city(cityBean)
              onItemClick { c -> onCityClick?.invoke(c) }
            }
        )
        //非最后一条添加分割线
        if (index2 < ((provinceBean.cmsRegionDtoList?.size ?: 0) - 1)) {
          addModel(
              DividerItem_().apply {
                id("sticky_city_line_${cityBean.id}")
              }
          )
        }
      }
    }
    notifyModelsChanged()
  }

  override fun isStickyHeader(position: Int) = models[position] is StickyTopItem
}