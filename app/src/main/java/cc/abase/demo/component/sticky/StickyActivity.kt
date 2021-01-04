package cc.abase.demo.component.sticky

import android.content.Context
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import cc.ab.base.ext.mActivity
import cc.ab.base.ext.toast
import cc.ab.base.ui.viewmodel.DataState
import cc.abase.demo.R
import cc.abase.demo.bean.local.DividerBean
import cc.abase.demo.bean.local.ProvinceBean
import cc.abase.demo.component.comm.CommTitleActivity
import cc.abase.demo.component.sticky.viewmodel.StickyViewModel2
import cc.abase.demo.constants.EventKeys
import cc.abase.demo.item.*
import cc.abase.demo.sticky.StickyAnyAdapter
import cc.abase.demo.sticky.StickyHeaderLinearLayoutManager
import com.blankj.utilcode.util.StringUtils
import com.jeremyliao.liveeventbus.LiveEventBus
import kotlinx.android.synthetic.main.activity_sticky.stickyBar
import kotlinx.android.synthetic.main.activity_sticky.stickyRecycler

/**
 * Description:
 * @author: CASE
 * @date: 2019/11/26 16:54
 */
class StickyActivity : CommTitleActivity() {
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
  private val viewModel: StickyViewModel2 by lazy { StickyViewModel2() }

  //是否需要选择
  private var needChoose = false

  //那个页面需要选择的标签
  private var mTag = ""

  //适配器
  private var multiTypeAdapter: StickyAnyAdapter = object : StickyAnyAdapter() {
    override fun isStickyHeader(position: Int): Boolean {
      return items[position] is ProvinceBean
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResContentId() = R.layout.activity_sticky
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化View">
  override fun initContentView() {
    setTitleText(StringUtils.getString(R.string.title_sticky))
    val manager = StickyHeaderLinearLayoutManager<StickyAnyAdapter>(mActivity, LinearLayoutManager.VERTICAL, false)
    stickyRecycler.layoutManager = manager
    stickyBar.setOnTouchingLetterChangedListener { tag ->
      val position = multiTypeAdapter.items.indexOfFirst { c -> c is ProvinceBean && c.pinYinFirst?.substring(0, 1) == tag }
      if (position >= 0) manager.scrollToPositionWithOffset(position, 0)
    }
    //注册多类型
    multiTypeAdapter.register(StickyTopItem {
      it.regionName?.toast()
    })
    multiTypeAdapter.register(StickyNormalItem {
      if (needChoose) {
        it.fromTag = mTag
        LiveEventBus.get(EventKeys.CHOOSE_STICKY).post(it)
        finish()
      } else it.regionFullName?.toast()
    })
    multiTypeAdapter.register(DividerItem())
    stickyRecycler.adapter = multiTypeAdapter
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化Data">
  override fun initData() {
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