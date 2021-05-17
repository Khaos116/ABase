package cc.abase.demo.component.bottomsheet

import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import cc.ab.base.ext.*
import cc.abase.demo.R
import cc.abase.demo.bean.local.DividerBean
import cc.abase.demo.bean.local.SimpleTxtBean
import cc.abase.demo.component.comm.CommBindTitleActivity
import cc.abase.demo.databinding.ActivityBottomSheetBinding
import cc.abase.demo.databinding.LayoutBottomSheetBinding
import cc.abase.demo.item.DividerItem
import cc.abase.demo.item.SimpleTxtItem
import com.blankj.utilcode.util.ColorUtils
import com.drakeet.multitype.MultiTypeAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog

/**
 * @Description
 * @Author：CASE
 * @Date：2021/2/19
 * @Time：10:38
 */
class BottomSheetActivity : CommBindTitleActivity<ActivityBottomSheetBinding>() {
  //<editor-fold defaultstate="collapsed" desc="变量">
  //弹窗
  private var mBottomSheetDialog: BottomSheetDialog? = null

  //列表适配器
  private val mAdapter = MultiTypeAdapter()
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化View">
  override fun initContentView() {
    setTitleText(R.string.title_bottom_sheet.xmlToString())
    viewBinding.bottomSheetBtn.pressEffectAlpha()
    viewBinding.bottomSheetBtn.click {
      if (mBottomSheetDialog == null) initBottomSheet()
      if (mBottomSheetDialog?.isShowing == false) mBottomSheetDialog?.show()
    }
    mAdapter.register(SimpleTxtItem().also { it.onItemClick = { bean -> bean.txt.toast() } })
    mAdapter.register(DividerItem())
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化BottomSheet">
  private fun initBottomSheet() {
    //弹窗View
    val binding = LayoutBottomSheetBinding.inflate(layoutInflater)
    //设置内部数据
    val recycler = binding.bottomSheetRecycler
    recycler.layoutManager = LinearLayoutManager(mContext)
    recycler.adapter = mAdapter
    //item文字颜色
    val typeColor = ColorUtils.getColor(R.color.style_Primary)
    //转化为item需要的数据
    val items = mutableListOf<Any>()
    for (i in 1..50) {
      if (i > 1) items.add(DividerBean(heightPx = 1, bgColor = R.color.colorPrimary.xmlToColor()))
      items.add(SimpleTxtBean(txt = "这是第${i}条数据").also { stb ->
        stb.textColor = typeColor
        stb.gravity = Gravity.CENTER_VERTICAL
        stb.paddingBottomPx = 15.dp2px()
        stb.paddingTopPx = 15.dp2px()
      })
    }
    mAdapter.items = items
    mAdapter.notifyDataSetChanged()
    //宽度全屏
    mBottomSheetDialog = BottomSheetDialog(mContext, android.R.style.Theme_Material_DialogWhenLarge_NoActionBar).also { bsd ->
      //点击外部可关闭
      bsd.setCanceledOnTouchOutside(true)
      //设置弹窗的View
      bsd.setContentView(binding.root)
      //去除默认背景色
      bsd.window?.setBackgroundDrawable(ColorDrawable(R.color.black_70.xmlToColor()))
      //设置默认弹窗高度
      bsd.behavior.peekHeight = (mContentView.height * 3 / 5f).toInt()
    }
  }
  //</editor-fold>
}