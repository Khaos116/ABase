package cc.abase.demo.component.main.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.view.Gravity
import androidx.lifecycle.rxLifeScope
import androidx.recyclerview.widget.LinearLayoutManager
import cc.ab.base.ext.*
import cc.abase.demo.R
import cc.abase.demo.bean.local.SimpleTxtBean
import cc.abase.demo.component.blur.BlurActivity
import cc.abase.demo.component.bottomsheet.BottomSheetActivity
import cc.abase.demo.component.chat.ChatActivity
import cc.abase.demo.component.comm.CommFragment
import cc.abase.demo.component.coordinator.CoordinatorActivity
import cc.abase.demo.component.count.CountActivity
import cc.abase.demo.component.decoration.DecorationActivity
import cc.abase.demo.component.drag.DragActivity
import cc.abase.demo.component.expand.ExpandActivity
import cc.abase.demo.component.flexbox.FlexboxActivity
import cc.abase.demo.component.locker.PatternLockerActivity
import cc.abase.demo.component.marquee.MarqueeActivity
import cc.abase.demo.component.playlist.PlayListActivity
import cc.abase.demo.component.playlist.VerticalPagerActivity
import cc.abase.demo.component.recyclerpage.RecyclerPagerActivity
import cc.abase.demo.component.set.SettingActivity
import cc.abase.demo.component.spedit.SpeditActivity
import cc.abase.demo.component.sticky.StickyActivity
import cc.abase.demo.component.sticky.StickyActivity2
import cc.abase.demo.component.test.TestActivity
import cc.abase.demo.component.update.CcUpdateService
import cc.abase.demo.component.update.UpdateEnum
import cc.abase.demo.component.video.VideoCompressActivity
import cc.abase.demo.component.zxing.ZxingActivity
import cc.abase.demo.constants.EventKeys
import cc.abase.demo.item.SimpleTxtItem
import cc.abase.demo.rxhttp.repository.UserRepository
import cc.abase.demo.widget.decoration.SpacesItemDecoration
import cc.abase.demo.widget.dialog.dateSelDialog
import com.blankj.utilcode.util.ColorUtils
import com.drakeet.multitype.MultiTypeAdapter
import com.jeremyliao.liveeventbus.LiveEventBus
import kotlinx.android.synthetic.main.fragment_mine.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

/**
 * Description:
 * @author: CASE
 * @date: 2019/9/30 18:13
 */
class MineFragment : CommFragment() {
  //<editor-fold defaultstate="collapsed" desc="外部获取实例">
  companion object {
    fun newInstance() = MineFragment()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="变量">
  //APK下载地址
  private val apkUrk = "https://down8.xiazaidb.com/app/yingyongbianliang.apk"
  private val apkUrk2 = "https://ftp.binance.com/pack/Binance.apk"

  //点击更新次数
  private var clickCount = 0

  //上次选中的日期
  private var lastYMD: String? = null

  //多类型适配器
  private val multiTypeAdapter = MultiTypeAdapter()

  //菜单列表
  private val menuList = mutableListOf(
      Pair(R.string.chat_title.xmlToString(), ChatActivity::class.java),
      Pair(R.string.video_compress_title.xmlToString(), VideoCompressActivity::class.java),
      Pair(R.string.update_app.xmlToString(), CcUpdateService::class.java),
      Pair(R.string.title_sticky.xmlToString(), StickyActivity::class.java),
      Pair(R.string.title_sticky2.xmlToString(), StickyActivity2::class.java),
      Pair(R.string.title_drag.xmlToString(), DragActivity::class.java),
      Pair(R.string.title_spedit.xmlToString(), SpeditActivity::class.java),
      Pair(R.string.coordinator_refresh.xmlToString(), CoordinatorActivity::class.java),
      Pair(R.string.expandable.xmlToString(), ExpandActivity::class.java),
      Pair(R.string.title_play_list.xmlToString(), PlayListActivity::class.java),
      Pair(R.string.title_play_pager.xmlToString(), VerticalPagerActivity::class.java),
      Pair(R.string.title_vertical_page.xmlToString(), RecyclerPagerActivity::class.java),
      Pair(R.string.title_decoration.xmlToString(), DecorationActivity::class.java),
      Pair(R.string.title_flexbox.xmlToString(), FlexboxActivity::class.java),
      Pair(R.string.title_marquee.xmlToString(), MarqueeActivity::class.java),
      Pair(R.string.title_blur.xmlToString(), BlurActivity::class.java),
      Pair(R.string.title_zxing.xmlToString(), ZxingActivity::class.java),
      Pair(R.string.title_pattern_locker.xmlToString(), PatternLockerActivity::class.java),
      Pair(R.string.title_count.xmlToString(), CountActivity::class.java),
      Pair(R.string.title_bottom_sheet.xmlToString(), BottomSheetActivity::class.java),
      Pair(R.string.title_test.xmlToString(), TestActivity::class.java),
  )
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="XML">
  override val contentXmlId = R.layout.fragment_mine
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="懒加载">
  override fun lazyInit() {
    mRootView?.setBackgroundColor(Color.WHITE)
    //分割线
    if (mineRecycler.itemDecorationCount == 0) mineRecycler.addItemDecoration(SpacesItemDecoration())
    //注册多类型
    multiTypeAdapter.register(SimpleTxtItem() { stb ->
      stb.cls?.let { cls ->
        val second = cls.newInstance()
        if (second is Activity) {
          mActivity.startActivity(Intent(mContext, cls))
        } else if (second is CcUpdateService) {
          clickCount++
          CcUpdateService.startIntent(
              path = if (clickCount % 2 == 0) apkUrk else apkUrk2,
              apk_name = if (clickCount % 2 == 0) "应用变量" else "币安",
              showNotification = true
          )
        }
      }
    })
    //设置适配器
    mineRecycler.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
    mineRecycler.adapter = multiTypeAdapter
    showLoadingView()
    mineRoot.gone()
    mineSetting.pressEffectAlpha()
    mineSetting.click { SettingActivity.startActivity(mContext) }
    //item文字颜色
    val typeColor = ColorUtils.getColor(R.color.style_Primary)
    //转化为item需要的数据
    val items = mutableListOf<SimpleTxtBean>()
    menuList.forEach { p ->
      items.add(SimpleTxtBean(txt = p.first).also { stb ->
        stb.cls = p.second
        stb.textColor = typeColor
        stb.gravity = Gravity.CENTER_VERTICAL
        stb.paddingBottomPx = 15.dp2Px()
        stb.paddingTopPx = 15.dp2Px()
      })
    }
    //获取积分
    rxLifeScope.launch({
      withContext(Dispatchers.IO) { UserRepository.myIntegral() }.let {
        mineIntegral.text = String.format(R.string.my_integral.xmlToString(), it.coinCount)
        multiTypeAdapter.items = items
        multiTypeAdapter.notifyDataSetChanged()
      }
    }, { e ->
      e.toast()
      mineIntegral.text = String.format(R.string.my_integral.xmlToString(), 0)
      multiTypeAdapter.items = items
      multiTypeAdapter.notifyDataSetChanged()
    }, {}, {
      dismissLoadingView()
      mineRoot?.visible()
    })
    //监听加载进度
    LiveEventBus.get(EventKeys.UPDATE_PROGRESS, Triple(UpdateEnum.START, 0f, "").javaClass).observe(this) {
      when (it.first) {
        UpdateEnum.START -> "APK开始下载".logE()
        UpdateEnum.DOWNLOADING -> "APK下载进度：${String.format(Locale.getDefault(), "%.1f", it.second) + "%"}"
        UpdateEnum.SUCCESS -> "APK下载成功".logE()
        UpdateEnum.FAIL -> "APK下载失败".logE()
      }
    }
    //高度比ScrollView高一个背景的高度
    mineScrollView.post { mineRecyclerParent.layoutParams.height = mineScrollView.height + 70.dp2Px() }
    //点击积分打开日期
    mineIntegral.click {
      dateSelDialog(childFragmentManager, lastYMD) {
        call = { r ->
          lastYMD = "${r.first}-${r.second}-${r.third}"
          "${r.first}年 ${r.second}月${r.third}日".toast()
        }
      }
    }
  }
  //</editor-fold>
}