package cc.abase.demo.epoxy.item

import android.view.View
import cc.ab.base.ext.*
import cc.ab.base.net.http.response.PicBean
import cc.ab.base.ui.holder.BaseEpoxyHolder
import cc.ab.base.widget.ninegridview.NineGridAdapter
import cc.ab.base.widget.ninegridview.NineGridView
import cc.abase.demo.R
import cc.abase.demo.epoxy.base.BaseEpoxyModel
import cc.abase.demo.repository.bean.gank.GankAndroidBean
import cc.abase.demo.utils.BrowserUtils
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import kotlinx.android.synthetic.main.item_gank_android.view.*

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/1 17:05
 */
@EpoxyModelClass(layout = R.layout.item_gank_android)
abstract class GankAndroidItem : BaseEpoxyModel<BaseEpoxyHolder>() {
  //true使用自己定义的Nine实现9宫格,false使用GlideImageView的9宫格实现
  private val userAdapterNine = false
  //数据源
  @EpoxyAttribute
  var dataBean: GankAndroidBean? = null
  //点击item
  @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
  var onItemClick: ((bean: GankAndroidBean?) -> Unit)? = null

  @Suppress("UNCHECKED_CAST")
  override fun onBind(itemView: View) {
    dataBean?.let {
      //发布者
      itemView.itemGankAndroidUser.text = it.who
      //发布时间
      itemView.itemGankAndroidTime.text = it.publishTime
      //内容
      itemView.itemGankAndroidDes.text = it.desc
      //图片
      if (userAdapterNine) {
        itemView.itemGankAndroidNine2.gone()
        itemView.itemGankAndroidNine.visibleGone(!it.images.isNullOrEmpty())
        if (!it.images.isNullOrEmpty()) {
          //多张图片，九宫格
          val nieView: NineGridView<PicBean> = itemView.findViewById(R.id.itemGankAndroidNine)
          setMultiImages(it.images ?: emptyList(), it.urlImgs, nieView)
        }
      } else {
        itemView.itemGankAndroidNine.gone()
        itemView.itemGankAndroidNine2.visibleGone(!it.images.isNullOrEmpty())
        if (!it.images.isNullOrEmpty()) {
          itemView.itemGankAndroidNine2.data = it.urlImgs2
          itemView.itemGankAndroidNine2.setOnItemClickListener { position, view ->
            BrowserUtils.instance.show(
                (it.images ?: emptyList<String>()) as ArrayList<String>, position
            )
          }
        }
      }
    }
    //item点击
    itemView.click { onItemClick?.invoke(dataBean) }
    //item按压效果
    itemView.pressEffectBgColor()
  }

  @Suppress("UNCHECKED_CAST")
  private fun setMultiImages(
    picStr: List<String?>,
    picBeans: List<PicBean>,
    nineGridView: NineGridView<PicBean>
  ) {
    nineGridView.setAdapter(NineGridAdapter())
    nineGridView.setImagesData(picBeans)
    nineGridView.setImageClickListener { _, imageView, index, list ->
      BrowserUtils.instance.show(picStr as ArrayList<String>, index)
    }
  }
}