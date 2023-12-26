import java.io.File

class Day16 {
    private data class Point(var row: Int, var col: Int, var direction: Char) {
        fun moveThrough(space: Char): List<Point> {
            when (space) {
                '.', '#' -> {
                    return listOf(this)
                }

                '/' -> {
                    when (this.direction) {
                        'R' -> {
                            this.direction = 'U'
                        }

                        'D' -> {
                            this.direction = 'L'
                        }

                        'L' -> {
                            this.direction = 'D'
                        }

                        'U' -> {
                            this.direction = 'R'
                        }
                    }
                    return listOf(this)
                }

                '\\' -> {
                    when (this.direction) {
                        'R' -> {
                            this.direction = 'D'
                        }

                        'D' -> {
                            this.direction = 'R'
                        }

                        'L' -> {
                            this.direction = 'U'
                        }

                        'U' -> {
                            this.direction = 'L'
                        }
                    }
                    return listOf(this)
                }

                '|' -> {
                    when (this.direction) {
                        'U', 'D' -> {
                            return listOf(this)
                        }

                        'R', 'L' -> {
                            return listOf(
                                this.copy(direction = 'U'), this.copy(direction = 'D')
                            )
                        }
                    }
                }

                '-' -> {
                    when (this.direction) {
                        'R', 'L' -> {
                            return listOf(this)
                        }

                        'U', 'D' -> {
                            return listOf(
                                this.copy(direction = 'R'), this.copy(direction = 'L')
                            )
                        }
                    }
                }
            }
            // Should never hit this.
            return listOf()
        }

        fun nudge(): Point {
            when (this.direction) {
                'R' -> {
                    this.col += 1
                }

                'D' -> {
                    this.row += 1
                }

                'L' -> {
                    this.col -= 1
                }

                'U' -> {
                    this.row -= 1
                }
            }
            return this
        }
    }

    companion object {
        private fun solve(grid: Array<CharArray>, start: Point): Int {
            // Copy.
            val nGrid = grid.map { it.clone() }.toTypedArray()
            // Queue.
            val moves: MutableList<Point> = mutableListOf(start)
            // Points we've passed through and their direction.
            val energized: MutableSet<Point> = mutableSetOf()
            while (moves.isNotEmpty()) {
                // Grab a point and move it.
                val currPoint = moves.removeFirst().nudge()
                val (row, col, _) = currPoint
                // Skip if we are outside the grid.
                if (!grid.indices.contains(row) || !grid[0].indices.contains(col)) {
                    continue
                }
                // Skip if we've already moved through this point in this direction.
                if (energized.contains(currPoint)) {
                    continue
                }
                energized.add(currPoint)
                // Only change the copy, as overwriting a mirror / splitter could be bad.
                nGrid[row][col] = '#'
                val space = grid[row][col]
                moves.addAll(currPoint.moveThrough(space))
            }
            // nGrid is all energized points.
            return nGrid.map { it -> it.count { it == '#' } }.reduce { a, b -> a + b }
        }

        fun part1(grid: Array<CharArray>): Int {
            // Come into the grid from the top left moving right.
            return solve(grid, Point(0, -1, 'R'))
        }

        fun part2(grid: Array<CharArray>): Int {
            var max = 0
            // Energize the grid from all directions one at a time.
            grid.indices.forEach { row -> max = maxOf(max, solve(grid, Point(row, -1, 'R'))) }
            grid.indices.forEach { row -> max = maxOf(max, solve(grid, Point(row, grid[0].size, 'L'))) }
            grid[0].indices.forEach { col -> max = maxOf(max, solve(grid, Point(-1, col, 'D'))) }
            grid[0].indices.forEach { col -> max = maxOf(max, solve(grid, Point(grid.size, col, 'U'))) }
            return max
        }
    }
}

fun main() {
    val steps: Array<CharArray> = File("src/inputs/day16.txt").readLines().map { it.toCharArray() }.toTypedArray()
    println("Part 1: ${Day16.part1(steps)}")
    println("Part 2: ${Day16.part2(steps)}")
}