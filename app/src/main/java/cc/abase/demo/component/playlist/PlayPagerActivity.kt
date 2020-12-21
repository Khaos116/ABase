package cc.abase.demo.component.playlist

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.viewpager.widget.ViewPager
import cc.ab.base.ext.*
import cc.abase.demo.R
import cc.abase.demo.bean.local.VideoBean
import cc.abase.demo.component.comm.CommActivity
import cc.abase.demo.component.playlist.adapter.PlayPagerAdapter
import cc.abase.demo.component.playlist.adapter.PlayPagerAdapter.PagerHolder
import cc.abase.demo.component.playlist.viewmoel.PlayPagerViewModel
import cc.abase.demo.widget.video.MyVideoView
import com.airbnb.mvrx.Success
import com.billy.android.swipe.SmartSwipeRefresh
import com.billy.android.swipe.SmartSwipeRefresh.SmartSwipeRefreshDataLoader
import com.gyf.immersionbar.ktx.immersionBar
import kotlinx.android.synthetic.main.activity_play_pager.playPagerBack
import kotlinx.android.synthetic.main.activity_play_pager.playPagerViewPager

/**
 * Description:
 * @author: CASE
 * @date: 2019/12/12 11:33
 */
class PlayPagerActivity : CommActivity() {

  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, PlayPagerActivity::class.java)
      context.startActivity(intent)
    }
  }

  //当前播放位置
  private var mCurPos = 0

  //适配器
  private var mPlayPagerAdapter: PlayPagerAdapter? = null

  //播放控件
  private var mVideoView: MyVideoView? = null

  //数据源
  private var mVideoList: MutableList<VideoBean> = mutableListOf()

  //下拉刷新
  var mSmartSwipeRefresh: SmartSwipeRefresh? = null

  //是否可以加载更多
  var hasMore: Boolean = true

  //数据层
  private val viewModel: PlayPagerViewModel by lazy {
    PlayPagerViewModel()
  }

  override fun fillStatus() = false

  override fun initStatus() {
    immersionBar { statusBarDarkFont(false) }
  }

  override fun layoutResId() = R.layout.activity_play_pager

  override fun initView() {
    //返回按钮
    playPagerBack.pressEffectAlpha()
    playPagerBack.click { onBackPressed() }
    //播放控件
    mVideoView = MyVideoView(mContext)
    mVideoView?.setFitSystemWindow(true)
    mVideoView?.backButton?.visibleInvisible(false)
    //列表
    playPagerViewPager.offscreenPageLimit = 4
    //下拉刷新
    mSmartSwipeRefresh = SmartSwipeRefresh.translateMode(playPagerViewPager, false)
    mSmartSwipeRefresh?.disableRefresh()
    mSmartSwipeRefresh?.disableLoadMore()
    mSmartSwipeRefresh?.isNoMoreData = !hasMore
    mSmartSwipeRefresh?.dataLoader = object : SmartSwipeRefreshDataLoader {
      override fun onLoadMore(ssr: SmartSwipeRefresh?) {
        viewModel.loadMore()
      }

      override fun onRefresh(ssr: SmartSwipeRefresh?) {
      }
    }
  }

  override fun initData() {
    viewModel.subscribe(this) {
      if (it.request.complete) {
        dismissLoadingView()
        mVideoList = it.videoList
        hasMore = it.hasMore
        mSmartSwipeRefresh?.finished(it.request is Success)
        mSmartSwipeRefresh?.isNoMoreData = !hasMore
        if (mPlayPagerAdapter == null) {
          initAdapter(it.videoList)
        } else {
          mPlayPagerAdapter?.setNewData(it.videoList)
        }
      }
    }
    showLoadingView()
    viewModel.loadData()
  }

  //初始化adapter
  private fun initAdapter(datas: MutableList<VideoBean>) {
    if (mPlayPagerAdapter == null && playPagerViewPager != null) {
      mPlayPagerAdapter = PlayPagerAdapter(datas)
      playPagerViewPager.adapter = mPlayPagerAdapter
      //随机从某一个开始播放
      val index = (Math.random() * datas.size).toInt()
      if (index != 0) {
        playPagerViewPager.currentItem = index
        //如果直接到最后一条需要显示没有更多
        if (index == mVideoList.size - 1) {
          mSmartSwipeRefresh?.swipeConsumer?.enableBottom()
          mSmartSwipeRefresh?.isNoMoreData = hasMore
        }
      }
      //第一次加载的时候设置currentItem会滚动刷新，所以播放需要延时
      playPagerViewPager.post {
        startPlay(index)
        playPagerViewPager.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
          override fun onPageScrollStateChanged(state: Int) {
          }

          override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
          }

          override fun onPageSelected(position: Int) {
            if (position == mCurPos) return
            startPlay(position)
            if (position == (mVideoList.size - 1)) {
              mSmartSwipeRefresh?.swipeConsumer?.enableBottom()
              mSmartSwipeRefresh?.isNoMoreData = !hasMore
            } else {
              mSmartSwipeRefresh?.disableLoadMore()
            }
          }
        })
      }
    }
  }

  //开始播放
  private fun startPlay(position: Int) {
    //预加载更多
    if (position >= mVideoList.size - 5) viewModel.loadMore()
    //遍历加载信息和播放
    val count: Int = playPagerViewPager.childCount
    var findCount = 0 //由于复用id是混乱的，所以需要保证3个都找到才跳出循环(为了节约性能)
    for (i in 0 until count) {
      val itemView: View = playPagerViewPager.getChildAt(i)
      val viewHolder: PagerHolder = itemView.tag as PagerHolder
      if (viewHolder.mPosition == position) {
        mVideoView?.release()
        mVideoView?.removeParent()
        val videoBean: VideoBean = mVideoList[viewHolder.mPosition]
        mVideoView?.setPlayUrl(videoBean.url ?: "", videoBean.title ?: "")
        viewHolder.mPlayerContainer?.addView(mVideoView, 0)
        //mVideoView?.startPlayLogic()
        mCurPos = position
        findCount++
      } else if (position > 0 && viewHolder.mPosition == position - 1) { //预加载上一个数据，否则滑动可能出现复用的数据
        mPlayPagerAdapter?.fillData(mVideoList[viewHolder.mPosition], viewHolder)
        findCount++
      } else if (position < mVideoList.size - 1 && viewHolder.mPosition == position + 1) { //预加载下一个数据，否则滑动可能出现复用的数据
        mPlayPagerAdapter?.fillData(mVideoList[viewHolder.mPosition], viewHolder)
        findCount++
      }
      if (findCount >= 3) break
    }
  }

  override fun onBackPressed() {
    if (mVideoView?.onBackPress() != true) super.onBackPressed()
  }
}