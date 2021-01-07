package cc.abase.demo.component.test

import cc.ab.base.ui.viewmodel.DataState
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommTitleActivity
import cc.abase.demo.component.test.viewmodel.TestViewModel
import com.blankj.utilcode.util.StringUtils
import kotlinx.android.synthetic.main.activity_test.testTv1
import kotlinx.android.synthetic.main.activity_test.testTv2

/**
 * Description:
 * @author: CASE
 * @date: 2020/5/7 9:41
 */
class TestActivity : CommTitleActivity() {
  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResContentId() = R.layout.activity_test
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="变量">
  private val viewModel: TestViewModel by lazy { TestViewModel() }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化View">
  override fun initContentView() {
    setTitleText(StringUtils.getString(R.string.title_test))
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化Data">
  override fun initData() {
    viewModel.timeLiveData1.observe(this) { if (it is DataState.SuccessRefresh) testTv1.append("时间1=${it.data}\n") }
    viewModel.timeLiveData2.observe(this) { if (it is DataState.SuccessRefresh) testTv2.append("时间2=${it.data}\n") }
    viewModel.startGetTime()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="注释">
  override fun finish() {
    super.finish()
    viewModel.release()
  }
  //</editor-fold>
}