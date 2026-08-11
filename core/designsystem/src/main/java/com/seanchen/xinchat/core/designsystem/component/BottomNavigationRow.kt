package com.seanchen.xinchat.core.designsystem.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seanchen.xinchat.core.designsystem.theme.Primary

@Composable
fun BottomNavigationRow(
    modifier: Modifier = Modifier,
    messageText: String,
    actionText: String,
    onCancelClick: () -> Unit = {},
    onActionClick: () -> Unit,
    divider: Boolean = false
) {
    CenterRow(
        modifier = modifier.padding(top = 32.dp),
    ) {
        if (!divider) {
            Text(
                text = messageText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
        } else {
            TextButton(onClick = onCancelClick) {
                Text(
                    text = messageText,
                    color = Color.Gray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        if (divider) {
            Text(
                text = "|",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            )
        }

        TextButton(onClick = onActionClick) {
            Text(
                text = actionText,
                color = if (divider) MaterialTheme.colorScheme.primary else Primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}