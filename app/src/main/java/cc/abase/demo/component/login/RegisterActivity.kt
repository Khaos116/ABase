package cc.abase.demo.component.login

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import androidx.lifecycle.rxLifeScope
import cc.ab.base.ext.*
import cc.ab.base.utils.CcInputHelper
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommTitleActivity
import cc.abase.demo.component.main.MainActivity
import cc.abase.demo.constants.LengthConstants
import cc.abase.demo.constants.UiConstants
import cc.abase.demo.rxhttp.repository.UserRepository
import com.blankj.utilcode.util.StringUtils
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Description:
 * @author: CASE
 * @date: 2019/10/10 21:03
 */
class RegisterActivity : CommTitleActivity() {
  //<editor-fold defaultstate="collapsed" desc="外部跳转">
  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, RegisterActivity::class.java)
      context.startActivity(intent)
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResContentId() = R.layout.activity_register
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化View">
  override fun initContentView() {
    setTitleText(StringUtils.getString(R.string.login_register_hint))
    checkSubmit()
    CcInputHelper.wrapCommCountLimit(registerEditAccount, LengthConstants.MAX_LEN_ACC, 0)
    CcInputHelper.wrapCommCountLimit(registerEditPassword1, LengthConstants.MAX_LEN_PASS, 0)
    CcInputHelper.wrapCommCountLimit(registerEditPassword2, LengthConstants.MAX_LEN_PASS, 0)
    registerEditAccount.addTextWatcher { checkSubmit() }
    registerEditPassword1.addTextWatcher { checkSubmit() }
    registerEditPassword2.addTextWatcher { checkSubmit() }
    registerSubmit.click {
      showActionLoading()
      rxLifeScope.launch({
        withContext(Dispatchers.IO) {
          UserRepository.register(
              registerEditAccount.text.toString(),
              registerEditPassword1.text.toString(),
              registerEditPassword2.text.toString()
          )
        }.let {
          MainActivity.startActivity(mContext)
        }
      }, { e ->
        e.toast()
      }, {}, {
        dismissActionLoading()
      })
    }
    extKeyBoard { statusHeight, navigationHeight, keyBoardHeight ->
      if (keyBoardHeight > 0) {
        val array1 = intArrayOf(0, 0)
        val array2 = intArrayOf(0, 0)
        val array3 = intArrayOf(0, 0)
        registerRoot.getLocationOnScreen(array1)
        registerSubmit.getLocationOnScreen(array2)
        registerInputPassword2.getLocationOnScreen(array3)
        array1[1] = array1[1] + registerRoot.height
        array2[1] = array2[1] + registerSubmit.height
        array3[1] = array3[1] + registerInputPassword2.height
        registerSubmit.translationY = (keyBoardHeight - (array1[1] - array2[1])) * -1f - 10.dp2Px()
        if (array1[1] - array3[1] < registerSubmit.height + 10.dp2Px()) {
          registerInputPassword2.translationY = (registerSubmit.height - (array1[1] - array3[1])) * -1f - 20.dp2Px()
          registerInputPassword1.translationY = registerInputPassword2.translationY
          registerInputAccount.translationY = registerInputPassword2.translationY
        }
      } else {
        registerSubmit.translationY = 0f
        registerInputAccount.translationY = 0f
        registerInputPassword1.translationY = 0f
        registerInputPassword2.translationY = 0f
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="注册操作">
  private fun checkSubmit() {
    val textAcc = registerEditAccount.text
    val textPass1 = registerEditPassword1.text
    val textPass2 = registerEditPassword2.text
    if (textAcc.isEmpty()) {
      registerInputAccount.hint = StringUtils.getString(R.string.login_account_hint)
    } else if (textAcc.isNotEmpty() && textAcc.length < LengthConstants.MIN_LEN_ACC) {
      registerInputAccount.hint = StringUtils.getString(R.string.login_account_short)
    } else {
      registerInputAccount.hint = ""
    }
    if (textPass1.isEmpty()) {
      registerInputPassword1.hint = StringUtils.getString(R.string.login_password_hint)
    } else if (textPass1.isNotEmpty() && textPass1.length < LengthConstants.MIN_LEN_PASS) {
      registerInputPassword1.hint = StringUtils.getString(R.string.login_password_short)
    } else {
      registerInputPassword1.hint = ""
    }
    if (textPass2.isEmpty()) {
      registerInputPassword2.hint = StringUtils.getString(R.string.login_password_again_hint)
    } else if (!TextUtils.equals(textPass1, textPass2)) {
      registerInputPassword2.hint = StringUtils.getString(R.string.login_password_not_same)
    } else {
      registerInputPassword2.hint = ""
    }
    val enable = textAcc.length >= LengthConstants.MIN_LEN_ACC &&
        textPass1.length >= LengthConstants.MIN_LEN_PASS &&
        TextUtils.equals(registerEditPassword1.text, registerEditPassword2.text)
    registerSubmit.isEnabled = enable
    registerSubmit.alpha = if (enable) 1f else UiConstants.disable_alpha
    if (enable) {
      registerSubmit.pressEffectAlpha()
    } else {
      registerSubmit.pressEffectDisable()
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化Data">
  override fun initData() {
  }
  //</editor-fold>
}