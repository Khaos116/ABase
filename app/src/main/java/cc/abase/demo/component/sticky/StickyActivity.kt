package cc.abase.demo.component.sticky

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import cc.ab.base.ext.*
import cc.ab.base.ui.viewmodel.DataState
import cc.abase.demo.R
import cc.abase.demo.bean.local.DividerBean
import cc.abase.demo.bean.local.ProvinceBean
import cc.abase.demo.component.comm.CommBindTitleActivity
import cc.abase.demo.component.sticky.viewmodel.StickyViewModel
import cc.abase.demo.constants.EventKeys
import cc.abase.demo.databinding.ActivityStickyBinding
import cc.abase.demo.item.*
import cc.abase.demo.sticky.StickyAnyAdapter
import cc.abase.demo.sticky.StickyHeaderLinearLayoutManager
import com.blankj.utilcode.util.StringUtils
import com.dlong.rep.dlsidebar.DLSideBar
import com.jeremyliao.liveeventbus.LiveEventBus

/**
 * Description:
 * @author: CASE
 * @date: 2019/11/26 16:54
 */
class StickyActivity : CommBindTitleActivity<ActivityStickyBinding>() {
  //<editor-fold defaultstate="collapsed" desc="外部跳转">
  companion object {
    private const val INTENT_KEY_CHOOSE = "INTENT_KEY_CHOOSE"
    private const val INTENT_KEY_TAG = "INTENT_KEY_TAG"
    fun startActivity(context: Context, needChoose: Boolean = false) {
      val intent = Intent(context, StickyActivity::class.java)
      intent.putExtra(INTENT_KEY_CHOOSE, needChoose)
      intent.putExtra(INTENT_KEY_TAG, context::class.java.name)
      context.startActivity(intent)
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="变量">
  //数据层
  private val viewModel: StickyViewModel by lazy { StickyViewModel() }

  //是否需要选择
  private var needChoose = false

  //那个页面需要选择的标签
  private var mTag = ""

  //Sticky悬浮背景色
  private var topBg = Color.parseColor("#e0e0e0")

  //适配器
  private var multiTypeAdapter: StickyAnyAdapter = object : StickyAnyAdapter(stickyBgColor = topBg, noStickyBgColor = topBg) {
    override fun isStickyHeader(position: Int) = items[position] is ProvinceBean
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun loadViewBinding(inflater: LayoutInflater) = ActivityStickyBinding.inflate(inflater)
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化View">
  override fun initContentView() {
    setTitleText(StringUtils.getString(R.string.title_sticky))
    val manager = StickyHeaderLinearLayoutManager<StickyAnyAdapter>(mActivity, LinearLayoutManager.VERTICAL, false)
    viewBinding.stickyRecycler.layoutManager = manager
    //由于自定义的DLSideBar里面dialog存在ViewBinding无法直接调用的异常(XML里面无法加载)，所以改为动态添加
    val viewBar = LayoutInflater.from(mContext).inflate(R.layout.merge_side_bar, viewBinding.root, true)
    viewBar.findViewById<DLSideBar>(R.id.stickyBar).setOnTouchingLetterChangedListener { tag ->
      val position = multiTypeAdapter.items.indexOfFirst { c -> c is ProvinceBean && c.pinYinFirst?.substring(0, 1) == tag }
      if (position >= 0) manager.scrollToPositionWithOffset(position, 0)
    }
    //注册多类型
    multiTypeAdapter.register(StickyTopItem().also {
      it.onItemClick = { bean ->
        bean.regionName?.toast()
      }
    })
    multiTypeAdapter.register(StickyNormalItem().also {
      it.onItemClick = { bean ->
        if (needChoose) {
          bean.fromTag = mTag
          LiveEventBus.get(EventKeys.CHOOSE_STICKY).post(bean)
          finish()
        } else bean.regionFullName?.toast()
      }
    })
    multiTypeAdapter.register(DividerItem())
    viewBinding.stickyRecycler.adapter = multiTypeAdapter
    needChoose = intent.getBooleanExtra(INTENT_KEY_CHOOSE, false)
    mTag = intent.getStringExtra(INTENT_KEY_TAG) ?: ""
    viewModel.countryLiveData.observe(this, observer)
    viewModel.cityLiveData.observe(this, observer)
    //随机加载数据
    if (System.currentTimeMillis() % 2 == 0L) {
      viewModel.loadCountry()
    } else {
      viewModel.loadProvince()
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="监听加载结果">
  private val observer = Observer<DataState<MutableList<ProvinceBean>>?> {
    when (it) {
      is DataState.SuccessRefresh -> {
        val items = mutableListOf<Any>()
        it.data?.forEach { p ->
          items.add(p)
          val cSize = p.cmsRegionDtoList?.size ?: 0
          p.cmsRegionDtoList?.forEachIndexed { index, cityBean ->
            items.add(cityBean)
            if (index < cSize - 1) items.add(DividerBean(heightPx = 1))
          }
        }
        multiTypeAdapter.items = items
        multiTypeAdapter.notifyDataSetChanged()
      }
      is DataState.Start -> {
        showLoadingView()
      }
      is DataState.Complete -> {
        dismissLoadingView()
      }
      else -> {
      }
    }
  }
  //</editor-fold>
}