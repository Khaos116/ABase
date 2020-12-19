package cc.abase.demo.widget

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.AttributeSet
import android.webkit.WebView


/**
 * Description:修复安卓5.x上webview的bug
 * @author: CASE
 * @date: 2019/10/3 16:06
 */
class LollipopFixedWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : WebView(getFixedContext(context), attrs) {
    companion object {

        private fun getFixedContext(context: Context): Context {
            return if (Build.VERSION.SDK_INT in 21..22) context.createConfigurationContext(
                Configuration()
            ) else context
        }
    }
}