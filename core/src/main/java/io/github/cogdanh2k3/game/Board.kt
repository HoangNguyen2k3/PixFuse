package io.github.cogdanh2k3.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

class Board (val size: Int){
    private val grid: Array<IntArray> = Array(size){
        IntArray(size){0}
    }

    private val tileTextures : MutableMap<Int, Texture> = mutableMapOf()

    init {
        tileTextures[2] = Texture("tiles/2.png")
        tileTextures[4] = Texture("tiles/4.png")
        tileTextures[8] = Texture("tiles/8.png")
        tileTextures[16] = Texture("tiles/16.png")
        tileTextures[32] = Texture("tiles/32.png")
        tileTextures[64] = Texture("tiles/64.png")
        tileTextures[128] = Texture("tiles/128.png")
    }

    fun getTile(row: Int, col: Int): Int = grid[row][col]

    fun setTile(row: Int, col: Int, value: Int){
        grid[row][col] = value
    }

    fun getEmptyCells(): List<Pair<Int, Int>> {
        val empty = mutableListOf<Pair<Int, Int>>()
        for (row in 0 until size){
            for (col in 0 until size){
                if(grid[row][col]==0){
                    empty.add(Pair(row, col))
                }
            }
        }
        return empty;
    }

    fun getRow(row: Int): List<Int> = grid[row].toList()

    fun getCol(col: Int): List<Int> {
        val column = mutableListOf<Int>()
        for (row in 0 until size) column.add(grid[row][col])
        return column
    }

    fun draw(batch: SpriteBatch){
        val tileSize = 128f
        val startX = 50f
        val startY = 100f

        for(row in 0 until size){
            for (col in 0 until size){
                val value = grid[row][col]
                if(value != 0){
                    val texture = tileTextures[value]
                    if(texture != null){
                        val drawX = startX + col * (tileSize + 10)
                        val drawY = startY + (size - 1 - row) * (tileSize + 10) // bottom -> top
                        batch.draw(texture, drawX, drawY, tileSize, tileSize)
                    }
                }
            }
        }
    }
    // Don tai nguyen
    fun dispose(){
        tileTextures.values.forEach { it.dispose() }
    }
}
