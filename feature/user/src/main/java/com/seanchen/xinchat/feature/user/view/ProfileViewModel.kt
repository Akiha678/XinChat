//package com.seanchen.xinchat.feature.user.ui
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.seanchen.xinchat.core.data.model.User
//import com.seanchen.xinchat.core.data.repository.AuthRepository
//import dagger.hilt.android.lifecycle.HiltViewModel
//import javax.inject.Inject
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.update
//import kotlinx.coroutines.launch
//
//data class ProfileUiState(
//    val user: User? = null,
//    val isLoggingOut: Boolean = false,
//)
//
//@HiltViewModel
//class ProfileViewModel @Inject constructor(
//    private val authRepository: AuthRepository,
//) : ViewModel() {
//    private val mutableUiState = MutableStateFlow(ProfileUiState())
//    val uiState = mutableUiState.asStateFlow()
//
//    init {
//        viewModelScope.launch {
//            authRepository.session.collect { session ->
//                mutableUiState.update { it.copy(user = session?.user, isLoggingOut = false) }
//            }
//        }
//    }
//
//    fun logout() {
//        if (mutableUiState.value.isLoggingOut) return
//        viewModelScope.launch {
//            mutableUiState.update { it.copy(isLoggingOut = true) }
//            authRepository.logout()
//        }
//    }
//}
