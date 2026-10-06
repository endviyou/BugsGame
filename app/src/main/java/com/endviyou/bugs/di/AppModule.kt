package com.endviyou.bugs.di

import com.endviyou.bugs.viewmodels.GameViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { GameViewModel() }
}