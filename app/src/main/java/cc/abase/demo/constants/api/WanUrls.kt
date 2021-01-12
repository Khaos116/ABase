package cc.abase.demo.constants.api

/**
 * Description:WanAndroid相关API接口
 * https://www.wanandroid.com/blog/show/2
 * @author: CASE
 * @date: 2019/10/9 21:31
 */
object WanUrls {
  object User {
    //登录
    val LOGIN = "${ApiUrl.appBaseUrl}user/login" //POST：username,password

    //注册
    val REGISTER = "${ApiUrl.appBaseUrl}user/register" //POST：username,password,repassword

    //退出
    val LOGOUT = "${ApiUrl.appBaseUrl}ser/logout/json" //GET

    //个人积分
    val INTEGRAL = "${ApiUrl.appBaseUrl}lg/coin/userinfo/json" //GET
  }

  object Home {
    //banner
    val BANNER = "${ApiUrl.appBaseUrl}banner/json"

    //文章
    val ARTICLE = "${ApiUrl.appBaseUrl}article/list/%s/json" //页数，从0开始
  }
}