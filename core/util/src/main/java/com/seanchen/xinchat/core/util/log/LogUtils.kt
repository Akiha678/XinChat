package com.seanchen.xinchat.core.util.log

import android.app.Application
import android.util.Log
import timber.log.Timber

object LogUtils {
    private var isDebugMode = false

    fun init(application: Application, isDebug: Boolean = false) {
        isDebugMode = isDebug

        if (isDebug) {
            Timber.plant(Timber.DebugTree())
        } else {
            // 植入自定义的发布版日志树
//            Timber.plant()
        }
    }

    /**
     * 植入自定义日志树
     */
    fun plant(tree: Timber.Tree) {
        Timber.plant(tree)
    }

    /**
     * 移除所有日志树
     */
    fun clearLogs(){
        Timber.uprootAll()
    }

    /**
     * 移除指定的日志树
     */
    fun removeTree(tree: Timber.Tree) {
        Timber.uproot(tree)
    }

    fun v(tag: String, message: String) {
        Timber.tag(tag).v(message)
    }

    fun d(tag: String, message: String) {
        Timber.tag(tag).d(message)
    }

    fun i(tag: String, message: String) {
        Timber.tag(tag).i(message)
    }

    fun w(tag: String, message: String) {
        Timber.tag(tag).w(message)
    }
    fun e(tag: String, message: String) {
        Timber.tag(tag).e(message)
    }
    fun e(tag: String, message: String, t: Throwable) {
        Timber.tag(tag).e(t, message)
    }


    fun v(message: String) {
        Timber.v(message)
    }

    fun v(message: String, vararg args: Any?) {
        Timber.v(message, *args)
    }

    fun v(t: Throwable, message: String) {
        Timber.v(t, message)
    }

    fun v(t: Throwable, message: String, vararg args: Any?) {
        Timber.v(t, message, *args)
    }

    fun d(message: String) {
        Timber.d(message)
    }

    fun d(message: String, vararg args: Any?) {
        Timber.d(message, *args)
    }

    fun d(t: Throwable, message: String) {
        Timber.d(t, message)
    }

    fun d(t: Throwable, message: String, vararg args: Any?) {
        Timber.d(t, message, *args)
    }

    fun i(message: String) {
        Timber.i(message)
    }

    fun i(message: String, vararg args: Any?) {
        Timber.i(message, *args)
    }

    fun i(t: Throwable, message: String) {
        Timber.i(t, message)
    }

    fun i(t: Throwable, message: String, vararg args: Any?) {
        Timber.i(t, message, *args)
    }

    fun w(message: String) {
        Timber.w(message)
    }

    /**
     * 记录 WARN 级别日志，带格式化参数
     * 用法示例：LogUtils.w("用户 %s 登录警告", username)
     *
     * @param message 日志消息，可包含格式化占位符
     * @param args 格式化参数
     * @author Joker.X
     */
    fun w(message: String, vararg args: Any?) {
        Timber.w(message, *args)
    }

    /**
     * 记录 WARN 级别日志，带异常
     * 用法示例：LogUtils.w(exception, "发生异常")
     *
     * @param t 异常对象
     * @param message 日志消息
     * @author Joker.X
     */
    fun w(t: Throwable, message: String) {
        Timber.w(t, message)
    }

    /**
     * 记录 WARN 级别日志，带异常和格式化参数
     * 用法示例：LogUtils.w(exception, "用户 %s 操作发生警告", username)
     *
     * @param t 异常对象
     * @param message 日志消息，可包含格式化占位符
     * @param args 格式化参数
     * @author Joker.X
     */
    fun w(t: Throwable, message: String, vararg args: Any?) {
        Timber.w(t, message, *args)
    }

    /**
     * 记录 ERROR 级别日志
     * 用法示例：LogUtils.e("错误消息")
     *
     * @param message 日志消息
     * @author Joker.X
     */
    fun e(message: String) {
        Timber.e(message)
    }

    /**
     * 记录 ERROR 级别日志，带格式化参数
     * 用法示例：LogUtils.e("用户 %s 登录失败", username)
     *
     * @param message 日志消息，可包含格式化占位符
     * @param args 格式化参数
     * @author Joker.X
     */
    fun e(message: String, vararg args: Any?) {
        Timber.e(message, *args)
    }

    /**
     * 记录 ERROR 级别日志，带异常
     * 用法示例：LogUtils.e(exception, "发生异常")
     *
     * @param t 异常对象
     * @param message 日志消息
     * @author Joker.X
     */
    fun e(t: Throwable, message: String) {
        Timber.e(t, message)
    }

    /**
     * 记录 ERROR 级别日志，带异常和格式化参数
     * 用法示例：LogUtils.e(exception, "用户 %s 操作发生错误", username)
     *
     * @param t 异常对象
     * @param message 日志消息，可包含格式化占位符
     * @param args 格式化参数
     * @author Joker.X
     */
    fun e(t: Throwable, message: String, vararg args: Any?) {
        Timber.e(t, message, *args)
    }

    /**
     * 记录致命级别日志（ASSERT）
     * 用法示例：LogUtils.wtf("严重错误消息")
     *
     * @param message 日志消息
     * @author Joker.X
     */
    fun wtf(message: String) {
        Timber.wtf(message)
    }

    /**
     * 记录致命级别日志（ASSERT），带格式化参数
     * 用法示例：LogUtils.wtf("系统 %s 完全崩溃", module)
     *
     * @param message 日志消息，可包含格式化占位符
     * @param args 格式化参数
     * @author Joker.X
     */
    fun wtf(message: String, vararg args: Any?) {
        Timber.wtf(message, *args)
    }

    /**
     * 记录致命级别日志（ASSERT），带异常
     * 用法示例：LogUtils.wtf(exception, "发生严重异常")
     *
     * @param t 异常对象
     * @param message 日志消息
     * @author Joker.X
     */
    fun wtf(t: Throwable, message: String) {
        Timber.wtf(t, message)
    }

    /**
     * 记录致命级别日志（ASSERT），带异常和格式化参数
     * 用法示例：LogUtils.wtf(exception, "模块 %s 发生致命错误", module)
     *
     * @param t 异常对象
     * @param message 日志消息，可包含格式化占位符
     * @param args 格式化参数
     * @author Joker.X
     */
    fun wtf(t: Throwable, message: String, vararg args: Any?) {
        Timber.wtf(t, message, *args)
    }

    class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
                // 低级别日志在生产环境不记录
                return
            }

            // 只上报错误和警告日志
            if (priority == Log.ERROR || priority == Log.WARN) {
                // 这里可以替换为实际的崩溃上报实现
                // 例如：Crashlytics.log(priority, tag, message)

                if (t != null) {
                    // 上报异常
                    // 例如：Crashlytics.logException(t)
                }
            }
        }
    }

}