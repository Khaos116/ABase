package cc.ab.base.widget.discretescrollview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.*;
import androidx.recyclerview.widget.RecyclerView;
import cc.ab.base.R;
import cc.ab.base.widget.discretescrollview.transform.DiscreteScrollItemTransformer;
import cc.ab.base.widget.discretescrollview.util.ScrollListenerAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yarolegovich on 18.02.2017.
 */
@SuppressWarnings("unchecked")
public class DiscreteScrollView extends RecyclerView {

  public static final int NO_POSITION = DiscreteScrollLayoutManager.NO_POSITION;

  private static final int DEFAULT_ORIENTATION = DSVOrientation.HORIZONTAL.ordinal();

  private DiscreteScrollLayoutManager layoutManager;

  private List<ScrollStateChangeListener> scrollStateChangeListeners;
  private List<OnItemChangedListener> onItemChangedListeners;

  private boolean isOverScrollEnabled;

  public DiscreteScrollView(Context context) {
    super(context);
    init(null);
  }

  public DiscreteScrollView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(attrs);
  }

  public DiscreteScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(attrs);
  }

  private void init(AttributeSet attrs) {
    scrollStateChangeListeners = new ArrayList<>();
    onItemChangedListeners = new ArrayList<>();

    int orientation = DEFAULT_ORIENTATION;
    if (attrs != null) {
      TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.DiscreteScrollView);
      orientation = ta.getInt(R.styleable.DiscreteScrollView_dsv_orientation, DEFAULT_ORIENTATION);
      ta.recycle();
    }

    isOverScrollEnabled = getOverScrollMode() != OVER_SCROLL_NEVER;

    layoutManager = new DiscreteScrollLayoutManager(
        getContext(), new ScrollStateListener(),
        DSVOrientation.values()[orientation]);
    setLayoutManager(layoutManager);
  }

  @Override
  public void setLayoutManager(LayoutManager layout) {
    if (layout instanceof DiscreteScrollLayoutManager) {
      super.setLayoutManager(layout);
    } else {
      throw new IllegalArgumentException("You should not set LayoutManager on DiscreteScrollView.class instance. Library uses a special one. Just don\\'t call the method.");
    }
  }


  @Override
  public boolean fling(int velocityX, int velocityY) {
    boolean isFling = super.fling(velocityX, velocityY);
    if (isFling) {
      layoutManager.onFling(velocityX, velocityY);
    } else {
      layoutManager.returnToCurrentPosition();
    }
    return isFling;
  }

  @Nullable
  public ViewHolder getViewHolder(int position) {
    View view = layoutManager.findViewByPosition(position);
    return view != null ? getChildViewHolder(view) : null;
  }

  /**
   * @return adapter position of the current item or -1 if nothing is selected
   */
  public int getCurrentItem() {
    return layoutManager.getCurrentPosition();
  }

  public void setItemTransformer(DiscreteScrollItemTransformer transformer) {
    layoutManager.setItemTransformer(transformer);
  }

  public void setItemTransitionTimeMillis(@IntRange(from = 10) int millis) {
    layoutManager.setTimeForItemSettle(millis);
  }

  public void setSlideOnFling(boolean result){
    layoutManager.setShouldSlideOnFling(result);
  }

  public void setSlideOnFlingThreshold(int threshold){
    layoutManager.setSlideOnFlingThreshold(threshold);
  }

  public void setOrientation(DSVOrientation orientation) {
    layoutManager.setOrientation(orientation);
  }

  public void setOffscreenItems(int items) {
    layoutManager.setOffscreenItems(items);
  }

  public void setClampTransformProgressAfter(@IntRange(from = 1) int itemCount) {
    if (itemCount <= 1) {
      throw new IllegalArgumentException("must be >= 1");
    }
    layoutManager.setTransformClampItemCount(itemCount);
  }

  public void setOverScrollEnabled(boolean overScrollEnabled) {
    isOverScrollEnabled = overScrollEnabled;
    setOverScrollMode(OVER_SCROLL_NEVER);
  }

  public void addScrollStateChangeListener(@NonNull ScrollStateChangeListener<?> scrollStateChangeListener) {
    scrollStateChangeListeners.add(scrollStateChangeListener);
  }

  public void addScrollListener(@NonNull ScrollListener<?> scrollListener) {
    addScrollStateChangeListener(new ScrollListenerAdapter(scrollListener));
  }

  public void addOnItemChangedListener(@NonNull OnItemChangedListener<?> onItemChangedListener) {
    onItemChangedListeners.add(onItemChangedListener);
  }

  public void removeScrollStateChangeListener(@NonNull ScrollStateChangeListener<?> scrollStateChangeListener) {
    scrollStateChangeListeners.remove(scrollStateChangeListener);
  }

  public void removeScrollListener(@NonNull ScrollListener<?> scrollListener) {
    removeScrollStateChangeListener(new ScrollListenerAdapter<>(scrollListener));
  }

  public void removeItemChangedListener(@NonNull OnItemChangedListener<?> onItemChangedListener) {
    onItemChangedListeners.remove(onItemChangedListener);
  }

  private void notifyScrollStart(ViewHolder holder, int current) {
    for (ScrollStateChangeListener listener : scrollStateChangeListeners) {
      listener.onScrollStart(holder, current);
    }
  }

  private void notifyScrollEnd(ViewHolder holder, int current) {
    for (ScrollStateChangeListener listener : scrollStateChangeListeners) {
      listener.onScrollEnd(holder, current);
    }
  }

  private void notifyScroll(float position,
      int currentIndex, int newIndex,
      ViewHolder currentHolder, ViewHolder newHolder) {
    for (ScrollStateChangeListener listener : scrollStateChangeListeners) {
      listener.onScroll(position, currentIndex, newIndex,
          currentHolder,
          newHolder);
    }
  }

  //因为预览和最后确定选中会回调2次，所以使用的时候进行判断
  private int lastPosition = 0;
  private void notifyCurrentItemChanged(ViewHolder holder, int current) {
    if (lastPosition == current) return;
    lastPosition = current;
    for (OnItemChangedListener listener : onItemChangedListeners) {
      listener.onCurrentItemChanged(holder, current);
    }
  }

  private void notifyCurrentItemChanged() {
    if (onItemChangedListeners.isEmpty()) {
      return;
    }
    int current = layoutManager.getCurrentPosition();
    ViewHolder currentHolder = getViewHolder(current);
    notifyCurrentItemChanged(currentHolder, current);
  }

  private class ScrollStateListener implements DiscreteScrollLayoutManager.ScrollStateListener {

    @Override
    public void onIsBoundReachedFlagChange(boolean isBoundReached) {
      if (isOverScrollEnabled) {
        setOverScrollMode(isBoundReached ? OVER_SCROLL_ALWAYS : OVER_SCROLL_NEVER);
      }
    }

    @Override
    public void onScrollStart() {
      if (scrollStateChangeListeners.isEmpty()) {
        return;
      }
      int current = layoutManager.getCurrentPosition();
      ViewHolder holder = getViewHolder(current);
      if (holder != null) {
        notifyScrollStart(holder, current);
      }
    }

    @Override
    public void onScrollEnd() {
      if (onItemChangedListeners.isEmpty() && scrollStateChangeListeners.isEmpty()) {
        return;
      }
      int current = layoutManager.getCurrentPosition();
      ViewHolder holder = getViewHolder(current);
      if (holder != null) {
        notifyScrollEnd(holder, current);
        notifyCurrentItemChanged(holder, current);
      }
    }

    //是否执行预览
    private boolean preview = false;
    @Override
    public void onScroll(float currentViewPosition) {
      //==新增，手指不放，滑动到完全显示执行回调(★★这只是预览回调，手指放开才是真的回调★★)==//
      if (currentViewPosition > 0.5f && !preview) {//上一页
        preview = true;
        int position = getCurrentItem() - 1;
        ViewHolder holder = getViewHolder(position);
        notifyCurrentItemChanged(holder, position);
      } else if (currentViewPosition < -0.5f && !preview) {//下一页
        preview = true;
        int position = getCurrentItem() + 1;
        ViewHolder holder = getViewHolder(position);
        notifyCurrentItemChanged(holder, position);
      } else if (currentViewPosition > -0.5f && currentViewPosition < 0.5f && preview) {//当前页
        preview = false;
        int position = getCurrentItem();
        ViewHolder holder = getViewHolder(position);
        notifyCurrentItemChanged(holder, position);
      }
      //===============================================================================//

      if (scrollStateChangeListeners.isEmpty()) {
        return;
      }
      int currentIndex = getCurrentItem();
      int newIndex = layoutManager.getNextPosition();
      if (currentIndex != newIndex) {
        notifyScroll(currentViewPosition,
            currentIndex, newIndex,
            getViewHolder(currentIndex),
            getViewHolder(newIndex));
      }
    }

    @Override
    public void onCurrentViewFirstLayout() {
      post(new Runnable() {
        @Override
        public void run() {
          notifyCurrentItemChanged();
        }
      });
    }

    @Override
    public void onDataSetChangeChangedPosition() {
      notifyCurrentItemChanged();
    }
  }

  public interface ScrollStateChangeListener<T extends ViewHolder> {

    void onScrollStart(@NonNull T currentItemHolder, int adapterPosition);

    void onScrollEnd(@NonNull T currentItemHolder, int adapterPosition);

    void onScroll(float scrollPosition,
        int currentPosition,
        int newPosition,
        @Nullable T currentHolder,
        @Nullable T newCurrent);
  }

  public interface ScrollListener<T extends ViewHolder> {

    void onScroll(float scrollPosition,
        int currentPosition, int newPosition,
        @Nullable T currentHolder,
        @Nullable T newCurrent);
  }

  public interface OnItemChangedListener<T extends ViewHolder> {
    /*
     * This method will be also triggered when view appears on the screen for the first time.
     * If data set is empty, viewHolder will be null and adapterPosition will be NO_POSITION
     */
    void onCurrentItemChanged(@Nullable T viewHolder, int adapterPosition);
  }
}