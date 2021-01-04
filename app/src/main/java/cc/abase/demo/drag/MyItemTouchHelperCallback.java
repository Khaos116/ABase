package cc.abase.demo.drag;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;

/**
 * https://github.com/LidongWen/MultiTypeAdapter/blob/master/app/src/main/java/com/wenld/app_multitypeadapter/itemTouch/MyItemTouchHelperCallback.java
 * Created by wenld on 2017/4/23.
 */
public class MyItemTouchHelperCallback extends ItemTouchHelper.Callback {
  private ItemTouchMoveListener mMoveListener;
  private boolean canSwipe = false;

  private MyItemTouchHelperCallback() {
    //不允许外部无参构造
  }

  public MyItemTouchHelperCallback(ItemTouchMoveListener moveListener, boolean swipe) {
    this.mMoveListener = moveListener;
    this.canSwipe = swipe;
  }

  //Callback回调监听时先调用的，用来判断当前是什么动作，比如判断方向（意思就是我要监听哪个方向的拖动）
  @Override
  public int getMovementFlags(@NotNull RecyclerView recyclerView, @NotNull RecyclerView.ViewHolder holder) {
    //我要监听的拖拽方向是哪两个方向
    int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
    //我要监听的swipe侧滑方向是哪个方向
    int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
    boolean can = mMoveListener.canDrag(holder.getAdapterPosition());
    return makeMovementFlags(can ? dragFlags : 0, canSwipe ? swipeFlags : 0);
  }

  @Override
  public boolean isLongPressDragEnabled() {
    // 是否允许长按拖拽效果
    return true;
  }

  //当移动的时候回调的方法--拖拽
  @Override
  public boolean onMove(@NotNull RecyclerView recyclerView, RecyclerView.ViewHolder srcHolder,
      RecyclerView.ViewHolder targetHolder) {
    int from = srcHolder.getAdapterPosition();
    int to = targetHolder.getAdapterPosition();
    if (srcHolder.getItemViewType() != targetHolder.getItemViewType() || mMoveListener == null || !mMoveListener.canDrag(to)) {
      return false;
    }
    // 在拖拽的过程当中不断地调用adapter.notifyItemMoved(from,to);
    return mMoveListener.onItemMove(from, to);
  }

  //侧滑的时候回调的
  @SuppressLint("LogNotTimber")
  @Override
  public void onSwiped(@NotNull RecyclerView.ViewHolder holder, int arg1) {
    // 监听侧滑，1.删除数据；2.调用adapter.notifyItemRemove(position)
    if (mMoveListener != null) {
      boolean suc = mMoveListener.onItemRemove(holder.getAdapterPosition());
      Log.i("ItemTouchHelperCallback", "is delete success:" + suc);
    }
  }

  @Override
  public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
    //判断选中状态
    if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
      viewHolder.itemView.setBackgroundColor(Color.parseColor("#f7f7f7"));
    }
    super.onSelectedChanged(viewHolder, actionState);
  }

  @Override
  public void clearView(@NotNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
    // 恢复
    viewHolder.itemView.setBackgroundColor(Color.WHITE);
    int alpha = 1;
    viewHolder.itemView.setAlpha(alpha);//1~0
    viewHolder.itemView.setScaleX(alpha);//1~0
    viewHolder.itemView.setScaleY(alpha);//1~0
    super.clearView(recyclerView, viewHolder);
  }

  @Override
  public void onChildDraw(@NotNull Canvas c, @NotNull RecyclerView recyclerView,
      @NotNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState,
      boolean isCurrentlyActive) {
    //dX:水平方向移动的增量（负：往左；正：往右）范围：0~View.getWidth  0~1
    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
      //透明度动画
      float alpha = 1 - Math.abs(dX) / viewHolder.itemView.getWidth();
      viewHolder.itemView.setAlpha(alpha);//1~0
      viewHolder.itemView.setScaleX(alpha);//1~0
      viewHolder.itemView.setScaleY(alpha);//1~0
    }
    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
  }
}