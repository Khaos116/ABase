package cc.abase.demo.component.video

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.MediaMetadataRetriever
import android.text.method.ScrollingMovementMethod
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ScrollView
import cc.ab.base.ext.*
import cc.ab.base.widget.engine.CoilEngine
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommBindTitleActivity
import cc.abase.demo.constants.UiConstants
import cc.abase.demo.databinding.ActivityVideoCompressBinding
import cc.abase.demo.utils.VideoUtils
import cc.abase.demo.widget.dkplayer.MyVideoView
import com.blankj.utilcode.constant.MemoryConstants
import com.blankj.utilcode.util.FileUtils
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import java.io.File

/**
 * Description:
 * @author: Khaos
 * @date: 2019/10/28 15:06
 */
class VideoCompressActivity : CommBindTitleActivity<ActivityVideoCompressBinding>() {
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

  //压缩后的视频地址
  private var compressVideoPath: String? = null

  //播放器(由于ViewBinding加载XML存在问题，所以改为代码加载)
  private lateinit var videoCompressPlayer: MyVideoView

  @SuppressLint("SetTextI18n", "SourceLockedOrientationActivity")
  override fun initContentView() {
    setTitleText(R.string.视频压缩与封面获取.xmlToString())
    viewBinding.videoCompressCompress.alpha = UiConstants.disable_alpha
    viewBinding.videoCompressCompress.isEnabled = false
    videoCompressPlayer = MyVideoView(mContext)
    val params = FrameLayout.LayoutParams(-1, -1)
    params.gravity = Gravity.CENTER
    viewBinding.root.addView(videoCompressPlayer, 0, params)
    videoCompressPlayer.invisible()
    viewBinding.videoCompressSel.click {
      videoCompressPlayer.release()
      videoCompressPlayer.clearCover()
      videoCompressPlayer.gone()
      mCompressProgress = 0
      viewBinding.videoCompressResult.text = ""
      mHeadInfo = ""
      //https://github.com/LuckSiege/PictureSelector/wiki/PictureSelector-Api%E8%AF%B4%E6%98%8E
      PictureSelector.create(this)
        .openGallery(SelectMimeType.ofVideo())
        .setImageEngine(CoilEngine())
        .isPageStrategy(true, PictureConfig.MAX_PAGE_SIZE, true) //过滤掉已损坏的
        .setMaxSelectNum(1)
        .setFilterMaxFileSize(1024L * MemoryConstants.MB)
        .isPreviewVideo(true)
        .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        .forResult(object : OnResultCallbackListener<LocalMedia> {
          override fun onResult(result: ArrayList<LocalMedia>?) {
            if (!result.isNullOrEmpty()) {
              val path = result.first().path
              val file = path.toFile()
              if (file?.exists() == true) parseVideo(file.path)
            }
          }

          override fun onCancel() {
          }
        })
    }
    //内部可滚动 https://www.jianshu.com/p/7a02253cd23e
    viewBinding.videoCompressResult.movementMethod = ScrollingMovementMethod.getInstance()
    val sv = ScrollView(mContext)
    sv.scrollY
    viewBinding.videoCompressCompress.click {
      selVideoPath?.let { path ->
        viewBinding.videoCompressSel.alpha = UiConstants.disable_alpha
        viewBinding.videoCompressSel.isEnabled = false
        viewBinding.videoCompressCompress.alpha = UiConstants.disable_alpha
        viewBinding.videoCompressCompress.isEnabled = false
        VideoUtils.startCompressed(File(path),
          result = { suc, info ->
            viewBinding.videoCompressSel.alpha = 1f
            viewBinding.videoCompressSel.isEnabled = true
            viewBinding.videoCompressCompress.alpha = 1f
            viewBinding.videoCompressCompress.isEnabled = true
            if (suc) {
              compressVideoPath = info
              viewBinding.videoCompressPlay.text = "播放本地压缩视频"
              viewBinding.videoCompressResult.append("\n压缩成功:$info")
              viewBinding.videoCompressResult.append("\n压缩后视频大小:${FileUtils.getSize(info)}")
            } else {
              viewBinding.videoCompressResult.append("\n$info")
            }
          },
          pro = { p ->
            val progress = p.toInt()
            if (progress > mCompressProgress) {
              mCompressProgress = progress
              if (mHeadInfo.isBlank()) mHeadInfo = viewBinding.videoCompressResult.text.toString()
              viewBinding.videoCompressResult.text = "${mHeadInfo}\n压缩进度:${progress}%"
            }
          })
      }
    }
    viewBinding.videoCompressPlay.click { VideoDetailActivity.startActivity(mContext, compressVideoPath) }
  }

  private var mCompressProgress = 0
  private var mHeadInfo = ""

  private fun initPlayer(videoPath: String) {
    videoCompressPlayer.let { videoView ->
      //设置尺寸
      val size = getVideoSize(videoPath)
      val parent = viewBinding.videoCompressPlayerParent
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
    viewBinding.videoCompressResult.text = videoPath
    viewBinding.videoCompressResult.append("\n原视频大小:${FileUtils.getSize(videoPath)}")
    viewBinding.videoCompressCompress.alpha = 1f
    viewBinding.videoCompressCompress.isEnabled = true
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
    viewBinding.videoCompressResult.append("\n原视频码率:${bitrate.toInt() / 1000}K")
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

  //<editor-fold defaultstate="collapsed" desc="生命周期">
  override fun onDestroy() {
    VideoUtils.release()
    super.onDestroy()
  }
  //</editor-fold>
}