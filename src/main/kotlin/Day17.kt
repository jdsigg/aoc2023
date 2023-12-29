import java.io.File
import java.util.PriorityQueue

class Day17 {
    companion object {
        private enum class Direction(val index: Int) {
            DIRECTION_UNKNOWN(-1), UP(0), RIGHT(1), DOWN(2), LEFT(3)
        }

        private val DIRECTIONS: Array<Direction> =
            arrayOf(Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT)

        private data class Crucible(
            var position: Pair<Int, Int>, val direction: Direction, val streak: Int, var weight: Int
        ) : Comparable<Crucible> {
            /**
             * Returns a comparison between two Crucibles.
             *
             * Behind the scenes, it is used for min-heap placement.
             */
            override fun compareTo(other: Crucible): Int {
                return if (this.weight < other.weight) {
                    -1
                } else if (this.weight > other.weight) {
                    1
                } else {
                    0
                }
            }

            /**
             * Create a turned copy of this Crucible.
             *
             * Only turns right and left 90 degrees.
             */
            fun turn(direction: Direction = Direction.DIRECTION_UNKNOWN): Crucible {
                var newDirection = this.direction
                when (direction) {
                    Direction.RIGHT -> {
                        newDirection = DIRECTIONS[(this.direction.index + 1) % DIRECTIONS.size]
                    }

                    Direction.LEFT -> {
                        newDirection = DIRECTIONS[(this.direction.index + 3) % DIRECTIONS.size]
                    }

                    else -> {
                        // Do nothing.
                    }
                }

                return this.copy(
                    direction = newDirection,
                    streak = if (this.direction == newDirection) this.streak + 1 else 1,
                )
            }

            /**
             * Move this Crucible forward in the direction it faces.
             */
            fun nudge() {
                var (newX, newY) = this.position
                when (this.direction) {
                    Direction.UP -> {
                        newX -= 1
                    }

                    Direction.RIGHT -> {
                        newY += 1
                    }

                    Direction.DOWN -> {
                        newX += 1
                    }

                    Direction.LEFT -> {
                        newY -= 1
                    }

                    else -> {
                        // Do nothing.
                    }
                }
                this.position = Pair(newX, newY)
            }
        }

        /**
         * Returns if a point can be within the provided height and width bounds.
         */
        private fun isValid(point: Pair<Int, Int>, height: Int, width: Int): Boolean {
            return (0..<height).contains(point.first) && (0..<width).contains(point.second)
        }

        fun solve(city: List<List<Int>>, minStreak: Int, maxStreak: Int): Int {
            // Where we want to end up.
            val goal: Pair<Int, Int> = Pair(city.size - 1, city[0].size - 1)
            // All seen <position, direction, streak> groupings.
            val seen: MutableSet<Triple<Pair<Int, Int>, Direction, Int>> = mutableSetOf()
            // Min-heap (see Crucible::compareTo).
            val pQueue = PriorityQueue<Crucible>()
            // Start moving right and down from the top-left corner.
            pQueue.addAll(listOf(Direction.RIGHT, Direction.DOWN).map { Crucible(Pair(0, 0), it, 1, 0) })

            while (pQueue.isNotEmpty()) {
                val curr: Crucible = pQueue.poll()!!
                // Move the Crucible forward in its current direction.
                curr.nudge()

                // Stop if the Crucible is not in the grid.
                if (!isValid(curr.position, city.size, city[0].size)) {
                    continue
                }

                // The smallest weight for a <p, d, s> triple appears the first time
                // we visit <p, d, s>. This is because of the min-heap.
                val sCurr = Triple(curr.position, curr.direction, curr.streak)
                if (seen.contains(sCurr)) {
                    continue
                }

                // Enter the city block and increment the weight.
                curr.weight += city[curr.position.first][curr.position.second]

                // Bail if we reached our goal.
                if (curr.position == goal && curr.streak >= minStreak) {
                    return curr.weight
                }

                // Never come back to <p, d, s>.
                seen.add(sCurr)

                // Only turn if we have a large enough streak.
                if (curr.streak >= minStreak) {
                    listOf(
                        curr.turn(Direction.RIGHT), curr.turn(Direction.LEFT)
                    ).forEach(pQueue::add)
                }

                // Continue straight if we have a small enough streak.
                val straight = curr.turn()
                if (straight.streak <= maxStreak) {
                    pQueue.add(straight)
                }
            }

            // This means no shortest path exists with the given constraints.
            return -1
        }
    }
}

fun main() {
    val city: List<List<Int>> = File("src/inputs/day17.txt").readLines().map { it.map { c -> c.digitToInt() } }
    println("Part 1: ${Day17.solve(city, 0, 3)}")
    println("Part 2: ${Day17.solve(city, 4, 10)}")
}