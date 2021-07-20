package cc.abase.demo.item

import android.annotation.SuppressLint
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import cc.ab.base.ext.click2Parent
import cc.ab.base.ui.item.BaseBindItemView
import cc.ab.base.ui.item.BaseViewHolder
import cc.abase.demo.bean.gank.GankAndroidBean
import cc.abase.demo.bean.local.GridImageBean
import cc.abase.demo.databinding.ItemGankParentBinding
import com.drakeet.multitype.MultiTypeAdapter

/**
 * Author:Khaos
 * Date:2021年1月8日
 * Time:16:23:13
 */
class GankParentItem(
  private val onImgClick: ((url: String, position: Int, iv: ImageView, list: MutableList<String>) -> Unit)? = null,
) : BaseBindItemView<GankAndroidBean, ItemGankParentBinding>() {
  //<editor-fold defaultstate="collapsed" desc="数据填充">
  @SuppressLint("ClickableViewAccessibility")
  override fun fillData(holder: BaseViewHolder<ItemGankParentBinding>, item: GankAndroidBean) {
    val viewBinding = holder.viewBinding
    val items = mutableListOf<Any>()
    items.add(item)
    if (item.imagesNoNull().isNotEmpty()) items.add(GridImageBean(url = item.url ?: "", list = item.imagesNoNull()))
    val recyclerView = viewBinding.itemGankParentRecycler
    recyclerView.click2Parent(holder.itemView)
    recyclerView.adapter = null
    recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
    val multiTypeAdapter = MultiTypeAdapter()
    multiTypeAdapter.register(GankItem(holder.itemView))
    multiTypeAdapter.register(NineGridItem(parentView = holder.itemView) { url, p, iv, list -> onImgClick?.invoke(url, p, iv, list) })
    recyclerView.adapter = multiTypeAdapter
    multiTypeAdapter.items = items
    multiTypeAdapter.notifyDataSetChanged()
  }
  //</editor-fold>
}