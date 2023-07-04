package cc.abase.demo.component.recyclerpage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.*
import cc.ab.base.ext.*
import cc.ab.base.ui.item.BaseViewHolder
import cc.ab.base.widget.discretescrollview.DSVOrientation
import cc.ab.base.widget.discretescrollview.adapter.DiscretePageAdapter
import cc.abase.demo.R
import cc.abase.demo.bean.local.VerticalPageBean
import cc.abase.demo.component.comm.CommBindTitleActivity
import cc.abase.demo.databinding.ActivtyVerticalpageBinding
import cc.abase.demo.databinding.ItemVerticalPageParentBinding
import cc.abase.demo.utils.VideoRandomUtils
import cc.abase.demo.widget.dkplayer.MyVideoView
import com.blankj.utilcode.util.*
import kotlinx.coroutines.*

/**
 * Description:
 * @author: Khaos
 * @date: 2020/5/13 9:23
 */
@Suppress("UNCHECKED_CAST")
class RecyclerPagerActivity : CommBindTitleActivity<ActivtyVerticalpageBinding>() {
  //<editor-fold defaultstate="collapsed" desc="外部跳转">
  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, RecyclerPagerActivity::class.java)
      context.startActivity(intent)
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="变量区">
  //数据
  private var mDatas = mutableListOf<VerticalPageBean>()

  //适配器
  private var recyclerPagerAdapter = object : DiscretePageAdapter<VerticalPageBean, ItemVerticalPageParentBinding>(mDatas) {
    override fun fillData(data: VerticalPageBean, binding: ItemVerticalPageParentBinding, position: Int, count: Int) {
      val h = (binding.itemRecyclePagerContainer.context as? Activity)?.mContentView?.height ?: ScreenUtils.getScreenHeight()
      val height = h - BarUtils.getStatusBarHeight() - 49.dp2px()
      binding.itemRecyclePagerCover.loadCoilSimpleUrl(url = data.cover ?: "", holderRatio = ScreenUtils.getScreenWidth() * 1f / height, hasHolder = false)
    }
  }

  //请求
  private var disposableRequest: Job? = null

  //播放器控件
  private var mVideoView: MyVideoView? = null
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="View初始化">
  override fun initContentView() {
    //设置标题
    setTitleText(StringUtils.getString(R.string.RecyclerView仿抖音))
    //初始化列表
    viewBinding.vvpDSV.setOrientation(DSVOrientation.VERTICAL)
    viewBinding.vvpDSV.addOnItemChangedListener { viewHolder, position, end ->
      //滑动过程中位置改变
      if (!end) {
        //更新UI
        (viewHolder as? BaseViewHolder<ItemVerticalPageParentBinding>)?.let { dh ->
          recyclerPagerAdapter.fillData(mDatas[position], dh.viewBinding, position, recyclerPagerAdapter.itemCount)
        }
        return@addOnItemChangedListener
      }
      //防止还存在播放器
      if (mVideoView?.parent != null) initVideoView()
      //滑动结束后的位置，添加播放器，开始播放
      mVideoView?.let { videoView ->
        //更新UI
        (viewHolder as? BaseViewHolder<ItemVerticalPageParentBinding>)?.let { dh ->
          recyclerPagerAdapter.fillData(mDatas[position], dh.viewBinding, position, recyclerPagerAdapter.itemCount)
          dh.viewBinding.itemRecyclePagerContainer.addView(videoView)
        }
        //开始播放
        val bean = mDatas[position]
        videoView.setPlayUrl(bean.videoUrl ?: "", bean.description ?: "", autoPlay = true, needHolder = false)
      }
      //判断是否可以上拉加载更多
      if (position == (mDatas.size - 1)) {
        viewBinding.vvpRefreshLayout.setEnableLoadMore(true)
      } else {
        viewBinding.vvpRefreshLayout.setEnableLoadMore(false)
      }
    }
    viewBinding.vvpDSV.setItemTransitionTimeMillis(100)
    viewBinding.vvpDSV.adapter = recyclerPagerAdapter
    //初始化播放器
    initVideoView()
    //加载更多
    viewBinding.vvpRefreshLayout.setEnableLoadMoreWhenContentNotFull(true) //解决不能上拉问题
    viewBinding.vvpRefreshLayout.setEnableScrollContentWhenLoaded(false)  //是否在加载完成时滚动列表显示新的内容(false相当于加载更多是一个item)
    viewBinding.vvpRefreshLayout.setEnableFooterFollowWhenNoMoreData(false) //没有更多不固定显示，只有上拉才能看到
    viewBinding.vvpRefreshLayout.setEnableRefresh(false)
    viewBinding.vvpRefreshLayout.setEnableLoadMore(false)
    viewBinding.vvpRefreshLayout.setOnLoadMoreListener {
      loadData(lastId = mDatas.last().id) {
        viewBinding.vvpRefreshLayout.finishLoadMore(true)
        viewBinding.vvpRefreshLayout.noMoreData()
        mDatas.addAll(it)
        recyclerPagerAdapter.notifyDataSetChanged()
      }
    }
    loadData(time = 100) {
      mDatas.addAll(it)
      recyclerPagerAdapter.notifyDataSetChanged()
      viewBinding.vvpRefreshLayout.hasMoreData()
      //默认进来第一个位置没有回调，所以手动进行播放
      viewBinding.vvpDSV.post {
        viewBinding.vvpDSV.getViewHolder(0)?.let { viewHolder ->
          (viewHolder as? BaseViewHolder<ItemVerticalPageParentBinding>)?.let { holder ->
            //防止还存在播放器
            if (mVideoView?.parent != null) initVideoView()
            mVideoView?.let { videoView ->
              holder.viewBinding.itemRecyclePagerContainer.addView(videoView)
              //开始播放
              val bean = mDatas.first()
              videoView.setPlayUrl(bean.videoUrl ?: "", bean.description ?: "", autoPlay = true, needHolder = false)
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
    //重置数据
    mVideoView = MyVideoView(mContext)
    mVideoView?.getMyController()?.setEnableInNormal(false) //禁止手势
    mVideoView?.setFullShow(View.GONE) //隐藏全屏
    mVideoView?.setBackShow(View.GONE) //隐藏返回
    mVideoView?.fitSpeedTitle(true) //适配倍速位置
    mVideoView?.fitSpeedStatus(false) //不适配状态栏
    mVideoView?.setLooping(true)
    mVideoView?.layoutParams = ViewGroup.LayoutParams(-1, -1)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="模拟数据获取">
  private fun loadData(time: Long = 1500L, lastId: Long = 0, call: ((list: MutableList<VerticalPageBean>) -> Unit)? = null) {
    if (disposableRequest != null && disposableRequest?.isActive == true) return
    disposableRequest = launchError {
      withContext(Dispatchers.IO) {
        delay(time)
        val list = mutableListOf<VerticalPageBean>()
        for (i in lastId until lastId + 10) {
          val video = VideoRandomUtils.getVideoPair(i.toInt())
          list.add(VerticalPageBean(id = i + 1, cover = video.second, videoUrl = video.second, description = video.first))
        }
        list
      }.let {
        call?.invoke(it)
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="释放">
  override fun finish() {
    super.finish()
    disposableRequest?.cancel()
  }
  //</editor-fold>
}