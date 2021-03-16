package cc.abase.demo.item

import android.view.*
import cc.ab.base.ext.loadImgHorizontal
import cc.ab.base.ui.item.BaseBindItemView
import cc.ab.base.ui.item.BaseViewHolder
import cc.ab.base.widget.discretescrollview.DSVOrientation
import cc.ab.base.widget.discretescrollview.DiscreteBanner
import cc.ab.base.widget.discretescrollview.holder.DiscreteHolder
import cc.ab.base.widget.discretescrollview.holder.DiscreteHolderCreator
import cc.abase.demo.R
import cc.abase.demo.bean.wan.BannerBean
import cc.abase.demo.databinding.ItemBannerBinding
import cc.abase.demo.databinding.ItemBannerImgBinding
import com.blankj.utilcode.util.ScreenUtils

/**
 * Author:CASE
 * Date:2020-11-25
 * Time:15:49
 */
class BannerItem(
    private val onItemBannerClick: ((item: BannerBean, position: Int) -> Unit)? = null
) : BaseBindItemView<MutableList<BannerBean>, ItemBannerBinding>() {
  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun loadViewBinding(inflater: LayoutInflater, parent: ViewGroup) = ItemBannerBinding.inflate(inflater, parent, false)
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="数据填充">
  override fun fillData(holder: BaseViewHolder<ItemBannerBinding>, item: MutableList<BannerBean>) {
    if (holder.itemView.getTag(R.id.tag_banner) != item) {
      holder.itemView.setTag(R.id.tag_banner, item)
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
          .setPages(object : DiscreteHolderCreator<BannerBean, ItemBannerImgBinding>() { //继承DiscreteHolderCreator
            //XML
            override fun loadViewBinding(inflater: LayoutInflater, parent: ViewGroup) = ItemBannerImgBinding.inflate(inflater, parent, false)

            //创建Holder
            override fun createHolder(binding: ItemBannerImgBinding) = object : DiscreteHolder<BannerBean, ItemBannerImgBinding>(binding) {
              //刷新数据
              override fun updateUI(data: BannerBean?, binding: ItemBannerImgBinding, position: Int, count: Int) {
                binding.itemBannerImg.loadImgHorizontal(data?.imagePath, 900f / 500)
              }
            }
          }, item) //BannerBean的数据列表MutableList<BannerBean>
    }
  }
  //</editor-fold>
}