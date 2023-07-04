package cc.abase.demo.widget.dkplayer.pipfloat;

import android.app.Application;
import android.net.Uri;
import android.text.TextUtils;
import android.view.*;
import android.widget.ImageView;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.Utils;

import java.io.File;

import cc.ab.base.ext.ImageViewExtKt;
import cc.ab.base.ext.ViewExtKt;
import cc.abase.demo.constants.StringConstants;
import cc.abase.demo.widget.dkplayer.MyVideoView;
import xyz.doikki.videoplayer.player.VideoViewManager;

/**
 * 悬浮播放 https://github.com/Doikki/DKVideoPlayer/blob/master/dkplayer-sample/src/main/java/com/dueeeke/dkplayer/util/PIPManager.java
 * Created by dueeeke on 2018/3/30.
 */
public class PIPManager {
  private static class SingleTonHolder {
    private static final PIPManager INSTANCE = new PIPManager();
  }

  public static PIPManager getInstance() {
    return SingleTonHolder.INSTANCE;
  }

  private PIPManager() {
    initVideoView();
  }

  private MyVideoView mVideoView;
  private FloatView mFloatView;
  private FloatController mFloatController;
  private boolean mIsShowing;
  private int mPlayingPosition = -1;
  private Class<?> mActClass;

  //初始化悬浮播放器
  private void initVideoView() {
    int locationX = 0;
    int locationY = 0;
    if (mFloatView != null) {
      ViewGroup.LayoutParams params = mFloatView.getLayoutParams();
      if (params instanceof WindowManager.LayoutParams) {
        locationX = ((WindowManager.LayoutParams) params).x;
        locationY = ((WindowManager.LayoutParams) params).y;
      }
    }
    mVideoView = null;
    mFloatController = null;
    mFloatView = null;
    Application application = Utils.getApp();
    mVideoView = new MyVideoView(application);
    VideoViewManager.instance().add(mVideoView, StringConstants.Tag.FLOAT_PLAY);
    mFloatController = new FloatController(application);
    mFloatView = new FloatView(application, locationX, locationY);
  }

  public void startFloatWindow() {
    if (mIsShowing) return;
    boolean needResume = mVideoView.isPlaying();
    ViewExtKt.removeParent(mVideoView);
    mVideoView.setVideoController(mFloatController);
    mFloatController.setPlayState(mVideoView.getCurrentPlayState());
    mFloatController.setPlayerState(mVideoView.getCurrentPlayerState());
    mFloatView.addView(mVideoView);
    mFloatView.addToWindow();
    mIsShowing = true;
    if (needResume) mVideoView.resume();
    //===============================封面相关START===============================//
    String coverPath = mVideoView.getMUrlCover();//封面地址
    float ratio = mVideoView.getMRatio();//封面比例
    boolean needHolder = mVideoView.getMNeedHolder();//是否需要占位图
    View playView = mFloatController.findViewById(xyz.doikki.videocontroller.R.id.start_play);//播放按钮
    if (playView != null) playView.setOnClickListener(v -> mVideoView.start());
    ImageView coverIv = mFloatController.findViewById(xyz.doikki.videocontroller.R.id.thumb);//封面
    if (coverIv != null && coverPath != null && !TextUtils.isEmpty(coverPath)) {//加载封面
      coverIv.setClickable(true);//防止透过去点击
      if (coverPath.equals(mVideoView.getMUrlVideo())) { //封面为空拿播放地址去加载
        if (coverPath.startsWith("http")) {
          ImageViewExtKt.loadNetVideoCover(coverIv, coverPath, ratio, ScreenUtils.getScreenWidth(), needHolder); //加载网络封面
        } else {
          File videoFile = new File(coverPath);
          if (videoFile.exists()) {
            ImageViewExtKt.loadCoilSimpleUrl(coverIv, Uri.fromFile(videoFile).toString(), ratio, needHolder); //加载封面
          } else {
            ImageViewExtKt.loadCoilSimpleUrl(coverIv, coverPath, ratio, needHolder); //加载封面
          }
        }
      } else { //封面防止可能是视频地址
        if (coverPath.startsWith("http")) {
          ImageViewExtKt.loadCoilSimpleUrl(coverIv, coverPath, ratio, needHolder);
        } else {
          File videoFile = new File(coverPath);
          if (videoFile.exists()) {
            ImageViewExtKt.loadCoilSimpleUrl(coverIv, Uri.fromFile(videoFile).toString(), ratio, needHolder); //加载封面
          } else {
            ImageViewExtKt.loadCoilSimpleUrl(coverIv, coverPath, ratio, needHolder); //加载封面
          }
        }
      }
    }
    //===============================封面相关END===============================//
  }

  public void stopFloatWindow() {
    if (!mIsShowing) return;
    mFloatView.removeFromWindow();
    ViewExtKt.removeParent(mVideoView);
    mIsShowing = false;
    //===============================释放点击事件START===============================//
    View playView = mFloatController.findViewById(xyz.doikki.videocontroller.R.id.start_play);//播放按钮
    if (playView != null) playView.setOnClickListener(null);
    //===============================释放点击事件END===============================//
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
    initVideoView();
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
