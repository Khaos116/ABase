package cc.abase.demo.widget.dkplayer.pipfloat;

import android.content.Context;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.dueeeke.videocontroller.component.*;
import com.dueeeke.videoplayer.controller.GestureVideoController;

/**
 * 悬浮播放控制器 https://github.com/Doikki/DKVideoPlayer/blob/master/dkplayer-sample/src/main/java/com/dueeeke/dkplayer/widget/controller/FloatController.java
 * Created by dueeeke on 2017/6/1.
 */
public class FloatController extends GestureVideoController {

  public FloatController(@NonNull Context context) {
    super(context);
  }

  public FloatController(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  protected int getLayoutId() {
    return 0;
  }

  @Override
  protected void initView() {
    super.initView();
    addControlComponent(new PrepareView(getContext()));
    addControlComponent(new CompleteView(getContext()));
    addControlComponent(new ErrorView(getContext()));
    addControlComponent(new PipControlView(getContext()));
  }
}
