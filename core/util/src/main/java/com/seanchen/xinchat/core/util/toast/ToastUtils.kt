package com.seanchen.xinchat.core.util.toast

import android.app.Application
import android.content.Context
import androidx.annotation.StringRes
import com.hjq.toast.ToastParams
import com.hjq.toast.Toaster
import com.hjq.toast.style.BlackToastStyle
import com.hjq.toast.style.CustomToastStyle
import com.hjq.toast.style.WhiteToastStyle
import com.seanchen.xinchat.core.util.R

object ToastUtils {
    /**
     * 是否为深色主题
     */
    private var isDarkMode = false

    fun init(application: Application, isDarkTheme: Boolean = false) {
        // 保存当前主题模式
        isDarkMode = isDarkTheme

        // 根据主题选择默认样式
        val style = if (isDarkTheme) {
            WhiteToastStyle()
        } else {
            BlackToastStyle()
        }
        Toaster.init(application, style)
    }

    fun setBlackStyle() {
        isDarkMode = false
        Toaster.setStyle(BlackToastStyle())
    }

    /**
     * 设置为白色样式
     */
    fun setWhiteStyle(){
        isDarkMode = true
        Toaster.setStyle(WhiteToastStyle())
    }

    /**
     * 显示普通Toast
     */
    fun show(text: CharSequence) {
        Toaster.show(text)
    }

    /**
     * 显示普通Toast(资源ID)
     */
    fun show(@StringRes resId: Int) {
        Toaster.show(resId)
    }

    /**
     * 显示成功样式的Toast
     */
    fun showSuccess(text: CharSequence) {
        val params = ToastParams()
        params.text = text
        params.style = CustomToastStyle(R.layout.toast_success)
        Toaster.show(params)
    }

    /**
     * 显示成功样式的Toast(资源ID)
     */
    fun showSuccess(context: Context, @StringRes resId: Int) {
        val text = context.getString(resId)
        showSuccess(text)
    }

    /**
     * 显示成功样式的Toast，不需要传递上下文
     */
    fun showSuccess(@StringRes resId: Int) {
        val currentStyle = if (isDarkMode) WhiteToastStyle() else BlackToastStyle()

        Toaster.setStyle(CustomToastStyle(R.layout.toast_success))
        Toaster.show(resId)
        Toaster.setStyle(currentStyle)
    }

    /**
     * 显示失败样式的Toast
     */
    fun showError(text: CharSequence) {
        val params = ToastParams()
        params.text = text
        params.style = CustomToastStyle(R.layout.toast_error)
        Toaster.show(params)
    }

    /**
     * 显示失败样式的Toast
     */
    fun showError(context: Context, @StringRes resId: Int) {
        val text = context.getString(resId)
        showError(text)
    }

    /**
     * 显示失败样式的Toast
     */
    fun showError(@StringRes resId: Int){
        val currentStyle = if (isDarkMode) WhiteToastStyle() else BlackToastStyle()

        Toaster.setStyle(CustomToastStyle(R.layout.toast_error))
        Toaster.show(resId)

        Toaster.setStyle(currentStyle)
    }

    /**
     * 显示警告样式的Toast
     */
    fun showWarning(text: CharSequence) {
        val params = ToastParams()
        params.text = text
        params.style = CustomToastStyle(R.layout.toast_warn)
        Toaster.show(params)
    }

    /**
     * 显示警告样式的Toast
     */
    fun showWarning(context: Context, @StringRes resId: Int) {
        val text = context.getString(resId)
        showWarning(text)
    }

    /**
     * 显示警告样式的Toast
     */
    fun showWarning(@StringRes resId: Int) {
        // 保存当前样式
        val currentStyle = if (isDarkMode) WhiteToastStyle() else BlackToastStyle()

        // 设置警告样式
        Toaster.setStyle(CustomToastStyle(R.layout.toast_warn))
        Toaster.show(resId)

        // 恢复默认样式
        Toaster.setStyle(currentStyle)
    }

    /**
     * 显示短时间Toast
     */
    fun showShort(text: CharSequence) {
        Toaster.showLong(text)
    }

    /**
     * 显示短时间Toast
     */
    fun showShort(@StringRes resId: Int) {
        Toaster.showShort(resId)
    }

    /**
     * 显示长时间Toast
     */
    fun showLong(text: CharSequence) {
        Toaster.showLong(text)
    }

    /**
     * 显示长时间Toast(资源ID)
     */
    fun showLong(@StringRes resId: Int) {
        Toaster.showLong(resId)
    }


    /**
     * 延迟显示Toast
     */
    fun delayedShow(text: CharSequence, delayMillis: Long) {
        Toaster.delayedShow(text, delayMillis)
    }

    /**
     * 延迟显示Toast(资源ID)
     */
    fun delayedShow(@StringRes resId: Int, delayMillis: Long) {
        Toaster.delayedShow(resId, delayMillis)
    }

    /**
     * 取消Toast显示
     */
    fun cancel(){
        Toaster.cancel()
    }
}
