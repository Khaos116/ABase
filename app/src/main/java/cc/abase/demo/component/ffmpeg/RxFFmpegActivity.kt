package cc.abase.demo.component.ffmpeg

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.media.MediaMetadataRetriever
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media
import android.widget.ImageView
import cc.ab.base.ext.*
import cc.abase.demo.component.comm.CommTitleActivity
import cc.abase.demo.constants.UiConstants
import cc.abase.demo.utils.VideoUtils
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.FileUtils
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import com.shuyu.gsyvideoplayer.utils.GSYVideoType
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import kotlinx.android.synthetic.main.activity_rxffmpeg.*
import me.panpf.sketch.SketchImageView
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager
import java.io.File

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/28 15:06
 */
class RxFFmpegActivity : CommTitleActivity() {
  companion object {
    private const val INTENT_SEL_VIDEO = 0x0101
    fun startActivity(context: Context) {
      val intent = Intent(context, RxFFmpegActivity::class.java)
      context.startActivity(intent)
    }
  }

  //选中的视频
  private var selVideoPath: String? = null
  //封面
  private var coverImage: SketchImageView? = null
  //播放器
  private var player: StandardGSYVideoPlayer? = null
  //播放器旋转工具
  private var orientationUtils: OrientationUtils? = null

  override fun layoutResContentId() = cc.abase.demo.R.layout.activity_rxffmpeg
  override fun onCreateBefore() {
    //全局统一设置
    PlayerFactory.setPlayManager(Exo2PlayerManager::class.java)//EXO播放内核
    GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_DEFAULT)//画面使用CenterCrop模式
    GSYVideoType.setRenderType(GSYVideoType.TEXTURE)//使用texture播放
  }

  override fun initContentView() {
    setTitleText(getString(cc.abase.demo.R.string.ffmpeg_title))
    player = ffmpegPlayer
    coverImage = SketchImageView(this)
    ffmpegCompress.alpha = UiConstants.disable_alpha
    ffmpegCompress.isEnabled = false
    ffmpegSel.click {
      player?.onVideoReset()
      val openAlbumIntent = Intent(Intent.ACTION_PICK)
      openAlbumIntent.setDataAndType(Media.EXTERNAL_CONTENT_URI, "video/*")
      openAlbumIntent.putExtra("return-data", true)
      startActivityForResult(openAlbumIntent, INTENT_SEL_VIDEO)
    }
    ffmpegCompress.click {
      selVideoPath?.let { path ->
        ffmpegSel.alpha = UiConstants.disable_alpha
        ffmpegSel.isEnabled = false
        ffmpegCompress.alpha = UiConstants.disable_alpha
        ffmpegCompress.isEnabled = false
        VideoUtils.instance.startCompressed(File(path),
            result = { suc, info ->
              ffmpegSel.alpha = 1f
              ffmpegSel.isEnabled = true
              ffmpegCompress.alpha = 1f
              ffmpegCompress.isEnabled = true
              if (suc) {
                ffmpegResult.append("\n压缩成功:$info")
                ffmpegResult.append("\n压缩后视频大小:${FileUtils.getFileSize(info)}")
              } else {
                ffmpegResult.append("\n$info")
              }
            },
            pro = { p -> ffmpegResult.append("\n压缩进度:${p}%") })
      }
    }
    coverImage?.scaleType = ImageView.ScaleType.CENTER_CROP
  }

  override fun initData() {
  }

  private var isPlay = false
  private var isPause = false
  private fun initPlayer(
    videoPath: String,
    coverPath: String
  ) {
    //设置尺寸
    val size = getVideoSize(videoPath)
    val ratioParent = ffmpegPlayerParent.width * 1f / ffmpegPlayerParent.height
    val ratioVideo = size.first * 1f / size.second
    if (ratioParent > ratioVideo) {//高视频
      player?.layoutParams?.height = ffmpegPlayerParent.height
      player?.layoutParams?.width =
        (ffmpegPlayerParent.height * 1f / size.second * size.first).toInt()
    } else {//宽视频
      player?.layoutParams?.height =
        (ffmpegPlayerParent.width * 1f / size.first * size.second).toInt()
      player?.layoutParams?.width = ffmpegPlayerParent.width
    }
    player?.visible()
    //设置封面
    coverImage?.removeParent()
    val builder = GSYVideoOptionBuilder()
    builder.setThumbImageView(coverImage)
        .setIsTouchWiget(true)
        .setRotateViewAuto(true)
        .setLockLand(false)
        .setAutoFullWithSize(true)
        .setShowFullAnimation(false)
        .setNeedLockFull(true)
        .setUrl(videoPath)
        .setCacheWithPlay(false)
        .setVideoTitle(" ")
        .setVideoAllCallBack(object : GSYSampleCallBack() {
          override fun onQuitFullscreen(
            url: String?,
            vararg objects: Any?
          ) {
            super.onQuitFullscreen(url, *objects)
            orientationUtils?.backToProtVideo()
          }

          override fun onPrepared(
            url: String?,
            vararg objects: Any?
          ) {
            super.onPrepared(url, *objects)
            //开始播放了才能旋转和全屏
            orientationUtils?.isEnable = true
            isPlay = true
          }
        })
        .setLockClickListener { view, lock ->
          //配合下方的onConfigurationChanged
          orientationUtils?.isEnable = !lock
        }
        .build(player)
    player?.let { play ->
      play.isLooping = true
      play.backButton?.gone()
      play.titleTextView?.gone()
      play.fullscreenButton?.setOnClickListener {
        if (play.width > play.height) orientationUtils?.resolveByClick()
        play.startWindowFullscreen(mActivity, true, true)
        BarUtils.setNavBarVisibility(mActivity, false)
      }
    }
    coverImage?.displayImage(coverPath)
  }

  //解析选择的视频
  private fun parseVideo(videoPath: String) {
    selVideoPath = videoPath
    ffmpegResult.text = videoPath
    ffmpegResult.append("\n原视频大小:${FileUtils.getFileSize(videoPath)}")
    ffmpegCompress.alpha = 1f
    ffmpegCompress.isEnabled = true
    VideoUtils.instance.getFirstFrame(File(videoPath)) { suc, info ->
      if (suc) {
        initPlayer(videoPath, info)
      } else {
        ffmpegResult.append("\n封面获取失败:$info")
      }
    }
  }

  //获取视频宽高
  private fun getVideoSize(originPath: String): Pair<Int, Int> {
    val mMetadataRetriever = MediaMetadataRetriever()
    mMetadataRetriever.setDataSource(originPath)
    val videoRotation =
      mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
    val videoHeight =
      mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
    val videoWidth =
      mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
    val width: Int
    val height: Int
    if (Integer.parseInt(videoRotation) == 90 || Integer.parseInt(videoRotation) == 270) {
      //角度不对需要宽高调换
      width = videoHeight.toInt()
      height = videoWidth.toInt()
    } else {
      width = videoWidth.toInt()
      height = videoHeight.toInt()
    }
    return Pair(width, height)
  }

  override fun onActivityResult(
    requestCode: Int,
    resultCode: Int,
    data: Intent?
  ) {
    super.onActivityResult(requestCode, resultCode, data)
    data?.let {
      if (requestCode == INTENT_SEL_VIDEO && resultCode == Activity.RESULT_OK) {
        selectVideo(mContext, it)?.let { path -> if (File(path).exists()) parseVideo(path) }
      }
    }
  }

  //获取选择的视频地址
  private fun selectVideo(
    context: Context,
    data: Intent
  ): String? {
    val selectedVideo = data.data
    if (selectedVideo != null) {
      val uriStr = selectedVideo.toString()
      val path = uriStr.substring(10, uriStr.length)
      if (path.startsWith("com.sec.android.gallery3d")) {
        return null
      }
      //file:///storage/emulated/0/ffmpeg2.mp4
      val index = selectedVideo.toString()
          .indexOf("/storage/emulated")
      if (index > 0) {
        return selectedVideo.toString()
            .substring(index)
      }
    } else {
      return null
    }
    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = context.contentResolver.query(selectedVideo, filePathColumn, null, null, null)
    var picturePath: String? = null
    if (cursor != null) {
      cursor.moveToFirst()
      val columnIndex = cursor.getColumnIndex(filePathColumn[0])
      picturePath = cursor.getString(columnIndex)
      cursor.close()
    }
    return picturePath
  }

  override fun onResume() {
    super.onResume()
    player?.currentPlayer?.onVideoResume(false)
    super.onResume()
    isPause = false
  }

  override fun onPause() {
    super.onPause()
    player?.currentPlayer?.onVideoPause()
    super.onPause()
    isPause = true
  }

  override fun onBackPressed() {
    orientationUtils?.backToProtVideo()
    if (GSYVideoManager.backFromWindowFull(this)) return
    super.onBackPressed()
  }

  override fun onDestroy() {
    super.onDestroy()
    if (isPlay) player?.currentPlayer?.release()
    orientationUtils?.releaseListener()
  }

  override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    //如果旋转了就全屏
    if (isPlay && !isPause) {
      player?.onConfigurationChanged(this, newConfig, orientationUtils, true, true)
    }
    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
      player?.postDelayed({ BarUtils.setNavBarVisibility(mActivity, false) }, 500)
    }
  }
}