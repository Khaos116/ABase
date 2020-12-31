package cc.abase.demo.component.playlist

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import cc.ab.base.ext.*
import cc.abase.demo.R
import cc.abase.demo.bean.local.VideoBean
import cc.abase.demo.component.comm.CommTitleActivity
import cc.abase.demo.component.playlist.viewmoel.PlayListState
import cc.abase.demo.component.playlist.viewmoel.PlayListViewModel
import cc.abase.demo.epoxy.base.loadMoreItem
import cc.abase.demo.epoxy.item.videoListItem
import cc.abase.demo.mvrx.MvRxEpoxyController
import cc.abase.demo.widget.dkplayer.MyVideoView
import com.blankj.utilcode.util.StringUtils
import kotlinx.android.synthetic.main.activity_play_list.playListRecycler
import kotlinx.android.synthetic.main.item_list_video.view.itemVideoContainer

/**
 * Description:https://github.com/dueeeke/DKVideoPlayer
 * @author: CASE
 * @date: 2019/12/12 11:32
 */
class PlayListActivity : CommTitleActivity() {

  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, PlayListActivity::class.java)
      context.startActivity(intent)
    }
  }

  private var mVideoView: MyVideoView? = null

  //当前播放的位置
  private var mCurPos = -1

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
    mVideoView = MyVideoView(mContext)
    mVideoView?.setInList(true)
    //列表相关
    playListRecycler.setController(epoxyController)
    playListRecycler.addOnChildAttachStateChangeListener(object : RecyclerView.OnChildAttachStateChangeListener {
      override fun onChildViewDetachedFromWindow(view: View) { //非全屏滑出去释放掉
        view.itemVideoContainer?.getChildAt(0)?.let { if (it == mVideoView && mVideoView?.isFullScreen == false) releaseVideoView() }
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
    viewModel.loadData()
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
        onItemPlayClick { startPlay(mVideoList.indexOf(it)) }
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

  //开始播放
  private fun startPlay(position: Int) {
    if (mCurPos == position) return
    if (mCurPos != -1) releaseVideoView()
    playListRecycler.layoutManager?.findViewByPosition(position)
        ?.let {
          mVideoView?.removeParent()
          it.itemVideoContainer.addView(mVideoView)
          val videoBean = mVideoList[position]
          mVideoView?.setPlayUrl(url = videoBean.url ?: "", autoPlay = true, needHolder = false)
          mCurPos = position
        }
  }

  //释放播放
  private fun releaseVideoView() {
    mVideoView?.let {
      it.release()
      it.removeParent()
      mCurPos = -1
    }
  }
}