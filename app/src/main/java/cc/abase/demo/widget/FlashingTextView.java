package cc.abase.demo.widget;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * https://www.jianshu.com/p/a9d09cb7577f
 * description: 闪动的TextView,滑动解锁用.
 *
 * @date 2018/7/5 16:03.
 * @author: YangYang.
 */
public class FlashingTextView extends AppCompatTextView {
  private int mWidth;
  private LinearGradient gradient;
  private Matrix matrix;
  //渐变的速度
  private int deltaX;
  private int flashColor = Color.WHITE;

  public FlashingTextView(Context context) {
    super(context, null);
  }

  public FlashingTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initView(context, attrs);
  }

  private void initView(Context context, AttributeSet attrs) {
    Paint paint1 = new Paint();
    paint1.setColor(getResources().getColor(android.R.color.holo_blue_dark));
    paint1.setStyle(Paint.Style.FILL);
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    if (mWidth == 0) {
      mWidth = getMeasuredWidth();
      Paint paint2 = getPaint();
      //颜色渐变器
      gradient =
          new LinearGradient(0, 0, mWidth, 0,
              new int[] { getCurrentTextColor(), flashColor, getCurrentTextColor() },
              new float[] { 0f, 0.5f, 1.0f }, Shader.TileMode.CLAMP);
      paint2.setShader(gradient);
      matrix = new Matrix();
    }
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (matrix != null) {
      deltaX += mWidth / 5;
      if (deltaX > 2 * mWidth) {
        deltaX = -mWidth;
      }
    }
    //关键代码通过矩阵的平移实现
    if (matrix != null) {
      matrix.setTranslate(deltaX, 0);
    }
    gradient.setLocalMatrix(matrix);
    postInvalidateDelayed(120);
  }

  public void setFlashColor(int flashColor) {
    this.flashColor = flashColor;
    postInvalidate();
  }
}
