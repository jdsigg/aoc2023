import java.io.File

class Day11 {
    companion object {
        /**
         * Returns each index in space that is a galaxy ('#').
         *
         * @param space The map of space, consisting of galaxies ('#') and empty space ('.').
         */
        private fun getGalaxies(space: Array<CharArray>): List<Pair<Int, Int>> {
            return space.flatMapIndexed { i, row -> row.mapIndexed { j, _ -> Pair(i, j) } }
                .filter { (row, col) -> space[row][col] == '#' }
        }

        /**
         * Returns the row indices of each row that contain no galaxies ('#').
         */
        private fun getEmptyRows(space: Array<CharArray>): Set<Int> {
            return space.mapIndexed { i, row -> if (row.all { it != '#' }) i else 0 }.filter { it > 0 }.toHashSet()
        }

        /**
         * Returns the column indices of each column that contain no galaxies ('#').
         */
        private fun getEmptyCols(space: Array<CharArray>): Set<Int> {
            // Matrix transposition allows us to reuse getEmptyRows.
            // Only works for truly rectangular matrices.
            return getEmptyRows(space[0].mapIndexed { j, _ -> space.mapIndexed { _, row -> row[j] }.toCharArray() }
                .toTypedArray())
        }

        /**
         * Returns the distance between two values.
         *
         * If the distance crosses over empty space, the distance increases by "gapSize" instead of 1.
         *
         * @param p1 The first value.
         * @param p2 The second value.
         * @param empties The values that are empty space.
         * @param gapSize The size of empty space.
         */
        private fun getDistance(p1: Int, p2: Int, empties: Set<Int>, gapSize: Long): Long {
            return if (p1 == p2) {
                0
            } else {
                (minOf(p1, p2) + 1..maxOf(p1, p2)).map { x -> if (empties.contains(x)) gapSize else 1 }
                    .reduce { a, b -> a + b }
            }
        }

        /**
         * Returns the sum of the minimum distance between each galaxy pair.
         *
         * We are only concerned with pair combinations (order does not matter).
         *
         * Space is not continuous, therefore, minimum distance is the right-angle (Manhattan) distance
         * between two points.
         *
         * @param space The map of empty space and galaxies.
         * @param gapSize The true size of space for an empty row / column.
         */
        fun solve(space: Array<CharArray>, gapSize: Long): Long {
            val galaxies: List<Pair<Int, Int>> = getGalaxies(space)
            val emptyRows: Set<Int> = getEmptyRows(space)
            val emptyCols: Set<Int> = getEmptyCols(space)

            return galaxies.indices.flatMap { i ->
                (i + 1..<galaxies.size).map { j ->
                    val (x1, y1) = galaxies[i]
                    val (x2, y2) = galaxies[j]
                    getDistance(x1, x2, emptyRows, gapSize) + getDistance(y1, y2, emptyCols, gapSize)
                }
            }.reduce { a, b -> a + b }
        }
    }
}

fun main() {
    val space: Array<CharArray> = File("src/inputs/day11.txt").readLines().map { it.toCharArray() }.toTypedArray()
    println("Part 1: ${Day11.solve(space, 2L)}")
    println("Part 2: ${Day11.solve(space, 1000000L)}")
}
