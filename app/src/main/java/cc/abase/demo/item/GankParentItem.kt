package cc.abase.demo.item

import android.annotation.SuppressLint
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import cc.ab.base.ext.click2Parent
import cc.ab.base.ui.item.BaseItemView
import cc.ab.base.ui.item.BaseViewHolder
import cc.abase.demo.R
import cc.abase.demo.bean.gank.GankAndroidBean
import cc.abase.demo.bean.local.GridImageBean
import com.drakeet.multitype.MultiTypeAdapter
import kotlinx.android.synthetic.main.item_gank_parent.itemGankParentRecycler

/**
 * Author:CASE
 * Date:2021年1月8日
 * Time:16:23:13
 */
class GankParentItem(
    private val onImgClick: ((url: String, position: Int, iv: ImageView, list: MutableList<String>) -> Unit)? = null,
) : BaseItemView<GankAndroidBean>() {
  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResId() = R.layout.item_gank_parent
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="数据填充">
  @SuppressLint("ClickableViewAccessibility")
  override fun fillData(item: GankAndroidBean): BaseViewHolder.() -> Unit = {
    val items = mutableListOf<Any>()
    items.add(item)
    if (item.imagesNoNull().isNotEmpty()) items.add(GridImageBean(url = item.url ?: "", list = item.imagesNoNull()))
    val recyclerView = itemGankParentRecycler
    recyclerView.click2Parent(itemView)
    recyclerView.adapter = null
    recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
    val multiTypeAdapter = MultiTypeAdapter()
    multiTypeAdapter.register(GankItem())
    multiTypeAdapter.register(NineGridItem(parentView = itemView) { url, p, iv, list -> onImgClick?.invoke(url, p, iv, list) })
    recyclerView.adapter = multiTypeAdapter
    multiTypeAdapter.items = items
    multiTypeAdapter.notifyDataSetChanged()
  }
  //</editor-fold>
}