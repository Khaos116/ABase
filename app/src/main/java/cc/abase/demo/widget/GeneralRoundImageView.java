package cc.abase.demo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import cc.ab.base.widget.roundlayout.abs.GeneralRoundViewImpl;
import cc.ab.base.widget.roundlayout.abs.IRoundView;
import cc.abase.demo.R;

/**
 * 自定义view增加圆角裁剪
 *
 * @author minminaya
 * @email minminaya@gmail.com
 * @time Created by 2019/6/8 18:11
 */
public class GeneralRoundImageView extends AppCompatImageView implements IRoundView {
  private GeneralRoundViewImpl generalRoundViewImpl;

  public GeneralRoundImageView(Context context) {
    this(context, null);
  }

  public GeneralRoundImageView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init(this, context, attrs);
  }

  private void init(GeneralRoundImageView view, Context context, AttributeSet attrs) {
    generalRoundViewImpl = new GeneralRoundViewImpl(view,
        context,
        attrs,
        R.styleable.GeneralRoundImageView,
        R.styleable.GeneralRoundImageView_corner_radius);
  }

  public GeneralRoundImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(this, context, attrs);
  }

  @Override
  public void setCornerRadius(float cornerRadius) {
    generalRoundViewImpl.setCornerRadius(cornerRadius);
  }

  @Override
  protected void dispatchDraw(Canvas canvas) {
    generalRoundViewImpl.beforeDispatchDraw(canvas);
    super.dispatchDraw(canvas);
    generalRoundViewImpl.afterDispatchDraw(canvas);
  }

  @Override
  public void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);
    generalRoundViewImpl.onLayout(changed, left, top, right, bottom);
  }
}