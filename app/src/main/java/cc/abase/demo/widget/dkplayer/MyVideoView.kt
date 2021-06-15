package cc.abase.demo.widget.dkplayer

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.lifecycle.*
import cc.ab.base.ext.*
import cc.abase.demo.R
import xyz.doikki.videocontroller.StandardVideoController
import xyz.doikki.videocontroller.component.*
import xyz.doikki.videoplayer.player.VideoView
import xyz.doikki.videoplayer.util.PlayerUtils
import java.io.File

/**
 * 将大部分控制封装后对外简单使用
 * Author:Khaos
 * Date:2020/12/23
 * Time:13:16
 */
@SuppressLint("ResourceType")
class MyVideoView : VideoView<MyExoMediaPlayer>, LifecycleObserver {
  //<editor-fold defaultstate="collapsed" desc="多构造">
  constructor(c: Context) : super(c, null, 0)
  constructor(c: Context, a: AttributeSet) : super(c, a, 0)
  constructor(c: Context, a: AttributeSet?, d: Int) : super(c, a, d)
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="变量">
  private val mCon = context

  //标准控制器
  private var controller: MyStandardController = MyStandardController(mCon)

  //直播控制条
  private var liveCV: LiveControlView = LiveControlView(mCon)

  //点播控制条
  private var vodCV: MyVodControlView = MyVodControlView(mCon)

  //封面
  private var coverIv: ImageView

  //标题
  private var titleView: MyTitleView

  //返回按钮
  private var backIv: ImageView

  //播放完成的回调
  private var mCallComplete: ((url: String?) -> Unit)? = null
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  init {
    //方便判断
    if (id <= 0) id = cc.ab.base.R.id.id_my_video_view
    //根据屏幕方向自动进入/退出全屏
    controller.setEnableOrientation(false)
    //1.准备播放界面
    controller.addControlComponent(PrepareView(mCon).also { p ->
      coverIv = p.findViewById(R.id.thumb) //封面
      coverIv.scaleType = ImageView.ScaleType.FIT_CENTER
      p.findViewById<View>(R.id.start_play).setOnClickListener { //点击播放
        if (!mUrl.isNullOrBlank()) start()
      }
    })
    //2.自动完成播放界面
    controller.addControlComponent(CompleteView(mCon))
    //3.错误界面
    controller.addControlComponent(ErrorView(mCon))
    //4.标题
    controller.addControlComponent(MyTitleView(mCon).also { tv ->
      titleView = tv
      backIv = tv.findViewById(R.id.back)
      backIv.setOnClickListener { if (isFullScreen) onBackPressed() else PlayerUtils.scanForActivity(context)?.onBackPressed() }
    })
    //5.滑动控制视图
    controller.addControlComponent(GestureView(mCon))
    //设置控制器到播放器
    setVideoController(controller)
    //默认不在列表中使用
    setInList(false)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Lifecycle生命周期">
  private var mLifecycle: Lifecycle? = null
  private var pauseIsPlaying = false
  private var isPagePause = true

  //通过Lifecycle内部自动管理暂停和播放(如果不需要后台播放)
  private fun setLifecycleOwner(owner: LifecycleOwner?) {
    if (owner == null) {
      mLifecycle?.removeObserver(this)
      mLifecycle = null
    } else {
      mLifecycle?.removeObserver(this)
      mLifecycle = owner.lifecycle
      mLifecycle?.addObserver(this)
    }
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
  private fun onPauseVideo() {
    isPagePause = true
    pauseIsPlaying = isPlaying
    pause()
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
  private fun onResumeVideo() {
    isPagePause = false
    if (pauseIsPlaying) resume()
    pauseIsPlaying = false
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
  private fun onDestroyVideo() {
    release()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="解决3.3.2版本isPausedByUser导致点击播放依然暂停的bug">
  //override fun onInfo(what: Int, extra: Int) {
  //  when (what) {
  //    AbstractPlayer.MEDIA_INFO_BUFFERING_START -> setPlayState(STATE_BUFFERING)
  //    AbstractPlayer.MEDIA_INFO_BUFFERING_END -> setPlayState(STATE_BUFFERED)
  //    AbstractPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> {
  //      setPlayState(STATE_PLAYING)
  //      mPlayerContainer.keepScreenOn = true
  //      // 视频准备完成之后，activity 如果处于 paused，则暂停播放
  //      if (isPagePause) pause()
  //    }
  //    AbstractPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED -> if (mRenderView != null) mRenderView.setVideoRotation(extra)
  //  }
  //}

  //打印无法正常播放的地址
  override fun setPlayerState(playerState: Int) {
    super.setPlayerState(playerState)
    if (playerState == STATE_ERROR) "无法播放:$mUrl".logE()
    else if (playerState == STATE_PLAYBACK_COMPLETED) mCallComplete?.invoke(mUrlVideo)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="外部调用">
  var mUrlCover: String? = ""
  var mUrlVideo: String? = ""
  var mRatio: Float = 16f / 9
  var mNeedHolder: Boolean = true

  //设置播放地址
  fun setPlayUrl(
    url: String, title: String? = null, cover: String? = null, autoPlay: Boolean = false,
    isLive: Boolean = false, ratio: Float = 16f / 9, needHolder: Boolean = true
  ) {
    if (mMediaPlayer == null) initPlayer()
    setUrl(url) //设置播放地址
    mUrlVideo = url
    mUrlCover = if (cover.isNullOrBlank()) url else cover
    mRatio = ratio
    mNeedHolder = needHolder
    titleView.setTitle(if (title.isNullOrBlank()) url else title) //设置标题
    if (cover.isNullOrBlank()) { //封面为空拿播放地址去加载
      if (url.startsWith("http")) coverIv.loadNetVideoCover(url, ratio, needHolder) //加载网络封面
      else {
        val videoFile = File(url)
        if (videoFile.exists()) {
          coverIv.loadImgHorizontal(Uri.fromFile(videoFile).toString(), ratio, needHolder) //加载封面
        } else {
          coverIv.loadImgHorizontal(url, ratio, needHolder) //加载封面
        }
      }
    } else { //封面防止可能是视频地址
      if (cover.startsWith("http")) coverIv.loadImgHorizontal(cover, ratio, needHolder)
      else {
        val videoFile = File(cover)
        if (videoFile.exists()) {
          coverIv.loadImgHorizontal(Uri.fromFile(videoFile).toString(), ratio, needHolder) //加载封面
        } else {
          coverIv.loadImgHorizontal(cover, ratio, needHolder) //加载封面
        }
      }
    } //加载封面
    if (autoPlay) start() //开始播放
    //修改控制器
    controller.removeControlComponent(liveCV)
    controller.removeControlComponent(vodCV)
    controller.addControlComponent(if (isLive) liveCV else vodCV)
  }

  //设置是否用于列表中(默认不在列表中)
  fun setInList(inList: Boolean) {
    //竖屏也开启手势操作，默认关闭
    controller.setEnableInNormal(!inList)
    titleView.setInList(inList)
  }

  //设置返回按钮的显示状态(比如视频详情页要显示自己的返回按钮就隐藏默认返回)
  fun setBackShow(visible: Int) {
    backIv.visibility = visible
    titleView.noFullBackVisibility = visible
  }

  //设置返回图标
  fun setBackResource(@DrawableRes id: Int) {
    backIv.setImageResource(id)
  }

  //清理封面(清理复用前的封面)
  fun clearCover() {
    coverIv.setImageDrawable(null)
  }

  //设置全屏按钮的显示状态
  fun setFullShow(visible: Int) {
    vodCV.setFullShow(visible)
  }

  //标题适配状态栏(主要用于ViewPager全屏模式)
  fun titleFitWindow(fit: Boolean) {
    titleView.forceFitWindow(fit)
    fitSpeedStatus(fit)
    fitSpeedTitle(fit)
    //竖屏也开启手势操作，默认关闭
    controller.setEnableInNormal(false)
    //不显示全屏按钮
    vodCV.setFullShow(View.GONE)
  }

  //倍速适配状态栏
  fun fitSpeedStatus(fit: Boolean) {
    controller.fitSpeedStatus(fit)
  }

  //倍速适配标题栏
  fun fitSpeedTitle(fit: Boolean) {
    controller.fitSpeedTitle(fit)
  }

  //外部获取控制器
  fun getMyController(): StandardVideoController {
    return controller
  }

  //外部回调视频尺寸
  var callSize: ((w: Int, h: Int) -> Unit)? = null

  //尺寸回调
  override fun onVideoSizeChanged(videoWidth: Int, videoHeight: Int) {
    super.onVideoSizeChanged(videoWidth, videoHeight)
    callSize?.invoke(videoWidth, videoHeight)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="自感应生命周期">
  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    setLifecycleOwner(getMyLifecycleOwner())
  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    setLifecycleOwner(null)
    onPauseVideo()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="重写">
  //防止空指针
  override fun startPrepare(reset: Boolean) {
    if (mMediaPlayer != null) super.startPrepare(reset)
  }

  //全屏需要Activity，如果是Application创建的，则需要从父控件获取
  override fun getActivity(): Activity? {
    return this.getMyParents().lastOrNull { v -> v.context is Activity }?.let { f -> f.context as Activity }
      ?: mVideoController?.getMyParents()?.lastOrNull { v -> v.context is Activity }?.let { f -> f.context as Activity }
      ?: super.getActivity()
  }
  //</editor-fold>
}