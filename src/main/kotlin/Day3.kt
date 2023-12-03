import java.io.File

class Day3() {
    companion object {
        private val NUM_REGEX = Regex("[0-9]+")

        // Given a range of (y) points and a row (x), find all points around the range.
        private fun expand(range: IntRange, rowIndex: Int): List<Pair<Int, Int>> {
            return (range.first - 1..range.last + 1).flatMap { y ->
                (rowIndex - 1..rowIndex + 1).map { x -> Pair(x, y) }
            }.filter { (x, y) -> !range.contains(y) || x != rowIndex }
        }

        // Remove all points from a list that cannot be on the schematic.
        private fun valid(points: List<Pair<Int, Int>>, width: Int, height: Int): List<Pair<Int, Int>> {
            return points.filter { point -> point.first in 0..<height && point.second in 0..<width }
        }

        fun part1(schematic: Array<String>): Int {
            // Sum a list of all integers in the schematic by
            return schematic.flatMapIndexed { rowIndex, line ->
                // changing each integer into itself or 0 based on
                NUM_REGEX.findAll(line).map { matchResult ->
                    // any non-integer / non-period points around a given integer.
                    val anyMatch = valid(
                        expand(matchResult.range, rowIndex),
                        schematic[0].length,
                        schematic.size
                    ).map { (x, y) -> schematic[x][y] }
                        .any { c -> c.digitToIntOrNull() != null || c != '.' }
                    if (anyMatch) matchResult.value.toInt() else 0
                }
            }.reduce { a, b -> a + b }
        }

        fun part2(schematic: Array<String>): Int {
            // Place every point each number can reach into a map. Map point to number (N:1 relationship)
            val pointsToNums: MutableMap<Pair<Int, Int>, MutableList<Int>> = mutableMapOf()
            schematic.forEachIndexed { rowIndex, line ->
                NUM_REGEX.findAll(line).forEach { matchResult ->
                    val gearRatio = matchResult.value.toInt()
                    valid(expand(matchResult.range, rowIndex), schematic[0].length, schematic.size).forEach { point ->
                        if (pointsToNums.contains(point)) {
                            pointsToNums[point]!!.add(gearRatio)
                        } else {
                            pointsToNums[point] = mutableListOf(gearRatio)
                        }
                    }
                }
            }

            // Look up every gear index in the map, keeping those who have exactly 2 numbers.
            val gearRegex = Regex("\\*")
            return schematic.flatMapIndexed { rowIndex, line ->
                gearRegex.findAll(line).map { matchResult ->
                    // Since matches will only ever be 1 character,  match result range is always X..X.
                    pointsToNums.getOrDefault(Pair(rowIndex, matchResult.range.first), mutableListOf())
                }.toList()
            }.filter { list -> list.size == 2 }.map { list -> list.first * list.last }.reduce { a, b -> a + b }
        }
    }
}

fun main() {
    val schematic: Array<String> =
        File("src/inputs/day3.txt").readLines().toTypedArray()
    println("Part 1: ${Day3.part1(schematic)}")
    println("Part 2: ${Day3.part2(schematic)}")
}