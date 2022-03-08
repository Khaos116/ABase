package cc.abase.demo.item

import cc.ab.base.ui.item.BaseBindItemView
import cc.ab.base.ui.item.BaseViewHolder
import cc.abase.demo.bean.readhub.TopicBean
import cc.abase.demo.databinding.ItemReadhubBinding

/**
 * Author:Khaos116
 * Date:2022/3/8
 * Time:18:06
 */
class ReadhubItem : BaseBindItemView<TopicBean, ItemReadhubBinding>() {
  //<editor-fold defaultstate="collapsed" desc="数据填充">
  override fun fillData(holder: BaseViewHolder<ItemReadhubBinding>, item: TopicBean) {
    val viewBinding = holder.viewBinding
    if (item.updatedAt.isNullOrBlank()) {
      viewBinding.itemTopicTime.text = ""
    } else {
      viewBinding.itemTopicTime.text = cc.abase.demo.utils.TimeUtils.utc2Local(item.updatedAt)
    }
    viewBinding.itemTopicTitle.text = item.newsArray.lastOrNull()?.title?.trim() ?: ""
    viewBinding.itemTopicDes.text = item.summary?.trim()
  }
  //</editor-fold>
}