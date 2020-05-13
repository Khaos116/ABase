package cc.abase.demo.widget.video.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.*;
import cc.ab.base.widget.roundlayout.abs.GeneralRoundViewImpl;
import cc.ab.base.widget.roundlayout.abs.IRoundView;
import cc.abase.demo.R;
import cc.abase.demo.widget.video.ExoVideoCacheUtils;
import cc.abase.demo.widget.video.player.CustomExoMediaPlayer;
import com.dueeeke.videoplayer.exo.ExoMediaSourceHelper;
import com.dueeeke.videoplayer.player.PlayerFactory;
import com.dueeeke.videoplayer.player.VideoView;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import java.util.Map;

/**
 * 新增圆角和生命周期监听
 * Author:caiyoufei
 * Date:2019/12/5
 * Time:11:15
 */
public class ExoVideoView extends VideoView<CustomExoMediaPlayer>
    /*添加的代码:1*/
    implements LifecycleObserver, IRoundView {

  private MediaSource mMediaSource;

  private boolean mIsCacheEnabled;

  private LoadControl mLoadControl;
  private RenderersFactory mRenderersFactory;
  private TrackSelector mTrackSelector;

  private ExoMediaSourceHelper mHelper;

  //添加的代码:2
  private GeneralRoundViewImpl generalRoundViewImpl;
  private Lifecycle mLifecycle;
  //是否要重新播放
  private boolean needResumePlay = false;
  //视频尺寸变化
  private OnVideoSizeChangeListener videoSizeChangeListener;

  {
    //由于传递了泛型，必须将CustomExoMediaPlayer设置进来，否者报错
    setPlayerFactory(new PlayerFactory<CustomExoMediaPlayer>() {
      @Override
      public CustomExoMediaPlayer createPlayer(Context context) {
        return new CustomExoMediaPlayer(context);
      }
    });
    mHelper = ExoMediaSourceHelper.getInstance(getContext());
  }

  public ExoVideoView(Context context) {
    //添加的代码:3
    this(context, null);
  }

  public ExoVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
    //添加的代码:4
    this(context, attrs, 0);
  }

  public ExoVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    //添加的代码:5
    generalRoundViewImpl = new GeneralRoundViewImpl(this,
        context,
        attrs,
        R.styleable.ExoVideoView,
        R.styleable.ExoVideoView_corner_radius);
  }

  //添加的代码:6
  @Override
  public void setCornerRadius(float cornerRadius) {
    generalRoundViewImpl.setCornerRadius(cornerRadius);
  }

  //添加的代码:7
  @Override
  public void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);
    generalRoundViewImpl.onLayout(changed, left, top, right, bottom);
  }

  //添加的代码:8
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
    if (mMediaSource != null) {
      mMediaPlayer.setDataSource(mMediaSource);
      return true;
    }
    return false;
  }

  //添加的代码:9
  @Override
  public void setUrl(String url, Map<String, String> headers) {
    mIsCacheEnabled = url.startsWith("http");
    if (mIsCacheEnabled) {
      String newUrl = ExoVideoCacheUtils.Companion.getInstance().getCacheUrl(url);
      mMediaSource = mHelper.getMediaSource(newUrl, headers, mIsCacheEnabled);
    } else {
      mMediaSource = mHelper.getMediaSource(url, headers, mIsCacheEnabled);
    }
  }

  //添加的代码:10
  @Override public void onVideoSizeChanged(int videoWidth, int videoHeight) {
    super.onVideoSizeChanged(videoWidth, videoHeight);
    if (videoSizeChangeListener != null) {
      videoSizeChangeListener.sizeChange(videoWidth, videoHeight);
    }
  }

  //添加的代码:11
  public void setLifecycleOwner(@Nullable LifecycleOwner owner) {
    if (owner == null) {
      if (mLifecycle != null) mLifecycle.removeObserver(this);
      mLifecycle = null;
    } else {
      if (mLifecycle != null) mLifecycle.removeObserver(this);
      mLifecycle = owner.getLifecycle();
      mLifecycle.addObserver(this);
    }
  }

  //添加的代码:12
  @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
  public void onResumeVideo() {
    if (needResumePlay) {
      needResumePlay = false;
      resume();
    }
  }

  //添加的代码:13
  @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
  public void onPauseVideo() {
    if (isPlaying()) {
      pause();
      needResumePlay = true;
    }
  }

  //添加的代码:14
  @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
  public void onDestroyVideo() {
    release();
  }

  /**
   * 设置ExoPlayer的MediaSource
   */
  public void setMediaSource(MediaSource mediaSource) {
    mMediaSource = mediaSource;
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

  //添加的代码:15
  public void setVideoSizeChangeListener(OnVideoSizeChangeListener mVideoSizeChangeListener) {
    videoSizeChangeListener = mVideoSizeChangeListener;
  }

  //添加的代码:16
  public interface OnVideoSizeChangeListener {
    void sizeChange(int videoWidth, int videoHeight);
  }
}