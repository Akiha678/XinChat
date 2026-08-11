package com.seanchen.xinchat.feature.auth.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.seanchen.xinchat.core.designsystem.component.CenterRow
import com.seanchen.xinchat.core.designsystem.component.StartRow
import com.seanchen.xinchat.core.designsystem.theme.SpaceVerticalMedium
import com.seanchen.xinchat.core.ui.component.text.AppText
import com.seanchen.xinchat.core.ui.component.text.TextSize
import com.seanchen.xinchat.core.ui.component.text.TextType
import com.seanchen.xinchat.feature.auth.R

@Composable
fun UserAgreement(
    modifier: Modifier = Modifier,
    prefix: String = "",
    centerAlignment: Boolean = false,
    onUserAgreementClick: () -> Unit = {},
    onPrivacyPolicyClick: () -> Unit = {}
) {
    val prefixText = if (prefix.isEmpty()) {
        stringResource(id = R.string.login_agreement_prefix)
    } else {
        prefix
    }

    // 根据centerAlignment参数选择不同的Row容器
    if (centerAlignment) {
        CenterRow(modifier = modifier) {
            AgreementContent(
                prefixText = prefixText,
                onUserAgreementClick = onUserAgreementClick,
                onPrivacyPolicyClick = onPrivacyPolicyClick
            )
        }
    } else {
        StartRow(modifier = modifier) {
            AgreementContent(
                prefixText = prefixText,
                onUserAgreementClick = onUserAgreementClick,
                onPrivacyPolicyClick = onPrivacyPolicyClick
            )
        }
        SpaceVerticalMedium()
    }
}

/**
 * 用户协议内容组件，抽取公共部分
 *
 * @param prefixText 前缀文本
 * @param onUserAgreementClick 用户协议点击回调
 * @param onPrivacyPolicyClick 隐私政策点击回调
 * @author Joker.X
 */
@Composable
private fun AgreementContent(
    prefixText: String,
    onUserAgreementClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit
) {
    AppText(
        text = prefixText,
        type = TextType.TERTIARY,
        size = TextSize.BODY_MEDIUM
    )

    AppText(
        text = stringResource(id = R.string.user_agreement),
        type = TextType.LINK,
        size = TextSize.BODY_MEDIUM,
        onClick = onUserAgreementClick
    )

    AppText(
        text = stringResource(id = R.string.and),
        type = TextType.TERTIARY,
        size = TextSize.BODY_MEDIUM
    )

    AppText(
        text = stringResource(id = R.string.privacy_policy),
        type = TextType.LINK,
        size = TextSize.BODY_MEDIUM,
        onClick = onPrivacyPolicyClick
    )
}