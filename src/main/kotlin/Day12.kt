import java.io.File

class Day12 {
    companion object {
        private fun recurse(
            springs: String, groups: List<Int>, springIndex: Int, groupIndex: Int, springCount: Int
        ): Long {
            if (groupIndex == groups.size) {
                // We have no more groups to check. Success if:
                //   - There are no damaged springs left
                return if (springs.substring(springIndex).all { it != '#' }) {
                    1L
                } else {
                    0L
                }
            }

            if (springIndex == springs.length) {
                // We have no more springs to check. Success if:
                //   - We are on the last group AND
                //   - We have the right number of springs in that group
                return if (groupIndex == groups.size - 1 && groups[groupIndex] == springCount) {
                    1L
                } else {
                    0L
                }
            }

            val spring = springs[springIndex]
            val group = groups[groupIndex]
            var left = 0L
            var right = 0L

            // We are at either a '?' or a '#'
            if (spring != '.') {
                // Only continue if this spring chain makes sense.
                right = if (springCount + 1 <= group) {
                    recurse(springs, groups, springIndex + 1, groupIndex, springCount + 1)
                } else {
                    0L
                }
            }

            // We are at either a '?' or a '.'
            if (spring != '#') {
                left = when (springCount) {
                    0 -> {
                        // We came from a healthy spring.
                        recurse(springs, groups, springIndex + 1, groupIndex, 0)
                    }

                    group -> {
                        // We ended a streak of damaged springs that was the right size.
                        recurse(springs, groups, springIndex + 1, groupIndex + 1, 0)
                    }

                    else -> {
                        // We ended a streak that was not the right size.
                        0
                    }
                }
            }
            return left + right
        }

        fun part1(springs: List<List<String>>): Long {
            return springs.map { (springs, groups) ->
                recurse(
                    springs.replace(Regex("\\.+"), "."),
                    groups.split(",").map { it.toInt() },
                    0,
                    0,
                    0
                )
            }
                .reduce { a, b -> a + b }
        }
    }
}

fun main() {
    val springs: List<List<String>> = File("src/inputs/day12.txt").readLines().map { it.split(" ") }
    println("Part 1: ${Day12.part1(springs)}")
}