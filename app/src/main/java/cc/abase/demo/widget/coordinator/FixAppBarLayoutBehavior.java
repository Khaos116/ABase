package cc.abase.demo.widget.coordinator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.OverScroller;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.blankj.utilcode.util.LogUtils;
import com.google.android.material.appbar.AppBarLayout;
import java.lang.reflect.Field;
import org.jetbrains.annotations.NotNull;

/**
 * Description:https://www.jianshu.com/p/7863310a4a6c
 *
 * @author: CASE
 * @date: 2019/11/18 18:25
 */
public class FixAppBarLayoutBehavior extends AppBarLayout.Behavior {

  private static final String TAG = "FixBehavior";
  private static final int TYPE_FLING = 1;
  private boolean isFlinging;
  private boolean shouldBlockNestedScroll;

  public FixAppBarLayoutBehavior(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public boolean onInterceptTouchEvent(@NotNull CoordinatorLayout parent, AppBarLayout child,
      MotionEvent ev) {
    LogUtils.d(TAG, "onInterceptTouchEvent:" + child.getTotalScrollRange());
    shouldBlockNestedScroll = isFlinging;
    if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {//手指触摸屏幕的时候停止fling事件
      stopAppbarLayoutFling(child);
    }
    return super.onInterceptTouchEvent(parent, child, ev);
  }

  /**
   * 停止appbarLayout的fling事件
   */
  private void stopAppbarLayoutFling(AppBarLayout appBarLayout) {
    //通过反射拿到HeaderBehavior中的flingRunnable变量
    try {
      Field flingRunnableField = getFlingRunnableField();
      Runnable flingRunnable;
      if (flingRunnableField != null) {
        flingRunnableField.setAccessible(true);
        flingRunnable = (Runnable) flingRunnableField.get(this);
        if (flingRunnable != null) {
          LogUtils.d(TAG, "存在flingRunnable");
          appBarLayout.removeCallbacks(flingRunnable);
          flingRunnableField.set(this, null);
        }
      }

      Field scrollerField = getScrollerField();
      if (scrollerField != null) {
        scrollerField.setAccessible(true);
        OverScroller overScroller = (OverScroller) scrollerField.get(this);
        if (overScroller != null && !overScroller.isFinished()) {
          overScroller.abortAnimation();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 反射获取私有的flingRunnable 属性，考虑support 28以后变量名修改的问题
   *
   * @return Field
   */
  private Field getFlingRunnableField() throws NoSuchFieldException {
    Class<?> superclass = this.getClass().getSuperclass();
    try {
      // support design 27及一下版本
      Class<?> headerBehaviorType = null;
      if (superclass != null) {
        headerBehaviorType = superclass.getSuperclass();
      }
      if (headerBehaviorType != null) {
        return headerBehaviorType.getDeclaredField("mFlingRunnable");
      } else {
        return null;
      }
    } catch (NoSuchFieldException e) {
      //e.printStackTrace();
      // 可能是28及以上版本
      Class<?> headerBehaviorType = superclass.getSuperclass().getSuperclass();
      if (headerBehaviorType != null) {
        return headerBehaviorType.getDeclaredField("flingRunnable");
      } else {
        return null;
      }
    }
  }

  /**
   * 反射获取私有的scroller 属性，考虑support 28以后变量名修改的问题
   *
   * @return Field
   */
  private Field getScrollerField() throws NoSuchFieldException {
    Class<?> superclass = this.getClass().getSuperclass();
    try {
      // support design 27及一下版本
      Class<?> headerBehaviorType = null;
      if (superclass != null) {
        headerBehaviorType = superclass.getSuperclass();
      }
      if (headerBehaviorType != null) {
        return headerBehaviorType.getDeclaredField("mScroller");
      } else {
        return null;
      }
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
      // 可能是28及以上版本
      Class<?> headerBehaviorType = superclass.getSuperclass().getSuperclass();
      if (headerBehaviorType != null) {
        return headerBehaviorType.getDeclaredField("scroller");
      } else {
        return null;
      }
    }
  }

  @Override
  public boolean onStartNestedScroll(@NotNull CoordinatorLayout parent, @NotNull AppBarLayout child,
      @NotNull View directTargetChild, View target,
      int nestedScrollAxes, int type) {
    LogUtils.d(TAG, "onStartNestedScroll");
    stopAppbarLayoutFling(child);
    return super.onStartNestedScroll(parent, child, directTargetChild, target,
        nestedScrollAxes, type);
  }

  @Override
  public void onNestedPreScroll(CoordinatorLayout coordinatorLayout,
      AppBarLayout child, View target,
      int dx, int dy, int[] consumed, int type) {
    LogUtils.d(TAG, "onNestedPreScroll:" + child.getTotalScrollRange()
        + " ,dx:" + dx + " ,dy:" + dy + " ,type:" + type);
    //type返回1时，表示当前target处于非touch的滑动，
    //该bug的引起是因为appbar在滑动时，CoordinatorLayout内的实现NestedScrollingChild2接口的滑动
    //子类还未结束其自身的fling
    //所以这里监听子类的非touch时的滑动，然后block掉滑动事件传递给AppBarLayout
    if (type == TYPE_FLING) {
      isFlinging = true;
    }
    if (!shouldBlockNestedScroll) {
      super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
    }
  }

  @Override
  public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, @NotNull AppBarLayout abl,
      View target, int type) {
    LogUtils.d(TAG, "onStopNestedScroll");
    super.onStopNestedScroll(coordinatorLayout, abl, target, type);
    isFlinging = false;
    shouldBlockNestedScroll = false;
  }

  @Override
  public void onNestedScroll(@NotNull CoordinatorLayout coordinatorLayout, AppBarLayout child,
      View target, int dxConsumed, int dyConsumed, int
      dxUnconsumed, int dyUnconsumed, int type) {
    LogUtils.d(TAG, "onNestedScroll: target:" + target.getClass() + " ,"
        + child.getTotalScrollRange() + " ,dxConsumed:"
        + dxConsumed + " ,dyConsumed:" + dyConsumed + " " + ",type:" + type);
    if (!shouldBlockNestedScroll) {
      super.onNestedScroll(coordinatorLayout, child, target, dxConsumed,
          dyConsumed, dxUnconsumed, dyUnconsumed, type);
    }
  }
}
