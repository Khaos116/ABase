package cc.abase.demo.component.playlist.adapter

import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import cc.ab.base.ext.load
import cc.abase.demo.R
import cc.abase.demo.component.playlist.view.PagerItemView
import cc.abase.demo.bean.local.VideoBean
import kotlinx.android.synthetic.main.item_play_pager.view.itemPlayPagerThumb
import kotlinx.android.synthetic.main.item_play_pager.view.itemPlayPagerTitle
import kotlinx.android.synthetic.main.item_play_pager_parent.view.itemPlayPagerContainer
import kotlinx.android.synthetic.main.item_play_pager_parent.view.itemPlayPagerItem
import me.panpf.sketch.SketchImageView

/**
 * Description:
 * @author: CASE
 * @date: 2019/12/13 11:56
 */
class PlayPagerAdapter(list: List<VideoBean>) : PagerAdapter() {
  //View缓存池，从ViewPager中移除的item将会存到这里面，用来复用
  private val mViewPool: MutableList<View> = mutableListOf()
  //数据源
  private var mVideoBeans: List<VideoBean>? = null

  init {
    mVideoBeans = list
  }

  //设置新的数据
  fun setNewData(videoList: MutableList<VideoBean>) {
    mVideoBeans = videoList
    notifyDataSetChanged()
  }

  override fun isViewFromObject(
    view: View,
    o: Any
  ) = view === o

  override fun getCount() = mVideoBeans?.size ?: 0

  override fun instantiateItem(
    container: ViewGroup,
    position: Int
  ): Any {
    //基础信息
    val context = container.context
    var view: View? = null
    if (!mViewPool.isNullOrEmpty()) {
      //取第一个进行复用
      view = mViewPool[0]
      mViewPool.removeAt(0)
    }
    val viewHolder: PagerHolder = if (view == null) {
      view = LayoutInflater.from(context)
          .inflate(R.layout.item_play_pager_parent, container, false)
      PagerHolder(view)
    } else view.tag as PagerHolder
    //填充数据
    mVideoBeans?.let { datas -> fillData(datas[position], viewHolder) }
    viewHolder.mPosition = position
    //添加控件
    container.addView(view)
    return view!!
  }

  //填充数据
  fun fillData(
    videoBean: VideoBean,
    viewHolder: PagerHolder
  ) {
    //封面
    viewHolder.mThumb?.load(
        url = videoBean.thumb,
        holderRes = R.drawable.place_holder_video_9_16,
        errorRes = R.drawable.error_holder_video_9_16
    )
    //标题
    viewHolder.mTitle?.text = videoBean.title
  }

  override fun destroyItem(
    container: ViewGroup,
    position: Int,
    o: Any
  ) {
    val itemView = o as View
    container.removeView(itemView)
    //保存起来用来复用
    mViewPool.add(itemView)
  }

  class PagerHolder(view: View) {
    var mPosition = 0
    var mTitle: TextView? = null//标题
    var mThumb: SketchImageView? = null//封面图
    var mPagerItemView: PagerItemView? = null
    var mPlayerContainer: FrameLayout? = null

    init {
      mTitle = view.itemPlayPagerTitle
      mThumb = view.itemPlayPagerThumb
      mPagerItemView = view.itemPlayPagerItem
      mPlayerContainer = view.itemPlayPagerContainer
      view.tag = this
    }
  }
}