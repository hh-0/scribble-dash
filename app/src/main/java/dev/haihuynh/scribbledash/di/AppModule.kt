package dev.haihuynh.scribbledash.di

import dev.haihuynh.scribbledash.drawing.DrawingViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::DrawingViewModel)
}