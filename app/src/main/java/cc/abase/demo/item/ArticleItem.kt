package cc.abase.demo.item

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import cc.ab.base.ext.click
import cc.ab.base.ext.visibleGone
import cc.ab.base.ui.item.BaseItemView
import cc.abase.demo.R
import cc.abase.demo.bean.wan.ArticleBean
import com.blankj.utilcode.util.TimeUtils
import kotlinx.android.synthetic.main.item_article.view.*

/**
 * Author:CASE
 * Date:2020-11-25
 * Time:15:05
 */
class ArticleItem(
    private val onItemClick: ((item: ArticleBean) -> Unit)? = null
) : BaseItemView<ArticleBean>() {

  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResId() = R.layout.item_article
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="数据填充">
  override fun fillData(holder: ViewHolder, itemView: View, item: ArticleBean) {
    itemView.itemArticleCapterName.text = item.chapterName
    itemView.itemArticleTime.text = TimeUtils.millis2String(item.publishTime)
    itemView.itemArticleTitle.text = item.title
    itemView.itemArticleDes.text = item.desc
    itemView.itemArticleTitle.visibleGone(!item.title.isNullOrBlank())
    itemView.itemArticleDes.visibleGone(!item.desc.isNullOrBlank())
    itemView.click { onItemClick?.invoke(item) }
  }
  //</editor-fold>
}