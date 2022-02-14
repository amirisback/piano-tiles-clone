package com.github.obedkristiaji.model

class Piano {
    var tiles: List<Tile> = listOf()
    val size: Int = 4

    init {
        for(i in 1..size) {
            this.tiles += Tile()
        }
    }
}