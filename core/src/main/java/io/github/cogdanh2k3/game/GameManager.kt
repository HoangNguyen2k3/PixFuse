package io.github.cogdanh2k3.game

import kotlin.random.Random

class GameManager(val board: Board){
    var score = 0
        private set

    var isMoved = false
        private set

    //Spawn random
    fun spawnTile(){
        val emptyCells = board.getEmptyCells()
        if(emptyCells.isNotEmpty()){
            val (x, y) = emptyCells.random()
            board.setTile(y,x, if(Random.nextFloat() < 0.9f) 2 else 4) // 90% - 2, 10% - 4
        }
    }

    fun update(){
        if(isMoved == true){
            spawnTile()
            isMoved = false
        }
    }

    // Movement
    fun moveLeft(){
        isMoved = false
        for(y in 0 until board.size){
            val row = board.getRow(y)
            val merged = mergeLine(row)

            for (x in 0 until board.size){
                if(board.getTile(y, x) !=  merged[x]){
                    isMoved = true
                }
                board.setTile(y, x, merged[x])
            }
        }
    }
    fun moveRight(){
        isMoved = false
        for (y in 0 until board.size){
            val row = board.getRow(y).reversed() // nguoc lai left
            val merged = mergeLine(row).reversed()

            for (x in 0 until board.size){
                if(board.getTile(y, x) !=  merged[x]){
                    isMoved = true
                }
                board.setTile(y, x, merged[x])
            }
        }
    }

    fun moveUp(){
        isMoved = false
        for (x in 0 until board.size){
            val col = board.getCol(x)
            val merged = mergeLine(col)

            for (y in 0 until board.size){
                if(board.getTile(y, x) != merged[y]){
                    isMoved = true
                }
                board.setTile(y, x, merged[y])
            }
        }
    }

    fun moveDown(){
        isMoved = false
        for (x in 0 until board.size){
            val col = board.getCol(x).reversed()
            val merged = mergeLine(col).reversed()

            for (y in 0 until board.size){
                if(board.getTile(y, x) != merged[y]){
                    isMoved = true
                }
                board.setTile(y, x, merged[y])
            }
        }
    }



    //merge 1 dong hoac 1 cot
    private fun mergeLine(line: List<Int>): List<Int>{
        val newLine = line.filter{it!=0}.toMutableList() //bo qua o trong
        val mergedLine = mutableListOf<Int>()
        var skip = false

        for(i in newLine.indices){
            if(skip){
                skip = false
                continue
            }

            if(i< newLine.size - 1 && newLine[i] == newLine[i+1]){
                val mergedValue = newLine[i] * 2
                score += mergedValue
                mergedLine.add(mergedValue)
                skip = true
            }else{
                mergedLine.add(newLine[i])
            }
        }

        // add 0 cho du size
        while (mergedLine.size < board.size) {
            mergedLine.add(0)
        }

        return mergedLine
    }

    fun isGameOver(): Boolean{
        if(board.getEmptyCells().isNotEmpty())return false

        // check con merge duoc ko
        for(y in 0 until board.size){
            for(x in 0 until board.size){
                val value = board.getTile(y, x)
                if(x < board.size - 1 && value == board.getTile(y + 1, x)) return false
                if(y < board.size - 1 && value == board.getTile(y, x+1)) return false

            }
        }
        return true
    }
}
