package cc.ab.base.widget.discretescrollview;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by yarolegovich on 10/25/17.
 */
public class RecyclerViewProxy {

  private RecyclerView.LayoutManager layoutManager;

  public RecyclerViewProxy(@NonNull RecyclerView.LayoutManager layoutManager) {
    this.layoutManager = layoutManager;
  }

  public void attachView(View view) {
    layoutManager.attachView(view);
  }

  public void detachView(View view) {
    layoutManager.detachView(view);
  }

  public void detachAndScrapView(View view, RecyclerView.Recycler recycler) {
    layoutManager.detachAndScrapView(view, recycler);
  }

  public void detachAndScrapAttachedViews(RecyclerView.Recycler recycler) {
    layoutManager.detachAndScrapAttachedViews(recycler);
  }

  public void recycleView(View view, RecyclerView.Recycler recycler) {
    recycler.recycleView(view);
  }

  public void removeAndRecycleAllViews(RecyclerView.Recycler recycler) {
    layoutManager.removeAndRecycleAllViews(recycler);
  }

  public int getChildCount() {
    return layoutManager.getChildCount();
  }

  public int getItemCount() {
    return layoutManager.getItemCount();
  }

  public View getMeasuredChildForAdapterPosition(int position, RecyclerView.Recycler recycler) {
    View view = recycler.getViewForPosition(position);
    layoutManager.addView(view);
    layoutManager.measureChildWithMargins(view, 0, 0);
    return view;
  }

  public void layoutDecoratedWithMargins(View v, float l, float t, float r, float b) {
    float left = l;
    float top = t;
    float right = r;
    float bottom = b;
    //纠正1px
    if (((int) top) * 1f != top) {
      top = top + 0.5f;
      bottom = bottom + 0.5f;
    }
    if (((int) left) * 1f != left) {
      left = left + 0.5f;
      right = right + 0.5f;
    }
    layoutManager.layoutDecoratedWithMargins(v, (int) left, (int) top, (int) right, (int) bottom);
  }

  public View getChildAt(int index) {
    return layoutManager.getChildAt(index);
  }

  public int getPosition(View view) {
    return layoutManager.getPosition(view);
  }

  public int getMeasuredWidthWithMargin(View child) {
    ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
    return layoutManager.getDecoratedMeasuredWidth(child) + lp.leftMargin + lp.rightMargin;
  }

  public int getMeasuredHeightWithMargin(View child) {
    ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
    return layoutManager.getDecoratedMeasuredHeight(child) + lp.topMargin + lp.bottomMargin;
  }

  public int getWidth() {
    return layoutManager.getWidth();
  }

  public int getHeight() {
    return layoutManager.getHeight();
  }

  public void offsetChildrenHorizontal(int amount) {
    layoutManager.offsetChildrenHorizontal(amount);
  }

  public void offsetChildrenVertical(int amount) {
    layoutManager.offsetChildrenVertical(amount);
  }

  public void requestLayout() {
    layoutManager.requestLayout();
  }

  public void startSmoothScroll(RecyclerView.SmoothScroller smoothScroller) {
    layoutManager.startSmoothScroll(smoothScroller);
  }

  public void removeAllViews() {
    layoutManager.removeAllViews();
  }
}