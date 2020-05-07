package cc.abase.demo.component.test

import cc.abase.demo.R
import cc.abase.demo.component.comm.CommTitleActivity
import cc.abase.demo.component.test.viewmodel.TestViewModel
import com.blankj.utilcode.util.StringUtils
import kotlinx.android.synthetic.main.activity_test.testTv1
import kotlinx.android.synthetic.main.activity_test.testTv2

/**
 * Description:
 * @author: caiyoufei
 * @date: 2020/5/7 9:41
 */
class TestActivity : CommTitleActivity() {

  override fun layoutResContentId() = R.layout.activity_test

  private val viewModel: TestViewModel by lazy { TestViewModel() }

  override fun initContentView() {
    setTitleText(StringUtils.getString(R.string.title_test))
  }

  override fun initData() {
    viewModel.subscribe(this) {
      testTv1.append("时间1=${it.time1}\n")
      testTv2.append("时间2=${it.time2}\n")
    }
    viewModel.startGetTime()
  }

  override fun finish() {
    super.finish()
    viewModel.release()
  }
}