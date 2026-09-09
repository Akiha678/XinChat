package com.seanchen.xinchat.feature.user.view

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seanchen.xinchat.core.designsystem.component.VerticalList
import com.seanchen.xinchat.core.designsystem.theme.ArrowRightIcon
import com.seanchen.xinchat.core.designsystem.theme.ColorDanger
import com.seanchen.xinchat.core.designsystem.theme.ColorSuccess
import com.seanchen.xinchat.core.designsystem.theme.ColorWarning
import com.seanchen.xinchat.core.designsystem.theme.SpaceHorizontalLarge
import com.seanchen.xinchat.core.designsystem.theme.SpaceVerticalXSmall
import com.seanchen.xinchat.core.model.entity.User
import com.seanchen.xinchat.core.navigation.user.UserNavigator
import com.seanchen.xinchat.core.ui.component.image.Avatar
import com.seanchen.xinchat.core.ui.R as CoreUiR
import com.seanchen.xinchat.core.ui.component.image.SmallAvatar
import com.seanchen.xinchat.core.ui.component.list.AppListItem
import com.seanchen.xinchat.core.ui.component.scaffold.CommonScaffold
import com.seanchen.xinchat.core.ui.component.text.AppText
import com.seanchen.xinchat.core.ui.component.text.TextSize
import com.seanchen.xinchat.core.ui.component.text.TextType
import com.seanchen.xinchat.feature.user.R
import com.seanchen.xinchat.feature.user.viewmodel.MeViewModel

private const val USER_AVATAR_SHARED_KEY = "user_avatar"

/**
 * 我的界面
 */
@Composable
fun MeRoute(
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedContentScope? = null,
    viewModel: MeViewModel = hiltViewModel(),
) {
    // 获取生命周期所有者
    val lifecycleOwner = LocalLifecycleOwner.current

    // 注册生命周期观察者
    DisposableEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(viewModel)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(viewModel)
        }
    }

    // 收集登录状态
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    // 收集用户信息
    val userInfo by viewModel.userInfo.collectAsStateWithLifecycle()

    MeScreen(
        sharedTransitionScope = sharedTransitionScope,
        animatedContentScope = animatedContentScope,
        isLoggedIn = isLoggedIn,
        userInfo = userInfo,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MeScreen(
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedContentScope? = null,
    isLoggedIn: Boolean = false,
    userInfo: User? = null,
) {
    CommonScaffold(topBar = { }) { paddingValues ->
        MeContentView(
            isLoggedIn = isLoggedIn,
            userInfo = userInfo,
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = animatedContentScope,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun MeContentView(
    isLoggedIn: Boolean,
    userInfo: User?,
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedContentScope? = null,
) {
    VerticalList(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        UserInfoSection(
            isLoggedIn = isLoggedIn,
            userInfo = userInfo,
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = animatedContentScope
        )

        MeFeatureMenuSection()
        MeSettingsSection()
    }
}

@Composable
private fun UserInfoSection(
    isLoggedIn: Boolean,
    userInfo: User?,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedContentScope? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable{ UserNavigator.toProfile() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar(
            avatarUrl = userInfo?.avatarUrl,
            size = 72.dp,
            modifier = Modifier.let { modifier ->
                if (sharedTransitionScope != null && animatedContentScope != null) {
                    with(receiver = sharedTransitionScope) {
                        modifier.sharedElement(
                            sharedContentState = rememberSharedContentState(key = "user_avatar"),
                            animatedVisibilityScope = animatedContentScope
                        )
                    }
                } else {
                    modifier
                }
            }
        )

        SpaceHorizontalLarge()

        Column(
            modifier = Modifier.weight(1f)
        ) {
            AppText(
                text = (if (isLoggedIn && userInfo != null) userInfo.nickName else stringResource(R.string.not_logged_in)).toString(),
                size = TextSize.DISPLAY_MEDIUM
            )

            SpaceVerticalXSmall()

            AppText(
                text = if (isLoggedIn && userInfo != null && !userInfo.phone.isNullOrEmpty()) "用户名: ${userInfo.phone}" else "点击登录账号",
                size = TextSize.BODY_MEDIUM,
                type = TextType.TERTIARY
            )
        }

        ArrowRightIcon(tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun MeFeatureMenuSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        AppListItem(
            title = stringResource(id = R.string.profile_menu_services),
            leadingIcon = CoreUiR.drawable.ic_menu_list,
            leadingIconTint = MaterialTheme.colorScheme.primary
        )
        AppListItem(
            title = stringResource(id = R.string.profile_menu_favorites),
            leadingIcon = CoreUiR.drawable.ic_star_fill,
            leadingIconTint = ColorWarning
        )
        AppListItem(
            title = stringResource(id = R.string.profile_menu_cards),
            leadingIcon = CoreUiR.drawable.ic_coupon,
            leadingIconTint = ColorSuccess
        )
        AppListItem(
            title = stringResource(id = R.string.profile_menu_stickers),
            leadingIcon = CoreUiR.drawable.ic_my_fill,
            leadingIconTint = ColorDanger,
            showDivider = false
        )
    }
}

@Composable
private fun MeSettingsSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        AppListItem(
            title = stringResource(id = R.string.settings),
            leadingIcon = CoreUiR.drawable.ic_menu,
            leadingIconTint = MaterialTheme.colorScheme.onSurfaceVariant,
            showDivider = false
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedAvatar(
    avatarUrl: String?,
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

    SmallAvatar(
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

@Preview(showBackground = true)
@Composable
private fun MeScreenPreview() {
    MaterialTheme {
        MeScreen(
            isLoggedIn = true,
            userInfo = User(
                id = 10086,
                unionid = "xinchat_akiha",
                nickName = "Akiha"
            )
        )
    }
}
