package com.example.photomine.di

import com.example.photomine.viewmodel.ViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainActivity = module {
    viewModel { ViewModel(get()) }
}

val listModule = listOf(
    mainActivity
)