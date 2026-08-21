package com.seanchen.xinchat.feature.auth.viewmodel

import android.content.Context
import com.seanchen.xinchat.core.common.base.viewmodel.BaseViewModel
import com.seanchen.xinchat.core.data.repository.AuthRepository
import com.seanchen.xinchat.core.data.state.AppState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class SmsLoginViewModel @Inject constructor(
    private val appState: AppState,
    private val authRepository: AuthRepository,
    @param:ApplicationContext private val context: Context
) : BaseViewModel(){
    companion object {
        private const val KEY_SAVED_PHONE = "saved_phone"
    }
}