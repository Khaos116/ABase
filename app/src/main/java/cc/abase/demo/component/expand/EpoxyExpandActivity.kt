package cc.abase.demo.component.expand

import android.content.Context
import android.content.Intent
import cc.ab.base.ext.mContext
import cc.ab.base.ext.toast
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommTitleActivity
import cc.abase.demo.component.sticky.viewmodel.StickyViewModel
import cc.abase.demo.epoxy.base.dividerItem
import cc.abase.demo.epoxy.item.stickyNormalItem
import cc.abase.demo.epoxy.item.stickyTopItem
import cc.abase.demo.mvrx.MvRxEpoxyController
import cc.abase.demo.bean.local.ProvinceBean
import com.blankj.utilcode.util.StringUtils
import kotlinx.android.synthetic.main.activity_epoxy_expand.expandRecycler

/**
 * Description: https://proandroiddev.com/expandable-recyclerview-with-epoxy-100515f5d026
 *
 * @author: CASE
 * @date: 2019/12/6 21:24
 */
class EpoxyExpandActivity : CommTitleActivity() {

  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, EpoxyExpandActivity::class.java)
      context.startActivity(intent)
    }
  }

  //数据层
  private val viewModel: StickyViewModel by lazy {
    StickyViewModel()
  }

  override fun layoutResContentId() = R.layout.activity_epoxy_expand

  override fun initContentView() {
    setTitleText(StringUtils.getString(R.string.epoxy_expandable))
    expandRecycler.setController(epoxyController)
    showLoadingView()
  }

  override fun initData() {
    viewModel.subscribe { state ->
      if (state.request.complete) {
        dismissLoadingView()
        epoxyController.data = state.provinces
      }
    }
    //随机加载数据
    if (System.currentTimeMillis() % 2 == 0L) {
      viewModel.loadCountry()
    } else {
      viewModel.loadProvince()
    }
  }

  //epoxy
  private val epoxyController = MvRxEpoxyController<MutableList<ProvinceBean>> { list ->
    list.forEachIndexed { index1, provinceBean ->
      //省份
      stickyTopItem {
        id("expand_province_${provinceBean.id}")
        province(provinceBean)
        onItemClick { p ->
          val temp = provinceBean.copy(expand = !p.expand)
          temp.cmsRegionDtoList = provinceBean.cmsRegionDtoList
          list[index1] = temp
          requestModelBuild()
        }
      }
      //展开后才显示
      if (provinceBean.expand) {
        provinceBean.cmsRegionDtoList?.forEachIndexed { index2, cityBean ->
          //城市
          stickyNormalItem {
            id("expand_city_${cityBean.id}")
            city(cityBean)
            onItemClick { c -> mContext.toast(c.regionFullName) }
          }
          //分割线
          if (index2 < (provinceBean.cmsRegionDtoList?.size ?: 0) - 1) {
            dividerItem {
              id("expand_city_line_${cityBean.id}")
            }
          }
        }
      } else {
        //分割线
        if (index1 < list.size - 1) {
          dividerItem {
            id("expand_province_line_${provinceBean.id}")
          }
        }
      }
    }
  }
}