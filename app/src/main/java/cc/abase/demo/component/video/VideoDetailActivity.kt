package cc.abase.demo.component.video

import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import cc.ab.base.ext.*
import cc.abase.demo.component.comm.CommBindActivity
import cc.abase.demo.constants.StringConstants
import cc.abase.demo.databinding.ActivityVideoDetailBinding
import cc.abase.demo.item.VideoJiItem
import cc.abase.demo.widget.decoration.GridItemDecoration
import cc.abase.demo.widget.dkplayer.MyVideoView
import cc.abase.demo.widget.dkplayer.pipfloat.PIPManager
import com.drakeet.multitype.MultiTypeAdapter
import com.gyf.immersionbar.ktx.immersionBar
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import xyz.doikki.videoplayer.player.VideoViewManager

/**
 * Description:
 * @author: Khaos
 * @date: 2019/11/2 9:51
 */
class VideoDetailActivity : CommBindActivity<ActivityVideoDetailBinding>() {
  //<editor-fold defaultstate="collapsed" desc="外部跳转">
  companion object {
    //视频地址
    private val moveUrlPairs = mutableListOf(
      //视频源 https://www.yugaopian.cn/allmovies + http://www.mtime.com/
      Pair("https://vfx.mtime.cn/Video/2021/04/04/mp4/210404085426055137.mp4", "黑寡妇预告片"),
      Pair("https://vfx.mtime.cn/Video/2021/06/07/mp4/210607160841583113.mp4", "繁花预告片"),
      //视频源 https://miao101.com/
      //Pair("https://v3.dious.cc/20210429/CnNHtUZs/index.m3u8", "不良人4(第一集)"),
      //Pair("https://v3.dious.cc/20210429/euEFOXZ2/index.m3u8", "不良人4(第二集)"),
      //Pair("https://v3.dious.cc/20210506/CXr0LMp8/index.m3u8", "不良人4(第三集)"),
      //Pair("https://v3.dious.cc/20210513/zodnofSz/index.m3u8", "不良人4(第四集)"),
      //Pair("https://v3.dious.cc/20210520/KJjHTrV9/index.m3u8", "不良人4(第五集)"),
      //Pair("https://v3.dious.cc/20210527/hq83F1Vq/index.m3u8", "不良人4(第六集)"),
      //Pair("https://v3.dious.cc/20210527/S8c0zImb/index.m3u8", "不良人4(第七集)"),
      //Pair("https://v3.dious.cc/20210603/a6JxY1mK/index.m3u8", "不良人4(第八集)"),
      //Pair("https://v3.dious.cc/20210610/a9B0XUbP/index.m3u8", "不良人4(第九集)"),
      //视频源 https://www.baogougou.net/
      Pair("https://vod3.buycar5.cn/20210429/XafBNy9D/index.m3u8", "不良人4(第一集)"),
      Pair("https://vod3.buycar5.cn/20210429/AiAwxTcl/index.m3u8", "不良人4(第二集)"),
      Pair("https://vod3.buycar5.cn/20210506/fIA1Vs8Y/index.m3u8", "不良人4(第三集)"),
      Pair("https://vod3.buycar5.cn/20210513/gfbKcZpL/index.m3u8", "不良人4(第四集)"),
      Pair("https://vod3.buycar5.cn/20210520/jj432kHf/index.m3u8", "不良人4(第五集)"),
      Pair("https://vod3.buycar5.cn/20210527/Eu6t4c5y/index.m3u8", "不良人4(第六集)"),
      Pair("https://vod3.buycar5.cn/20210527/K4CStLay/index.m3u8", "不良人4(第七集)"),
      Pair("https://vod3.buycar5.cn/20210603/treYkqfe/index.m3u8", "不良人4(第八集)"),
      Pair("https://v3.dious.cc/20210610/a9B0XUbP/index.m3u8", "不良人4(第九集)"),
      Pair("https://v3.dious.cc/20210617/3YEoWQnq/index.m3u8", "不良人4(第十集)"),
      Pair("https://v3.dious.cc/20210624/fzTZaa7z/index.m3u8", "不良人4(第十一集)"),
      Pair("https://v10.dious.cc/20210701/YjTZjO5D/index.m3u8", "不良人4(第十二集)"),
      Pair("https://v10.dious.cc/20210708/WV8p1Zgl/index.m3u8", "不良人4(第十三集)"),
    )
    private const val INTENT_KEY_VIDEO_URL = "INTENT_KEY_VIDEO_URL"
    fun startActivity(context: Context, videoUrl: String?) {
      val intent = Intent(context, VideoDetailActivity::class.java)
      if (!videoUrl.isNullOrBlank()) intent.putExtra(INTENT_KEY_VIDEO_URL, videoUrl)
      context.startActivity(intent)
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="是否显示默认状态栏占位">
  override fun showHolderStatusView() = false
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="状态栏颜色设置">
  override fun initStatus() {
    immersionBar { statusBarDarkFont(false) }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="变量">
  private var mPIPManager = PIPManager.getInstance()
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化View">
  override fun initView() {
    val videoDetailVideoView = VideoViewManager.instance().get(StringConstants.Tag.FLOAT_PLAY) as MyVideoView
    var url = intent.getStringExtra(INTENT_KEY_VIDEO_URL)
    if (url == null) {
      //多类型适配器
      val multiTypeAdapter = MultiTypeAdapter()
      multiTypeAdapter.register(VideoJiItem { p ->
        videoDetailVideoView.release()
        videoDetailVideoView.setPlayUrl(p.first, title = p.second, autoPlay = true)
      })
      viewBinding.videoDetailRecycler.layoutManager = GridLayoutManager(this, 4)
      viewBinding.videoDetailRecycler.addItemDecoration(GridItemDecoration(10.dp2px()))
      viewBinding.videoDetailRecycler.adapter = multiTypeAdapter
      val listDatas = moveUrlPairs.filter { p -> p.second.contains("不良人") }.also { l -> l.firstOrNull()?.let { ss -> url = ss.first } }
      multiTypeAdapter.items = listDatas
      //不是最后一集，播放完成后自动播放下一集
      videoDetailVideoView.mCallComplete = { u ->
        val index = listDatas.indexOfFirst { p -> p.first == u }
        if (index >= 0 && index < listDatas.size - 1) {
          videoDetailVideoView.release()
          videoDetailVideoView.setPlayUrl(listDatas[index + 1].first, title = listDatas[index + 1].second, autoPlay = true)
        }
      }
    }
    viewBinding.videoDetailBack.pressEffectAlpha()
    viewBinding.videoDetailFloat.pressEffectAlpha()
    viewBinding.videoDetailBack.click { onBackPressed() }
    viewBinding.videoDetailStatus.layoutParams.height = mStatusBarHeight
    viewBinding.videoDetailFloat.click {
      XXPermissions.with(this)
        .permission(Permission.SYSTEM_ALERT_WINDOW)
        .request(object : OnPermissionCallback {
          override fun onGranted(permissions: MutableList<String>, all: Boolean) {
            if (all) {
              mPIPManager.startFloatWindow()
              finish()
            }
          }

          override fun onDenied(permissions: MutableList<String>, never: Boolean) {
            // 如果是被永久拒绝就跳转到应用权限系统设置页面
            if (never) XXPermissions.startPermissionActivity(mActivity, permissions)
          }
        })
    }
    if (mPIPManager.isStartFloatWindow) {
      mPIPManager.stopFloatWindow()
    } else {
      mPIPManager.actClass = VideoDetailActivity::class.java
      url?.let { videoDetailVideoView.setPlayUrl(url = it, title = moveUrlPairs.firstOrNull { p -> p.first == it }?.second) }
    }
    viewBinding.videoDetailVideoViewParent.addView(videoDetailVideoView, ViewGroup.LayoutParams(-1, -1))
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="生命周期">
  override fun onDestroy() {
    mPIPManager.reset()
    super.onDestroy()
  }
  //</editor-fold>
}