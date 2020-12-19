package cc.abase.demo.component.sticky

import android.content.Context
import android.content.Intent
import cc.ab.base.ext.mContext
import cc.ab.base.ext.toast
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommTitleActivity
import cc.abase.demo.component.sticky.adapter.StickyHeaderAdapter
import cc.abase.demo.component.sticky.viewmodel.StickyViewModel
import cc.abase.demo.component.sticky.widget.StickyHeaderLinearLayoutManager
import cc.abase.demo.constants.EventKeys
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

  //数据层
  private val viewModel: StickyViewModel by lazy {
    StickyViewModel()
  }

  //是否需要选择
  private var needChoose = false
  //那个页面需要选择的标签
  private var mTag = ""
  //适配器
  private var adapter: StickyHeaderAdapter? = null

  override fun layoutResContentId() = R.layout.activity_sticky

  override fun initContentView() {
    setTitleText(StringUtils.getString(R.string.title_sticky))
    val manager = StickyHeaderLinearLayoutManager<StickyHeaderAdapter>(this)
    stickyRecycler.layoutManager = manager
    showLoadingView()
    stickyBar.setOnTouchingLetterChangedListener { tag ->
      adapter?.let { ad ->
        val position = ad.getTagPosition(tag)
        if (position >= 0) manager.scrollToPositionWithOffset(position, 0)
      }
    }
  }

  override fun initData() {
    needChoose = intent.getBooleanExtra(INTENT_KEY_CHOOSE, false)
    mTag = intent.getStringExtra(INTENT_KEY_TAG) ?: ""
    viewModel.subscribe { state ->
      if (state.request.complete) {
        dismissLoadingView()
        if (adapter == null) {
          adapter = StickyHeaderAdapter(state.provinces,
            onProvinceClick = { mContext.toast(it.regionName) },
            onCityClick = {
              if (needChoose) {
                it.fromTag = mTag
                LiveEventBus.get(EventKeys.CHOOSE_STICKY).post(it)
                finish()
              } else {
                mContext.toast(it.regionFullName)
              }
            })
          stickyRecycler.adapter = adapter
        }
      }
    }
    //随机加载数据
    if (System.currentTimeMillis() % 2 == 0L) {
      viewModel.loadCountry()
    } else {
      viewModel.loadProvince()
    }
  }
}