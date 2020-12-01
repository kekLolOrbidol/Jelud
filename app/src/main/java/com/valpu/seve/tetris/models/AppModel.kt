package com.valpu.seve.tetris.models

import android.graphics.Point
import com.valpu.seve.tetris.constants.CellConsts
import com.valpu.seve.tetris.constants.FieldConsts
import com.valpu.seve.tetris.helpers.array2dOfByte
import com.valpu.seve.tetris.storage.AppPreferences

class AppModel {

    var score: Int = 0
    private var preferences: AppPreferences? = null

    var currentPuzzle: Puzzle? = null
    var currentState: String = Statuses.AWAITING_START.name

    private var field: Array<ByteArray> = array2dOfByte(
            FieldConsts.ROW_COUNT.value,
            FieldConsts.COLUMN_COUNT.value)

    enum class Statuses {
        AWAITING_START, ACTIVE, INACTIVE, OVER
    }

    enum class Motions {
        LEFT, RIGHT, DOWN, ROTATE
    }

    fun generateField(action: String) {
        if (isGameActive()) {
            resetField()
            var frameNumber: Int? = currentPuzzle?.getFrameNumber()
            val coordinate: Point? = Point()
            coordinate?.x = currentPuzzle?.getPosition()?.x
            coordinate?.y = currentPuzzle?.getPosition()?.y

            when (action) {
                Motions.LEFT.name -> coordinate?.x = currentPuzzle?.getPosition()?.x?.minus(1)
                Motions.RIGHT.name -> coordinate?.x = currentPuzzle?.getPosition()?.x?.plus(1)
                Motions.DOWN.name -> coordinate?.y = currentPuzzle?.getPosition()?.y?.plus(1)
                Motions.ROTATE.name -> {
                    frameNumber = frameNumber?.plus(1)

                    if (frameNumber != null) {
                        if (frameNumber >= currentPuzzle?.frameCount as Int) {
                            frameNumber = 0
                        }
                    }
                }
            }

            if (!moveValid(coordinate as Point, frameNumber)) {
                translateBlock(currentPuzzle?.getPosition() as Point,
                        currentPuzzle?.getFrameNumber() as Int)

                if (Motions.DOWN.name == action) {
                    boostScore()
                    persistCellData()
                    assessField()
                    generateNextBlock()

                    if (!blockAdditionPossible()) {
                        currentState = Statuses.OVER.name
                        currentPuzzle = null
                        resetField(false)
                    }
                }
            } else {
                if (frameNumber != null) {
                    translateBlock(coordinate, frameNumber)
                    currentPuzzle?.setState(frameNumber, coordinate)
                }
            }
        }
    }

    private fun resetField(ephemeralCellsOnly: Boolean = true) {

        for (i in 0 until FieldConsts.ROW_COUNT.value) {
            (0 until FieldConsts.COLUMN_COUNT.value)
                    .filter { !ephemeralCellsOnly || field[i][it] == CellConsts.EPHEMERAL.value }
                    .forEach { field[i][it] = CellConsts.EMPTY.value }
        }
    }

    private fun persistCellData() {

        for (i in 0 until field.size) {
            for (j in 0 until field[i].size) {
                var status = getCellStatus(i, j)

                if (status == CellConsts.EPHEMERAL.value) {
                    status = currentPuzzle?.staticValue!!
                    setCellStatus(i, j, status)
                }
            }
        }
    }

    private fun assessField() {
        for (i in 0 until field.size) {
            var emptyCells = 0

            for (j in 0 until field[i].size) {
                val status = getCellStatus(i, j)
                val isEmpty = CellConsts.EMPTY.value == status
                if (isEmpty)
                    emptyCells++
            }

            if (emptyCells == 0)
                shiftRows(i)
        }
    }

    private fun translateBlock(position: Point, frameNumber: Int) {

        synchronized(field) {
            val shape: Array<ByteArray>? = currentPuzzle?.getShape(frameNumber)

            if (shape != null) {
                for (i in shape.indices) {
                    for (j in 0 until shape[i].size) {
                        val y = position.y + i
                        val x = position.x + j

                        if (CellConsts.EMPTY.value != shape[i][j])
                            field[y][x] = shape[i][j]
                    }
                }
            }
        }
    }

    private fun blockAdditionPossible(): Boolean {

        if (!moveValid(currentPuzzle?.getPosition() as Point, currentPuzzle?.getFrameNumber()))
            return false
        return true
    }

    private fun shiftRows(nToRow: Int) {

        if (nToRow > 0) {
            for (j in nToRow - 1 downTo 0) {
                for (m in 0 until field[j].size) {
                    setCellStatus(j + 1, m, getCellStatus(j, m))
                }
            }
        }

        for (j in 0 until field[0].size) {
            setCellStatus(0, j, CellConsts.EMPTY.value)
        }
    }

    fun startGame() {

        if (!isGameActive()) {
            currentState = Statuses.ACTIVE.name
            generateNextBlock()
        }
    }

    fun restartGame() {
        resetModel()
        startGame()
    }

    fun endGame() {
        score = 0
        currentState = AppModel.Statuses.OVER.name
    }

    fun resetModel() {
        resetField(false)
        currentState = Statuses.AWAITING_START.name
        score = 0
    }

    fun setPreferences(preferences: AppPreferences?) {
        this.preferences = preferences
    }

    fun getCellStatus(row: Int, column: Int): Byte {
        return field[row][column]
    }

    private fun setCellStatus(row: Int, column: Int, status: Byte?) {
        if (status != null)
            field[row][column] = status
    }

    fun isGameOver(): Boolean {
        return currentState == Statuses.OVER.name
    }

    fun isGameActive(): Boolean {
        return currentState == Statuses.ACTIVE.name
    }

    fun isGameAwaitingStart(): Boolean {
        return currentState == Statuses.AWAITING_START.name
    }

    private fun boostScore() {
        score += 10

        if (score > preferences?.getHighScore() as Int)
            preferences?.saveHighScore(score)
    }

    private fun generateNextBlock() {
        currentPuzzle = Puzzle.createBlock()
    }

    private fun validTranslation(position: Point, shape: Array<ByteArray>): Boolean {

        return if (position.y < 0 || position.x < 0) {
            false
        } else if (position.y + shape.size > FieldConsts.COLUMN_COUNT.value) {
            false
        } else if (position.x + shape[0].size > FieldConsts.ROW_COUNT.value) {
            false
        } else {
            for (i in 0 until shape.size) {
                for (j in 0 until shape[i].size) {
                    val y = position.y + i
                    val x = position.x + j

                    if (CellConsts.EMPTY.value != shape[i][j] &&
                            CellConsts.EMPTY.value != field[y][x]) {
                        return false
                    }
                }
            }
            true
        }
    }

    private fun moveValid(position: Point, frameNumber: Int?): Boolean {

        val shape: Array<ByteArray>? = currentPuzzle?.getShape(frameNumber as Int)

        return validTranslation(position, shape as Array<ByteArray>)
    }
}