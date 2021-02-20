package cc.abase.demo.item

import cc.ab.base.ext.visibleGone
import cc.ab.base.ui.item.BaseItemView
import cc.ab.base.ui.item.BaseViewHolder
import cc.abase.demo.R
import cc.abase.demo.bean.wan.ArticleBean
import com.blankj.utilcode.util.TimeUtils
import kotlinx.android.synthetic.main.item_article.*

/**
 * Author:CASE
 * Date:2020-11-25
 * Time:15:05
 */
class ArticleItem : BaseItemView<ArticleBean>() {
  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResId() = R.layout.item_article
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="数据填充">
  override fun fillData(item: ArticleBean): BaseViewHolder.() -> Unit = {
    itemArticleCapterName.text = item.chapterName
    itemArticleTime.text = TimeUtils.millis2String(item.publishTime)
    itemArticleTitle.text = item.title
    itemArticleDes.text = item.desc
    itemArticleTitle.visibleGone(!item.title.isNullOrBlank())
    itemArticleDes.visibleGone(!item.desc.isNullOrBlank())
  }
  //</editor-fold>
}