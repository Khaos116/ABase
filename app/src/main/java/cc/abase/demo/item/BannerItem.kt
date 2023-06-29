package cc.abase.demo.item

import android.view.Gravity
import cc.ab.base.ext.loadImgHorizontal
import cc.ab.base.ui.item.BaseBindItemView
import cc.ab.base.ui.item.BaseViewHolder
import cc.ab.base.widget.discretescrollview.DSVOrientation
import cc.ab.base.widget.discretescrollview.DiscreteBanner
import cc.ab.base.widget.discretescrollview.adapter.DiscretePageAdapter
import cc.abase.demo.R
import cc.abase.demo.bean.wan.BannerBean
import cc.abase.demo.databinding.ItemBannerBinding
import cc.abase.demo.databinding.ItemBannerImgBinding
import com.blankj.utilcode.util.ScreenUtils

/**
 * Author:Khaos
 * Date:2020-11-25
 * Time:15:49
 */
class BannerItem(
  private val onItemBannerClick: ((item: BannerBean, position: Int) -> Unit)? = null
) : BaseBindItemView<MutableList<BannerBean>, ItemBannerBinding>() {
  //<editor-fold defaultstate="collapsed" desc="数据填充">
  override fun fillData(holder: BaseViewHolder<ItemBannerBinding>, item: MutableList<BannerBean>) {
    if (holder.itemView.getTag(cc.ab.base.R.id.tag_banner) != item) {
      holder.itemView.setTag(cc.ab.base.R.id.tag_banner, item)
      val banner = holder.itemView.findViewById<DiscreteBanner<BannerBean, ItemBannerImgBinding>>(R.id.itemBanner)
      banner.layoutParams.height = (ScreenUtils.getScreenWidth() * 500f / 900).toInt()
      banner.setLooper(true) //无限循环
        .setAutoPlay(true) //自动播放
        .setOrientation(if (System.currentTimeMillis() % 2 == 0L) DSVOrientation.HORIZONTAL else DSVOrientation.VERTICAL)
        .setOnItemClick { position, t -> onItemBannerClick?.invoke(t, position) } //banner点击
        .apply {
          getIndicator()?.needSpecial = false //去除引导页的特殊指示器
          if (getOrientation() == DSVOrientation.HORIZONTAL.ordinal) { //由于默认是横向原点居底部(引导页使用)，所以banner处修改为底部居右
            setIndicatorGravity(Gravity.BOTTOM or Gravity.END)
            setIndicatorOffsetY(-defaultOffset / 2f)
            setIndicatorOffsetX(-defaultOffset)
          }
        }
        .setPages(object : DiscretePageAdapter<BannerBean, ItemBannerImgBinding>(item) {
          override fun fillData(data: BannerBean, binding: ItemBannerImgBinding, position: Int, count: Int) {
            binding.itemBannerImg.loadImgHorizontal(data.imagePath, 900f / 500)
          }
        }, item) //BannerBean的数据列表MutableList<BannerBean>
    }
  }
  //</editor-fold>
}