package com.fcossetta.simpletest

import com.bumptech.glide.Glide
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val glideModule = module {
    single { Glide.with(androidContext()) }
}