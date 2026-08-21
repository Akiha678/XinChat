//package com.seanchen.xinchat.feature.user.ui
//
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Button
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.unit.dp
//import com.seanchen.xinchat.feature.user.R
//
//@Composable
//internal fun ProfileScreen(
//    uiState: ProfileUiState,
//    onLogout: () -> Unit,
//    modifier: Modifier = Modifier,
//) {
//    Column(
//        modifier = modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 24.dp),
//        verticalArrangement = Arrangement.spacedBy(20.dp),
//    ) {
//        Text(stringResource(R.string.profile_title), style = MaterialTheme.typography.headlineMedium)
//        Surface(
//            modifier = Modifier.fillMaxWidth(),
//            shape = MaterialTheme.shapes.large,
//            color = MaterialTheme.colorScheme.surfaceContainer,
//        ) {
//            Column(
//                modifier = Modifier.padding(24.dp),
//                verticalArrangement = Arrangement.spacedBy(8.dp),
//            ) {
//                Text(
//                    uiState.user?.displayName ?: stringResource(R.string.profile_guest_title),
//                    style = MaterialTheme.typography.titleLarge,
//                )
//                uiState.user?.let { user ->
//                    Text("@${user.username}", color = MaterialTheme.colorScheme.onSurfaceVariant)
//                    Text(user.email, color = MaterialTheme.colorScheme.onSurfaceVariant)
//                }
//            }
//        }
//        Button(
//            onClick = onLogout,
//            modifier = Modifier.fillMaxWidth(),
//            enabled = !uiState.isLoggingOut,
//        ) {
//            Text(
//                if (uiState.isLoggingOut) {
//                    stringResource(R.string.logging_out)
//                } else {
//                    stringResource(R.string.logout)
//                },
//            )
//        }
//    }
//}
