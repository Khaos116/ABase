package cc.abase.demo.component.video

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.MediaMetadataRetriever
import android.provider.MediaStore
import android.text.method.ScrollingMovementMethod
import cc.ab.base.ext.*
import cc.ab.base.widget.engine.ImageEngine
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommTitleActivity
import cc.abase.demo.constants.UiConstants
import cc.abase.demo.utils.VideoUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.LogUtils
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import kotlinx.android.synthetic.main.activity_video_compress.*
import java.io.File

/**
 * Description:
 * @author: CASE
 * @date: 2019/10/28 15:06
 */
class VideoCompressActivity : CommTitleActivity() {
  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, VideoCompressActivity::class.java)
      context.startActivity(intent)
    }
  }

  //选中的视频
  private var selVideoPath: String? = null

  //每次设置资源后的第一次播放
  private var isFirstPlay = true

  override fun layoutResContentId() = R.layout.activity_video_compress

  //压缩后的视频地址
  private var compressVideoPath: String? = null

  override fun initContentView() {
    setTitleText(R.string.video_compress_title.xmlToString())
    videoCompressCompress.alpha = UiConstants.disable_alpha
    videoCompressCompress.isEnabled = false
    videoCompressSel.click {
      videoCompressPlayer.release()
      videoCompressPlayer.clearCover()
      videoCompressPlayer.gone()
      PictureSelector.create(this)
          .openGallery(PictureMimeType.ofVideo())
          .imageEngine(ImageEngine())
          .maxSelectNum(1)
          .isCamera(false)
          .isPageStrategy(true, PictureConfig.MAX_PAGE_SIZE, true) //过滤掉已损坏的
          .maxSelectNum(1)
          .queryMaxFileSize(1024f)
          .isPreviewVideo(true)
          .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
          .forResult(PictureConfig.CHOOSE_REQUEST)
    }
    //内部可滚动 https://www.jianshu.com/p/7a02253cd23e
    videoCompressResult.movementMethod = ScrollingMovementMethod.getInstance()
    videoCompressCompress.click {
      selVideoPath?.let { path ->
        videoCompressSel.alpha = UiConstants.disable_alpha
        videoCompressSel.isEnabled = false
        videoCompressCompress.alpha = UiConstants.disable_alpha
        videoCompressCompress.isEnabled = false
        VideoUtils.startCompressed(File(path),
            result = { suc, info ->
              videoCompressSel.alpha = 1f
              videoCompressSel.isEnabled = true
              videoCompressCompress.alpha = 1f
              videoCompressCompress.isEnabled = true
              if (suc) {
                compressVideoPath = info
                videoCompressPlay.text = "播放本地压缩视频"
                videoCompressResult.append("\n压缩成功:$info")
                videoCompressResult.append("\n压缩后视频大小:${FileUtils.getSize(info)}")
              } else {
                videoCompressResult.append("\n$info")
              }
            },
            pro = { p -> videoCompressResult.append("\n压缩进度:${p}%") })
      }
    }
    videoCompressPlay.click { VideoDetailActivity.startActivity(mContext, compressVideoPath) }
  }

  override fun initData() {}

  private fun initPlayer(videoPath: String) {
    videoCompressPlayer?.let { videoView ->
      //设置尺寸
      val size = getVideoSize(videoPath)
      val parent = videoCompressPlayerParent
      val width = parent.width - parent.paddingStart - parent.paddingEnd
      val height = parent.height - parent.paddingTop - parent.paddingBottom
      val ratioParent = width * 1f / height
      val ratioVideo = size.first * 1f / size.second
      if (ratioParent > ratioVideo) { //高视频
        videoView.layoutParams?.height = height
        videoView.layoutParams?.width = (height * 1f / size.second * size.first).toInt()
      } else { //宽视频
        videoView.layoutParams?.height = (width * 1f / size.first * size.second).toInt()
        videoView.layoutParams?.width = width
      }
      videoView.setInList(true)
      videoView.setPlayUrl(videoPath, "", "", ratio = ratioVideo)
      videoView.visible()
      videoView.requestLayout()
      isFirstPlay = true
    }
  }

  //解析选择的视频
  private fun parseVideo(videoPath: String) {
    selVideoPath = videoPath
    videoCompressResult.text = videoPath
    videoCompressResult.append("\n原视频大小:${FileUtils.getSize(videoPath)}")
    videoCompressCompress.alpha = 1f
    videoCompressCompress.isEnabled = true
    initPlayer(videoPath)
  }

  //获取视频宽高
  private fun getVideoSize(originPath: String): Pair<Int, Int> {
    val mMetadataRetriever = MediaMetadataRetriever()
    mMetadataRetriever.setDataSource(originPath)
    val videoRotation = mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION) ?: "0"
    val videoHeight = mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT) ?: "0"
    val videoWidth = mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH) ?: "0"
    val bitrate = mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE) ?: "0"
    mMetadataRetriever.release()
    videoCompressResult.append("\n原视频码率:${bitrate?.toInt() / 1000}K")
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

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    data?.let {
      if (requestCode == PictureConfig.CHOOSE_REQUEST && resultCode == Activity.RESULT_OK) {
        // 图片、视频、音频选择结果回调
        PictureSelector.obtainMultipleResult(data)
            ?.let { medias ->
              if (medias.isNotEmpty()) {
                val path = medias.first().path
                val file = path.toFile()
                if (file?.exists() == true) parseVideo(file.path)
              }
            }
      } else {
        LogUtils.e("CASE:onActivityResult:other")
      }
    }
  }

  //获取选择的视频地址
  private fun selectVideo(context: Context, data: Intent): String? {
    val selectedVideo = data.data
    if (selectedVideo != null) {
      val uriStr = selectedVideo.toString()
      val path = uriStr.substring(10, uriStr.length)
      if (path.startsWith("com.sec.android.gallery3d")) {
        return null
      }
      //file:///storage/emulated/0/ffmpeg2.mp4
      val index = selectedVideo.toString().indexOf("/storage/emulated")
      if (index > 0) {
        return selectedVideo.toString().substring(index)
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

  //<editor-fold defaultstate="collapsed" desc="生命周期">
  override fun onDestroy() {
    VideoUtils.release()
    super.onDestroy()
  }
  //</editor-fold>
}