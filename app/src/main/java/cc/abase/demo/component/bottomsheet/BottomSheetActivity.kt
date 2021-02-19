package cc.abase.demo.component.bottomsheet

import android.view.Gravity
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import cc.ab.base.ext.*
import cc.abase.demo.R
import cc.abase.demo.bean.local.DividerBean
import cc.abase.demo.bean.local.SimpleTxtBean
import cc.abase.demo.component.comm.CommTitleActivity
import cc.abase.demo.item.DividerItem
import cc.abase.demo.item.SimpleTxtItem
import com.blankj.utilcode.util.ColorUtils
import com.drakeet.multitype.MultiTypeAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_bottom_sheet.bottomSheetBtn
import kotlinx.android.synthetic.main.layout_bottom_sheet.view.bottomSheetRecycler
import kotlin.math.roundToInt

/**
 * @Description
 * @Author：CASE
 * @Date：2021/2/19
 * @Time：10:38
 */
class BottomSheetActivity : CommTitleActivity() {
  //<editor-fold defaultstate="collapsed" desc="变量">
  //弹窗
  private var mBottomSheetDialog: BottomSheetDialog? = null

  //列表适配器
  private val mAdapter = MultiTypeAdapter()
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResContentId() = R.layout.activity_bottom_sheet
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化View">
  override fun initContentView() {
    setTitleText(R.string.title_bottom_sheet.xmlToString())
    bottomSheetBtn.pressEffectAlpha()
    bottomSheetBtn.click {
      if (mBottomSheetDialog == null) initBottomSheet()
      if (mBottomSheetDialog?.isShowing == false) mBottomSheetDialog?.show()
    }
    mAdapter.register(SimpleTxtItem { it.txt.toast() })
    mAdapter.register(DividerItem())
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化Data">
  override fun initData() {
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化BottomSheet">
  private fun initBottomSheet() {
    //弹窗View
    val viewBottomSheet = mContext.inflate(R.layout.layout_bottom_sheet)
    //设置内部数据
    val recycler = viewBottomSheet.bottomSheetRecycler
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
        stb.paddingBottomPx = 15.dp2Px()
        stb.paddingTopPx = 15.dp2Px()
      })
    }
    mAdapter.items = items
    mAdapter.notifyDataSetChanged()
    //宽度全屏
    mBottomSheetDialog = BottomSheetDialog(mContext)
    //设置弹窗的View
    mBottomSheetDialog?.setContentView(viewBottomSheet)
    //去除默认背景色
    (viewBottomSheet.parent as? View)?.setBackgroundResource(android.R.color.transparent)
    //设置默认弹窗高度
    BottomSheetBehavior.from<View>(viewBottomSheet.parent as View).peekHeight = getPeekHeight()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="设置弹窗内容默认高度">
  //弹窗高度，默认为屏幕高度的四分之三
  private fun getPeekHeight(): Int {
    val peekHeight = resources.displayMetrics.heightPixels
    //设置弹窗高度为屏幕高度的3/4
    return (peekHeight * 3f / 4).roundToInt()
  }
  //</editor-fold>
}