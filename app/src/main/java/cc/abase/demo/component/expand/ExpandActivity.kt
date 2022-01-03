package cc.abase.demo.component.expand

import android.content.Context
import android.content.Intent
import android.graphics.Color
import cc.ab.base.ext.mContext
import cc.ab.base.ext.toast
import cc.ab.base.ui.viewmodel.DataState
import cc.ab.base.widget.livedata.MyObserver
import cc.abase.demo.R
import cc.abase.demo.bean.local.DividerBean
import cc.abase.demo.bean.local.ProvinceBean
import cc.abase.demo.component.comm.CommBindTitleActivity
import cc.abase.demo.component.sticky.viewmodel.StickyViewModel
import cc.abase.demo.databinding.ActivityExpandBinding
import cc.abase.demo.item.*
import cc.abase.demo.widget.SpeedLinearLayoutManager
import com.blankj.utilcode.util.StringUtils
import com.drakeet.multitype.MultiTypeAdapter

/**
 * @author: Khaos
 * @date: 2019/12/6 21:24
 */
class ExpandActivity : CommBindTitleActivity<ActivityExpandBinding>() {
  //<editor-fold defaultstate="collapsed" desc="外部跳转">
  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, ExpandActivity::class.java)
      context.startActivity(intent)
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="变量">
  //数据层
  private val viewModel: StickyViewModel by lazy { StickyViewModel() }

  //适配器
  private val multiTypeAdapter = MultiTypeAdapter()
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化View">
  override fun initContentView() {
    setTitleText(StringUtils.getString(R.string.列表收缩展开效果))
    multiTypeAdapter.register(StickyTopItem().also {
      it.onItemClick = { pb ->
        val items = multiTypeAdapter.items.toMutableList()
        val index = items.indexOf(pb)
        if (pb.expand) { //收起
          pb.expand = false
          val remainList = items.subList(index + 1, items.size) //找到当前到最后一个数据
          val next = remainList.firstOrNull { a -> a is ProvinceBean } //找到下一个数据
          val end = if (next == null) items.size - 2/*多减1是最后一个分割线*/ else items.indexOf(next) - 2/*多减1是最后一个分割线*/
          for (i in end downTo index + 1) items.removeAt(i) //全部移除掉
          multiTypeAdapter.items = items
          multiTypeAdapter.notifyItemChanged(index)
          multiTypeAdapter.notifyItemRangeRemoved(index + 1, end - index)
        } else { //展开
          pb.expand = true
          val temps = mutableListOf<Any>()
          val size = pb.cmsRegionDtoList?.size ?: 0
          pb.cmsRegionDtoList?.forEachIndexed { i, city -> //添加展开数据和分割线
            temps.add(city)
            if (i < size - 1) temps.add(DividerBean(heightPx = 1, bgColor = Color.CYAN))
          }
          items.addAll(index + 1, temps)
          multiTypeAdapter.items = items
          multiTypeAdapter.notifyItemChanged(index)
          multiTypeAdapter.notifyItemRangeInserted(index + 1, temps.size)
          if (pb == items.last { a -> a is ProvinceBean }) viewBinding.expandRecycler.smoothScrollToPosition(items.size) //最后一条滚动到底部
        }
      }
    })
    multiTypeAdapter.register(StickyNormalItem().also { it.onItemClick = { bean -> bean.regionFullName.toast() } })
    multiTypeAdapter.register(DividerItem())
    viewBinding.expandRecycler.layoutManager = SpeedLinearLayoutManager(mContext)
    viewBinding.expandRecycler.adapter = multiTypeAdapter
    viewModel.cityLiveData.observe(this, MyObserver {
      if (it is DataState.SuccessRefresh) fillData(it.data ?: mutableListOf())
    })
    viewModel.countryLiveData.observe(this, MyObserver {
      if (it is DataState.SuccessRefresh) fillData(it.data ?: mutableListOf())
    })
    showLoadingView()
    //随机加载数据
    if (System.currentTimeMillis() % 2 == 0L) {
      viewModel.loadCountry(2000)
    } else {
      viewModel.loadProvince(2000)
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="填充数据">
  private fun fillData(list: MutableList<ProvinceBean>) {
    dismissLoadingView()
    val items = mutableListOf<Any>()
    list.forEach {
      items.add(it)
      items.add(DividerBean(heightPx = 1, bgColor = Color.RED))
    }
    multiTypeAdapter.items = items
    multiTypeAdapter.notifyDataSetChanged()
  }
  //</editor-fold>
}