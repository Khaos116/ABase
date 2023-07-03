package cc.abase.demo.component.playlist.adapter

import android.app.Activity
import android.view.*
import androidx.viewpager.widget.PagerAdapter
import cc.ab.base.ext.*
import cc.abase.demo.R
import cc.abase.demo.bean.local.VideoBean
import cc.abase.demo.databinding.ItemPlayPagerParentBinding
import com.blankj.utilcode.util.ScreenUtils

/**
 * Description:
 * @author: Khaos
 * @date: 2019/12/13 11:56
 */
class VerticalPagerAdapter(list: List<VideoBean>) : PagerAdapter() {
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

  override fun isViewFromObject(view: View, o: Any) = view === o

  override fun getCount() = mVideoBeans?.size ?: 0

  override fun instantiateItem(container: ViewGroup, position: Int): Any {
    //基础信息
    val context = container.context
    var view: View? = null
    if (!mViewPool.isNullOrEmpty()) {
      //取第一个进行复用
      view = mViewPool[0]
      mViewPool.removeAt(0)
    }
    val viewHolder: PagerHolder = if (view == null) {
      view = LayoutInflater.from(context).inflate(R.layout.item_play_pager_parent, container, false)
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
  fun fillData(videoBean: VideoBean, viewHolder: PagerHolder) {
    val height = (viewHolder.viewBinding.itemVerticalPagerContainer.context as? Activity)?.mContentView?.height ?: ScreenUtils.getScreenHeight()
    videoBean.thumb?.let {
      if (it.isVideoUrl()) {
        viewHolder.viewBinding.itemVerticalPagerCover.loadNetVideoCover(url = it, holderRatio = ScreenUtils.getScreenWidth() * 1f / height, hasHolder = false)
      } else {
        viewHolder.viewBinding.itemVerticalPagerCover.loadCoilSimpleUrl(url = it, holderRatio = ScreenUtils.getScreenWidth() * 1f / height, hasHolder = false)
      }
    }
  }

  override fun destroyItem(container: ViewGroup, position: Int, o: Any) {
    val itemView = o as View
    container.removeView(itemView)
    //保存起来用来复用
    mViewPool.add(itemView)
  }

  class PagerHolder(view: View) {
    var mPosition = 0
    val viewBinding: ItemPlayPagerParentBinding = ItemPlayPagerParentBinding.bind(view)

    init {
      view.tag = this
    }
  }
}