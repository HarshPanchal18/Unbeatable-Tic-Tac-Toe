package org.harsh

import kotlin.math.max
import kotlin.math.min

data class Board(
    val array: Array<CharArray> = Array(3) { CharArray(3) { '-' } },
    var score: Int = 0,
    var index: Int = 0,
)

// Terminal state check
fun check(board: Board): Char {
    for (i in 0..<3) {

        var count = 0
        for (j in 0..<3)
            if (board.array[i][j] == 'X')
                count++

        if (count == 3)
            return 'X'

        count = 0
        for (j in 0..<3)
            if (board.array[i][j] == 'O')
                count++

        if (count == 3)
            return 'O'

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

    if (board.array[0][0] == 'X' && board.array[1][1] == 'X' && board.array[2][2] == 'X')
        return 'X'

    if (board.array[0][0] == 'O' && board.array[1][1] == 'O' && board.array[2][2] == 'O')
        return 'O'

    if (board.array[0][2] == 'X' && board.array[1][1] == 'X' && board.array[2][0] == 'X')
        return 'X'

    if (board.array[0][2] == 'O' && board.array[1][1] == 'O' && board.array[2][0] == 'O')
        return 'O'

    var count = 0
    for (i in 0..<3)
        for (j in 0..<3)
            if (board.array[i][j] == '-')
                count++

    return if (count == 0) 'D'
    else '-'

}

var len = 0
var win = 0
var lose = 0
var draw = 0
val store = mutableListOf<Board>()
val mapping = mutableMapOf<Int, Int>()
val game = MutableList(1000000) { mutableListOf<Int>() }

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

    var maximum = -10_000
    var minimum = 10_000

    if (turn == 'C') {
        for (i in 0..<3) {
            for (j in 0..<3) {
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
                    build(next, 'P', len - 1, depth + 1)
                    maximum = max(maximum, store[mapping[x]!!].score)
                }
            }
        }
        store[mapping[index]!!].score = maximum - depth
    }

    if (turn == 'P') {
        for (i in 0..<3) {
            for (j in 0..<3) {
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

fun compare(b1: Board, b2: Board): Boolean {
    var count = 0

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
    val m = mutableMapOf<Int, Pair<Int, Int>>()

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

    println("Position map: ")
    var position = 1
    for (i in 0..<3) {
        for (j in 0..<3)
            print("${position++} ")
        println()
    }
    println()

    while (true) {

        if (over(board))
            return

        for (i in 0..<3) {
            for (j in 0..<3)
                print("${board.array[i][j]} ")
            println()
        }
        println()

        print("Enter your move: ")
        var choose = readln().toInt()

        while (choose !in 1..9) {
            print("Invalid move, Enter again: ")
            choose = readln().toInt()
        }

        var i = m[choose]?.first
        var j = m[choose]?.second

        while (board.array[i!!][j!!] != '-') {
            print("Invalid move, Enter again: ")
            choose = readln().toInt()
            i = m[choose]?.first
            j = m[choose]?.second
        }

        board.array[i][j] = 'O'

        if (over(board)) return

        for (ij in 0..<game[board.index].size) {
            if (compare(board, store[mapping[game[board.index][ij]]!!])) {
                board = store[mapping[game[board.index][ij]]!!]

                var ma = -1
                var temp = Board()

                for (jk in 0..<game[board.index].size) {
                    if (store[mapping[game[board.index][jk]]!!].score > ma) {
                        ma = store[mapping[game[board.index][jk]]!!].score
                        temp = store[mapping[game[board.index][jk]]!!]
                    }
                }
                board = temp
                break
            }
        }
    }
}
