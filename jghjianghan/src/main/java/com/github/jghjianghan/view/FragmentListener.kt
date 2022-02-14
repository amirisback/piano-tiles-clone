package com.github.jghjianghan.view

import com.github.jghjianghan.model.GameMode
import com.github.jghjianghan.model.Page

interface FragmentListener {
    fun changePage(page: Page)
    fun closeApplication()
    fun updateHighscore(newHighscore : Number, mode: GameMode)
}