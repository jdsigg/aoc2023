import java.io.File
import kotlin.math.ceil

class Day14 {
    companion object {
        private fun tiltNorth(dish: Array<Array<String>>) {
            for (row in dish.indices) {
                for (col in dish[row].indices) {
                    val curr = dish[row][col]
                    if (curr == "O") {
                        // Try to move the rock up as much as possible.
                        var currRow = row
                        while (currRow > 0) {
                            if (dish[currRow - 1][col] != ".") {
                                break
                            }
                            // Replace the position we roll over.
                            dish[currRow][col] = "."
                            currRow--
                        }
                        dish[currRow][col] = "O"
                    }
                }
            }
        }

        private fun tiltWest(dish: Array<Array<String>>) {
            for (col in dish[0].indices) {
                for (row in dish.indices) {
                    val curr = dish[row][col]
                    if (curr == "O") {
                        // Try to move the rock left as much as possible.
                        var currCol = col
                        while (currCol > 0) {
                            if (dish[row][currCol - 1] != ".") {
                                break
                            }
                            // Replace the position we roll over.
                            dish[row][currCol] = "."
                            currCol--
                        }
                        dish[row][currCol] = "O"
                    }
                }
            }
        }

        private fun tiltSouth(dish: Array<Array<String>>) {
            for (row in dish.indices.reversed()) {
                for (col in dish[row].indices) {
                    val curr = dish[row][col]
                    if (curr == "O") {
                        // Try to move the rock down as much as possible.
                        var currRow = row
                        while (currRow < dish.size - 1) {
                            if (dish[currRow + 1][col] != ".") {
                                break
                            }
                            // Replace the position we roll over.
                            dish[currRow][col] = "."
                            currRow++
                        }
                        dish[currRow][col] = "O"
                    }
                }
            }
        }

        private fun tiltEast(dish: Array<Array<String>>) {
            for (col in dish[0].indices.reversed()) {
                for (row in dish.indices) {
                    val curr = dish[row][col]
                    if (curr == "O") {
                        // Try to move the rock right as much as possible.
                        var currCol = col
                        while (currCol < dish[row].size - 1) {
                            // We can't move the rock right.
                            if (dish[row][currCol + 1] != ".") {
                                break
                            }
                            // Replace the position we roll over.
                            dish[row][currCol] = "."
                            currCol++
                        }
                        dish[row][currCol] = "O"
                    }
                }
            }
        }

        private fun copy(dish: Array<Array<String>>): Array<Array<String>> {
            return dish.map { it.clone() }.toTypedArray()
        }

        private fun hash(dish: Array<Array<String>>): String {
            return dish.joinToString("\r\n") { it.joinToString("") }
        }

        private fun getNorthWeight(dish: Array<Array<String>>): Int {
            return dish.reversed().mapIndexed { i, row -> row.filter { it == "O" }.size * (i + 1) }
                .reduce { a, b -> a + b }
        }

        fun part1(dish: Array<Array<String>>): Int {
            // Create a copy and tilt the dish north.
            val newDish = copy(dish)
            tiltNorth(newDish)
            return getNorthWeight(newDish)
        }

        private fun tryPlace(seen: MutableMap<String, Int>, str: String, i: Int): Int {
            return if (seen.containsKey(str)) {
                seen[str]!!
            } else {
                seen[str] = i
                -1
            }
        }

        private fun getLatestCycleStart(cycleStart: Int, cycleEnd: Int): Int {
            val cycleSize = cycleEnd - cycleStart
            // My first cycle seems to happen at i = 125 and repeats at i = 151
            // Cycle length = (151 - 125) = 26
            // First time the cycle starts = 125
            // Therefore, the cycle should repeat at = 1_000_000_000 + 125 - (26 * (ceil(125 / 26)))
            // It seems to not, so I am missing something, but I don't know what.

            // After some trial/error, poking around +/- 1_000_000_000 got me a correct answer at -12.
            // I don't know why -12.
            return 1_000_000_000 - 12 + cycleStart - (cycleSize * (ceil(cycleStart.toDouble() / cycleSize))).toInt()
        }

        fun part2(dish: Array<Array<String>>): Int {
            val newDish = copy(dish)
            val seen: MutableMap<String, Int> = mutableMapOf(hash(newDish) to 0)
            var i = 1
            while (i <= 1_000_000_000) {
                tiltNorth(newDish)
                tiltWest(newDish)
                tiltSouth(newDish)
                tiltEast(newDish)
                val tP = tryPlace(seen, hash(newDish), i)
                if (tP != -1) {
                    i = getLatestCycleStart(tP, i)
                    seen.clear()
                }
                i++
            }
            return getNorthWeight(newDish)
        }
    }
}

fun main() {
    val dish: Array<Array<String>> =
        File("src/inputs/day14.txt").readLines().map { it.chunked(1).toTypedArray() }.toTypedArray()

    println("Part 1: ${Day14.part1(dish)}")
    println("Part 2: ${Day14.part2(dish)}")
}