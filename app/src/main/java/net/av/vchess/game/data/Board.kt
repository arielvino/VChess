package net.av.vchess.game.data

import net.av.vchess.reusables.Vector2D


class Board(val width: Int, val height: Int) {
    private var tiles: ArrayList<ArrayList<Tile>> = arrayListOf()

    init {
        for (x in 0..<width) {
            tiles.add(arrayListOf())
            for (y in 0..<height) {
                tiles[x].add(Tile(Vector2D(x, y)))
            }
        }
    }

    fun getTile(x: Int, y: Int): Tile {
        return tiles[x][y]
    }

    fun getTile(location: Vector2D):Tile{
        return tiles[location.x][location.y]
    }

    fun hasTile(location: Vector2D): Boolean {
        return location.x in 0..<width && location.y in 0..< height
    }
}