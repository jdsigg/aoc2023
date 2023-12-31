import java.io.File

class Day21 {
    companion object {
        private fun findStart(garden: List<String>): Pair<Int, Int> {
            for (i in garden.indices) {
                for (j in garden[i].indices) {
                    if (garden[i][j] == 'S') {
                        return Pair(i, j)
                    }
                }
            }
            // This should never happen.
            return Pair(-1, -1)
        }

        /**
         * Returns if a point can be within the provided height and width bounds.
         */
        private fun isValid(point: Pair<Int, Int>, height: Int, width: Int): Boolean {
            return (0..<height).contains(point.first) && (0..<width).contains(point.second)
        }

        fun part1(garden: List<String>): Int {
            var currentSteps: Set<Pair<Int, Int>> = setOf(findStart(garden))
            (1..64).forEach { _ ->
                currentSteps = currentSteps.flatMap { (i, j) ->
                    listOf(
                        Pair(i + 1, j), Pair(i - 1, j), Pair(i, j + 1), Pair(i, j - 1)
                    )
                        .filter { isValid(it, garden.size, garden[0].length) }
                        .filter { (i, j) -> garden[i][j] == 'S' || garden[i][j] == '.' }
                }.toSet()
            }
            return currentSteps.size
        }
    }
}

fun main() {
    val garden: List<String> = File("src/inputs/day21.txt").readLines()
    println("Part 1: ${Day21.part1(garden)}")
}