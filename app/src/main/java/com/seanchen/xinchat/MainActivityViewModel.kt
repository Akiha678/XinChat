package com.seanchen.xinchat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seanchen.xinchat.core.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class MainActivityUiState(
    val isLoading: Boolean = true,
    val isLoggedIn: Boolean = false,
)

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    authRepository: AuthRepository,
//    @Suppress("unused") chatRepository: ChatRepository,
) : ViewModel() {
}
