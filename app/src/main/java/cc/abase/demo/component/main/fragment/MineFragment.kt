package cc.abase.demo.component.main.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.view.Gravity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import cc.ab.base.ext.*
import cc.abase.demo.R
import cc.abase.demo.bean.local.SimpleTxtBean
import cc.abase.demo.component.blur.BlurActivity
import cc.abase.demo.component.bottomsheet.BottomSheetActivity
import cc.abase.demo.component.calendar.CalendarFragment
import cc.abase.demo.component.chat.ChatActivity
import cc.abase.demo.component.coil.CoilFragment
import cc.abase.demo.component.comm.CommBindFragment
import cc.abase.demo.component.comm.FragmentParentActivity
import cc.abase.demo.component.coordinator.CoordinatorActivity
import cc.abase.demo.component.count.CountActivity
import cc.abase.demo.component.decoration.DecorationActivity
import cc.abase.demo.component.drag.DragActivity
import cc.abase.demo.component.expand.ExpandActivity
import cc.abase.demo.component.flexbox.FlexboxActivity
import cc.abase.demo.component.js.JSFragment
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
import cc.abase.demo.component.verification.VerificationFragment
import cc.abase.demo.component.video.VideoCompressActivity
import cc.abase.demo.component.web.HtmlFragment
import cc.abase.demo.component.zxing.ZxingActivity
import cc.abase.demo.constants.EventKeys
import cc.abase.demo.databinding.FragmentMineBinding
import cc.abase.demo.item.SimpleTxtItem
import cc.abase.demo.rxhttp.repository.UserRepository
import cc.abase.demo.widget.decoration.SpacesItemDecoration
import cc.abase.demo.widget.dialog.dateSelDialog
import com.blankj.utilcode.util.ColorUtils
import com.drakeet.multitype.MultiTypeAdapter
import com.jeremyliao.liveeventbus.LiveEventBus
import kotlinx.coroutines.launch
import rxhttp.awaitResult
import rxhttp.delay
import java.util.*

/**
 * Description:
 * @author: Khaos
 * @date: 2019/9/30 18:13
 */
class MineFragment : CommBindFragment<FragmentMineBinding>() {
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
    Pair(R.string.聊天模拟.xmlToString(), ChatActivity::class.java),
    Pair(R.string.视频压缩与封面获取.xmlToString(), VideoCompressActivity::class.java),
    Pair(R.string.APP更新.xmlToString(), CcUpdateService::class.java),
    Pair(R.string.Sticky吸顶效果.xmlToString(), StickyActivity::class.java),
    Pair(R.string.学生成绩单效果.xmlToString(), StickyActivity2::class.java),
    Pair(R.string.九宫格拖拽效果.xmlToString(), DragActivity::class.java),
    Pair(R.string.AT展示效果.xmlToString(), SpeditActivity::class.java),
    Pair(R.string.Coordinator嵌套下拉刷新.xmlToString(), CoordinatorActivity::class.java),
    Pair(R.string.列表收缩展开效果.xmlToString(), ExpandActivity::class.java),
    Pair(R.string.列表中视频播放.xmlToString(), PlayListActivity::class.java),
    Pair(R.string.VerticalViewPager仿抖音.xmlToString(), VerticalPagerActivity::class.java),
    Pair(R.string.RecyclerView仿抖音.xmlToString(), RecyclerPagerActivity::class.java),
    Pair(R.string.列表分割线效果.xmlToString(), DecorationActivity::class.java),
    Pair(R.string.FlexboxLayout.xmlToString(), FlexboxActivity::class.java),
    Pair(R.string.跑马灯.xmlToString(), MarqueeActivity::class.java),
    Pair(R.string.高斯模糊.xmlToString(), BlurActivity::class.java),
    Pair(R.string.Zxing扫码.xmlToString(), ZxingActivity::class.java),
    Pair(R.string.手势解锁.xmlToString(), PatternLockerActivity::class.java),
    Pair(R.string.统计信息展示.xmlToString(), CountActivity::class.java),
    Pair(R.string.BottomSheetDialog.xmlToString(), BottomSheetActivity::class.java),
    Pair(R.string.Coil特殊图片加载.xmlToString(), CoilFragment::class.java),
    Pair(R.string.滑动验证码.xmlToString(), VerificationFragment::class.java),
    Pair(R.string.Html代码加载.xmlToString(), HtmlFragment::class.java),
    Pair(R.string.JS交互调用.xmlToString(), JSFragment::class.java),
    Pair(R.string.日历选择.xmlToString(), CalendarFragment::class.java),
    Pair(R.string.测试专用页面.xmlToString(), TestActivity::class.java),
  )
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="懒加载">
  @SuppressLint("NotifyDataSetChanged")
  @Suppress("UNCHECKED_CAST")
  override fun lazyInit() {
    mRootLayout?.setBackgroundColor(Color.WHITE)
    //分割线
    if (viewBinding.mineRecycler.itemDecorationCount == 0) viewBinding.mineRecycler.addItemDecoration(SpacesItemDecoration())
    //注册多类型
    multiTypeAdapter.register(SimpleTxtItem().also {
      it.onItemClick = { stb ->
        stb.cls?.let { cls ->
          when (val second = cls.newInstance()) {
            is Activity -> {
              mActivity.startActivity(Intent(mContext, cls))
            }

            is CcUpdateService -> {
              clickCount++
              CcUpdateService.startIntent(
                path = if (clickCount % 2 == 0) apkUrk else apkUrk2,
                apk_name = if (clickCount % 2 == 0) "应用变量" else "币安",
                showNotification = true
              )
            }

            is Fragment -> {
              FragmentParentActivity.startFragment(mContext, second.javaClass)
            }
          }
        }
      }
    })
    //设置适配器
    viewBinding.mineRecycler.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
    viewBinding.mineRecycler.adapter = multiTypeAdapter
    showLoadingView()
    viewBinding.root.gone()
    viewBinding.mineSetting.pressEffectAlpha()
    viewBinding.mineSetting.click { SettingActivity.startActivity(mContext) }
    //item文字颜色
    val typeColor = ColorUtils.getColor(cc.ab.base.R.color.style_Primary)
    //转化为item需要的数据
    val items = mutableListOf<SimpleTxtBean>()
    menuList.forEach { p ->
      items.add(SimpleTxtBean(txt = p.first).also { stb ->
        stb.cls = p.second
        stb.textColor = typeColor
        stb.gravity = Gravity.CENTER_VERTICAL
        stb.paddingBottomPx = 15.dp2px()
        stb.paddingTopPx = 15.dp2px()
      })
    }
    //获取积分
    lifecycleScope.launch {
      val b1 = UserRepository.myIntegral(true).await()
      if (b1.coinCount >= 0) {//读取缓存成功
        viewBinding.mineIntegral.text = String.format(R.string.我的积分.xmlToString(), b1.coinCount)
        multiTypeAdapter.items = items
        multiTypeAdapter.notifyDataSetChanged()
        dismissLoadingView()
        viewBinding.root.visible()
      }
      UserRepository.myIntegral(false)//更新数据
        .delay(3000)
        .awaitResult { b2 ->
          if (b1.coinCount < 0) {//没有缓存，请求完成也要显示结果
            multiTypeAdapter.items = items
            multiTypeAdapter.notifyDataSetChanged()
            dismissLoadingView()
            viewBinding.root.visible()
          }
          if (b2.coinCount >= 0) {//拿到最新积分
            viewBinding.mineIntegral.text = String.format(R.string.我的积分.xmlToString(), b2.coinCount)
          } else if (b1.coinCount < 0) {//没有请求成功，也没有拿到缓存
            viewBinding.mineIntegral.text = String.format(R.string.我的积分.xmlToString(), "0")
          }
        }
    }
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
    viewBinding.mineScrollView.post { viewBinding.mineRecyclerParent.layoutParams.height = viewBinding.mineScrollView.height + 70.dp2px() }
    //点击积分打开日期
    viewBinding.mineIntegral.click {
      dateSelDialog(childFragmentManager, lastYMD) {
        call = { r ->
          lastYMD = "${r.first}-${r.second}-${r.third}"
          String.format(R.string.年月日.xmlToString(), r.first, r.second, r.third).toast()
        }
      }
    }
  }
  //</editor-fold>
}