package com.github.jghjianghan.model.audio

import com.github.jghjianghan.model.audio.MusicalNote.*

enum class Song(val noteList: Array<MusicalNote>) {
    FUR_ELISE(
        arrayOf(
            E5, EB5, E5, EB5, E5, B4, D5, C5, A4,
            C4, E4, A4, B4,
            E4, AB4, B4, C5,
            E4, E5, EB5, E5, EB5, E5, B4, D5, C5, A4,
            C4, E4, A4, B4,
            E4, C5, B4, A4,

            B4, C5, D5, E5,
            G4, F5, E5, D5,
            F4, E5, D5, C5,
            E4, D5, C5, B4,
            E4, E4, E5, E5, E4, E4
        )
    )

}