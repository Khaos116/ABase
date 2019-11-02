package cc.abase.demo.widget.video.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.lifecycle.*;

import cc.ab.base.widget.roundlayout.abs.GeneralRoundViewImpl;
import cc.ab.base.widget.roundlayout.abs.IRoundView;
import cc.abase.demo.R;
import cc.abase.demo.widget.video.player.CustomExoMediaPlayer;

import com.dueeeke.videoplayer.player.PlayerFactory;
import com.dueeeke.videoplayer.player.VideoView;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.TrackSelector;

public class ExoVideoView extends VideoView<CustomExoMediaPlayer>
    implements LifecycleObserver, IRoundView {
  private GeneralRoundViewImpl generalRoundViewImpl;
  private MediaSource mMediaSource;

  private boolean mIsCacheEnabled;

  private LoadControl mLoadControl;
  private RenderersFactory mRenderersFactory;
  private TrackSelector mTrackSelector;
  private Lifecycle mLifecycle;
  //是否要重新播放
  private boolean needResumePlay = false;

  public ExoVideoView(Context context) {
    this(context, null);
  }

  public ExoVideoView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ExoVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    //由于传递了泛型，必须将CustomExoMediaPlayer设置进来，否者报错
    setPlayerFactory(new PlayerFactory<CustomExoMediaPlayer>() {
      @Override
      public CustomExoMediaPlayer createPlayer() {
        return new CustomExoMediaPlayer();
      }
    });
    generalRoundViewImpl = new GeneralRoundViewImpl(this,
        context,
        attrs,
        R.styleable.ExoVideoView,
        R.styleable.ExoVideoView_corner_radius);
  }

  @Override
  public void setCornerRadius(float cornerRadius) {
    generalRoundViewImpl.setCornerRadius(cornerRadius);
  }

  @Override
  public void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);
    generalRoundViewImpl.onLayout(changed, left, top, right, bottom);
  }

  @Override
  protected void dispatchDraw(Canvas canvas) {
    generalRoundViewImpl.beforeDispatchDraw(canvas);
    super.dispatchDraw(canvas);
    generalRoundViewImpl.afterDispatchDraw(canvas);
  }

  @Override
  protected void setInitOptions() {
    super.setInitOptions();
    mMediaPlayer.setLoadControl(mLoadControl);
    mMediaPlayer.setRenderersFactory(mRenderersFactory);
    mMediaPlayer.setTrackSelector(mTrackSelector);
  }

  @Override
  protected boolean prepareDataSource() {
    mIsCacheEnabled = mUrl.startsWith("http");
    if (mIsCacheEnabled) {
      mMediaPlayer.setDataSource(mUrl, mHeaders, true);
      return true;
    } else if (mMediaSource != null) {
      mMediaPlayer.setDataSource(mMediaSource);
      return true;
    }
    return super.prepareDataSource();
  }

  public void setLifecycleOwner(@NonNull LifecycleOwner owner) {
    if (mLifecycle != null) mLifecycle.removeObserver(this);
    mLifecycle = owner.getLifecycle();
    mLifecycle.addObserver(this);
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
  public void onResumeVideo() {
    if (needResumePlay) {
      needResumePlay = false;
      resume();
    }
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
  public void onPauseVideo() {
    if (isPlaying()) {
      pause();
      needResumePlay = true;
    }
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
  public void onDestroyVideo() {
    release();
  }

  /**
   * 设置ExoPlayer的MediaSource
   */
  public void setMediaSource(MediaSource mediaSource) {
    this.mMediaSource = mediaSource;
  }

  /**
   * 是否打开缓存
   */
  public void setCacheEnabled(boolean isEnabled) {
    mIsCacheEnabled = isEnabled;
  }

  public void setLoadControl(LoadControl loadControl) {
    mLoadControl = loadControl;
  }

  public void setRenderersFactory(RenderersFactory renderersFactory) {
    mRenderersFactory = renderersFactory;
  }

  public void setTrackSelector(TrackSelector trackSelector) {
    mTrackSelector = trackSelector;
  }
}