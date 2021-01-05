package cc.abase.demo.drag

import android.graphics.Color
import androidx.recyclerview.widget.*
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.drakeet.multitype.MultiTypeAdapter
import java.util.Collections

/**
 * 参考：https://github.com/CuiChenbo/DragRecyclerViewDemo/blob/master/app/src/main/java/com/ccb/pactera/dragrecyclerviewdemo/MainActivity.java
 * Author:CASE
 * Date:2021/1/5
 * Time:13:38
 */
class GridItemTouchHelperCallback(
    private val mAdapter: MultiTypeAdapter,
    private val normalBgColor: Int = 0,
    private val dragBgColor: Int = 0,
    private val dragStart: (() -> Unit)? = null,
    private val dragEnd: (() -> Unit)? = null,
    //特定位置是否可以交换和拖动(默认全部可以拖拽)
    private val canMove: ((position: Int) -> Boolean)? = null,
) : ItemTouchHelper.Callback() {
  //<editor-fold defaultstate="collapsed" desc="拖拽方向判断">
  override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: ViewHolder): Int {
    var dragFlag = 0 //0代表不允许拖拽
    val ok = canMove?.invoke(viewHolder.adapterPosition) ?: true
    if (ok && recyclerView.layoutManager is GridLayoutManager) {
      dragFlag = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    }
    return makeMovementFlags(dragFlag, 0)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="拖拽数据交换">
  override fun onMove(recyclerView: RecyclerView, viewHolder: ViewHolder, target: ViewHolder): Boolean {
    //得到当拖拽的viewHolder的Position
    val fromPosition = viewHolder.adapterPosition
    //拿到当前拖拽到的item的viewHolder
    val toPosition = target.adapterPosition
    //类型不同或者进行拖拽，则返回false
    if (viewHolder.itemViewType != target.itemViewType || canMove?.invoke(toPosition) == false) {
      return false
    }
    //遍历数据交换
    if (fromPosition < toPosition) {
      for (i in fromPosition until toPosition) Collections.swap(mAdapter.items, i, i + 1)
    } else {
      for (i in fromPosition downTo toPosition + 1) Collections.swap(mAdapter.items, i, i - 1)
    }
    //交换刷新
    mAdapter.notifyItemMoved(fromPosition, toPosition)
    return true
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="删除处理">
  //不需要侧滑删除，不做处理
  override fun onSwiped(viewHolder: ViewHolder, direction: Int) {}
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="拖拽时背景设置">
  override fun onSelectedChanged(viewHolder: ViewHolder?, actionState: Int) {
    //ItemTouchHelper.ACTION_STATE_IDLE   闲置状态
    //ItemTouchHelper.ACTION_STATE_SWIPE  滑动中状态
    //ItemTouchHelper.ACTION_STATE_DRAG   拖拽中状态
    //选中状态
    if (dragBgColor != 0 && actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
      viewHolder?.itemView?.setBackgroundColor(dragBgColor)
    }
    super.onSelectedChanged(viewHolder, actionState)
    if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) dragStart?.invoke()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="拖拽完成后恢复背景">
  override fun clearView(recyclerView: RecyclerView, viewHolder: ViewHolder) {
    //操作完毕后恢复颜色
    viewHolder.itemView.setBackgroundColor(if (normalBgColor != 0) normalBgColor else Color.TRANSPARENT)
    super.clearView(recyclerView, viewHolder)
    dragEnd?.invoke()
  }
  //</editor-fold>
}