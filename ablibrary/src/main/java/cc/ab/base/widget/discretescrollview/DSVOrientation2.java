package cc.ab.base.widget.discretescrollview;

import android.graphics.PointF;
import android.view.View;

/**
 * Author:Khaos116
 * Date:2023/7/3
 * Time:18:17
 */
public class DSVOrientation2 {
  public static Helper createHelper(DSVOrientation orientation) {
    if (orientation == DSVOrientation.HORIZONTAL) {
      return new HorizontalHelper();
    } else {
      return new VerticalHelper();
    }
  }

  interface Helper {

    int getViewEnd(int recyclerWidth, int recyclerHeight);

    int getDistanceToChangeCurrent(int childWidth, int childHeight);

    void setCurrentViewCenter(PointF recyclerCenter, int scrolled, PointF outPoint);

    void shiftViewCenter(Direction direction, int shiftAmount, PointF outCenter);

    int getFlingVelocity(int velocityX, int velocityY);

    int getPendingDx(int pendingScroll);

    int getPendingDy(int pendingScroll);

    void offsetChildren(int amount, RecyclerViewProxy lm);

    float getDistanceFromCenter(PointF center, float viewCenterX, float viewCenterY);

    boolean isViewVisible(PointF center, float halfWidth, float halfHeight, float endBound, float extraSpace);

    boolean hasNewBecomeVisible(DiscreteScrollLayoutManager lm);

    boolean canScrollVertically();

    boolean canScrollHorizontally();
  }

  protected static class HorizontalHelper implements Helper {

    @Override
    public int getViewEnd(int recyclerWidth, int recyclerHeight) {
      return recyclerWidth;
    }

    @Override
    public int getDistanceToChangeCurrent(int childWidth, int childHeight) {
      return childWidth;
    }

    @Override
    public void setCurrentViewCenter(PointF recyclerCenter, int scrolled, PointF outPoint) {
      float newX = recyclerCenter.x - scrolled;
      outPoint.set(newX, recyclerCenter.y);
    }

    @Override
    public void shiftViewCenter(Direction direction, int shiftAmount, PointF outCenter) {
      float newX = outCenter.x + Direction2.applyTo(direction, shiftAmount);
      outCenter.set(newX, outCenter.y);
    }

    @Override
    public boolean isViewVisible(PointF viewCenter, float halfWidth, float halfHeight, float endBound, float extraSpace) {
      float viewLeft = viewCenter.x - halfWidth;
      float viewRight = viewCenter.x + halfWidth;
      return viewLeft < (endBound + extraSpace) && viewRight > -extraSpace;
    }

    @Override
    public boolean hasNewBecomeVisible(DiscreteScrollLayoutManager lm) {
      View firstChild = lm.getFirstChild(), lastChild = lm.getLastChild();
      int leftBound = -lm.getExtraLayoutSpace();
      int rightBound = lm.getWidth() + lm.getExtraLayoutSpace();
      boolean isNewVisibleFromLeft = lm.getDecoratedLeft(firstChild) > leftBound
          && lm.getPosition(firstChild) > 0;
      boolean isNewVisibleFromRight = lm.getDecoratedRight(lastChild) < rightBound
          && lm.getPosition(lastChild) < lm.getItemCount() - 1;
      return isNewVisibleFromLeft || isNewVisibleFromRight;
    }

    @Override
    public void offsetChildren(int amount, RecyclerViewProxy helper) {
      helper.offsetChildrenHorizontal(amount);
    }

    @Override
    public float getDistanceFromCenter(PointF center, float viewCenterX, float viewCenterY) {
      return viewCenterX - center.x;
    }

    @Override
    public int getFlingVelocity(int velocityX, int velocityY) {
      return velocityX;
    }

    @Override
    public boolean canScrollHorizontally() {
      return true;
    }

    @Override
    public boolean canScrollVertically() {
      return false;
    }

    @Override
    public int getPendingDx(int pendingScroll) {
      return pendingScroll;
    }

    @Override
    public int getPendingDy(int pendingScroll) {
      return 0;
    }
  }

  protected static class VerticalHelper implements Helper {

    @Override
    public int getViewEnd(int recyclerWidth, int recyclerHeight) {
      return recyclerHeight;
    }

    @Override
    public int getDistanceToChangeCurrent(int childWidth, int childHeight) {
      return childHeight;
    }

    @Override
    public void setCurrentViewCenter(PointF recyclerCenter, int scrolled, PointF outPoint) {
      float newY = recyclerCenter.y - scrolled;
      outPoint.set(recyclerCenter.x, newY);
    }

    @Override
    public void shiftViewCenter(Direction direction, int shiftAmount, PointF outCenter) {
      float newY = outCenter.y + Direction2.applyTo(direction, shiftAmount);
      outCenter.set(outCenter.x, newY);
    }

    @Override
    public void offsetChildren(int amount, RecyclerViewProxy helper) {
      helper.offsetChildrenVertical(amount);
    }

    @Override
    public float getDistanceFromCenter(PointF center, float viewCenterX, float viewCenterY) {
      return viewCenterY - center.y;
    }

    @Override
    public boolean isViewVisible(PointF viewCenter, float halfWidth, float halfHeight, float endBound, float extraSpace) {
      float viewTop = viewCenter.y - halfHeight;
      float viewBottom = viewCenter.y + halfHeight;
      return viewTop < (endBound + extraSpace) && viewBottom > -extraSpace;
    }

    @Override
    public boolean hasNewBecomeVisible(DiscreteScrollLayoutManager lm) {
      View firstChild = lm.getFirstChild(), lastChild = lm.getLastChild();
      int topBound = -lm.getExtraLayoutSpace();
      int bottomBound = lm.getHeight() + lm.getExtraLayoutSpace();
      boolean isNewVisibleFromTop = lm.getDecoratedTop(firstChild) > topBound
          && lm.getPosition(firstChild) > 0;
      boolean isNewVisibleFromBottom = lm.getDecoratedBottom(lastChild) < bottomBound
          && lm.getPosition(lastChild) < lm.getItemCount() - 1;
      return isNewVisibleFromTop || isNewVisibleFromBottom;
    }

    @Override
    public int getFlingVelocity(int velocityX, int velocityY) {
      return velocityY;
    }

    @Override
    public boolean canScrollHorizontally() {
      return false;
    }

    @Override
    public boolean canScrollVertically() {
      return true;
    }

    @Override
    public int getPendingDx(int pendingScroll) {
      return 0;
    }

    @Override
    public int getPendingDy(int pendingScroll) {
      return pendingScroll;
    }
  }
}
