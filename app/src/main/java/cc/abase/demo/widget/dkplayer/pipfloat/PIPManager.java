package cc.abase.demo.widget.dkplayer.pipfloat;

import android.app.Application;
import android.view.View;
import cc.ab.base.ext.ViewExtKt;
import cc.abase.demo.constants.StringConstants;
import cc.abase.demo.widget.dkplayer.MyVideoView;
import com.blankj.utilcode.util.Utils;
import com.dueeeke.videoplayer.player.VideoViewManager;

/**
 * 悬浮播放 https://github.com/Doikki/DKVideoPlayer/blob/master/dkplayer-sample/src/main/java/com/dueeeke/dkplayer/util/PIPManager.java
 * Created by dueeeke on 2018/3/30.
 */
public class PIPManager {
  private static PIPManager instance;
  private MyVideoView mVideoView;
  private FloatView mFloatView;
  private FloatController mFloatController;
  private boolean mIsShowing;
  private int mPlayingPosition = -1;
  private Class mActClass;

  private PIPManager() {
    Application application = Utils.getApp();
    mVideoView = new MyVideoView(application);
    VideoViewManager.instance().add(mVideoView, StringConstants.Tag.FLOAT_PLAY);
    mFloatController = new FloatController(application);
    mFloatView = new FloatView(application, 0, 0);
  }

  public static PIPManager getInstance() {
    if (instance == null) {
      synchronized (PIPManager.class) {
        if (instance == null) {
          instance = new PIPManager();
        }
      }
    }
    return instance;
  }

  public void startFloatWindow() {
    if (mIsShowing) return;
    ViewExtKt.removeParent(mVideoView);
    mVideoView.setVideoController(mFloatController);
    mFloatController.setPlayState(mVideoView.getCurrentPlayState());
    mFloatController.setPlayerState(mVideoView.getCurrentPlayerState());
    mFloatView.addView(mVideoView);
    mFloatView.addToWindow();
    mIsShowing = true;
  }

  public void stopFloatWindow() {
    if (!mIsShowing) return;
    mFloatView.removeFromWindow();
    ViewExtKt.removeParent(mVideoView);
    mIsShowing = false;
  }

  public void setPlayingPosition(int position) {
    this.mPlayingPosition = position;
  }

  public int getPlayingPosition() {
    return mPlayingPosition;
  }

  public void pause() {
    if (mIsShowing) return;
    mVideoView.pause();
  }

  public void resume() {
    if (mIsShowing) return;
    mVideoView.resume();
  }

  public void reset() {
    if (mIsShowing) return;
    ViewExtKt.removeParent(mVideoView);
    mVideoView.release();
    mVideoView.setVideoController(null);
    mPlayingPosition = -1;
    mActClass = null;
  }

  public boolean onBackPress() {
    return !mIsShowing && mVideoView.onBackPressed();
  }

  public boolean isStartFloatWindow() {
    return mIsShowing;
  }

  /**
   * 显示悬浮窗
   */
  public void setFloatViewVisible() {
    if (mIsShowing) {
      mVideoView.resume();
      mFloatView.setVisibility(View.VISIBLE);
    }
  }

  public void setActClass(Class cls) {
    this.mActClass = cls;
  }

  public Class getActClass() {
    return mActClass;
  }
}
