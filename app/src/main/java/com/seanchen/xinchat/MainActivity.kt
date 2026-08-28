package com.seanchen.xinchat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation3.runtime.NavKey
import com.seanchen.widget.ui.theme.AppTheme
import com.seanchen.xinchat.core.data.state.AppState
import com.seanchen.xinchat.core.navigation.AppNavigator
import com.seanchen.xinchat.core.navigation.auth.AuthRoutes
import com.seanchen.xinchat.core.navigation.main.MainRoutes
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigator: AppNavigator

    @Inject
    lateinit var appState: AppState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val startDestination = resolveStartDestination()
        setContent {
            AppTheme {
                AppNavHost(
                    navigator = navigator,
                    startDestination = startDestination
                )
            }
        }
    }

    private fun resolveStartDestination(): NavKey {
        return runBlocking(Dispatchers.IO) {
            appState.initialize()
            if (appState.isLoggedIn.value) {
                MainRoutes.Main
            } else {
                AuthRoutes.Login
            }
        }
    }
}
