package cc.abase.demo.component.playlist

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cc.ab.base.ext.mContext
import cc.ab.base.ext.removeParent
import cc.ab.base.ui.viewmodel.DataState
import cc.ab.base.widget.livedata.MyObserver
import cc.abase.demo.R
import cc.abase.demo.bean.local.NoMoreBean
import cc.abase.demo.bean.local.VideoBean
import cc.abase.demo.component.comm.CommBindTitleActivity
import cc.abase.demo.component.playlist.viewmoel.PlayListViewModel
import cc.abase.demo.databinding.ActivityPlayListBinding
import cc.abase.demo.databinding.ItemListVideoBinding
import cc.abase.demo.item.NoMoreItem
import cc.abase.demo.item.VideoListItem
import cc.abase.demo.widget.dkplayer.MyVideoView
import com.blankj.utilcode.util.StringUtils
import com.drakeet.multitype.MultiTypeAdapter

/**
 * Description:https://github.com/dueeeke/DKVideoPlayer
 * @author: Khaos
 * @date: 2019/12/12 11:32
 */
class PlayListActivity : CommBindTitleActivity<ActivityPlayListBinding>() {
  //<editor-fold defaultstate="collapsed" desc="外部跳转">
  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, PlayListActivity::class.java)
      context.startActivity(intent)
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="变量">
  //播放器
  private var mVideoView: MyVideoView? = null

  //当前播放的位置
  private var mCurPos = -1

  //数据层
  private val viewModel: PlayListViewModel by lazy { PlayListViewModel() }

  //适配器
  private val multiTypeAdapter = MultiTypeAdapter()
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化View">
  @SuppressLint("NotifyDataSetChanged")
  override fun initContentView() {
    setTitleText(StringUtils.getString(R.string.列表中视频播放))
    multiTypeAdapter.register(VideoListItem { startPlay(multiTypeAdapter.items.indexOf(it)) })
    multiTypeAdapter.register(NoMoreItem())
    //播放相关
    mVideoView = MyVideoView(mContext)
    mVideoView?.setInList(true)
    //列表相关
    viewBinding.playListRecycler.layoutManager = LinearLayoutManager(mContext)
    viewBinding.playListRecycler.adapter = multiTypeAdapter
    viewBinding.playListRecycler.addOnChildAttachStateChangeListener(object : RecyclerView.OnChildAttachStateChangeListener {
      override fun onChildViewDetachedFromWindow(view: View) { //非全屏滑出去释放掉
        if (view.findViewById<View>(R.id.itemVideoContainer) != null) { //有可能是其他View，所以要判断
          val binding = ItemListVideoBinding.bind(view)
          binding.itemVideoContainer.getChildAt(0)?.let { if (it == mVideoView && mVideoView?.isFullScreen == false) releaseVideoView() }
        }
      }

      override fun onChildViewAttachedToWindow(view: View) {
      }
    })
    viewModel.videoLiveData.observe(this, MyObserver {
      when (it) {
        is DataState.Start -> showLoadingView()
        is DataState.SuccessRefresh -> {
          val items = mutableListOf<Any>()
          it.data?.let { list ->
            items.addAll(list)
            items.add(NoMoreBean())
          }
          multiTypeAdapter.items = items
          multiTypeAdapter.notifyDataSetChanged()
        }
        else -> {
        }
      }
      if (it?.isComplete() == true) {
        dismissLoadingView()
      }
    })
    viewModel.loadData()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="开始播放">
  //开始播放
  private fun startPlay(position: Int) {
    if (mCurPos == position) return
    if (mCurPos != -1) releaseVideoView()
    viewBinding.playListRecycler.layoutManager?.findViewByPosition(position)?.let {
      mVideoView?.removeParent()
      val binding = ItemListVideoBinding.bind(it)
      binding.itemVideoContainer.addView(mVideoView)
      val videoBean = multiTypeAdapter.items[position] as? VideoBean
      mVideoView?.setPlayUrl(url = videoBean?.url ?: "", autoPlay = true, needHolder = false)
      mCurPos = position
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="释放播放">
  //释放播放
  private fun releaseVideoView() {
    mVideoView?.let {
      it.release()
      it.removeParent()
      mCurPos = -1
    }
  }
  //</editor-fold>
}