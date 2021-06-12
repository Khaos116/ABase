package cc.abase.demo.item

import android.view.LayoutInflater
import android.view.ViewGroup
import cc.ab.base.ext.visibleGone
import cc.ab.base.ui.item.BaseBindItemView
import cc.ab.base.ui.item.BaseViewHolder
import cc.abase.demo.bean.wan.ArticleBean
import cc.abase.demo.databinding.ItemArticleBinding
import com.blankj.utilcode.util.TimeUtils

/**
 * Author:Khaos
 * Date:2020-11-25
 * Time:15:05
 */
class ArticleItem : BaseBindItemView<ArticleBean, ItemArticleBinding>() {
  //<editor-fold defaultstate="collapsed" desc="数据填充">
  override fun fillData(holder: BaseViewHolder<ItemArticleBinding>, item: ArticleBean) {
    val viewBinding = holder.viewBinding
    viewBinding.itemArticleCapterName.text = item.chapterName
    viewBinding.itemArticleTime.text = TimeUtils.millis2String(item.publishTime)
    viewBinding.itemArticleTitle.text = item.title
    viewBinding.itemArticleDes.text = item.desc
    viewBinding.itemArticleTitle.visibleGone(!item.title.isNullOrBlank())
    viewBinding.itemArticleDes.visibleGone(!item.desc.isNullOrBlank())
  }
  //</editor-fold>
}