package cc.abase.demo.epoxy.item

import android.view.Gravity
import android.view.View
import cc.ab.base.ext.load
import cc.ab.base.ui.holder.BaseEpoxyHolder
import cc.ab.base.widget.discretescrollview.DSVOrientation
import cc.ab.base.widget.discretescrollview.DiscreteBanner
import cc.ab.base.widget.discretescrollview.holder.DiscreteHolder
import cc.ab.base.widget.discretescrollview.holder.DiscreteHolderCreator
import cc.abase.demo.R
import cc.abase.demo.component.web.WebActivity
import cc.abase.demo.epoxy.base.BaseEpoxyModel
import cc.abase.demo.repository.bean.wan.BannerBean
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import kotlinx.android.synthetic.main.item_banner_child.view.itemBannerIV
import me.panpf.sketch.SketchImageView

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/13 19:11
 */
@EpoxyModelClass(layout = R.layout.item_banner)
abstract class BannerItem : BaseEpoxyModel<BaseEpoxyHolder>() {
  //数据源
  @EpoxyAttribute
  var dataList: MutableList<BannerBean>? = null
  override fun onBind(itemView: View) {
    //随机横竖屏切换
    val vertical = System.currentTimeMillis() % 2 == 0L
    dataList?.let { data ->
      //防止每次滑出屏幕再滑入后重新创建
      val tag = data.hashCode() + data.size
      if (itemView.tag == tag) return@let
      itemView.tag = tag
      val banner: DiscreteBanner<BannerBean> = itemView.findViewById(R.id.itemBanner)
      banner.setOrientation(if (vertical) DSVOrientation.VERTICAL else DSVOrientation.HORIZONTAL)
          .setLooper(true)
          .setAutoPlay(true)
          .setOnItemClick { _, t -> WebActivity.startActivity(banner.context, t.url ?: "") }
          .also {
            if (!vertical) {
              it.setIndicatorGravity(Gravity.BOTTOM or Gravity.END)
              it.setIndicatorOffsetY(-it.defaultOffset / 2f)
              it.setIndicatorOffsetX(-it.defaultOffset)
            }
          }
          .setPages(object : DiscreteHolderCreator {
            override fun createHolder(view: View) = HomeBannerHolderView(view)
            override fun getLayoutId() = R.layout.item_banner_child
          }, data)
    }
  }
}

class HomeBannerHolderView(view: View?) : DiscreteHolder<BannerBean>(view) {
  private var imageView: SketchImageView? = null
  override fun updateUI(
    data: BannerBean,
    position: Int,
    count: Int
  ) {
    imageView?.load(data.imagePath)
  }

  override fun initView(view: View) {
    this.imageView = view.itemBannerIV
  }
}