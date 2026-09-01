package com.seanchen.xinchat.feature.user.view

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seanchen.xinchat.core.designsystem.component.VerticalList
import com.seanchen.xinchat.core.designsystem.theme.SpaceHorizontalLarge
import com.seanchen.xinchat.core.designsystem.theme.SpaceVerticalLarge
import com.seanchen.xinchat.core.designsystem.theme.SpaceVerticalSmall
import com.seanchen.xinchat.core.model.entity.User
import com.seanchen.xinchat.core.navigation.NavigationOptions
import com.seanchen.xinchat.core.navigation.auth.AuthRoutes
import com.seanchen.xinchat.core.navigation.main.MainRoutes
import com.seanchen.xinchat.core.navigation.navigate
import com.seanchen.xinchat.core.navigation.navigateBack
import com.seanchen.xinchat.core.ui.component.image.SmallAvatar
import com.seanchen.xinchat.core.ui.component.list.AppListItem
import com.seanchen.xinchat.core.ui.component.scaffold.AppScaffold
import com.seanchen.xinchat.core.ui.component.text.AppText
import com.seanchen.xinchat.core.ui.component.text.TextSize
import com.seanchen.xinchat.core.ui.component.text.TextType
import com.seanchen.xinchat.core.ui.component.title.TitleWithLine
import com.seanchen.xinchat.feature.user.component.FunctionMenuSection
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
    val scope = rememberCoroutineScope()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val userInfo by viewModel.userInfo.collectAsStateWithLifecycle()
    val isLoggingOut by viewModel.isLoggingOut.collectAsStateWithLifecycle()

    ProfileScreen(
        sharedTransitionScope = sharedTransitionScope,
        animatedContentScope = animatedContentScope,
        isLoggedIn = isLoggedIn,
        isLoggingOut = isLoggingOut,
        onBackClick = { navigateBack() },
        onLogoutClick = {
            scope.launch {
                viewModel.logout()
                navigate(
                    route = AuthRoutes.Login,
                    navOptions = NavigationOptions(
                        popUpToRoute = MainRoutes.Main,
                        inclusive = true,
                        allowPopToEmpty = true
                    )
                )
            }
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
    userInfo: User? = null,
    onBackClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
) {
    AppScaffold(
        title = com.seanchen.xinchat.feature.user.R.string.profile_detail_title,
        useLargeTopBar = true,
        onBackClick = onBackClick
    ) {
        ProfileContentView(
            modifier = modifier,
            isLoggedIn = isLoggedIn,
            isLoggingOut = isLoggingOut,
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
            text = stringResource(id = com.seanchen.xinchat.feature.user.R.string.profile_account_info),
            modifier = Modifier.padding(top = SpaceVerticalSmall)
        )

        ProfileInfoSection(
            userInfo = userInfo,
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
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedContentScope? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        AppListItem(
            title = stringResource(id = com.seanchen.xinchat.feature.user.R.string.profile_avatar),
            showArrow = false,
            verticalPadding = SpaceVerticalSmall,
            horizontalPadding = SpaceHorizontalLarge,
            trailingContent = {
                SharedAvatar(
                    avatarUrl = userInfo?.avatarUrl,
                    size = 44.dp,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedContentScope = animatedContentScope
                )
            }
        )
        ProfileValueItem(
            title = com.seanchen.xinchat.feature.user.R.string.profile_nickname,
            value = userInfo?.nickName?.takeIf { it.isNotBlank() }
                ?: stringResource(id = com.seanchen.xinchat.feature.user.R.string.profile_not_set)
        )
        ProfileValueItem(
            title = com.seanchen.xinchat.feature.user.R.string.profile_account_id,
            value = accountValue(userInfo)
        )
        ProfileValueItem(
            title = com.seanchen.xinchat.feature.user.R.string.profile_phone,
            value = userInfo?.phone?.takeIf { it.isNotBlank() }?.maskPhone()
                ?: stringResource(id = com.seanchen.xinchat.feature.user.R.string.profile_not_set)
        )
        ProfileValueItem(
            title = com.seanchen.xinchat.feature.user.R.string.profile_login_type,
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
private fun profileName(
    isLoggedIn: Boolean,
    userInfo: User?,
): String {
    return userInfo?.nickName?.takeIf { it.isNotBlank() }
        ?: stringResource(
            id = if (isLoggedIn) {
                com.seanchen.xinchat.feature.user.R.string.profile_default_nickname
            } else {
                com.seanchen.xinchat.feature.user.R.string.profile_guest_title
            }
        )
}

@Composable
private fun accountValue(userInfo: User?): String {
    return userInfo?.unionid?.takeIf { it.isNotBlank() }
        ?: userInfo?.id?.takeIf { it > 0 }?.toString()
        ?: stringResource(id = com.seanchen.xinchat.feature.user.R.string.profile_not_set)
}

@Composable
private fun loginTypeText(loginType: String?): String {
    return when (loginType?.takeIf { it.isNotBlank() }) {
        "0" -> stringResource(id = com.seanchen.xinchat.feature.user.R.string.profile_login_type_account)
        "1" -> stringResource(id = com.seanchen.xinchat.feature.user.R.string.profile_login_type_sms)
        null -> stringResource(id = com.seanchen.xinchat.feature.user.R.string.profile_not_set)
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
            userInfo = User(
                id = 10086,
                unionid = "xinchat_akiha",
                nickName = "Akiha",
                phone = "13800138000"
            )
        )
    }
}
