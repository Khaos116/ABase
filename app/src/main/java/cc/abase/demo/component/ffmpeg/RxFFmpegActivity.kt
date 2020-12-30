package cc.abase.demo.component.ffmpeg

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.provider.MediaStore
import android.text.method.ScrollingMovementMethod
import cc.ab.base.ext.*
import cc.ab.base.widget.engine.PicSelEngine
import cc.abase.demo.component.comm.CommTitleActivity
import cc.abase.demo.constants.UiConstants
import cc.abase.demo.utils.VideoUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.LogUtils
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import kotlinx.android.synthetic.main.activity_rxffmpeg.*
import java.io.File

/**
 * Description:
 * @author: CASE
 * @date: 2019/10/28 15:06
 */
class RxFFmpegActivity : CommTitleActivity() {
  companion object {
    private const val INTENT_SEL_VIDEO = 0x0101
    private const val INTENT_SEL_VIDEO2 = 0x0201
    fun startActivity(context: Context) {
      val intent = Intent(context, RxFFmpegActivity::class.java)
      context.startActivity(intent)
    }
  }

  //选中的视频
  private var selVideoPath: String? = null

  //每次设置资源后的第一次播放
  private var isFirstPlay = true

  override fun layoutResContentId() = cc.abase.demo.R.layout.activity_rxffmpeg

  //压缩后的视频地址
  private var compressVideoPath: String? = null

  override fun initContentView() {
    setTitleText(getString(cc.abase.demo.R.string.ffmpeg_title))
    ffmpegCompress.alpha = UiConstants.disable_alpha
    ffmpegCompress.isEnabled = false
    ffmpegSel.click {
      ffmpegPlayer.release()
      ffmpegPlayer.clearCover()
      ffmpegPlayer.gone()
      PictureSelector.create(mActivity)
          .openGallery(PictureMimeType.ofVideo())
          .maxSelectNum(1)
          .isCamera(false)
          .loadImageEngine(PicSelEngine())
          .previewVideo(true)
          .forResult(INTENT_SEL_VIDEO2)
      //val openAlbumIntent = Intent(Intent.ACTION_PICK)
      //openAlbumIntent.setDataAndType(Media.EXTERNAL_CONTENT_URI, "video/*")
      //openAlbumIntent.putExtra("return-data", true)
      //startActivityForResult(openAlbumIntent, INTENT_SEL_VIDEO)
    }
    //内部可滚动 https://www.jianshu.com/p/7a02253cd23e
    ffmpegResult.movementMethod = ScrollingMovementMethod.getInstance()
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
                compressVideoPath = info
                ffmpegPlay.text = "播放本地压缩视频"
                ffmpegResult.append("\n压缩成功:$info")
                ffmpegResult.append("\n压缩后视频大小:${FileUtils.getSize(info)}")
              } else {
                ffmpegResult.append("\n$info")
              }
            },
            pro = { p -> ffmpegResult.append("\n压缩进度:${p}%") })
      }
    }
    ffmpegPlay.click { VideoDetailActivity.startActivity(mContext, compressVideoPath) }
  }

  override fun initData() {
    //高斯模糊测试代码
    //val url = PathUtils.getExternalStoragePath() + File.separator + "251C90F4C97303004ACF85BDE3164342.jpg"
    //iv1.load(url)
    //iv2.loadBlur(url, blur = 15)
    //iv3.loadCornerBlur(url, cornerDP = 8f, blur = 25)
  }

  private fun initPlayer(
      videoPath: String,
      coverPath: String
  ) {
    ffmpegPlayer?.let { videoView ->
      //设置尺寸
      val size = getVideoSize(videoPath)
      val parent = ffmpegPlayerParent
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
      videoView.setPlayUrl(videoPath, "", coverPath, ratio = ratioVideo)
      videoView.visible()
      videoView.requestLayout()
      isFirstPlay = true
    }
  }

  //解析选择的视频
  private fun parseVideo(videoPath: String) {
    selVideoPath = videoPath
    ffmpegResult.text = videoPath
    ffmpegResult.append("\n原视频大小:${FileUtils.getSize(videoPath)}")
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
    val videoRotation = mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION) ?: "0"
    val videoHeight = mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT) ?: "0"
    val videoWidth = mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH) ?: "0"
    val bitrate = mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE) ?: "0"
    mMetadataRetriever.release()
    ffmpegResult.append("\n原视频码率:${bitrate?.toInt() / 1000}K")
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
      if (requestCode == INTENT_SEL_VIDEO && resultCode == Activity.RESULT_OK) {
        selectVideo(mContext, it)?.let { path -> if (File(path).exists()) parseVideo(path) }
      } else if (requestCode == INTENT_SEL_VIDEO2 && resultCode == Activity.RESULT_OK) {
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
}