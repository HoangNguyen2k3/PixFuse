package io.github.cogdanh2k3.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer

class Board (val size: Int){
    private val grid: Array<IntArray> = Array(size){
        IntArray(size){0}
    }
    private val shapeRenderer = ShapeRenderer()

    private val tileImages = mapOf(
        2 to Texture("titles/pikachu_2.png"),
        4 to Texture("titles/charmander_4.png"),
        8 to Texture("titles/bullbasaur_8.png"),
        16 to Texture("titles/squirtle_16.png"),
        32 to Texture("titles/eevee_32.png"),
        64 to Texture("titles/jigglypuff_64.png"),
        128 to Texture("titles/snorlax_128.png"),
        256 to Texture("titles/dratini_256.png"),
        512 to Texture("titles/pidgey_512.png"),
        1024 to Texture("titles/abra_1024.png"),
        2048 to Texture("titles/venonat_2048.png"),
    )

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
    var x: Float = 0f
    var y: Float = 0f
    var tileSize = 128f
    var padding = 30f

    val pixelSize: Float
        get() = size * tileSize + (size - 1) * padding

    fun setPosition(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    fun draw(batch: SpriteBatch) {
        for (row in 0 until size) {
            for (col in 0 until size) {
                val value = grid[row][col]
                if (value == 0) continue

                val drawX = x + col * (tileSize + padding)
                val drawY = y + row * (tileSize + padding)   // ✅ vẽ từ dưới lên
                tileImages[value]?.let { batch.draw(it, drawX, drawY, tileSize, tileSize) }
            }
        }
    }

//    fun draw(batch: SpriteBatch){
//        val tileSize = 128f
//        val padding = 15f
//        val boardSize = size * tileSize + (size - 1) * padding
//        val startX = (800 - boardSize) / 2f
//        val startY = 100f
//
//        // Ve tung tile
//        for(row in 0 until size){
//            for (col in 0 until size){
//                val value = grid[row][col]
//                if(value == 0) continue
//
//                val drawX = startX + col * (tileSize + padding)
//                val drawY = startY + (size - 1 - row) * (tileSize + padding)
//
//                val image = tileImages[value]
//
//                if(image != null){
//                    batch.draw(image, drawX, drawY, tileSize, tileSize)
//                }
//            }
//        }
//    }
    // Don tai nguyen
    fun dispose(){
        tileImages.values.forEach { it.dispose() }
        shapeRenderer.dispose()
    }
}
