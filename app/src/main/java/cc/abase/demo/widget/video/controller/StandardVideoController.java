package cc.abase.demo.widget.video.controller;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.*;
import androidx.annotation.*;
import cc.abase.demo.R;
import com.dueeeke.videocontroller.component.VodControlView;
import com.dueeeke.videocontroller.component.*;
import com.dueeeke.videoplayer.controller.GestureVideoController;
import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.util.L;
import com.dueeeke.videoplayer.util.PlayerUtils;

/**
 * CASE主要修改功能：2019年12月3日17:53:01
 * 1、Base中只禁止了竖屏旋转，仍然可以横屏旋转
 * 2、Base中的变量为private外部无法访问，因此还重写了变量设置
 * 3、通过外部传入是否能旋转，禁止横屏旋转和功能
 * <p>
 * 直播/点播控制器
 * Created by dueeeke on 2017/4/7.
 */
public class StandardVideoController extends GestureVideoController
    implements View.OnClickListener {

  protected ImageView mLockButton;

  protected ProgressBar mLoadingProgress;
  //修改部分1:内部无法访问，所以自己重写判断
  private boolean enableOrientation = false;

  public StandardVideoController(@NonNull Context context) {
    this(context, null);
  }

  public StandardVideoController(@NonNull Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public StandardVideoController(@NonNull Context context, @Nullable AttributeSet attrs,
      @AttrRes int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected int getLayoutId() {
    return R.layout.dkplayer_layout_standard_controller;
  }

  @Override
  public boolean onBackPressed() {
    if (isLocked()) {
      show();
      Toast.makeText(getContext(), R.string.dkplayer_lock_tip, Toast.LENGTH_SHORT).show();
      return true;
    }
    if (mControlWrapper.isFullScreen()) {
      return stopFullScreen();
    }
    return super.onBackPressed();
  }

  //修改部分2:
  @Override
  public void setEnableOrientation(boolean enableOrientation) {
    super.setEnableOrientation(enableOrientation);
    this.enableOrientation = enableOrientation;
  }

  //修改部分3:
  @Override
  protected void onOrientationLandscape(Activity activity) {
    if (enableOrientation) {
      super.onOrientationLandscape(activity);
    }
  }

  //修改部分4:
  @Override
  protected void onOrientationReverseLandscape(Activity activity) {
    if (enableOrientation) {
      super.onOrientationReverseLandscape(activity);
    }
  }

  @Override
  protected void onVisibilityChanged(boolean isVisible, Animation anim) {
    if (mControlWrapper.isFullScreen()) {
      if (isVisible) {
        if (mLockButton.getVisibility() == GONE) {
          mLockButton.setVisibility(VISIBLE);
          if (anim != null) {
            mLockButton.startAnimation(anim);
          }
        }
      } else {
        mLockButton.setVisibility(GONE);
        if (anim != null) {
          mLockButton.startAnimation(anim);
        }
      }
    }
  }

  @Override
  protected void onPlayStateChanged(int playState) {
    super.onPlayStateChanged(playState);
    switch (playState) {
      //调用release方法会回到此状态
      case VideoView.STATE_IDLE:
        L.e("STATE_IDLE");
        mLockButton.setSelected(false);
        mLoadingProgress.setVisibility(GONE);
        break;
      case VideoView.STATE_PLAYING:
        L.e("STATE_PLAYING");
        mLoadingProgress.setVisibility(GONE);
        break;
      case VideoView.STATE_PAUSED:
        L.e("STATE_PAUSED");
        mLoadingProgress.setVisibility(GONE);
        break;
      case VideoView.STATE_PREPARING:
        L.e("STATE_PREPARING");
        mLoadingProgress.setVisibility(VISIBLE);
        break;
      case VideoView.STATE_PREPARED:
        L.e("STATE_PREPARED");
        mLoadingProgress.setVisibility(GONE);
        break;
      case VideoView.STATE_ERROR:
        L.e("STATE_ERROR");
        mLoadingProgress.setVisibility(GONE);
        break;
      case VideoView.STATE_BUFFERING:
        L.e("STATE_BUFFERING");
        mLoadingProgress.setVisibility(VISIBLE);
        break;
      case VideoView.STATE_BUFFERED:
        L.e("STATE_BUFFERED");
        mLoadingProgress.setVisibility(GONE);
        break;
      case VideoView.STATE_PLAYBACK_COMPLETED:
        L.e("STATE_PLAYBACK_COMPLETED");
        mLoadingProgress.setVisibility(GONE);
        mLockButton.setVisibility(GONE);
        mLockButton.setSelected(false);
        break;
    }
  }

  @Override
  protected void onPlayerStateChanged(int playerState) {
    super.onPlayerStateChanged(playerState);
    switch (playerState) {
      case VideoView.PLAYER_NORMAL:
        L.e("PLAYER_NORMAL");
        setLayoutParams(new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT));
        mLockButton.setVisibility(GONE);
        break;
      case VideoView.PLAYER_FULL_SCREEN:
        L.e("PLAYER_FULL_SCREEN");
        if (isShowing()) {
          mLockButton.setVisibility(VISIBLE);
        } else {
          mLockButton.setVisibility(GONE);
        }
        break;
    }

    if (mActivity != null && hasCutout()) {
      int orientation = mActivity.getRequestedOrientation();
      int dp24 = PlayerUtils.dp2px(getContext(), 24);
      int cutoutHeight = getCutoutHeight();
      if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
        FrameLayout.LayoutParams lblp = (LayoutParams) mLockButton.getLayoutParams();
        lblp.setMargins(dp24, 0, dp24, 0);
      } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
        FrameLayout.LayoutParams layoutParams = (LayoutParams) mLockButton.getLayoutParams();
        layoutParams.setMargins(dp24 + cutoutHeight, 0, dp24 + cutoutHeight, 0);
      } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
        FrameLayout.LayoutParams layoutParams = (LayoutParams) mLockButton.getLayoutParams();
        layoutParams.setMargins(dp24, 0, dp24, 0);
      }
    }
  }

  @Override
  protected void onLockStateChanged(boolean isLocked) {
    if (isLocked) {
      mLockButton.setSelected(true);
      Toast.makeText(getContext(), R.string.dkplayer_locked, Toast.LENGTH_SHORT).show();
    } else {
      mLockButton.setSelected(false);
      Toast.makeText(getContext(), R.string.dkplayer_unlocked, Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  protected void initView() {
    super.initView();
    mLockButton = findViewById(R.id.lock);
    mLockButton.setOnClickListener(this);
    mLoadingProgress = findViewById(R.id.loading);
  }

  /**
   * 快速添加各个组件
   *
   * @param title 标题
   * @param isLive 是否为直播
   */
  public void addDefaultControlComponent(String title, boolean isLive) {
    CompleteView completeView = new CompleteView(getContext());
    ErrorView errorView = new ErrorView(getContext());
    PrepareView prepareView = new PrepareView(getContext());
    prepareView.setClickStart();
    TitleView titleView = new TitleView(getContext());
    titleView.setTitle(title);
    addControlComponent(completeView, errorView, prepareView, titleView);
    if (isLive) {
      addControlComponent(new LiveControlView(getContext()));
    } else {
      addControlComponent(new VodControlView(getContext()));
    }
    addControlComponent(new GestureView(getContext()));
    setCanChangePosition(!isLive);
  }

  @Override
  public void onClick(View v) {
    int i = v.getId();
    if (i == R.id.lock) {
      mControlWrapper.toggleLockState();
    }
  }
}
