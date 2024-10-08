package cc.abase.demo.component.test

import android.annotation.SuppressLint
import cc.ab.base.ext.xmlToString
import cc.ab.base.ui.viewmodel.DataState
import cc.ab.base.widget.livedata.MyObserver
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommBindTitleActivity
import cc.abase.demo.component.test.viewmodel.TestViewModel
import cc.abase.demo.databinding.ActivityTestBinding
import com.blankj.utilcode.util.StringUtils

/**
 * Description:
 * @author: Khaos
 * @date: 2020/5/7 9:41
 */
class TestActivity : CommBindTitleActivity<ActivityTestBinding>() {
    //<editor-fold defaultstate="collapsed" desc="变量">
    private val viewModel: TestViewModel by lazy { TestViewModel() }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="初始化View">
    @SuppressLint("SetTextI18n")
    override fun initContentView() {
        setTitleText(StringUtils.getString(R.string.测试专用页面))
        viewBinding.testTv1.text = "Rxjava"
        viewBinding.testTv2.text = R.string.协程.xmlToString()
        viewModel.timeLiveData1.observe(this, MyObserver {
            if (it is DataState.SuccessRefresh) viewBinding.testTv1.append("\n时间1=${it.data}")
        })
        viewModel.timeLiveData2.observe(this, MyObserver {
            if (it is DataState.SuccessRefresh) viewBinding.testTv2.append("\n时间2=${it.data}")
        }
        )
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