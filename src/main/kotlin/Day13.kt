import java.io.File

class Day13 {
    companion object {

        private fun countReflections(pattern: Array<Array<String>>): List<Int> {
            val allReflections: MutableList<Int> = mutableListOf()
            // Assume the reflection line is between [row - 1, row]
            for (row in 1..<pattern.size) {
                val minDistance = minOf(row, pattern.size - row)
                var isPerfectReflection = true
                outer@ for (cRow in row..<(row + minDistance)) {
                    val cRowOpposite = row - 1
                    for (col in pattern[row].indices) {
                        if (pattern[cRow][col] != pattern[cRowOpposite - cRow + row][col]) {
                            isPerfectReflection = false
                            break@outer
                        }
                    }
                }

                if (isPerfectReflection) {
                    allReflections.add(100*row)
                }
            }

            // Assume the reflection line is between [col - 1, col]
            for (col in 1..<pattern[0].size) {
                val minDistance = minOf(col, pattern[0].size - col)
                var isPerfectReflection = true
                outer@ for (cCol in col..<(col + minDistance)) {
                    val cColOpposite = col - 1
                    for (row in pattern.indices) {
                        if (pattern[row][cCol] != pattern[row][cColOpposite - cCol + col]) {
                            isPerfectReflection = false
                            break@outer
                        }
                    }
                }

                if (isPerfectReflection) {
                    allReflections.add(col)
                }
            }

            return allReflections
        }

        fun part1(patterns: List<Array<Array<String>>>): List<Int> {
            // Assume that there can only be one reflection for part 1.
            return patterns.map { countReflections(it).first() }
        }

        fun part2(patterns: List<Array<Array<String>>>, reflections: List<Int>): Int {
            return patterns.mapIndexed { i, pattern ->
                val allReflections: MutableSet<Int> = mutableSetOf()
                // Just brute force it.
                outer@ for (row in pattern.indices) {
                    for (col in pattern[row].indices) {
                        // Swap the position and assume it is the smudge.
                        val temp = pattern[row][col]
                        pattern[row][col] = if (temp == ".") "#" else "."
                        allReflections.addAll(countReflections(pattern))
                        // Swap it back for the next run.
                        pattern[row][col] = temp
                    }
                }
                // Ignore smudge maps that repeat the original reflection.
                allReflections.remove(reflections[i])
                allReflections.first()
            }.reduce { a, b -> a + b }
        }
    }
}

fun main() {
    val patterns: List<Array<Array<String>>> = File("src/inputs/day13.txt")
        .readText()
        .split("\r\n\r\n")
        .map { it -> it.split("\r\n").map { it.chunked(1).toTypedArray() }.toTypedArray() }

    val reflections = Day13.part1(patterns)
    println("Part 1: ${reflections.reduce { a, b -> a + b }}")
    println("Part 2: ${Day13.part2(patterns, reflections)}")
}