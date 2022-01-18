package chess

const val FIELD_SIZE = 8
const val BOTTOM_LETTERS = "    a   b   c   d   e   f   g   h"



object Board {

    private val cells: MutableList<MutableList<Cell>> = mutableListOf()
    private val pawns: MutableList<Pawn> = mutableListOf()

    val LETTERS = listOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h')

    init {
        initCells()
        initPawns()
    }

    fun getCell(x: Char, y: Int): Cell {
        return cells[LETTERS.indexOf(x)][y - 1]
    }

    private fun initCells() {
        for (x in 'a'..'h') {
            val row = mutableListOf<Cell>().apply {
                for (y in 1..FIELD_SIZE) {
                    this.add(Cell(x, y, null))
                }
            }
            cells.add(row)
        }
    }

    private fun initPawns() {
        for (i in 'a'..'h') {
            val cell = getCell(i, 2)
            val whitePawn = Pawn(PawnColor.WHITE, cell)
            cell.pawn = whitePawn
            pawns.add(whitePawn)
        }
        for (i in 'a'..'h') {
            val cell = getCell(i, 7)
            val blackPawn = Pawn(PawnColor.BLACK, cell)
            cell.pawn = blackPawn
            pawns.add(blackPawn)
        }
    }

    fun printBoard() {
        printSeparator()
        for (i in FIELD_SIZE downTo 1) {
            printRow(i)
            printSeparator()
        }
        println(BOTTOM_LETTERS)
    }

    private fun printRow(i: Int) {
        print("$i |")
        for (letter in 'a'..'h') {
            val pawn = pawns.find { it.cell.x == letter && it.cell.y == i } ?: " "
            print(" $pawn |")
        }
        println()
    }

    private fun printSeparator() {
        print("  +")
        repeat(FIELD_SIZE) {
            print("---+")
        }
        println()
    }

    fun getPawn(cell: Cell): Pawn? {
        return pawns.find { it.cell.x == cell.x && it.cell.y == cell.y }
    }

    fun removePawn(pawn: Pawn?) {
        pawns.remove(pawn)
    }

    fun getAllPawns(): MutableList<Pawn> {
        return pawns
    }
}

enum class PawnColor {
    WHITE,
    BLACK
}

data class Cell(val x: Char, val y: Int, var pawn: Pawn?)

class Pawn(val color: PawnColor, var cell: Cell) {
    override fun toString(): String {
        return if (color == PawnColor.WHITE) "W" else "B"
    }
}