package cc.abase.demo.component.coil

import android.annotation.SuppressLint
import cc.ab.base.ext.*
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommBindFragment
import cc.abase.demo.databinding.FragmentCoilBinding
import cc.abase.demo.databinding.LayoutCoilImgBinding
import com.blankj.utilcode.util.ScreenUtils

/**
 * @Description Coil加载特殊图片测试
 * @Author：khaos
 * @Date：2021-07-22
 * @Time：13:44
 */
class CoilFragment : CommBindFragment<FragmentCoilBinding>() {
  @SuppressLint("SetTextI18n")
  override fun lazyInit() {
    viewBinding.flTitle.commTitleBack.pressEffectAlpha()
    viewBinding.flTitle.commTitleText.text = R.string.Coil特殊图片加载.xmlToString()
    viewBinding.flTitle.commTitleBack.click { mActivity.onBackPressed() }
    val ration = ScreenUtils.getScreenWidth() / 200f
    val urls = mutableListOf(
      "https://raw.githubusercontent.com/nokiatech/heif/gh-pages/content/images/autumn_1440x960.heic",

      "https://raw.githubusercontent.com/coil-kt/coil/590646ca1f46b3af52d12e11bb98676f0415b449/coil-test/src/main/assets/large.jpg",
      "https://raw.githubusercontent.com/coil-kt/coil/590646ca1f46b3af52d12e11bb98676f0415b449/coil-test/src/main/assets/large.png",
      "https://raw.githubusercontent.com/coil-kt/coil/590646ca1f46b3af52d12e11bb98676f0415b449/coil-test/src/main/assets/large.webp",
      "https://raw.githubusercontent.com/coil-kt/coil/590646ca1f46b3af52d12e11bb98676f0415b449/coil-test/src/main/assets/large.heic",

      "https://raw.githubusercontent.com/coil-kt/coil/590646ca1f46b3af52d12e11bb98676f0415b449/coil-test/src/main/assets/animated.gif",
      "https://raw.githubusercontent.com/coil-kt/coil/590646ca1f46b3af52d12e11bb98676f0415b449/coil-test/src/main/assets/animated.heif",
      "https://raw.githubusercontent.com/coil-kt/coil/590646ca1f46b3af52d12e11bb98676f0415b449/coil-test/src/main/assets/animated.webp",
    )
    urls.forEach { str ->
      val vb = LayoutCoilImgBinding.inflate(layoutInflater)
      vb.tv.text = str
      vb.iv.loadCoilSimpleUrl(url = str, holderRatio = ration)
      viewBinding.llContent.addView(vb.root)
    }
  }
}