package chess

import kotlin.system.exitProcess

object Game {
    private var player1: String = ""
    private var player2: String = ""
    private var turn = 1
    private var lastMove = ""

    fun start() {
        inputPlayers()
        Board.printBoard()
        startGame()
    }

    fun win(color: PawnColor?) {
        when (color) {
            PawnColor.WHITE -> println("White Wins!")
            PawnColor.BLACK -> println("Black Wins!")
            else -> println("Stalemate!")
        }
        print("Bye!")
        exitProcess(1)
    }

    private fun startGame() {
        while (true) {
            val movingPlayer = if (turn.mod(2) == 0) player2 else player1
            checkStaleMate(movingPlayer)
            val move = readMove(movingPlayer)
            makeMove(move)
            Board.printBoard()
            checkWin()
            turn++
        }
    }

    private fun checkWin() {
        if (lastMove.endsWith("8")) {
            win(PawnColor.WHITE)
        }
        if (lastMove.endsWith("1")) {
            win(PawnColor.BLACK)
        }
        if (Board.getAllPawns().none { it.color == PawnColor.WHITE }) {
            win(PawnColor.BLACK)
        }
        if (Board.getAllPawns().none { it.color == PawnColor.BLACK }) {
            win(PawnColor.WHITE)
        }
    }

    private fun checkStaleMate(movingPlayer: String) {
        val currentColor = if (movingPlayer == player1) PawnColor.WHITE else PawnColor.BLACK
        val isStaleMate = Board.getAllPawns().filter { it.color == currentColor }.all {
            getValidMoves(it.cell).isEmpty()
        }
        if (isStaleMate) {
            win(null)
        }
    }

    private fun makeMove(move: String) {
        val startX = move[0]
        val startY = move[1].toString().toInt()
        val endX = move[2]
        val endY = move[3].toString().toInt()

        val startCell = Board.getCell(startX, startY)
        val endCell = Board.getCell(endX, endY)

        if (endCell.pawn != null) {
            Board.removePawn(endCell.pawn)
        } else {
            if (endX != startX) {
                val realEnemyCell = Board.getCell(endX, startY)
                Board.removePawn(realEnemyCell.pawn)
                realEnemyCell.pawn = null
            }
        }

        val pawn = Board.getPawn(startCell)
        pawn!!.cell = endCell
        endCell.pawn = pawn
        startCell.pawn = null
        lastMove = move
    }

    private fun readMove(player: String): String {
        while (true) {
            println("$player's turn:")
            val currentMove = readLine()!!
            if (currentMove == "exit") {
                println("Bye!")
                exitProcess(1)
            }
            if (isValidMove(player, currentMove)) {
                return currentMove
            }
        }
    }

    private fun isValidMove(player: String, move: String): Boolean {
        try {
            if (!move.matches("[a-h][1-8][a-h][1-8]".toRegex())) {
                throw Exception()
            }
            val startX = move[0]
            val startY = move[1].toString().toInt()
            val startingCell = Board.getCell(startX, startY)
            val playerColor = if (player == player1) PawnColor.WHITE else PawnColor.BLACK
            if (startingCell.pawn?.color != playerColor) {
                println("No ${playerColor.name.lowercase()} pawn at $startX${startY}")
                return false
            }
            val endX = move[2]
            val endY = move[3].toString().toInt()

            if ("$endX$endY" !in getValidMoves(startingCell) && "$endX$endY>" !in getValidMoves(startingCell)) {
                throw Exception()
            }

            return true
        } catch (e: Exception) {
            println("Invalid Input")
            return false
        }
    }

    private fun getValidMoves(startingCell: Cell): List<String> {
        val result = mutableListOf<String>()
        val currentColor = startingCell.pawn!!.color

        val startPosY = if (startingCell.pawn?.color == PawnColor.WHITE) 2 else 7
        val moveVector = if (startingCell.pawn?.color == PawnColor.WHITE) 1 else -1

        if (Board.getCell(startingCell.x, startingCell.y + 1 * moveVector).pawn == null) {
            result.add("${startingCell.x}${startingCell.y + 1 * moveVector}")
        }
        if (startingCell.y == startPosY &&
            Board.getCell(startingCell.x, startingCell.y + 1 * moveVector).pawn == null &&
            Board.getCell(startingCell.x, startingCell.y + 2 * moveVector).pawn == null
        ) {
            result.add("${startingCell.x}${startingCell.y + 2 * moveVector}")
        }
        //check left side
        val hasLeftPawnToEat =
            startingCell.x > 'a' && Board.getCell(startingCell.x - 1, startingCell.y + 1 * moveVector).pawn != null
                    && Board.getCell(startingCell.x - 1, startingCell.y + 1 * moveVector).pawn?.color != currentColor
        val enPassantY = if (currentColor == PawnColor.WHITE) 5 else 4
        val enPassantEnemyY = if (currentColor == PawnColor.WHITE) 7 else 2
        val canEnPassantLeft =
            startingCell.x > 'a'
                    && startingCell.y == enPassantY
                    && lastMove == "${startingCell.x - 1}${enPassantEnemyY}${startingCell.x - 1}${enPassantEnemyY - 2 * moveVector}"
        if (startingCell.x - 1 >= Board.LETTERS.first()
            && (hasLeftPawnToEat || canEnPassantLeft)
        ) {
            result.add("${startingCell.x - 1}${startingCell.y + 1 * moveVector}${if (canEnPassantLeft) ">" else ""}")
        }
        //check right side
        val hasRightPawnToEat =
            startingCell.x < 'h' && Board.getCell(startingCell.x + 1, startingCell.y + 1 * moveVector).pawn != null
                    && Board.getCell(startingCell.x + 1, startingCell.y + 1 * moveVector).pawn?.color != currentColor
        val canEnPassantRight =
            startingCell.x < 'h'
                    && startingCell.y == enPassantY
                    && lastMove == "${startingCell.x + 1}${enPassantEnemyY}${startingCell.x + 1}${enPassantEnemyY - 2 * moveVector}"
        if (startingCell.x + 1 <= Board.LETTERS.last()
            && (hasRightPawnToEat || canEnPassantRight)
        ) {
            result.add("${startingCell.x + 1}${startingCell.y + 1 * moveVector}")
        }

        return result
    }

    private fun inputPlayers() {
        println("First Player's name:")
        player1 = readLine()!!
        println("Second Player's name:")
        player2 = readLine()!!
    }
}