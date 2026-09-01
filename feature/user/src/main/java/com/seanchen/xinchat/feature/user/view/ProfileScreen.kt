package com.seanchen.xinchat.feature.user.view

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
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
import com.seanchen.xinchat.core.ui.component.scaffold.CommonScaffold
import com.seanchen.xinchat.core.ui.component.text.AppText
import com.seanchen.xinchat.core.ui.component.text.TextType
import com.seanchen.xinchat.core.ui.component.title.TitleWithLine
import com.seanchen.xinchat.feature.user.component.FunctionMenuSection
import com.seanchen.xinchat.feature.user.viewmodel.ProfileViewModel

/**
 * 个人中心界面
 */
@Composable
fun ProfileRoute(
//    sharedTransitionScope: SharedTransitionScope? = null,
//    animatedContentScope: AnimatedContentScope? = null,
    viewModel: ProfileViewModel = hiltViewModel()
){
    val userInfo by viewModel.userInfo.collectAsStateWithLifecycle()

    ProfileScreen(
        onLogoutClick = viewModel::logout,
//        sharedTransitionScope = sharedTransitionScope,
//        animatedContentScope = animatedContentScope,
        userInfo = userInfo
    )
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProfileScreen(
    onLogoutClick: () -> Unit = {},
//    sharedTransitionScope: SharedTransitionScope? = null,
//    animatedContentScope: AnimatedContentScope? = null,
    userInfo: User? = null
){
    AppScaffold(
        titleText = "个人中心",
        useLargeTopBar = true,
        onBackClick = { navigateBack() }
    ) {
        ProfileContentView(
            onLogoutClick = onLogoutClick,
//            sharedTransitionScope = sharedTransitionScope,
//            animatedContentScope = animatedContentScope,
            userInfo = userInfo
        )
    }
}

@Composable
private fun ProfileContentView(
    onLogoutClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedContentScope? = null,
    userInfo: User? = null,
){
    VerticalList(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        Card {
            AppListItem(
                title = "头像",
                showArrow = false,
                verticalPadding = SpaceVerticalSmall,
                horizontalPadding = SpaceHorizontalLarge,
                trailingContent = {
                    SmallAvatar(
                        avatarUrl = userInfo?.avatarUrl,
                        modifier = Modifier.let{ modifier ->
                            if (sharedTransitionScope != null && animatedContentScope != null) {
                                with(sharedTransitionScope) {
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
                }
            )

            AppListItem(
                title = "昵称",
                showArrow = false,
                showDivider = false,
                horizontalPadding = SpaceHorizontalLarge,
                verticalPadding = SpaceVerticalLarge,
                trailingContent = {
                    AppText(
                        userInfo?.nickName ?: "未设置",
                        type = TextType.TERTIARY
                    )
                }
            )
        }

        TitleWithLine(
            text = "账号信息",
            modifier = Modifier.padding(top = SpaceVerticalSmall)
        )
    }
}
