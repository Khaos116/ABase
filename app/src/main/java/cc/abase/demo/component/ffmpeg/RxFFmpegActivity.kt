package cc.abase.demo.component.ffmpeg

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.MediaMetadataRetriever
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media
import android.widget.ImageView
import cc.ab.base.ext.*
import cc.abase.demo.component.comm.CommTitleActivity
import cc.abase.demo.constants.UiConstants
import cc.abase.demo.utils.VideoUtils
import com.blankj.utilcode.util.FileUtils
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.player.PlayerFactory
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

  override fun initContentView() {
    PlayerFactory.setPlayManager(Exo2PlayerManager::class.java)//EXO模式
    setTitleText(getString(cc.abase.demo.R.string.ffmpeg_title))
    ffmpegPlayer.pressEffectAlpha()
    player = ffmpegPlayer
    player?.backButton?.gone()
    player?.titleTextView?.gone()
    coverImage = SketchImageView(this)
    ffmpegCompress.alpha = UiConstants.disable_alpha
    ffmpegCompress.isEnabled = false
    ffmpegSel.click {
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

  private fun initPlayer(videoPath: String, coverPath: String) {
    val size = getVideoSize(videoPath)
    player?.onVideoReset()
    player?.layoutParams?.height =
      ((player?.width ?: 0) * 1f * size.second / size.first).toInt()
    player?.setUp(videoPath, false, "测试视频")
    //增加封面
    coverImage?.removeParent()
    player?.thumbImageView = coverImage
    coverImage?.displayImage(coverPath)
    orientationUtils = OrientationUtils(this, player)
    //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏
    player?.fullscreenButton?.setOnClickListener { orientationUtils?.resolveByClick() }
    //是否可以滑动调整
    player?.setIsTouchWiget(true)
    coverImage?.click { player?.startPlayLogic() }
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
    player?.onVideoResume()
  }

  override fun onPause() {
    super.onPause()
    player?.onVideoPause()
  }

  override fun onBackPressed() {
    //先返回正常状态
    if (orientationUtils?.screenType == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
      player?.fullscreenButton?.performClick()
      return
    }
    //释放所有
    player?.setVideoAllCallBack(null)
    super.onBackPressed()
  }

  override fun onDestroy() {
    super.onDestroy()
    GSYVideoManager.releaseAllVideos()
    orientationUtils?.releaseListener()
  }
}