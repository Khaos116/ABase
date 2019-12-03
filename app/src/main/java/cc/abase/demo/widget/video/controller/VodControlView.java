package cc.abase.demo.widget.video.controller;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.dueeeke.videoplayer.controller.ControlWrapper;
import com.dueeeke.videoplayer.controller.IControlComponent;
import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.util.PlayerUtils;

import static com.dueeeke.videoplayer.util.PlayerUtils.stringForTime;

/**
 * CASE主要修改功能:2019年12月3日17:52:56
 * 1、播放完毕后自动从全屏恢复到非全屏
 * 2、如果是竖屏的视频，播放改为不旋转，直接变为竖屏全屏
 *
 * 点播底部控制栏
 */
public class VodControlView extends FrameLayout
    implements IControlComponent, View.OnClickListener, SeekBar.OnSeekBarChangeListener {

  protected ControlWrapper mControlWrapper;

  private TextView mTotalTime, mCurrTime;
  private ImageView mFullScreen;
  private LinearLayout mBottomContainer;
  private SeekBar mVideoProgress;
  private ProgressBar mBottomProgress;
  private ImageView mPlayButton;

  private boolean mIsDragging;

  private boolean mIsShowBottomProgress = true;
  //修改部分1:竖屏视频全屏监听
  private VerticalFullListener verticalFullListener;

  {
    setVisibility(GONE);
    LayoutInflater.from(getContext()).inflate(getLayoutId(), this, true);
    mFullScreen = findViewById(com.dueeeke.videocontroller.R.id.fullscreen);
    mFullScreen.setOnClickListener(this);
    mBottomContainer = findViewById(com.dueeeke.videocontroller.R.id.bottom_container);
    mVideoProgress = findViewById(com.dueeeke.videocontroller.R.id.seekBar);
    mVideoProgress.setOnSeekBarChangeListener(this);
    mTotalTime = findViewById(com.dueeeke.videocontroller.R.id.total_time);
    mCurrTime = findViewById(com.dueeeke.videocontroller.R.id.curr_time);
    mPlayButton = findViewById(com.dueeeke.videocontroller.R.id.iv_play);
    mPlayButton.setOnClickListener(this);
    mBottomProgress = findViewById(com.dueeeke.videocontroller.R.id.bottom_progress);
  }

  public VodControlView(@NonNull Context context) {
    super(context);
  }

  public VodControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public VodControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }
  //修改部分2:
  public void setVerticalFullListener(VerticalFullListener mVerticalFullListener) {
    verticalFullListener = mVerticalFullListener;
  }

  protected int getLayoutId() {
    return com.dueeeke.videocontroller.R.layout.dkplayer_layout_vod_control_view;
  }

  /**
   * 是否显示底部进度条，默认显示
   */
  public void showBottomProgress(boolean isShow) {
    mIsShowBottomProgress = isShow;
  }

  @Override
  public void attach(@NonNull ControlWrapper controlWrapper) {
    mControlWrapper = controlWrapper;
  }

  @Override
  public View getView() {
    return this;
  }

  @Override
  public void show(Animation showAnim) {
    mBottomContainer.setVisibility(VISIBLE);
    if (showAnim != null) {
      mBottomContainer.startAnimation(showAnim);
    }
    if (mIsShowBottomProgress) {
      mBottomProgress.setVisibility(GONE);
    }
  }

  @Override
  public void hide(Animation hideAnim) {
    mBottomContainer.setVisibility(GONE);
    if (hideAnim != null) {
      mBottomContainer.startAnimation(hideAnim);
    }
    if (mIsShowBottomProgress) {
      mBottomProgress.setVisibility(VISIBLE);
      AlphaAnimation animation = new AlphaAnimation(0f, 1f);
      animation.setDuration(300);
      mBottomProgress.startAnimation(animation);
    }
  }

  @Override
  public void onPlayStateChanged(int playState) {
    switch (playState) {
      case VideoView.STATE_IDLE:
      case VideoView.STATE_PLAYBACK_COMPLETED:
        setVisibility(GONE);
        //修改部分3:
        if (verticalFullListener != null && verticalFullListener.isStopOutFull()) {
          mControlWrapper.stopFullScreen();
        }
        mBottomProgress.setProgress(0);
        mBottomProgress.setSecondaryProgress(0);
        mVideoProgress.setProgress(0);
        mVideoProgress.setSecondaryProgress(0);
        break;
      case VideoView.STATE_START_ABORT:
      case VideoView.STATE_PREPARING:
      case VideoView.STATE_PREPARED:
      case VideoView.STATE_ERROR:
        setVisibility(GONE);
        break;
      case VideoView.STATE_PLAYING:
        mPlayButton.setSelected(mControlWrapper.isPlaying());
        if (mIsShowBottomProgress) {
          if (mControlWrapper.isShowing()) {
            mBottomProgress.setVisibility(GONE);
            mBottomContainer.setVisibility(VISIBLE);
          } else {
            mBottomContainer.setVisibility(GONE);
            mBottomProgress.setVisibility(VISIBLE);
          }
        } else {
          mBottomContainer.setVisibility(GONE);
        }
        setVisibility(VISIBLE);
        //开始刷新进度
        mControlWrapper.startProgress();
        break;
      case VideoView.STATE_PAUSED:
      case VideoView.STATE_BUFFERING:
      case VideoView.STATE_BUFFERED:
        mPlayButton.setSelected(mControlWrapper.isPlaying());
        break;
    }
  }

  @Override
  public void onPlayerStateChanged(int playerState) {
    switch (playerState) {
      case VideoView.PLAYER_NORMAL:
        mFullScreen.setSelected(false);
        break;
      case VideoView.PLAYER_FULL_SCREEN:
        mFullScreen.setSelected(true);
        break;
    }
  }

  @Override
  public void adjustView(int orientation, int space) {
    if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
      mBottomContainer.setPadding(0, 0, 0, 0);
      mBottomProgress.setPadding(0, 0, 0, 0);
    } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
      mBottomContainer.setPadding(space, 0, 0, 0);
      mBottomProgress.setPadding(space, 0, 0, 0);
    } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
      mBottomContainer.setPadding(0, 0, space, 0);
      mBottomProgress.setPadding(0, 0, space, 0);
    }
  }

  @Override
  public void setProgress(int duration, int position) {
    if (mIsDragging) {
      return;
    }

    if (mVideoProgress != null) {
      if (duration > 0) {
        mVideoProgress.setEnabled(true);
        int pos = (int) (position * 1.0 / duration * mVideoProgress.getMax());
        mVideoProgress.setProgress(pos);
        mBottomProgress.setProgress(pos);
      } else {
        mVideoProgress.setEnabled(false);
      }
      int percent = mControlWrapper.getBufferedPercentage();
      if (percent >= 95) { //解决缓冲进度不能100%问题
        mVideoProgress.setSecondaryProgress(mVideoProgress.getMax());
        mBottomProgress.setSecondaryProgress(mBottomProgress.getMax());
      } else {
        mVideoProgress.setSecondaryProgress(percent * 10);
        mBottomProgress.setSecondaryProgress(percent * 10);
      }
    }

    if (mTotalTime != null) {
      mTotalTime.setText(stringForTime(duration));
    }
    if (mCurrTime != null) {
      mCurrTime.setText(stringForTime(position));
    }
  }

  @Override
  public void onLockStateChanged(boolean isLocked) {
    if (isLocked) {
      hide(null);
    } else {
      show(null);
    }
  }

  @Override
  public void onClick(View v) {
    int id = v.getId();
    if (id == com.dueeeke.videocontroller.R.id.fullscreen) {
      toggleFullScreen();
    } else if (id == com.dueeeke.videocontroller.R.id.iv_play) {
      mControlWrapper.togglePlay();
    }
  }

  /**
   * 横竖屏切换
   */
  private void toggleFullScreen() {
    Activity activity = PlayerUtils.scanForActivity(getContext());
    //修改部分4:
    if (verticalFullListener != null && verticalFullListener.isVerticalVideo()) {
      mControlWrapper.toggleFullScreen();
    } else {
      mControlWrapper.toggleFullScreen(activity);
    }
  }

  @Override
  public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    if (!fromUser) {
      return;
    }

    long duration = mControlWrapper.getDuration();
    long newPosition = (duration * progress) / mVideoProgress.getMax();
    if (mCurrTime != null) {
      mCurrTime.setText(stringForTime((int) newPosition));
    }
  }

  @Override
  public void onStartTrackingTouch(SeekBar seekBar) {
    mIsDragging = true;
    mControlWrapper.stopProgress();
    mControlWrapper.stopFadeOut();
  }

  @Override
  public void onStopTrackingTouch(SeekBar seekBar) {
    long duration = mControlWrapper.getDuration();
    long newPosition = (duration * seekBar.getProgress()) / mVideoProgress.getMax();
    mControlWrapper.seekTo((int) newPosition);
    mIsDragging = false;
    mControlWrapper.startProgress();
    mControlWrapper.startFadeOut();
  }

  //修改部分5:
  public interface VerticalFullListener {
    boolean isVerticalVideo();

    boolean isStopOutFull();
  }
}
