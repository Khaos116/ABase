package cc.abase.demo.constants

/**
 * Description:WanAndroid相关API接口
 * https://www.wanandroid.com/blog/show/2
 * @author: caiyoufei
 * @date: 2019/10/9 21:31
 */
interface WanAndroidUrls {
  companion object {
    const val BASE = "https://www.wanandroid.com/"
  }

  interface User {
    companion object {
      //登录
      const val LOGIN = "user/login"//POST：username,password
      //注册
      const val REGISTER = "user/register"//POST：username,password,repassword
      //退出
      const val LOGOUT = "user/logout/json"//GET
    }
  }
}