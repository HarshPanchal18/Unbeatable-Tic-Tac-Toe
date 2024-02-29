package org.harsh

import kotlin.math.max
import kotlin.math.min

// Representing the game board
data class Board(
    val array: Array<CharArray> = Array(3) { CharArray(3) { '-' } },
    var score: Int = 0,
    var index: Int = 0,
)

// Terminal state check
fun check(board: Board): Char {

    for (i in 0..<3) {
        // Finding three X or O in each row.
        var count = 0
        for (j in 0..<3)
            if (board.array[i][j] == 'X')
                count++

        if (count == 3)
            return 'X' // machine wins.

        count = 0
        for (j in 0..<3)
            if (board.array[i][j] == 'O')
                count++

        if (count == 3)
            return 'O' // user wins.

        count = 0
        for (j in 0..<3)
            if (board.array[j][i] == 'O')
                count++

        if (count == 3)
            return 'O'

        count = 0
        for (j in 0..<3)
            if (board.array[j][i] == 'X')
                count++

        if (count == 3)
            return 'X'

    }

    // If no winner is found in the rows - columns, check the two diagonals for a win condition.
    if (board.array[0][0] == 'X' && board.array[1][1] == 'X' && board.array[2][2] == 'X')
        return 'X'

    if (board.array[0][0] == 'O' && board.array[1][1] == 'O' && board.array[2][2] == 'O')
        return 'O'

    if (board.array[0][2] == 'X' && board.array[1][1] == 'X' && board.array[2][0] == 'X')
        return 'X'

    if (board.array[0][2] == 'O' && board.array[1][1] == 'O' && board.array[2][0] == 'O')
        return 'O'

    // check if the game is a draw by counting the number of '-'
    var count = 0
    for (i in 0..<3)
        for (j in 0..<3)
            if (board.array[i][j] == '-')
                count++

    return if (count == 0) 'D'
    else '-'

}

var len = 0 // Keeping length of moves
var win = 0
var lose = 0
var draw = 0
val store = mutableListOf<Board>() // store list of all Board objects
// When a new Board object is created, it’s added to the store list, and its index in the list is also stored in the mapping map.
// The key in the map is the index of the Board object,
val mapping = mutableMapOf<Int, Int>()
val game = MutableList(1000000) { mutableListOf<Int>() } // Game tree

// Build game tree
fun build(board: Board, turn: Char, index: Int, depth: Int) {
    if (check(board) == 'X') {
        store[mapping[index]!!].score = 10
        win++
        return
    }

    if (check(board) == 'O') {
        store[mapping[index]!!].score = -10
        lose++
        return
    }

    if (check(board) == 'D') {
        store[mapping[index]!!].score = 0
        draw++
        return
    }

    // To keep track of the best score that the machine (the ‘C’ player) can achieve, and the worst score that the user (the ‘P’ player) can achieve.
    var maximum = -10_000
    var minimum = 10_000

    if (turn == 'C') {
        for (i in 0..<3) {
            for (j in 0..<3) {
                // For each empty cell, create new board state where machine has made its move in that cell,
                if (board.array[i][j] == '-') {
                    val next = Board()
                    for (k in 0..<3)
                        next.array[k] = board.array[k].clone()

                    next.array[i][j] = 'X'
                    next.index = len
                    store.add(next)
                    mapping[len] = store.size - 1
                    len++
                    game[index].add(len - 1)
                    val x = len - 1
                    // build the game tree from that state recursively
                    build(next, 'P', len - 1, depth + 1)
                    // After considering all possible moves, the score of the current state is set to the max score found
                    maximum = max(maximum, store[mapping[x]!!].score)
                }
            }
        }
        // minus the depth of the current state in the game tree.
        // This depth adjustment encourages the machine to win as quickly as possible or to delay losing as long as possible.
        store[mapping[index]!!].score = maximum - depth
    }

    if (turn == 'P') {
        for (i in 0..<3) {
            for (j in 0..<3) {
                // Similar as machine algorithm, but in opposite way.
                if (board.array[i][j] == '-') {
                    val next = Board()
                    for (k in 0..<3)
                        next.array[k] = board.array[k].clone()

                    next.array[i][j] = 'O'
                    next.index = len
                    store.add(next)
                    mapping[len] = store.size - 1
                    len++
                    game[index].add(len - 1)
                    val x = len - 1
                    build(next, 'C', len - 1, depth + 1)
                    // Look for minimum score instead of max, because we’re assuming that the user will play optimally and try to minimize the AI’s score.
                    minimum = min(minimum, store[mapping[x]!!].score)
                }
            }
        }
        store[mapping[index]!!].score = minimum + depth
    }
}

fun printBoard(board: Board) {
    for (i in 0..<3) {
        for (j in 0..<3)
            print("${board.array[i][j]} ")
        println()
    }
    println()
}

// checks if two game boards are identical
fun compare(b1: Board, b2: Board): Boolean {
    var count = 0

    // Returns true if all corresponding cells in the two boards are the same, and false otherwise.
    for (i in 0..<3)
        for (j in 0..<3)
            if (b1.array[i][j] == b2.array[i][j])
                count++

    return count == 9
}

// Exit condition
fun over(board: Board): Boolean {
    if (check(board) == 'X') {
        printBoard(board)
        println("You lost! HA-HA-HA-HA-HA-HA-HA")
        return true
    } else return if (check(board) == 'D') {
        printBoard(board)
        println("We got a Tie match!")
        true
    } else if (check(board) == 'O') {
        printBoard(board)
        println("You win!")
        true
    } else false
}

fun main() {
    var board = Board()
    board.index = 0
    store.add(board)
    mapping[0] = 0
    store.add(board)
    len++

    build(board, 'P', 0, 0)
    val m = mutableMapOf<Int, Pair<Int, Int>>() // Game board

    /*1 2 3
      4 5 6
      7 8 9*/

    m[1] = Pair(0, 0)
    m[2] = Pair(0, 1)
    m[3] = Pair(0, 2)
    m[4] = Pair(1, 0)
    m[5] = Pair(1, 1)
    m[6] = Pair(1, 2)
    m[7] = Pair(2, 0)
    m[8] = Pair(2, 1)
    m[9] = Pair(2, 2)

    println("You can't beat me, but you can try!!")
    println("You have 'O' to play.")

    println("Position map: ")
    var position = 1
    for (i in 0..<3) {
        for (j in 0..<3)
            print("${position++} ")
        println()
    }
    println()

    // continue until the game is over.
    while (true) {

        if (over(board)) return

        for (i in 0..<3) {
            for (j in 0..<3)
                print("${board.array[i][j]} ")
            println()
        }

        println()

        print("Enter your position: ")
        var choose = readln().toInt()

        while (choose !in 1..9) {
            print("Invalid move, Enter again: ")
            choose = readln().toInt()
        }

        // Convert entered position to row and column
        var i = m[choose]?.first
        var j = m[choose]?.second

        while (board.array[i!!][j!!] != '-') {
            print("Invalid move, Enter again: ")
            choose = readln().toInt()
            i = m[choose]?.first
            j = m[choose]?.second
        }

        // Mark the move
        board.array[i][j] = 'O'

        if (over(board)) return

        for (ij in 0..<game[board.index].size) {
            // For each possible state, check if the state is identical to the current state of the game (after the player’s move).
            // If it finds an identical state, update the current board to that state.
            if (compare(board, store[mapping[game[board.index][ij]]!!])) {
                board = store[mapping[game[board.index][ij]]!!]

                var maxScore = -1
                var temp = Board()

                for (jk in 0..<game[board.index].size) {
                    // The machine then chooses the next state that has the highest score
                    if (store[mapping[game[board.index][jk]]!!].score > maxScore) {
                        maxScore = store[mapping[game[board.index][jk]]!!].score
                        temp = store[mapping[game[board.index][jk]]!!]
                    }
                }
                // updating the current game board to the board state that has the highest score
                board = temp
                // exit the loop once the optimal board state has been found
                break
            }
        }
    }
}
