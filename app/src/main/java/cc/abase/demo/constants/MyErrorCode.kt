package cc.abase.demo.constants

/**
 * Description:接口业务错误码
 * @author: Khaos
 * @date: 2019/10/13 16:33
 */
interface MyErrorCode {
  companion object {
    const val ALREADY_DEAL = -8888 //已全局处理
    const val NO_LOGIN = -1001 //未登录
  }
}