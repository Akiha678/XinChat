package com.seanchen.xinchat.feature.auth.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seanchen.xinchat.feature.auth.R

/**
 * 现代简约风格的协议勾选栏（含圆形勾选框、协议和政策链接）
 */
@Composable
fun ModernAuthAgreement(
    isAgreed: Boolean,
    onAgreedChange: (Boolean) -> Unit,
    onUserAgreementClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularCheckbox(
            checked = isAgreed,
            onCheckedChange = onAgreedChange,
            size = 15.dp,
            activeColor = Color(0xFF1A1A1A),
            inactiveBorderColor = Color(0xFFC7CBD1)
        )

        Spacer(modifier = Modifier.width(6.dp))

        val linkColor = Color(0xFF3370FF)
        val normalColor = Color(0xFF888E99)

        Text(
            text = stringResource(id = R.string.read_and_agree_prefix),
            color = normalColor,
            fontSize = 12.sp,
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onAgreedChange(!isAgreed) }
            )
        )

        Text(
            text = stringResource(id = R.string.user_agreement),
            color = linkColor,
            fontSize = 12.sp,
            modifier = Modifier.clickable(onClick = onUserAgreementClick)
        )

        Text(
            text = stringResource(id = R.string.and),
            color = normalColor,
            fontSize = 12.sp,
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onAgreedChange(!isAgreed) }
            )
        )

        Text(
            text = stringResource(id = R.string.privacy_policy),
            color = linkColor,
            fontSize = 12.sp,
            modifier = Modifier.clickable(onClick = onPrivacyPolicyClick)
        )
    }
}
