package cc.abase.demo.component.login

import android.content.Context
import android.content.Intent
import androidx.lifecycle.rxLifeScope
import cc.ab.base.ext.*
import cc.ab.base.utils.CcInputHelper
import cc.ab.base.utils.PressEffectHelper
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommActivity
import cc.abase.demo.component.main.MainActivity
import cc.abase.demo.config.UserManager
import cc.abase.demo.constants.LengthConstants
import cc.abase.demo.constants.UiConstants
import cc.abase.demo.rxhttp.repository.UserRepository
import cc.abase.demo.utils.AppInfoUtils
import cc.abase.demo.utils.MMkvUtils
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.StringUtils
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Description:
 * @author: CASE
 * @date: 2019/10/10 14:58
 */
class LoginActivity : CommActivity() {

  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, LoginActivity::class.java)
      context.startActivity(intent)
    }
  }

  override fun layoutResId() = R.layout.activity_login

  //隐藏功能点击次数
  private var count = 0
  override fun initView() {
    loginAppInfo.setPadding(0, mStatusBarHeight, 0, 0)
    loginAppInfo.setOnClickListener {
      if ((++count) == 10) loginAppInfo.text = AppInfoUtils.getAppInfo()
    }
    checkSubmit()
    PressEffectHelper.alphaEffect(loginRegister)
    CcInputHelper.wrapCommCountLimit(loginEditAccount, LengthConstants.MAX_LEN_ACC, 0)
    CcInputHelper.wrapCommCountLimit(loginEditPassword, LengthConstants.MAX_LEN_PASS, 0)
    loginEditAccount.addTextWatcher { checkSubmit() }
    loginEditPassword.addTextWatcher { checkSubmit() }
    loginSubmit.click {
      showActionLoading()
      rxLifeScope.launch({
        withContext(Dispatchers.IO) {
          UserRepository.login(
              loginEditAccount.text.toString(),
              loginEditPassword.text.toString()
          )
        }.let {
          MainActivity.startActivity(mContext)
        }
      }, { e ->
        e.toast()
      }, {}, {
        dismissLoadingView()
      })
    }
    loginRegister.click { RegisterActivity.startActivity(mContext) }
    extKeyBoard { statusHeight, navigationHeight, keyBoardHeight -> }
    //读取上次的数据
    loginEditAccount.setText(MMkvUtils.getAccount())
    loginEditPassword.setText(MMkvUtils.getPassword())
  }

  override fun initData() {
    //来到登录页默认需要清除数据
    UserManager.clearUserInfo()
    //关闭其他所有页面
    ActivityUtils.finishOtherActivities(javaClass)
  }

  private fun checkSubmit() {
    val textAcc = loginEditAccount.text
    val textPass = loginEditPassword.text
    if (textAcc.isEmpty()) {
      loginInputAccount.hint = StringUtils.getString(R.string.login_account_hint)
    } else if (textAcc.isNotEmpty() && textAcc.length < LengthConstants.MIN_LEN_ACC) {
      loginInputAccount.hint = StringUtils.getString(R.string.login_account_short)
    } else {
      loginInputAccount.hint = ""
    }
    if (textPass.isEmpty()) {
      loginInputPassword.hint = StringUtils.getString(R.string.login_password_hint)
    } else if (textPass.isNotEmpty() && textPass.length < LengthConstants.MIN_LEN_PASS) {
      loginInputPassword.hint = StringUtils.getString(R.string.login_password_short)
    } else {
      loginInputPassword.hint = ""
    }
    val enable = textAcc.length >= LengthConstants.MIN_LEN_ACC &&
        textPass.length >= LengthConstants.MIN_LEN_PASS
    loginSubmit.isEnabled = enable
    loginSubmit.alpha = if (enable) 1f else UiConstants.disable_alpha
    if (enable) {
      loginSubmit.pressEffectAlpha()
    } else {
      loginSubmit.pressEffectDisable()
    }
  }
}