package com.seanchen.xinchat.core.util.permission

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import com.hjq.permissions.XXPermissions
import com.hjq.permissions.permission.PermissionLists
import com.hjq.permissions.permission.base.IPermission
import com.seanchen.xinchat.core.util.toast.ToastUtils

object PermissionUtil {

    private fun handlePermissionResult(
        activity: Activity,
        grantedList: MutableList<IPermission>,
        deniedList: MutableList<IPermission>,
        permissionName: String,
        callback: (granted: Boolean) -> Unit,
        vararg permissions: IPermission
    ) {
        val allGranted = deniedList.isEmpty()
        if (allGranted) {
            callback(true)
        } else {
            if (XXPermissions.isDoNotAskAgainPermissions(activity, deniedList)) {
                ToastUtils.showError("${permissionName}被永久拒绝，请手动授予")
                XXPermissions.startPermissionActivity(activity, *permissions)
            } else {
                ToastUtils.showError("${permissionName}获取失败")
            }
            callback(false)
        }
    }


    fun requestNotificationPermission(
        context: Context,
        callback: (granted: Boolean) -> Unit
    ) {
        val activity = getActivityFromContext(context)
        if (activity == null) {
            ToastUtils.showError("无法获取Activity实例，权限申请失败")
            callback(false)
            return
        }

        XXPermissions.with(activity)
            .permission(PermissionLists.getPostNotificationsPermission())
            .request { grantedList, deniedList ->
                handlePermissionResult(
                    activity,
                    grantedList,
                    deniedList,
                    "通知权限",
                    callback,
                    PermissionLists.getPostNotificationsPermission()
                )
            }
    }

    private fun getActivityFromContext(context: Context): Activity? {
        return when (context) {
            is Activity -> context
            is ContextWrapper -> {
                var baseContext = context.baseContext
                while (baseContext is ContextWrapper && baseContext !is  Activity) {
                    baseContext = baseContext.baseContext
                }
                baseContext as? Activity
            }
            else -> null
        }
    }
}