package cc.abase.demo.component.verticalpage

import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import cc.ab.base.ext.mContext
import cc.ab.base.ext.removeParent
import cc.ab.base.utils.RxUtils
import cc.ab.base.widget.discretescrollview.DSVOrientation
import cc.ab.base.widget.discretescrollview.adapter.DiscretePageAdapter
import cc.abase.demo.R
import cc.abase.demo.bean.local.VerticalPageBean
import cc.abase.demo.component.comm.CommTitleActivity
import cc.abase.demo.component.playlist.view.PagerController
import cc.abase.demo.utils.VideoRandomUtils
import cc.abase.demo.widget.video.controller.VodControlView
import cc.abase.demo.widget.video.view.ExoVideoView
import com.billy.android.swipe.SmartSwipeRefresh
import com.billy.android.swipe.SmartSwipeRefresh.SmartSwipeRefreshDataLoader
import com.blankj.utilcode.util.StringUtils
import com.dueeeke.videoplayer.player.VideoView
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activty_verticalpage.vvpDSV
import java.util.concurrent.TimeUnit

/**
 * Description:
 * @author: caiyoufei
 * @date: 2020/5/13 9:23
 */
class VerticalPageActivity : CommTitleActivity() {
  //<editor-fold defaultstate="collapsed" desc="外部跳转">
  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, VerticalPageActivity::class.java)
      context.startActivity(intent)
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="变量区">
  //数据
  private var mDatas = mutableListOf<VerticalPageBean>()

  //适配器
  private var pageAdapter: DiscretePageAdapter<VerticalPageBean> = DiscretePageAdapter(VerticalPageHolderCreator(), mDatas)

  //请求
  private var disposableRequest: Disposable? = null

  //播放器控件
  private var mVideoView: ExoVideoView? = null

  //不显示移动网络播放
  private var mController: PagerController? = null

  //加载更多
  var mSmartSwipeRefresh: SmartSwipeRefresh? = null
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResContentId() = R.layout.activty_verticalpage
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="View初始化">
  override fun initContentView() {
    //设置标题
    setTitleText(StringUtils.getString(R.string.title_vertical_page))
    //初始化列表
    vvpDSV.setOrientation(DSVOrientation.VERTICAL)
    vvpDSV.addOnItemChangedListener { viewHolder, position, end ->
      //滑动过程中位置改变
      if (!end) {
        //更新UI
        if (viewHolder is VerticalPageHolderView) viewHolder.updateUI(mDatas[position], position, mDatas.size)
        return@addOnItemChangedListener
      }
      //防止还存在播放器
      if (mVideoView?.parent != null) initVideoView()
      //滑动结束后的位置，添加播放器，开始播放
      mVideoView?.let { videoView ->
        //更新UI
        if (viewHolder is VerticalPageHolderView) {
          viewHolder.updateUI(mDatas[position], position, mDatas.size)
          viewHolder.piv.removeParent()
          mController?.addControlComponent(viewHolder.piv)
          viewHolder.parentVideo.addView(videoView, 0)
        }
        //开始播放
        videoView.setUrl(mDatas[position].videoUrl)
        videoView.start()
      }
      //判断是否可以上拉加载更多
      if (position == (mDatas.size - 1)) {
        mSmartSwipeRefresh?.swipeConsumer?.enableBottom()
      } else {
        mSmartSwipeRefresh?.disableLoadMore()
      }
    }
    vvpDSV.adapter = pageAdapter
    //初始化播放器
    initVideoView()
    //加载更多
    mSmartSwipeRefresh = SmartSwipeRefresh.translateMode(vvpDSV, false)
    mSmartSwipeRefresh?.disableRefresh()
    mSmartSwipeRefresh?.disableLoadMore()
    mSmartSwipeRefresh?.isNoMoreData = false
    mSmartSwipeRefresh?.dataLoader = object : SmartSwipeRefreshDataLoader {
      override fun onLoadMore(ssr: SmartSwipeRefresh) {
        loadData(mDatas.last().id) {
          mSmartSwipeRefresh?.isNoMoreData = true
          mSmartSwipeRefresh?.finished(true)
          mDatas.addAll(it)
          pageAdapter.notifyDataSetChanged()
        }
      }

      override fun onRefresh(ssr: SmartSwipeRefresh) {
        vvpDSV.postDelayed({ ssr.finished(true) }, 1000)
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="数据初始化">
  override fun initData() {
    loadData {
      mDatas.addAll(it)
      pageAdapter.notifyDataSetChanged()
      //默认进来第一个位置没有回调，所以手动进行播放
      vvpDSV.post {
        vvpDSV.getViewHolder(0)?.let { viewHolder ->
          if (viewHolder is VerticalPageHolderView) {
            //防止还存在播放器
            if (mVideoView?.parent != null) initVideoView()
            mVideoView?.let { videoView ->
              viewHolder.piv.removeParent()
              mController?.addControlComponent(viewHolder.piv)
              viewHolder.parentVideo.addView(videoView, 0)
              //开始播放
              videoView.setUrl(mDatas.first().videoUrl)
              videoView.start()
            }
          }
        }
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="内部方法">
  //初始化视频控件
  private fun initVideoView() {
    //清除数据
    mVideoView?.release()
    mVideoView?.removeParent()
    mController?.removeParent()
    mController?.removeAllViews()
    mVideoView?.setVideoController(null)
    mVideoView?.setLifecycleOwner(null)
    //重置数据
    mVideoView = ExoVideoView(mContext)
    mVideoView?.layoutParams = ViewGroup.LayoutParams(-1, -1)
    mVideoView?.setLooping(true)
    mVideoView?.setScreenScaleType(VideoView.SCREEN_SCALE_DEFAULT)
    //控制器
    mController = PagerController(mContext)
    mController?.addControlComponent(VodControlView(mContext))
    mVideoView?.setVideoController(mController)
    mVideoView?.setLifecycleOwner(this)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="模拟数据获取">
  private fun loadData(lastId: Long = 0, call: ((list: MutableList<VerticalPageBean>) -> Unit)? = null) {
    if (disposableRequest != null && disposableRequest?.isDisposed == false) return
    disposableRequest = Observable.timer(1500, TimeUnit.MILLISECONDS).flatMap {
      val list = mutableListOf<VerticalPageBean>()
      for (i in lastId until lastId + 10) {
        val video = VideoRandomUtils.instance.getVideoPair(i.toInt())
        list.add(VerticalPageBean(id = i + 1, cover = video.first,
            videoUrl = video.second, description = "测试数据${i + 1}"))
      }
      Observable.just(list)
    }.compose(RxUtils.instance.rx2SchedulerHelperO(lifecycleProvider)).subscribe { call?.invoke(it) }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="释放">
  override fun finish() {
    super.finish()
    disposableRequest?.dispose()
  }
  //</editor-fold>
}