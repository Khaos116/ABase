package cc.abase.demo.widget.decoration;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * 给 GridLayoutManager or StaggeredGridLayoutManager 设置间距，可设置去除首尾间距个数
 *
 * @author jingbin
 * 原文地址：https://github.com/youlookwhat/ByRecyclerView
 *
 * 修改By CASE 2020年11月21日17:52:11(注意拖拽"上"+"下"会有1/2的间距)
 * 是否处于第一行和最后一行参考：https://github.com/airbnb/epoxy/blob/master/epoxy-adapter/src/main/java/com/airbnb/epoxy/EpoxyItemSpacingDecorator.java
 */
public class GridSpaceItemDecoration extends RecyclerView.ItemDecoration {
  /**
   * 每行个数
   */
  private int mSpanCount;
  /**
   * 间距
   */
  private int mSpacing;
  /**
   * 距屏幕周围是否也有间距
   */
  private boolean mIncludeEdge;
  /**
   * 头部 不显示间距的item个数
   */
  private int mStartFromSize;
  /**
   * 尾部 不显示间距的item个数 默认不处理最后一个item的间距
   */
  private int mEndFromSize = 0;//CASE修改为默认0
  /**
   * 瀑布流 头部第一个整行的position
   */
  private int fullPosition = -1;

  public GridSpaceItemDecoration(int spacing) {
    this(spacing, true);
  }

  /**
   * @param spacing item 间距
   * @param includeEdge item 距屏幕周围是否也有间距
   */
  public GridSpaceItemDecoration(int spacing, boolean includeEdge) {
    this.mSpacing = spacing;
    this.mIncludeEdge = includeEdge;
  }

  /**
   * 已不需要手动设置spanCount
   */
  @Deprecated
  public GridSpaceItemDecoration(int spanCount, int spacing) {
    this(spanCount, spacing, true);
  }

  /**
   * 已不需要手动设置spanCount
   */
  @Deprecated
  public GridSpaceItemDecoration(int spanCount, int spacing, boolean includeEdge) {
    this.mSpanCount = spanCount;
    this.mSpacing = spacing;
    this.mIncludeEdge = includeEdge;
  }

  @Override
  public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent,
      @NonNull RecyclerView.State state) {
    //<editor-fold defaultstate="collapsed" desc="CASE添加的代码1">
    outRect.setEmpty();
    int position = parent.getChildAdapterPosition(view);
    if (position == RecyclerView.NO_POSITION) {
      // View is not shown
      return;
    }
    RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
    calculatePositionDetails(parent, position, layoutManager);
    //</editor-fold>
    int lastPosition = state.getItemCount() - 1;
    if (mStartFromSize <= position && position <= lastPosition - mEndFromSize) {
      // 行
      int spanGroupIndex = -1;
      // 列
      int column = 0;
      // 瀑布流是否占满一行
      boolean fullSpan = false;
      if (layoutManager instanceof GridLayoutManager) {
        GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
        GridLayoutManager.SpanSizeLookup spanSizeLookup = gridLayoutManager.getSpanSizeLookup();
        int spanCount = gridLayoutManager.getSpanCount();
        // 当前position的spanSize
        int spanSize = spanSizeLookup.getSpanSize(position);
        // 一行几个
        mSpanCount = spanCount / spanSize;
        // =0 表示是最左边 0 2 4
        int spanIndex = spanSizeLookup.getSpanIndex(position, spanCount);
        // 列
        column = spanIndex / spanSize;
        // 行 减去mStartFromSize,得到从0开始的行
        spanGroupIndex = spanSizeLookup.getSpanGroupIndex(position, spanCount) - mStartFromSize;
      } else if (layoutManager instanceof StaggeredGridLayoutManager) {
        // 瀑布流获取列方式不一样
        StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
        // 列
        column = params.getSpanIndex();
        // 是否是全一行
        fullSpan = params.isFullSpan();
        mSpanCount = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
      }
      // 减掉不设置间距的position,得到从0开始的position
      position = position - mStartFromSize;
      if (mIncludeEdge || isIncludeStartEnd) {//CASE添加的代码2"||isIncludeStartEnd"
        /*
         *示例：
         * spacing = 10 ；spanCount = 3
         * ---------10--------
         * 10   3+7   6+4    10
         * ---------10--------
         * 10   3+7   6+4    10
         * ---------10--------
         */
        if (fullSpan) {
          outRect.left = 0;
          outRect.right = 0;
        } else {
          outRect.left = mSpacing - column * mSpacing / mSpanCount;
          outRect.right = (column + 1) * mSpacing / mSpanCount;
        }
        if (spanGroupIndex > -1) {
          // grid 显示规则
          if (spanGroupIndex < 1 && position < mSpanCount) {
            // 第一行才有上间距
            outRect.top = mSpacing;
          }
        } else {
          if (fullPosition == -1 && position < mSpanCount && fullSpan) {
            // 找到头部第一个整行的position，后面的上间距都不显示
            fullPosition = position;
          }
          // Stagger显示规则 头部没有整行或者头部体验整行但是在之前的position显示上间距
          boolean isFirstLineStagger = (fullPosition == -1 || position < fullPosition) && (position < mSpanCount);
          if (isFirstLineStagger) {
            // 第一行才有上间距
            outRect.top = mSpacing;
          }
        }
        outRect.bottom = mSpacing;
      } else {
        /*
         *示例：
         * spacing = 10 ；spanCount = 3
         * --------0--------
         * 0   3+7   6+4    0
         * -------10--------
         * 0   3+7   6+4    0
         * --------0--------
         */
        if (fullSpan) {
          outRect.left = 0;
          outRect.right = 0;
        } else {
          outRect.left = column * mSpacing / mSpanCount;
          outRect.right = mSpacing - (column + 1) * mSpacing / mSpanCount;
        }
        if (spanGroupIndex > -1) {
          if (spanGroupIndex >= 1) {
            // 超过第0行都显示上间距
            outRect.top = mSpacing;
          }
        } else {
          if (fullPosition == -1 && position < mSpanCount && fullSpan) {
            // 找到头部第一个整行的position
            fullPosition = position;
          }
          // Stagger上间距显示规则
          boolean isStaggerShowTop =
              position >= mSpanCount || (fullSpan && position != 0) || (fullPosition != -1 && position != 0);
          if (isStaggerShowTop) {
            // 超过第0行都显示上间距
            outRect.top = mSpacing;
          }
        }
      }
    }
    //<editor-fold defaultstate="collapsed" desc="CASE添加的代码3">
    if (layoutManager instanceof GridLayoutManager) {//只处理上下间距，左右交给原本处理即可
      if (isDragGrid) {
        outRect.top = mSpacing / 2;
        outRect.bottom = mSpacing / 2;
      } else {
        outRect.top = isInFirstRow ? (isIncludeTop ? mSpacing : 0) : mSpacing / 2;
        outRect.bottom = isInLastRow ? (isIncludeBottom ? mSpacing : 0) : mSpacing / 2;
      }
    }
    //</editor-fold>
  }

  /**
   * 设置从哪个位置 开始设置间距
   *
   * @param startFromSize 一般为HeaderView的个数 + 刷新布局(不一定设置)
   */
  public GridSpaceItemDecoration setStartFrom(int startFromSize) {
    this.mStartFromSize = startFromSize;
    return this;
  }

  /**
   * 设置从哪个位置 结束设置间距。默认为1，默认用户设置了上拉加载
   *
   * @param endFromSize 一般为FooterView的个数 + 加载更多布局(不一定设置)
   */
  public GridSpaceItemDecoration setEndFromSize(int endFromSize) {
    this.mEndFromSize = endFromSize;
    return this;
  }

  /**
   * 设置从哪个位置 结束设置间距
   *
   * @param startFromSize 一般为HeaderView的个数 + 刷新布局(不一定设置)
   * @param endFromSize 默认为1，一般为FooterView的个数 + 加载更多布局(不一定设置)
   */
  public GridSpaceItemDecoration setNoShowSpace(int startFromSize, int endFromSize) {
    this.mStartFromSize = startFromSize;
    this.mEndFromSize = endFromSize;
    return this;
  }

  //<editor-fold defaultstate="collapsed" desc="CASE添加的代码4">
  private boolean isInFirstRow;//是否在第一行
  private boolean isInLastRow;//是否在最后一行，如果只有一行isInFirstRow和isInLastRow都为true
  private boolean isIncludeStartEnd;//是否包含最前面和最后面
  private boolean isIncludeTop;//是否包含最上面
  private boolean isIncludeBottom;//是否包含最下面
  private boolean isDragGrid;//是否是可拖动的grid模式，如果为true则最顶部和最底部默认也是有间距的

  public GridSpaceItemDecoration setDragGridEdge(boolean includeStartEnd) {
    this.isDragGrid = true;
    this.isIncludeStartEnd = includeStartEnd;
    this.mIncludeEdge = includeStartEnd;
    this.isIncludeTop = false;
    this.isIncludeBottom = false;
    mEndFromSize = 0;
    return this;
  }

  public GridSpaceItemDecoration setNoDragGridEdge(boolean includeStartEnd, boolean includeTop, boolean includeBottom) {
    this.isDragGrid = false;
    this.isIncludeStartEnd = includeStartEnd;
    this.mIncludeEdge = includeStartEnd;
    this.isIncludeTop = includeTop;
    this.isIncludeBottom = includeBottom;
    mEndFromSize = 0;
    return this;
  }

  private void calculatePositionDetails(RecyclerView parent, int position, RecyclerView.LayoutManager layout) {
    if (layout instanceof GridLayoutManager) {
      int itemCount = parent.getAdapter() == null ? 0 : parent.getAdapter().getItemCount();
      GridLayoutManager grid = (GridLayoutManager) layout;
      final GridLayoutManager.SpanSizeLookup spanSizeLookup = grid.getSpanSizeLookup();
      int spanCount = grid.getSpanCount();
      isInFirstRow = isInFirstRow(position, spanSizeLookup, spanCount);
      isInLastRow = isInLastRow(position, itemCount, spanSizeLookup, spanCount);
    }
  }

  private boolean isInFirstRow(int position, GridLayoutManager.SpanSizeLookup spanSizeLookup, int spanCount) {
    int totalSpan = 0;
    for (int i = 0; i <= position; i++) {
      totalSpan += spanSizeLookup.getSpanSize(i);
      if (totalSpan > spanCount) {
        return false;
      }
    }
    return true;
  }

  private boolean isInLastRow(int position, int itemCount, GridLayoutManager.SpanSizeLookup spanSizeLookup, int spanCount) {
    int totalSpan = 0;
    for (int i = itemCount - 1; i >= position; i--) {
      totalSpan += spanSizeLookup.getSpanSize(i);
      if (totalSpan > spanCount) {
        return false;
      }
    }
    return true;
  }
  //</editor-fold>
}