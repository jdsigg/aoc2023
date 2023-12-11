import java.io.File

class Day10 {
    companion object {

        private fun getNextPoints(point: Pair<Int, Int>, grid: List<List<String>>): List<Pair<Int, Int>> {
            val gridPiece = grid[point.first][point.second]
            if (gridPiece == "|") return listOf(
                Pair(point.first - 1, point.second), Pair(point.first + 1, point.second)
            )
            if (gridPiece == "-") return listOf(
                Pair(point.first, point.second - 1), Pair(point.first, point.second + 1)
            )
            if (gridPiece == "L") return listOf(
                Pair(point.first - 1, point.second), Pair(point.first, point.second + 1)
            )
            if (gridPiece == "J") return listOf(
                Pair(point.first - 1, point.second), Pair(point.first, point.second - 1)
            )
            if (gridPiece == "7") return listOf(
                Pair(point.first + 1, point.second), Pair(point.first, point.second - 1)
            )

            // We are only checking loops, so "F" is the only other option.
            return listOf(Pair(point.first + 1, point.second), Pair(point.first, point.second + 1))
        }

        /**
         * My starting position's surroundings look like:
         *    F 7 F
         *    | S L
         *    | | |
         *
         * The western piece, |, connects north to south.
         * The northern piece, 7, connects south to west.
         * The southern piece, |, connects north to south.
         * The western piece, L, connects north to east.
         *
         * Since 7 (northern piece) and | (southern piece) connect into S, we can assume that
         * S is actually |, as it must connect north to south.
         *
         * @return A pair, containing the solution to part 1 and a set of the coordinates for the pipe loop (for part 2).
         */
        fun part1(grid: List<MutableList<String>>): Pair<Int, Set<Pair<Int, Int>>> {
            var startingRow = 0
            var startingCol = 0
            grid.forEachIndexed { row, list ->
                list.forEachIndexed { col, str ->
                    if (str == "S") {
                        startingRow = row
                        startingCol = col
                    }
                }
            }
            // Specific to my solution.
            grid[startingRow][startingCol] = "|"

            // Walk the loop, counting the entire distance traveled to get back to the start.
            val seen: MutableSet<Pair<Int, Int>> = mutableSetOf(Pair(startingRow, startingCol))
            // Arbitrarily pick this location (dependent on "|" being our start).
            var next = Pair(startingRow - 1, startingCol)
            // We are starting on the first piece of the loop.
            var distance = 1
            while (true) {
                distance++
                // Never come back to the point we are at.
                seen.add(next)
                val nextPoints = getNextPoints(next, grid).filter { !seen.contains(it) }
                if (nextPoints.isEmpty()) {
                    break
                }
                // There should only ever be one place to go next.
                next = nextPoints.first()
            }

            return if (distance % 2 == 0) {
                Pair(distance / 2, seen)
            } else {
                Pair((distance - 1) / 2, seen)
            }
        }

        /**
         * Take a pipe and place it in a larger grid as a 3x3 equivalent.
         */
        private fun enlargePipeInGrid(point: Pair<Int, Int>, newGrid: Array<CharArray>, oldGrid: List<List<String>>) {
            val (row, col) = point
            val newRow = 3 * row
            val newCol = 3 * col
            val pipe = oldGrid[row][col]
            // Iterate over the entire grid, changing each piece into a 3x3 version of itself.
            // | becomes:
            // . x .
            // . x .
            // . x .
            if (pipe == "|") {
                newGrid[newRow][newCol] = '.'
                newGrid[newRow][newCol + 1] = 'x'
                newGrid[newRow][newCol + 2] = '.'
                newGrid[newRow + 1][newCol] = '.'
                newGrid[newRow + 1][newCol + 1] = 'x'
                newGrid[newRow + 1][newCol + 2] = '.'
                newGrid[newRow + 2][newCol] = '.'
                newGrid[newRow + 2][newCol + 1] = 'x'
                newGrid[newRow + 2][newCol + 2] = '.'
            }

            // - becomes:
            // . . .
            // x x x
            // . . .
            if (pipe == "-") {
                newGrid[newRow][newCol] = '.'
                newGrid[newRow][newCol + 1] = '.'
                newGrid[newRow][newCol + 2] = '.'
                newGrid[newRow + 1][newCol] = 'x'
                newGrid[newRow + 1][newCol + 1] = 'x'
                newGrid[newRow + 1][newCol + 2] = 'x'
                newGrid[newRow + 2][newCol] = '.'
                newGrid[newRow + 2][newCol + 1] = '.'
                newGrid[newRow + 2][newCol + 2] = '.'
            }

            // L becomes:
            // . x .
            // . x x
            // . . .
            if (pipe == "L") {
                newGrid[newRow][newCol] = '.'
                newGrid[newRow][newCol + 1] = 'x'
                newGrid[newRow][newCol + 2] = '.'
                newGrid[newRow + 1][newCol] = '.'
                newGrid[newRow + 1][newCol + 1] = 'x'
                newGrid[newRow + 1][newCol + 2] = 'x'
                newGrid[newRow + 2][newCol] = '.'
                newGrid[newRow + 2][newCol + 1] = '.'
                newGrid[newRow + 2][newCol + 2] = '.'
            }

            // J becomes:
            // . x .
            // x x .
            // . . .
            if (pipe == "J") {
                newGrid[newRow][newCol] = '.'
                newGrid[newRow][newCol + 1] = 'x'
                newGrid[newRow][newCol + 2] = '.'
                newGrid[newRow + 1][newCol] = 'x'
                newGrid[newRow + 1][newCol + 1] = 'x'
                newGrid[newRow + 1][newCol + 2] = '.'
                newGrid[newRow + 2][newCol] = '.'
                newGrid[newRow + 2][newCol + 1] = '.'
                newGrid[newRow + 2][newCol + 2] = '.'
            }

            // 7 becomes:
            // . . .
            // x x .
            // . x .
            if (pipe == "7") {
                newGrid[newRow][newCol] = '.'
                newGrid[newRow][newCol + 1] = '.'
                newGrid[newRow][newCol + 2] = '.'
                newGrid[newRow + 1][newCol] = 'x'
                newGrid[newRow + 1][newCol + 1] = 'x'
                newGrid[newRow + 1][newCol + 2] = '.'
                newGrid[newRow + 2][newCol] = '.'
                newGrid[newRow + 2][newCol + 1] = 'x'
                newGrid[newRow + 2][newCol + 2] = '.'
            }

            // F becomes:
            // . . .
            // . x x
            // . x .
            if (pipe == "F") {
                newGrid[newRow][newCol] = '.'
                newGrid[newRow][newCol + 1] = '.'
                newGrid[newRow][newCol + 2] = '.'
                newGrid[newRow + 1][newCol] = '.'
                newGrid[newRow + 1][newCol + 1] = 'x'
                newGrid[newRow + 1][newCol + 2] = 'x'
                newGrid[newRow + 2][newCol] = '.'
                newGrid[newRow + 2][newCol + 1] = 'x'
                newGrid[newRow + 2][newCol + 2] = '.'
            }
        }

        /**
         * Returns if a point can be within the provided height and width bounds.
         */
        private fun isValid(point: Pair<Int, Int>, height: Int, width: Int): Boolean {
            return (0..<height).contains(point.first) && (0..<width).contains(point.second)
        }

        /**
         * For a given point, obtain the points above, below, to the right, and to the left of it.
         */
        private fun getSurroundings(point: Pair<Int, Int>, height: Int, width: Int): List<Pair<Int, Int>> {
            return listOf(
                Pair(point.first - 1, point.second),
                Pair(point.first + 1, point.second),
                Pair(point.first, point.second - 1),
                Pair(point.first, point.second + 1),
            ).filter { isValid(it, height, width) }
        }

        /**
         * This example puzzled me:
         * ..........
         * .S------7.
         * .|F----7|.
         * .||OOOO||.
         * .||OOOO||.
         * .|L-7F-J|.
         * .|II||II|.
         * .L--JL--J.
         * ..........
         *
         * It claims that each "O" is not constrained by the maze. Logically, it makes sense, but visually it does not.
         *
         * Instead, if we "zoom in" such that each pipe now consumes a 3x3 space, then, the aforementioned buffer
         * becomes visible.
         *
         * This creates a lot of wasted space around the outside of the pipe loop, but now, we should be able to BFS
         * from the outside of the loop to eliminate all points that are not contained by the pipe loop.
         *
         * After this is complete, any remaining 3x3 that was not touched by the BFS is contained by the pipe loop.
         */
        fun part2(grid: List<List<String>>, pipes: Set<Pair<Int, Int>>): Int {
            // Create an entirely empty grid of ground.
            val newGrid: Array<CharArray> = Array(grid.size * 3) { CharArray(grid[0].size * 3) { '.' } }
            // Fill all pipes into the new grid.
            for (point in pipes) {
                enlargePipeInGrid(point, newGrid, grid)
            }
            // BFS fill from the top left.
            val queue: MutableList<Pair<Int, Int>> = mutableListOf(Pair(0, 0))
            val seen: MutableSet<Pair<Int, Int>> = mutableSetOf()
            while (queue.isNotEmpty()) {
                val next = queue.removeFirst()
                if (seen.contains(next) || newGrid[next.first][next.second] == 'x') {
                    continue
                }
                seen.add(next)
                newGrid[next.first][next.second] = '*'
                queue.addAll(getSurroundings(next, newGrid.size, newGrid[0].size))
            }

            var totalContained = 0
            // Look by each index in grid, not newGrid.
            // This way, we avoid double-counting, for example:
            // . . . .
            // . . . .
            // . . . .
            // Would otherwise be two points inside the pipe loop.
            grid.indices.forEach { i ->
                grid[0].indices.forEach { j ->
                    val isContained = (0..2).flatMap { iR -> (0..2).map { jC -> Pair(3 * i + iR, 3 * j + jC) } }
                        .all { newGrid[it.first][it.second] == '.' }
                    if (isContained) totalContained++
                }
            }
            return totalContained
        }
    }
}

fun main() {
    val grid: List<MutableList<String>> = File("src/inputs/day10.txt").readLines().map { it.chunked(1).toMutableList() }
    val (part1, pipeLoop) = Day10.part1(grid)
    println("Part 1: $part1")
    println("Part 2: ${Day10.part2(grid, pipeLoop)}")
}