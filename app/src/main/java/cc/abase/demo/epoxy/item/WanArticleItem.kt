package cc.abase.demo.epoxy.item

import android.view.View
import cc.ab.base.ext.click
import cc.ab.base.ext.pressEffectBgColor
import cc.ab.base.ui.holder.BaseEpoxyHolder
import cc.abase.demo.R
import cc.abase.demo.epoxy.base.BaseEpoxyModel
import cc.abase.demo.repository.bean.wan.ArticleBean
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import kotlinx.android.synthetic.main.item_wan_article.view.itemArticleTitle
import kotlinx.android.synthetic.main.item_wan_article.view.itemArticleUser

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/13 19:38
 */
@EpoxyModelClass(layout = R.layout.item_wan_article)
abstract class WanArticleItem : BaseEpoxyModel<BaseEpoxyHolder>() {
  //数据源
  @EpoxyAttribute
  var dataBean: ArticleBean? = null
  //点击item
  @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
  var onItemClick: ((bean: ArticleBean?) -> Unit)? = null

  override fun onBind(itemView: View) {
    dataBean?.let {
      //发布者
      itemView.itemArticleUser.text = it.shareUser
      //标题
      itemView.itemArticleTitle.text = it.title
    }
    //item点击
    itemView.click { onItemClick?.invoke(dataBean) }
    //item按压效果
    itemView.pressEffectBgColor()
  }
}