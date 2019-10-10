package cc.abase.demo.component.login

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import cc.ab.base.ext.*
import cc.ab.base.utils.CcInputHelper
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommTitleActivity
import cc.abase.demo.constants.UiConstants
import cc.abase.demo.repository.UserRepository
import kotlinx.android.synthetic.main.activity_register.*

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/10 21:03
 */
class RegisterActivity : CommTitleActivity() {
  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, RegisterActivity::class.java)
      context.startActivity(intent)
    }
  }

  override fun layoutResContentId() = R.layout.activity_register
  //输入长度
  private var countAcc = 0
  private var countPass1 = 0
  private var countPass2 = 0
  override fun initContentView() {
    setTitleText(mContext.getString(R.string.login_register_hint))
    checkSubmit()
    CcInputHelper.wrapCommCountLimit(
        registerEditAccount,
        30,
        0,
        inputCountCallBack = { hasInputCount, maxCount ->
          countAcc = hasInputCount
          checkSubmit()
        })
    CcInputHelper.wrapCommCountLimit(
        registerEditPassword1,
        30,
        0,
        inputCountCallBack = { hasInputCount, maxCount ->
          countPass1 = hasInputCount
          checkSubmit()
        })
    CcInputHelper.wrapCommCountLimit(
        registerEditPassword2,
        30,
        0,
        inputCountCallBack = { hasInputCount, maxCount ->
          countPass2 = hasInputCount
          checkSubmit()
        })
    registerSubmit.click {
      showActionLoading()
      UserRepository.instance.register(
          registerEditAccount.text.toString(),
          registerEditPassword1.text.toString(),
          registerEditPassword2.text.toString()
      )
          .subscribe { result, error ->
            dismissActionLoading()
            Log.e("CASE", "result=${result}")
            Log.e("CASE", "error=${error}")
          }
    }
  }

  private fun checkSubmit() {
    val enable = countAcc >= 3 && countPass1 >= 6 && countPass1 == countPass2 &&
        TextUtils.equals(registerEditPassword1.text, registerEditPassword2.text)
    registerSubmit.isEnabled = enable
    registerSubmit.alpha = if (enable) 1f else UiConstants.disable_alpha
    if (enable) {
      registerSubmit.pressEffectAlpha()
    } else {
      registerSubmit.pressEffectDisable()
    }
  }

  override fun initData() {
  }
}