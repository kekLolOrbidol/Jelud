package com.valpu.seve.tetris.models

import java.lang.IllegalArgumentException

enum class Figure(val frameCount: Int, val startPosition: Int) {

    Tetromino1O(1, 1) {
        override fun getFrame(frameNumber: Int): Borders {
            return Borders(2)
                    .addRow("11")
                    .addRow("11")
        }
    },

    Tetromino2Z(2, 1) {
        override fun getFrame(frameNumber: Int): Borders {
            return when (frameNumber) {
                0 -> Borders(3)
                        .addRow("110")
                        .addRow("011")
                1 -> Borders(2)
                        .addRow("01")
                        .addRow("11")
                        .addRow("10")
                else -> throw IllegalArgumentException("$frameNumber is an invalid frame number.")
            }
        }
    },

    Tetromino3S(2, 1) {
        override fun getFrame(frameNumber: Int): Borders {
            return when (frameNumber) {
                0 -> Borders(3)
                        .addRow("011")
                        .addRow("110")
                1 -> Borders(2)
                        .addRow("10")
                        .addRow("11")
                        .addRow("01")
                else -> throw IllegalArgumentException("$frameNumber is an invalid frame number.")
            }
        }
    },

    Tetromino4I(2, 2) {
        override fun getFrame(frameNumber: Int): Borders {
            return when (frameNumber) {
                0 -> Borders(4).addRow("1111")
                1 -> Borders(1)
                        .addRow("1")
                        .addRow("1")
                        .addRow("1")
                        .addRow("1")
                else -> throw IllegalArgumentException("$frameNumber is an invalid frame number.")
            }
        }
    },

    Tetromino5T(4, 1) {
        override fun getFrame(frameNumber: Int): Borders {
            return when (frameNumber) {
                0 -> Borders(3)
                        .addRow("010")
                        .addRow("111")
                1 -> Borders(2)
                        .addRow("10")
                        .addRow("11")
                        .addRow("10")
                2 -> Borders(3)
                        .addRow("111")
                        .addRow("010")
                3 -> Borders(2)
                        .addRow("01")
                        .addRow("11")
                        .addRow("01")
                else -> throw IllegalArgumentException("$frameNumber is an invalid frame number.")
            }
        }
    },

    Tetromino6J(4, 1) {
        override fun getFrame(frameNumber: Int): Borders {
            return when (frameNumber) {
                0 -> Borders(3)
                        .addRow("100")
                        .addRow("111")
                1 -> Borders(2)
                        .addRow("11")
                        .addRow("10")
                        .addRow("10")
                2 -> Borders(3)
                        .addRow("111")
                        .addRow("001")
                3 -> Borders(2)
                        .addRow("01")
                        .addRow("01")
                        .addRow("11")
                else -> throw IllegalArgumentException("$frameNumber is an invalid frame number.")
            }
        }
    },

    Tetromino7L(4, 1) {
        override fun getFrame(frameNumber: Int): Borders {
            return when (frameNumber) {
                0 ->  Borders(3)
                        .addRow("001")
                        .addRow("111")
                1 -> Borders(2)
                        .addRow("10")
                        .addRow("10")
                        .addRow("11")
                2 -> Borders(3)
                        .addRow("111")
                        .addRow("100")
                3 -> Borders(2)
                        .addRow("11")
                        .addRow("01")
                        .addRow("01")
                else -> throw IllegalArgumentException("$frameNumber is an invalid frame number.")
            }
        }
    };

    abstract fun getFrame(frameNumber: Int): Borders
}