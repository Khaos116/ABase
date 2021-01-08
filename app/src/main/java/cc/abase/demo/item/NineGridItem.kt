package cc.abase.demo.item

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import cc.ab.base.ui.item.BaseItemView
import cc.abase.demo.R
import cc.abase.demo.bean.local.GridImageBean
import cc.abase.demo.drag.GridItemTouchHelperCallback
import cc.abase.demo.widget.decoration.GridSpaceItemDecoration
import com.blankj.utilcode.util.SizeUtils
import com.drakeet.multitype.MultiTypeAdapter
import kotlinx.android.synthetic.main.item_nine_grid.view.itemNineGridRecycler

/**
 * Author:CASE
 * Date:2020-11-25
 * Time:15:46
 */
class NineGridItem(
    private val onItemImgClick: ((url: String, position: Int, iv: ImageView, list: MutableList<String>) -> Unit)? = null,
    private val onItemClick: ((url: String) -> Unit)? = null,
) : BaseItemView<GridImageBean>() {
  //<editor-fold defaultstate="collapsed" desc="变量">
  //Item间距
  private val spaceItem = SizeUtils.dp2px(6f)

  //拖拽效果
  private var mapHelper: MutableMap<Int, ItemTouchHelper> = hashMapOf()
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResId() = R.layout.item_nine_grid
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="数据填充">
  @SuppressLint("ClickableViewAccessibility")
  override fun fillData(holder: ViewHolder, itemView: View, item: GridImageBean) {
    val recyclerView = itemView.itemNineGridRecycler
    recyclerView.setOnTouchListener { _, event -> itemView.onTouchEvent(event) }
    val list = item.list
    val count = if (list.size == 1) 1 else if (list.size == 2 || list.size == 4) 2 else 3
    recyclerView.layoutManager = GridLayoutManager(itemView.context, count)
    if (recyclerView.itemDecorationCount > 0) recyclerView.removeItemDecorationAt(0)
    recyclerView.addItemDecoration(GridSpaceItemDecoration(spaceItem).setDragGridEdge(false))
    val multiTypeAdapter = MultiTypeAdapter()
    multiTypeAdapter.register(NineImgItem(onItemClick = { url, position, iv -> onItemImgClick?.invoke(url, position, iv, list) }))
    recyclerView.adapter = multiTypeAdapter
    multiTypeAdapter.items = list
    multiTypeAdapter.notifyDataSetChanged()
    //点击事件会导致拖拽无法触发
    //itemView.click { onItemClick?.invoke(item.url) }
    //拖拽开始---->>>先置空，防止复用的时候一样的RecyclerView导致不执行attachToRecyclerView
    mapHelper[recyclerView.hashCode()]?.attachToRecyclerView(null)
    ItemTouchHelper(GridItemTouchHelperCallback(multiTypeAdapter))
        .apply { attachToRecyclerView(recyclerView) }
        .let { mapHelper[recyclerView.hashCode()] = it }
    //拖拽结束---<<<
  }
  //</editor-fold>
}