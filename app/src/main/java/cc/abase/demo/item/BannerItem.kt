package cc.abase.demo.item

import android.view.Gravity
import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import cc.ab.base.ext.loadImgHorizontal
import cc.ab.base.ui.item.BaseItemView
import cc.ab.base.widget.discretescrollview.DSVOrientation
import cc.ab.base.widget.discretescrollview.DiscreteBanner
import cc.ab.base.widget.discretescrollview.holder.DiscreteHolder
import cc.ab.base.widget.discretescrollview.holder.DiscreteHolderCreator
import cc.abase.demo.R
import cc.abase.demo.bean.wan.BannerBean
import com.blankj.utilcode.util.ScreenUtils
import kotlinx.android.synthetic.main.item_banner_img.view.itemBannerImg

/**
 * Author:CASE
 * Date:2020-11-25
 * Time:15:49
 */
class BannerItem(
    private val onItemBannerClick: ((item: BannerBean, position: Int) -> Unit)? = null
) : BaseItemView<MutableList<BannerBean>>() {
  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResId() = R.layout.item_banner
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="数据填充">
  override fun fillData(holder: ViewHolder, itemView: View, item: MutableList<BannerBean>) {
    if (holder.itemView.getTag(R.id.tag_banner) == item) return
    holder.itemView.setTag(R.id.tag_banner, item)
    val banner = holder.itemView.findViewById<DiscreteBanner<BannerBean>>(R.id.itemBanner)
    banner.layoutParams.height = (ScreenUtils.getScreenWidth() * 500f / 900).toInt()
    banner.setLooper(true)
        .setAutoPlay(true)
        .setOrientation(DSVOrientation.VERTICAL)
        .setOnItemClick { position, t -> onItemBannerClick?.invoke(t, position) }
        .apply {
          getIndicator()?.needSpecial = false
          if (getOrientation() == DSVOrientation.HORIZONTAL.ordinal) {
            setIndicatorGravity(Gravity.BOTTOM or Gravity.END)
            setIndicatorOffsetY(-defaultOffset / 2f)
            setIndicatorOffsetX(-defaultOffset)
          }
        }
        .setPages(object : DiscreteHolderCreator {
          override fun getLayoutId() = R.layout.item_banner_img

          override fun createHolder(itemView: View) = object : DiscreteHolder<BannerBean>(itemView) {
            override fun updateUI(data: BannerBean?, position: Int, count: Int) {
              itemView.itemBannerImg.loadImgHorizontal(data?.imagePath, 900f / 500)
            }
          }
        }, item)
  }
  //</editor-fold>
}