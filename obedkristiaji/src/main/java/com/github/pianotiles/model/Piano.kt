package com.github.pianotiles.model

class Piano {
    var tiles: List<Tile> = listOf()
    val size: Int = 4

    init {
        for(i in 1..size) {
            this.tiles += Tile()
        }
    }
}