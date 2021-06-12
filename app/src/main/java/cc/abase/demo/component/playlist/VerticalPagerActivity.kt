package cc.abase.demo.component.playlist

import android.content.Context
import android.content.Intent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.viewpager.widget.ViewPager
import cc.ab.base.ext.*
import cc.ab.base.ui.viewmodel.DataState
import cc.abase.demo.bean.local.VideoBean
import cc.abase.demo.component.comm.CommBindActivity
import cc.abase.demo.component.playlist.adapter.VerticalPagerAdapter
import cc.abase.demo.component.playlist.adapter.VerticalPagerAdapter.PagerHolder
import cc.abase.demo.component.playlist.viewmoel.VerticalPagerViewModel
import cc.abase.demo.databinding.ActivityPlayPagerBinding
import cc.abase.demo.widget.dkplayer.MyVideoView
import com.gyf.immersionbar.ktx.immersionBar
import com.scwang.smart.refresh.layout.wrapper.RefreshHeaderWrapper

/**
 * Description:
 * @author: Khaos
 * @date: 2019/12/12 11:33
 */
class VerticalPagerActivity : CommBindActivity<ActivityPlayPagerBinding>() {
  //<editor-fold defaultstate="collapsed" desc="外部跳转">
  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, VerticalPagerActivity::class.java)
      context.startActivity(intent)
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="变量">
  //当前播放位置
  private var mCurPos = 0

  //适配器
  private var mVerticalPagerAdapter: VerticalPagerAdapter? = null

  //播放控件
  private var mVideoView: MyVideoView? = null

  //数据源
  private var mVideoList: MutableList<VideoBean> = mutableListOf()

  //是否可以加载更多
  var hasMore: Boolean = true

  //数据层
  private val viewModel: VerticalPagerViewModel by lazy { VerticalPagerViewModel() }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="状态栏操作">
  override fun fillStatus() = false

  override fun initStatus() {
    immersionBar { statusBarDarkFont(false) }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化View">
  override fun initView() {
    //返回按钮
    (viewBinding.verticalPagerBack.layoutParams as? MarginLayoutParams)?.topMargin = mStatusBarHeight
    viewBinding.verticalPagerBack.pressEffectAlpha()
    viewBinding.verticalPagerBack.click { onBackPressed() }
    //播放控件
    mVideoView = MyVideoView(mContext)
    mVideoView?.titleFitWindow(true)
    mVideoView?.setBackShow(View.INVISIBLE)
    mVideoView?.setLooping(true)
    //列表
    viewBinding.verticalPagerViewPager.offscreenPageLimit = 4
    //下拉刷新
    viewBinding.verticalPagerRefresh.setEnableLoadMoreWhenContentNotFull(true) //解决不能上拉问题
    viewBinding.verticalPagerRefresh.setEnableFooterFollowWhenNoMoreData(false) //没有更多不固定显示，只有上拉才能看到
    viewBinding.verticalPagerRefresh.setEnableRefresh(false) //不要下拉刷新
    viewBinding.verticalPagerRefresh.setRefreshHeader(RefreshHeaderWrapper(View(mContext))) //使用简单的header以节约内存
    viewBinding.verticalPagerRefresh.setEnableLoadMore(false) //暂时禁止上拉加载
    viewBinding.verticalPagerRefresh.setOnLoadMoreListener { viewModel.loadMore() }
    viewModel.videoLiveData.observe(this) {
      when (it) {
        is DataState.SuccessRefresh -> {
          mVideoList = it.data ?: mutableListOf()
          if (mVerticalPagerAdapter == null) {
            initAdapter(mVideoList)
          } else {
            mVerticalPagerAdapter?.setNewData(mVideoList)
          }
        }
        is DataState.SuccessMore -> {
          viewBinding.verticalPagerRefresh.finishLoadMore()
          mVideoList = it.data ?: mutableListOf()
          if (mVerticalPagerAdapter == null) {
            initAdapter(mVideoList)
          } else {
            mVerticalPagerAdapter?.setNewData(mVideoList)
          }
        }
        is DataState.Start -> {
          if (it.data.isNullOrEmpty()) showLoadingView()
        }
        is DataState.Complete -> {
          dismissLoadingView()
          hasMore = it.hasMore
          if (viewBinding.verticalPagerViewPager.currentItem == mVideoList.size - 1) {
            if (hasMore) {
              viewBinding.verticalPagerRefresh.hasMoreData()
            } else {
              viewBinding.verticalPagerRefresh.noMoreData()
            }
          } else {
            viewBinding.verticalPagerRefresh.setEnableLoadMore(false)
          }
        }
        else -> {
        }
      }
    }
    viewModel.loadData()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="拿到数据第一次初始化Adapter">
  //初始化adapter
  private fun initAdapter(datas: MutableList<VideoBean>) {
    if (mVerticalPagerAdapter == null) {
      mVerticalPagerAdapter = VerticalPagerAdapter(datas)
      viewBinding.verticalPagerViewPager.adapter = mVerticalPagerAdapter
      //随机从某一个开始播放
      val index = (Math.random() * datas.size).toInt()
      if (index != 0) {
        viewBinding.verticalPagerViewPager.currentItem = index
        //如果直接到最后一条需要显示可以加载更多
        if (index == mVideoList.size - 1) viewBinding.verticalPagerRefresh.hasMoreData()
      }
      //第一次加载的时候设置currentItem会滚动刷新，所以播放需要延时
      viewBinding.verticalPagerViewPager.post {
        startPlay(index)
        viewBinding.verticalPagerViewPager.setOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
          override fun onPageSelected(position: Int) {
            if (position == mCurPos) return
            startPlay(position)
            if (position == (mVideoList.size - 1)) {
              if (hasMore) {
                viewBinding.verticalPagerRefresh.hasMoreData()
              } else {
                viewBinding.verticalPagerRefresh.noMoreData()
              }
            }
          }
        })
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="播放视频">
  //开始播放
  private fun startPlay(position: Int) {
    //预加载更多
    //if (position >= mVideoList.size - 5) viewModel.loadMore()
    //遍历加载信息和播放
    val count: Int = viewBinding.verticalPagerViewPager.childCount
    var findCount = 0 //由于复用id是混乱的，所以需要保证3个都找到才跳出循环(为了节约性能)
    for (i in 0 until count) {
      val itemView: View = viewBinding.verticalPagerViewPager.getChildAt(i)
      val viewHolder: PagerHolder = itemView.tag as PagerHolder
      if (viewHolder.mPosition == position) {
        mVideoView?.release()
        mVideoView?.removeParent()
        val videoBean: VideoBean = mVideoList[viewHolder.mPosition]
        mVideoView?.setPlayUrl(videoBean.url ?: "", videoBean.title ?: "", autoPlay = true, needHolder = false)
        viewHolder.viewBinding.itemVerticalPagerContainer.addView(mVideoView)
        mCurPos = position
        findCount++
      } else if (position > 0 && viewHolder.mPosition == position - 1) { //预加载上一个数据，否则滑动可能出现复用的数据
        mVerticalPagerAdapter?.fillData(mVideoList[viewHolder.mPosition], viewHolder)
        findCount++
      } else if (position < mVideoList.size - 1 && viewHolder.mPosition == position + 1) { //预加载下一个数据，否则滑动可能出现复用的数据
        mVerticalPagerAdapter?.fillData(mVideoList[viewHolder.mPosition], viewHolder)
        findCount++
      }
      if (findCount >= 3) break
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="防止闪退">
  override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
    return try {
      super.dispatchTouchEvent(ev)
    } catch (e: Exception) {
      e.printStackTrace()
      if (ev != null && (ev.action and MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
        viewBinding.verticalPagerViewPager.scrollBy(1, 0)
      }
      false
    }
  }
  //</editor-fold>
}