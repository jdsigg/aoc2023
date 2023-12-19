import java.io.File
import kotlin.math.floor

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

        private fun place(
            graph: MutableMap<String, MutableMap<String, Int>>,
            curr: String,
            next: String,
            index: Int
        ): Int {
            val children: MutableMap<String, Int>? = graph[curr]
            if (children == null) {
                // We've never moved from curr to anything before.
                graph[curr] = mutableMapOf(next to index)
                return -1
            }

            // Make sure we aren't going back to where we've been.
            return if (children[next] != null) {
                // This is a cycle.
                children[next]!!
            } else {
                children[next] = index
                -1
            }
        }

        private fun getLatestCycleStart(cycleStart: Int, cycleEnd: Int): Int {
            val cycleSize = cycleEnd - cycleStart
            val spaceLeft = 1_000_000_000 - cycleEnd
            // How many cycles can we do in spaceLeft without crossing over 1_000_000_000?
            val numCycles = floor(spaceLeft.toDouble() / cycleSize).toInt()
            return cycleEnd + numCycles * cycleSize
        }

        fun part2(dish: Array<Array<String>>): Int {
            val newDish = copy(dish)
            val graph: MutableMap<String, MutableMap<String, Int>> = mutableMapOf()
            var curr = hash(newDish)
            var next: String; var p: Int; var i = 0
            while (i < 1_000_000_000) {
                tiltNorth(newDish)
                next = "N${hash(newDish)}"
                p = place(graph, curr, next, i)
                if (p != -1) {
                    graph.clear()
                    i = getLatestCycleStart(p, i)
                }
                curr = next

                tiltWest(newDish)
                next = "W${hash(newDish)}"
                p = place(graph, curr, next, i)
                if (p != -1) {
                    graph.clear()
                    i = getLatestCycleStart(p, i)
                }
                curr = next

                tiltSouth(newDish)
                next = "S${hash(newDish)}"
                p = place(graph, curr, next, i)
                if (p != -1) {
                    graph.clear()
                    i = getLatestCycleStart(p, i)
                }
                curr = next

                tiltEast(newDish)
                next = "E${hash(newDish)}"
                p = place(graph, curr, next, i)
                if (p != -1) {
                    graph.clear()
                    i = getLatestCycleStart(p, i)
                }
                curr = next
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