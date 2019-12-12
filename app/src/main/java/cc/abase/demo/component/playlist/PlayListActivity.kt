package cc.abase.demo.component.playlist

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import cc.ab.base.ext.*
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommTitleActivity
import cc.abase.demo.component.playlist.viewmoel.PlayListState
import cc.abase.demo.component.playlist.viewmoel.PlayListViewModel
import cc.abase.demo.epoxy.base.loadMoreItem
import cc.abase.demo.epoxy.item.videoListItem
import cc.abase.demo.mvrx.MvRxEpoxyController
import cc.abase.demo.repository.bean.local.VideoBean
import cc.abase.demo.widget.video.controller.StandardVideoController
import cc.abase.demo.widget.video.view.ExoVideoView
import com.blankj.utilcode.util.StringUtils
import com.dueeeke.videocontroller.component.*
import com.dueeeke.videoplayer.player.VideoView
import kotlinx.android.synthetic.main.activity_play_list.playListRecycler
import kotlinx.android.synthetic.main.item_list_video.view.itemVideoContainer
import kotlinx.android.synthetic.main.item_list_video.view.itemVideoPrepareView

/**
 * Description:https://github.com/dueeeke/DKVideoPlayer
 * @author: caiyoufei
 * @date: 2019/12/12 11:32
 */
class PlayListActivity : CommTitleActivity() {

  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, PlayListActivity::class.java)
      context.startActivity(intent)
    }
  }

  private var mVideoView: VideoView<*>? = null
  private var mController: StandardVideoController? = null
  private var mErrorView: ErrorView? = null
  private var mCompleteView: CompleteView? = null
  private var mTitleView: TitleView? = null
  //当前播放的位置
  private var mCurPos = -1
  //上次播放的位置，用于页面切回来之后恢复播放
  private var mLastPos: Int = mCurPos
  //数据
  private var mVideoList: MutableList<VideoBean> = mutableListOf()

  //数据层
  private val viewModel: PlayListViewModel by lazy {
    PlayListViewModel()
  }

  override fun layoutResContentId() = R.layout.activity_play_list

  override fun onCreateBefore() {
    super.onCreateBefore()
    extKeepScreenOn()
  }

  override fun initContentView() {
    setTitleText(StringUtils.getString(R.string.title_play_list))
    //播放相关
    mVideoView = ExoVideoView(mContext)
    mVideoView?.setOnStateChangeListener(object : VideoView.SimpleOnStateChangeListener() {
      override fun onPlayStateChanged(playState: Int) {
        super.onPlayStateChanged(playState)
        //监听VideoViewManager释放，重置状态
        if (playState == VideoView.STATE_IDLE) {
          mVideoView?.removeParent()
          mLastPos = mCurPos
          mCurPos = -1
        }
      }
    })
    //播放控制器
    mController = StandardVideoController(mContext)
    mErrorView = ErrorView(mContext)
    mCompleteView = CompleteView(mContext)
    mTitleView = TitleView(mContext)
    mController?.addControlComponent(mErrorView)
    mController?.addControlComponent(mCompleteView)
    mController?.addControlComponent(mTitleView)
    mController?.addControlComponent(VodControlView(mContext))
    mController?.addControlComponent(GestureView(mContext))
    mController?.setEnableOrientation(true)
    mVideoView?.setVideoController(mController)
    //列表相关
    playListRecycler.setController(epoxyController)
    playListRecycler.addOnChildAttachStateChangeListener(object :
        RecyclerView.OnChildAttachStateChangeListener {
      override fun onChildViewDetachedFromWindow(view: View) {
        view.itemVideoContainer?.getChildAt(0)
            ?.let {
              if (it == mVideoView && mVideoView?.isFullScreen == false) {
                releaseVideoView()
              }
            }
      }

      override fun onChildViewAttachedToWindow(view: View) {
      }
    })
  }

  override fun initData() {
    viewModel.subscribe(this) {
      if (it.request.complete) {
        dismissLoadingView()
        epoxyController.data = it
      }
    }
    showLoadingView()
    viewModel.loadVideoList()
  }

  //epoxy
  private val epoxyController = MvRxEpoxyController<PlayListState> { state ->
    //记录数据，方便点击的时候计算位置，因为没有添加分割线，所以不需要处理播放位置
    mVideoList = state.videoList
    //添加视频item
    state.videoList.forEachIndexed { index, videoBean ->
      videoListItem {
        id("play_list_${videoBean.id}")
        videoBean(videoBean)
        onItemClick { mContext.toast(videoBean.title) }
        onContainerClick { startPlay(mVideoList.indexOf(it)) }
      }
    }
    //添加没有更多的item
    if (!state.hasMore) {
      loadMoreItem {
        id("play_list_more")
        fail(true)
        tipsText(StringUtils.getString(R.string.no_more_data))
      }
    }
  }

  override fun onPause() {
    super.onPause()
    releaseVideoView()
  }

  override fun onResume() {
    super.onResume()
    if (mLastPos == -1) return
    //恢复上次播放的位置
    startPlay(mLastPos)
  }

  //开始播放
  private fun startPlay(position: Int) {
    if (mCurPos == position) return
    if (mCurPos != -1) releaseVideoView()
    val videoBean = mVideoList[position]
    mVideoView?.setUrl(videoBean.url)
    playListRecycler.layoutManager?.findViewByPosition(position)
        ?.let {
          //把列表中预置的PrepareView添加到控制器中，注意isPrivate此处只能为true
          mController?.addControlComponent(it.itemVideoPrepareView, true)
          mVideoView?.removeParent()
          it.itemVideoContainer.addView(mVideoView, 0)
          mVideoView?.start()
          mCurPos = position
        }
  }

  //释放播放
  private fun releaseVideoView() {
    mVideoView?.let {
      it.release()
      if (it.isFullScreen) it.stopFullScreen()
      if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
      }
      mCurPos = -1
    }
  }
}