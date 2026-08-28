package com.seanchen.xinchat.core.util.validation

object ValidationUtil {
    /**
     * 验证手机号是否有效
     *
     * - 手机号不为空
     * - 手机号为11位
     * - 满足正则表达式
     */
    fun isValidPhone(phone: String): Boolean {
        return phone.isNotEmpty() && phone.length == 11 && phone.matches(Regex("^1[3-9]\\d{9}$"))
    }

    /**
     * 验证邮箱是否有效
     */
    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && email.matches(
            Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        )
    }

    /**
     * 验证短信验证码
     * - 验证码为4位
     * - 每位都为数字
     */
    fun isValidSmsCode(code: String): Boolean {
        return code.length == 4 && code.all { it.isDigit() }
    }

    /**
     * 验证密码是否有效
     */
    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }
}
