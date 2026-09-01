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
import androidx.compose.runtime.getValue
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
import com.seanchen.xinchat.core.designsystem.theme.ArrowRightIcon
import com.seanchen.xinchat.core.designsystem.theme.ColorDanger
import com.seanchen.xinchat.core.designsystem.theme.ColorSuccess
import com.seanchen.xinchat.core.designsystem.theme.ColorWarning
import com.seanchen.xinchat.core.designsystem.theme.SpaceHorizontalLarge
import com.seanchen.xinchat.core.designsystem.theme.SpacePaddingLarge
import com.seanchen.xinchat.core.designsystem.theme.SpaceVerticalLarge
import com.seanchen.xinchat.core.model.entity.User
import com.seanchen.xinchat.core.navigation.navigate
import com.seanchen.xinchat.core.ui.R as CoreUiR
import com.seanchen.xinchat.core.ui.component.image.SmallAvatar
import com.seanchen.xinchat.core.ui.component.list.AppListItem
import com.seanchen.xinchat.core.ui.component.scaffold.AppScaffold
import com.seanchen.xinchat.core.ui.component.text.AppText
import com.seanchen.xinchat.core.ui.component.text.TextSize
import com.seanchen.xinchat.core.ui.component.text.TextType
import com.seanchen.xinchat.feature.user.R
import com.seanchen.xinchat.feature.user.navigation.ProfileRoutes
import com.seanchen.xinchat.feature.user.viewmodel.MeViewModel

private const val USER_AVATAR_SHARED_KEY = "user_avatar"

@Composable
fun MeRoute(
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedContentScope? = null,
    viewModel: MeViewModel = hiltViewModel(),
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val userInfo by viewModel.userInfo.collectAsStateWithLifecycle()

    MeScreen(
        sharedTransitionScope = sharedTransitionScope,
        animatedContentScope = animatedContentScope,
        isLoggedIn = isLoggedIn,
        userInfo = userInfo,
        onProfileClick = { navigate(ProfileRoutes.Profile) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MeScreen(
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedContentScope? = null,
    isLoggedIn: Boolean = false,
    userInfo: User? = null,
    onProfileClick: () -> Unit = {},
) {
    AppScaffold(
        title = R.string.profile_title,
        showBackIcon = false,
    ) {
        MeContentView(
            isLoggedIn = isLoggedIn,
            userInfo = userInfo,
            onProfileClick = onProfileClick,
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = animatedContentScope
        )
    }
}

@Composable
private fun MeContentView(
    isLoggedIn: Boolean,
    userInfo: User?,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedContentScope? = null,
) {
    VerticalList(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        UserInfoSection(
            isLoggedIn = isLoggedIn,
            userInfo = userInfo,
            onProfileClick = onProfileClick,
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
    onProfileClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedContentScope? = null,
) {
    val displayName = userInfo?.nickName?.takeIf { it.isNotBlank() }
        ?: stringResource(
            id = if (isLoggedIn) {
                R.string.profile_default_nickname
            } else {
                R.string.profile_guest_title
            }
        )
    val accountText = if (isLoggedIn) {
        stringResource(
            id = R.string.profile_account_format,
            accountValue(userInfo)
        )
    } else {
        stringResource(id = R.string.profile_guest_description)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onProfileClick)
            .padding(horizontal = SpacePaddingLarge, vertical = SpaceVerticalLarge),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SharedAvatar(
            avatarUrl = userInfo?.avatarUrl,
            size = 64.dp,
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = animatedContentScope
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = SpaceHorizontalLarge)
        ) {
            AppText(
                text = displayName,
                size = TextSize.DISPLAY_MEDIUM,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            AppText(
                text = accountText,
                type = TextType.TERTIARY,
                size = TextSize.BODY_MEDIUM,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        ArrowRightIcon(size = 18.dp)
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
