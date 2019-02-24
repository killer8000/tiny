package com.tiny.sbus

import androidx.annotation.IntDef

object SEvent {
    const val EV_0 = 0
    const val EV_1 = 1
    const val EV_2 = 2
    const val PLUGIN = 3
    const val HOST = 4

    @IntDef(EV_0, EV_1, EV_2, PLUGIN, HOST)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Event
}


