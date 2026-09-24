package com.seanchen.xinchat.feature.user.viewmodel

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.seanchen.xinchat.core.common.base.viewmodel.BaseViewModel
import com.seanchen.xinchat.core.data.repository.UserInfoRepository
import com.seanchen.xinchat.core.data.state.AppState
import com.seanchen.xinchat.core.model.entity.User
import com.seanchen.xinchat.core.model.request.UpdateNicknameRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val appState: AppState,
    private val userInfoRepository: UserInfoRepository,
) : BaseViewModel() {
    private val _isLoggingOut = MutableStateFlow(false)
    val isLoggingOut: StateFlow<Boolean> = _isLoggingOut.asStateFlow()

    private val _isUploadingAvatar = MutableStateFlow(false)
    val isUploadingAvatar: StateFlow<Boolean> = _isUploadingAvatar.asStateFlow()

    private val _isUpdatingNickname = MutableStateFlow(false)
    val isUpdatingNickname: StateFlow<Boolean> = _isUpdatingNickname.asStateFlow()

    // 选中的本地临时头像 Uri，用于实现乐观更新（即时渲染预览）
    private val _previewAvatarUri = MutableStateFlow<Uri?>(null)
    val previewAvatarUri: StateFlow<Uri?> = _previewAvatarUri.asStateFlow()

    val isLoggedIn: StateFlow<Boolean> = appState.isLoggedIn
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val userInfo: StateFlow<User?> = appState.userInfo
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    init {
        if (appState.isLoggedIn.value && appState.userInfo.value == null) {
            appState.refreshUserInfo()
        }
    }

    /**
     * 上传并更换用户头像（支持本地即时预览以实现乐观更新）
     *
     * @param part 上传的图片表单数据
     * @param previewUri 本地选中的图片 Uri，传入后立即可见，避免网络耗时导致的界面闪白
     */
    fun uploadAvatar(
        part: MultipartBody.Part,
        previewUri: Uri? = null,
        onSuccess: (newAvatarUrl: String?) -> Unit = {},
        onError: (String?) -> Unit = {}
    ) {
        if (_isUploadingAvatar.value) return
        _isUploadingAvatar.value = true
        _previewAvatarUri.value = previewUri // 1. 立即展示选中的本地图片（0 延迟视觉反馈）
        viewModelScope.launch {
            try {
                userInfoRepository.uploadAvatar(part)
                    .catch { throwable ->
                        _isUploadingAvatar.value = false
                        _previewAvatarUri.value = null // 失败回滚
                        onError(throwable.message)
                    }
                    .collect { response ->
                        _isUploadingAvatar.value = false
                        val user = response.data
                        if (response.isSucceeded && user != null) {
                            appState.updateUserInfo(user)
                            // 保持 previewAvatarUri，避免网络图片未缓存造成界面闪现默认头像，
                            // 由 UI 在完成后台预加载后调用 clearPreviewAvatar() 平滑清除
                            onSuccess(user.avatarUrl)
                        } else {
                            _previewAvatarUri.value = null // 业务失败回滚
                            onError(response.message)
                        }
                    }
            } catch (e: Exception) {
                _isUploadingAvatar.value = false
                _previewAvatarUri.value = null // 异常回滚
                onError(e.message)
            }
        }
    }

    /**
     * 清空本地预览头像，平滑交接至正式的用户网络头像
     */
    fun clearPreviewAvatar() {
        _previewAvatarUri.value = null
    }

    /**
     * 修改用户昵称
     *
     * @param nickName 用户输入的新昵称
     * @param onSuccess 成功回调
     * @param onError 失败回调，携带错误原因
     */
    fun updateNickname(
        nickName: String,
        onSuccess: () -> Unit = {},
        onError: (String?) -> Unit = {}
    ) {
        val trimmed = nickName.trim()
        if (trimmed.isEmpty()) {
            onError("昵称不能为空")
            return
        }
        if (trimmed.length > 80) {
            onError("昵称长度不能超过 80 个字符")
            return
        }
        if (_isUpdatingNickname.value) return
        _isUpdatingNickname.value = true
        viewModelScope.launch {
            try {
                userInfoRepository.updateNickname(UpdateNicknameRequest(trimmed))
                    .catch { throwable ->
                        _isUpdatingNickname.value = false
                        onError(throwable.message)
                    }
                    .collect { response ->
                        _isUpdatingNickname.value = false
                        val updatedUser = response.data
                        if (response.isSucceeded && updatedUser != null) {
                            appState.updateUserInfo(updatedUser)
                            onSuccess()
                        } else {
                            onError(response.message)
                        }
                    }
            } catch (e: Exception) {
                _isUpdatingNickname.value = false
                onError(e.message)
            }
        }
    }

    suspend fun logout() {
        _isLoggingOut.value = true
        try {
            appState.logout()
        } finally {
            _isLoggingOut.value = false
        }
    }
}
