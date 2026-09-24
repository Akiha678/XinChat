package com.seanchen.xinchat.feature.user.view

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.imageLoader
import coil.request.ImageRequest
import com.seanchen.xinchat.core.designsystem.component.AppAvatar
import com.seanchen.widget.ui.list.AppListItem
import com.seanchen.widget.ui.scaffold.AppScaffold
import com.seanchen.widget.ui.text.AppText
import com.seanchen.widget.ui.text.TextType
import com.seanchen.widget.ui.title.TitleWithLine
import com.seanchen.xinchat.core.designsystem.component.VerticalList
import com.seanchen.xinchat.core.designsystem.theme.SpaceHorizontalLarge
import com.seanchen.xinchat.core.designsystem.theme.SpaceVerticalLarge
import com.seanchen.xinchat.core.designsystem.theme.SpaceVerticalSmall
import com.seanchen.xinchat.core.model.entity.User
import com.seanchen.xinchat.core.navigation.navigateBack
import com.seanchen.xinchat.core.util.media.toFullMediaUrl
import com.seanchen.xinchat.feature.user.R
import com.seanchen.xinchat.feature.user.component.FunctionMenuSection
import com.seanchen.xinchat.feature.user.util.AvatarFileUtil
import com.seanchen.xinchat.feature.user.util.AvatarValidationResult
import com.seanchen.xinchat.feature.user.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

private const val USER_AVATAR_SHARED_KEY = "user_avatar"

/**
 * 个人中心界面
 */
@Composable
fun ProfileRoute(
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedContentScope? = null,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val userInfo by viewModel.userInfo.collectAsStateWithLifecycle()
    val isLoggingOut by viewModel.isLoggingOut.collectAsStateWithLifecycle()
    val isUploadingAvatar by viewModel.isUploadingAvatar.collectAsStateWithLifecycle()
    val previewAvatarUri by viewModel.previewAvatarUri.collectAsStateWithLifecycle()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            when (val result = AvatarFileUtil.validateAndCreatePart(context, uri)) {
                is AvatarValidationResult.Success -> {
                    viewModel.uploadAvatar(
                        part = result.part,
                        previewUri = uri,
                        onSuccess = { newAvatarUrl ->
                            scope.launch {
                                val fullUrl = newAvatarUrl.toFullMediaUrl()
                                if (fullUrl != null) {
                                    try {
                                        // 同步预加载新图片并写入 Coil 缓存
                                        val request = ImageRequest.Builder(context)
                                            .data(fullUrl)
                                            .build()
                                        context.imageLoader.execute(request)
                                    } catch (_: Exception) {
                                        // 即使网络微小波动，也不阻断后续主流程
                                    }
                                }
                                viewModel.clearPreviewAvatar()
                                Toast.makeText(context, R.string.avatar_update_success, Toast.LENGTH_SHORT).show()
                            }
                        },
                        onError = { msg ->
                            Toast.makeText(
                                context,
                                msg ?: context.getString(R.string.avatar_update_failed),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
                is AvatarValidationResult.InvalidFormat -> {
                    Toast.makeText(context, R.string.avatar_invalid_format, Toast.LENGTH_SHORT).show()
                }
                is AvatarValidationResult.SizeExceeded -> {
                    Toast.makeText(context, R.string.avatar_size_exceeded, Toast.LENGTH_SHORT).show()
                }
                is AvatarValidationResult.ReadFailed -> {
                    Toast.makeText(context, R.string.avatar_read_failed, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    ProfileScreen(
        sharedTransitionScope = sharedTransitionScope,
        animatedContentScope = animatedContentScope,
        isLoggedIn = isLoggedIn,
        isLoggingOut = isLoggingOut,
        isUploadingAvatar = isUploadingAvatar,
        previewAvatarUri = previewAvatarUri,
        onAvatarClick = {
            if (!isUploadingAvatar) {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        },
        onBackClick = { navigateBack() },
        onLogoutClick = {
            // 登出后的跳转与返回栈清理由导航层根据登录态统一处理
            scope.launch { viewModel.logout() }
        },
        userInfo = userInfo
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProfileScreen(
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedContentScope? = null,
    isLoggedIn: Boolean = false,
    isLoggingOut: Boolean = false,
    isUploadingAvatar: Boolean = false,
    previewAvatarUri: Uri? = null,
    userInfo: User? = null,
    onAvatarClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
) {
    AppScaffold(
        title = R.string.profile_detail_title,
        useLargeTopBar = true,
        onBackClick = onBackClick
    ) {
        ProfileContentView(
            modifier = modifier,
            isLoggedIn = isLoggedIn,
            isLoggingOut = isLoggingOut,
            isUploadingAvatar = isUploadingAvatar,
            previewAvatarUri = previewAvatarUri,
            onAvatarClick = onAvatarClick,
            onLogoutClick = onLogoutClick,
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = animatedContentScope,
            userInfo = userInfo
        )
    }
}

@Composable
private fun ProfileContentView(
    modifier: Modifier = Modifier,
    isLoggedIn: Boolean,
    isLoggingOut: Boolean,
    isUploadingAvatar: Boolean,
    previewAvatarUri: Uri? = null,
    onAvatarClick: () -> Unit,
    onLogoutClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedContentScope? = null,
    userInfo: User? = null,
) {
    VerticalList(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        TitleWithLine(
            text = stringResource(id = R.string.profile_account_info),
            modifier = Modifier.padding(top = SpaceVerticalSmall)
        )

        ProfileInfoSection(
            userInfo = userInfo,
            previewAvatarUri = previewAvatarUri,
            isUploadingAvatar = isUploadingAvatar,
            onAvatarClick = onAvatarClick,
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = animatedContentScope
        )

        FunctionMenuSection(
            isLoggedIn = isLoggedIn,
            isLoggingOut = isLoggingOut,
            onLogoutClick = onLogoutClick
        )
    }
}

@Composable
private fun ProfileInfoSection(
    userInfo: User?,
    previewAvatarUri: Uri? = null,
    isUploadingAvatar: Boolean,
    onAvatarClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedContentScope? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        AppListItem(
            title = stringResource(id = R.string.profile_avatar),
            showArrow = true,
            onClick = onAvatarClick,
            verticalPadding = SpaceVerticalSmall,
            horizontalPadding = SpaceHorizontalLarge,
            trailingContent = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(44.dp)
                ) {
                    val displayAvatar: Any? = previewAvatarUri ?: userInfo?.avatarUrl.toFullMediaUrl()
                    SharedAvatar(
                        avatarUrl = displayAvatar,
                        size = 44.dp,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedContentScope = animatedContentScope
                    )
                    if (isUploadingAvatar) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(
                                    color = Color.Black.copy(alpha = 0.4f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        )
        ProfileValueItem(
            title = R.string.profile_nickname,
            value = userInfo?.nickName?.takeIf { it.isNotBlank() }
                ?: stringResource(id = R.string.profile_not_set)
        )
        ProfileValueItem(
            title = R.string.profile_account_id,
            value = accountValue(userInfo)
        )
        ProfileValueItem(
            title = R.string.profile_phone,
            value = userInfo?.phone?.takeIf { it.isNotBlank() }?.maskPhone()
                ?: stringResource(id = R.string.profile_not_set)
        )
        ProfileValueItem(
            title = R.string.profile_login_type,
            value = loginTypeText(userInfo?.loginType),
            showDivider = false
        )
    }
}

@Composable
private fun ProfileValueItem(
    @StringRes title: Int,
    value: String,
    showDivider: Boolean = true,
) {
    AppListItem(
        title = stringResource(id = title),
        showArrow = false,
        showDivider = showDivider,
        horizontalPadding = SpaceHorizontalLarge,
        verticalPadding = SpaceVerticalLarge,
        trailingContent = {
            AppText(
                text = value,
                type = TextType.TERTIARY,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.widthIn(max = 180.dp)
            )
        }
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedAvatar(
    avatarUrl: Any?,
    size: Dp,
    sharedTransitionScope: SharedTransitionScope?,
    animatedContentScope: AnimatedContentScope?,
    modifier: Modifier = Modifier,
) {
    val avatarModifier = if (sharedTransitionScope != null && animatedContentScope != null) {
        with(sharedTransitionScope) {
            modifier.sharedElement(
                sharedContentState = rememberSharedContentState(key = USER_AVATAR_SHARED_KEY),
                animatedVisibilityScope = animatedContentScope
            )
        }
    } else {
        modifier
    }

    AppAvatar(
        avatarUrl = avatarUrl,
        size = size,
        modifier = avatarModifier
    )
}

@Composable
private fun accountValue(userInfo: User?): String {
    return userInfo?.unionid?.takeIf { it.isNotBlank() }
        ?: userInfo?.id?.takeIf { it > 0 }?.toString()
        ?: stringResource(id = R.string.profile_not_set)
}

@Composable
private fun loginTypeText(loginType: String?): String {
    return when (loginType?.takeIf { it.isNotBlank() }) {
        "0" -> stringResource(id = R.string.profile_login_type_account)
        "1" -> stringResource(id = R.string.profile_login_type_sms)
        null -> stringResource(id = R.string.profile_not_set)
        else -> loginType
    }
}

private fun String.maskPhone(): String {
    return if (length >= 7) {
        replaceRange(3, length - 4, "****")
    } else {
        this
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    MaterialTheme {
        ProfileScreen(
            isLoggedIn = true,
            isUploadingAvatar = false,
            userInfo = User(
                id = 10086,
                unionid = "xinchat_akiha",
                nickName = "Akiha",
                phone = "13800138000"
            )
        )
    }
}
