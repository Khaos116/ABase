package cc.ab.base.widget.engine

import android.net.Uri
import androidx.fragment.app.Fragment
import cc.ab.base.config.PathConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.engine.CropEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.utils.DateUtils
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.model.AspectRatio
import java.io.File

/**
 * https://github.com/LuckSiege/PictureSelector/wiki/PictureSelector-3.0-%E5%A6%82%E4%BD%95%E8%A3%81%E5%89%AA%EF%BC%9F
 * Author:Khaos116
 * Date:2022/3/2
 * Time:19:06
 */
class MyCropEngine : CropEngine {
  override fun onStartCrop(fragment: Fragment, currentLocalMedia: LocalMedia, dataSource: ArrayList<LocalMedia>, requestCode: Int) {
    // 注意* 如果你实现自己的裁剪库，需要在Activity的.setResult();
    // Intent中需要给MediaStore.EXTRA_OUTPUT，塞入裁剪后的路径；如果有额外数据也可以通过CustomIntentKey.EXTRA_CUSTOM_EXTRA_DATA字段存入；
    // 1、构造可用的裁剪数据源
    val currentCropPath = currentLocalMedia.availablePath
    val inputUri: Uri = if (PictureMimeType.isContent(currentCropPath) || PictureMimeType.isHasHttp(currentCropPath)) {
      Uri.parse(currentCropPath)
    } else {
      Uri.fromFile(File(currentCropPath))
    }
    val fileName = DateUtils.getCreateFileName("CROP_") + ".jpg"
    val destinationUri = Uri.fromFile(File(PathConfig.TEMP_IMG_DIR, fileName))
    val dataCropSource: ArrayList<String> = ArrayList()
    for (i in 0 until dataSource.size) {
      val media = dataSource[i]
      dataCropSource.add(media.availablePath)
    }
    val uCrop = UCrop.of(inputUri, destinationUri, dataCropSource)
    uCrop.setImageEngine(MyUCropImageEngine())
    val options = UCrop.Options()
    //设置裁切比例为1:1
    val aspectRatios = mutableListOf<AspectRatio>()
    for (localMedia in dataSource) {
      aspectRatios.add(AspectRatio("1:1", 1.0f, 1.0f))
    }
    options.setMultipleCropAspectRatio(*(aspectRatios.toTypedArray()))
    //不显示裁切内部网格
    options.setShowCropGrid(false)
    //不显示底部操作按钮
    options.setHideBottomControls(true)
    uCrop.withOptions(options)
    uCrop.start(fragment.requireContext(), fragment, requestCode)
  }
}