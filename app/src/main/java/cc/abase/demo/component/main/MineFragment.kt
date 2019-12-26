package cc.abase.demo.component.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import cc.ab.base.ext.gone
import cc.ab.base.ext.toast
import cc.ab.base.ext.visible
import cc.abase.demo.R
import cc.abase.demo.component.chat.ChatActivity
import cc.abase.demo.component.comm.CommFragment
import cc.abase.demo.component.coordinator.CoordinatorActivity
import cc.abase.demo.component.drag.DragActivity
import cc.abase.demo.component.expand.EpoxyExpandActivity
import cc.abase.demo.component.ffmpeg.RxFFmpegActivity
import cc.abase.demo.component.playlist.PlayListActivity
import cc.abase.demo.component.playlist.PlayPagerActivity
import cc.abase.demo.component.spedit.SpeditActivity
import cc.abase.demo.component.sticky.StickyActivity
import cc.abase.demo.component.update.CcUpdateService
import cc.abase.demo.component.update.UpdateEnum
import cc.abase.demo.constants.EventKeys
import cc.abase.demo.epoxy.base.dividerItem
import cc.abase.demo.epoxy.item.simpleTextItem
import cc.abase.demo.mvrx.MvRxEpoxyController
import cc.abase.demo.repository.UserRepository
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.StringUtils
import com.jeremyliao.liveeventbus.LiveEventBus
import kotlinx.android.synthetic.main.fragment_mine.*
import java.util.*

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/9/30 18:13
 */
class MineFragment : CommFragment() {
  //APK下载地址
  private val apkUrk = "https://down8.xiazaidb.com/app/yingyongbianliang.apk"
  //菜单列表
  private val menuList = mutableListOf(
    Pair(StringUtils.getString(R.string.chat_title), ChatActivity::class.java),
    Pair(StringUtils.getString(R.string.ffmpeg_title), RxFFmpegActivity::class.java),
    Pair(StringUtils.getString(R.string.update_app), CcUpdateService::class.java),
    Pair(StringUtils.getString(R.string.title_sticky), StickyActivity::class.java),
    Pair(StringUtils.getString(R.string.title_drag), DragActivity::class.java),
    Pair(StringUtils.getString(R.string.title_spedit), SpeditActivity::class.java),
    Pair(StringUtils.getString(R.string.coordinator_refresh), CoordinatorActivity::class.java),
    Pair(StringUtils.getString(R.string.epoxy_expandable), EpoxyExpandActivity::class.java),
    Pair(StringUtils.getString(R.string.title_play_list), PlayListActivity::class.java),
    Pair(StringUtils.getString(R.string.title_play_pager), PlayPagerActivity::class.java)
  )
  //item文字颜色
  private var typeColor = ColorUtils.getColor(R.color.style_Primary)

  companion object {
    fun newInstance(): MineFragment {
      return MineFragment()
    }
  }

  override val contentLayout = R.layout.fragment_mine

  override fun initView(root: View?) {
    mineRecycler.setController(epoxyController)
  }

  @SuppressLint("CheckResult")
  override fun initData() {
    showLoadingView()
    mineRoot.gone()
    UserRepository.instance.myIntegral()
      .compose(lifecycleProvider.bindUntilEvent(Lifecycle.Event.ON_DESTROY))
      .subscribe { t1, t2 ->
        dismissLoadingView()
        mineRoot?.visible()
        if (t1 != null) {
          mineIntegral.text =
            String.format(StringUtils.getString(R.string.my_integral), t1.coinCount)
        } else if (t2 != null) {
          mineIntegral.text =
            String.format(StringUtils.getString(R.string.my_integral), 0)
          mContext.toast(t2.message)
        }
        epoxyController.data = menuList
      }
    val cls = Pair(UpdateEnum.START, 0f).javaClass
    LiveEventBus.get(EventKeys.UPDATE_PROGRESS, cls)
      .observe(this, Observer {
        when {
          it.first == UpdateEnum.START -> {
            Log.e("CASE", "APK开始下载")
          }
          it.first == UpdateEnum.DOWNLOADING -> {
            Log.e(
              "CASE", "APK下载进度：${String.format(Locale.getDefault(), "%.1f", it.second) + "%"}"
            )
          }
          it.first == UpdateEnum.SUCCESS -> {
            Log.e("CASE", "APK下载成功")
          }
          it.first == UpdateEnum.FAIL -> {
            Log.e("CASE", "APK下载失败")
          }
        }
      })
    mineScrollView.post {
      mineRecyclerParent.layoutParams.height = mineScrollView.height + SizeUtils.dp2px(70f)
    }
  }

  //epoxy
  private val epoxyController =
    MvRxEpoxyController<List<Pair<String, Class<out Any>>>> { list ->
      list.forEachIndexed { index, pair ->
        //内容
        simpleTextItem {
          id("type_$index")
          msg(pair.first)
          textColor(typeColor)
          gravity(Gravity.CENTER_VERTICAL)
          onItemClick {
            val second = pair.second.newInstance()
            if (second is Activity) {
              mActivity.startActivity(Intent(mContext, pair.second))
            } else if (second is CcUpdateService) {
              CcUpdateService.startIntent(apkUrk, showNotification = true)
            }
          }
        }
        //分割线
        dividerItem {
          id("line_$index")
        }
      }
    }
}