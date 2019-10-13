package cc.abase.demo.epoxy.item

import android.view.View
import cc.ab.base.ext.load
import cc.ab.base.ui.holder.BaseEpoxyHolder
import cc.abase.demo.R
import cc.abase.demo.component.web.WebActivity
import cc.abase.demo.epoxy.base.BaseEpoxyModel
import cc.abase.demo.repository.bean.wan.BannerBean
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.bigkoo.convenientbanner.ConvenientBanner
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator
import com.bigkoo.convenientbanner.holder.Holder
import com.blankj.utilcode.util.EncryptUtils
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
  private var bannerMD5: String? = null
  override fun onBind(itemView: View) {
    dataList?.let { data ->
      val sb = StringBuilder()
      for (ba in data) {
        sb.append(ba.url ?: "")
      }
      if (bannerMD5 == EncryptUtils.encryptMD5ToString(sb.toString())) return
      bannerMD5 = EncryptUtils.encryptMD5ToString(sb.toString())
      val banner: ConvenientBanner<BannerBean> = itemView.findViewById(R.id.itemBanner)
      banner.setPages(
          object : CBViewHolderCreator {
            override fun createHolder(view: View): Holder<BannerBean> {
              return LocalImageHolderView(view)
            }

            override fun getLayoutId() = R.layout.item_banner_child

          }
          , data)
          .setPageIndicator(
              intArrayOf(R.drawable.circle_primary, R.drawable.circle_accent)
          )
          .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT)
          .setOnItemClickListener { p ->
            data[p].url?.let { url -> WebActivity.startActivity(banner.context, url) }
          }
      if (!banner.isTurning) {
        banner.startTurning()
      }
    }
  }
}

class LocalImageHolderView(view: View?) : Holder<BannerBean>(view) {
  private var imageView: SketchImageView? = null
  override fun updateUI(data: BannerBean) {
    imageView?.load(data.imagePath)
  }

  override fun initView(view: View) {
    this.imageView = view.itemBannerIV
  }
}