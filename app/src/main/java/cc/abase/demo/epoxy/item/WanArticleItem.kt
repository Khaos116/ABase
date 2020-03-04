package cc.abase.demo.epoxy.item

import android.view.View
import cc.ab.base.ext.click
import cc.ab.base.ext.pressEffectBgColor
import cc.ab.base.ui.holder.BaseEpoxyHolder
import cc.abase.demo.R
import cc.abase.demo.epoxy.base.BaseEpoxyModel
import cc.abase.demo.bean.wan.ArticleBean
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import kotlinx.android.synthetic.main.item_wan_article.view.*

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
      //作者
      itemView.itemArticleUser.text = it.showAuthor
      //时间
      itemView.itemArticleTime.text = it.showTime
      //类型
      itemView.itemArticleType.text = it.showType
      //标题+描述
      itemView.itemArticleDes.text = it.showInfo
    }
    //item点击
    itemView.click { onItemClick?.invoke(dataBean) }
    //item按压效果
    itemView.pressEffectBgColor()
  }
}