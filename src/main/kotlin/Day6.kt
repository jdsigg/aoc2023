import java.io.File
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

class Day6() {
    companion object {
        fun part1(records: List<List<Long>>): Int {
            return records[0].zip(records[1]).map { (time, distance) ->
                (0..time).map { it -> (time - it) * it }
                    .filter { it > distance }
            }.map { it.size }.reduce { a, b -> a * b }
        }

        fun part2(records: List<List<Long>>): Long {
            val (time, distance) = records.map { it.joinToString("").toLong() }
            // Given max race time: T
            // We can have a race for each R where 0 <= R <= T
            // Each race produces a distance, d
            //   d = (T- R) * R = or TR - R^2
            // We are curious of each whole number value of d where d >= record distance
            // Therefore, we can:
            //   1. Solve the quadratic equation: TR - R^2 = record distance
            //   2. This produces 2 roots, X1 and X2
            //   3. Round up X1, round down X2, and our answer is (X2 - X1) + 1

            // To solve a quadratic: ax^2 + bx + c = 0, we use quadratic formula
            // X1 = (-b + sqrt(b^2 - 4ac)) / 2a
            // X2 = (-b - sqrt(b^2 - 4ac)) / 2a

            // Our quadratic is -R^2 + TR - record distance = 0
            val x1 = (-time + sqrt(time*time - 4*(-1.0)*(-distance))) / (2*-1)
            val x2 = (-time - sqrt(time*time - 4*(-1.0)*(-distance))) / (2*-1)
            return (floor(x2) - ceil(x1) + 1).toLong()
        }
    }
}

fun main() {
    val records: List<List<Long>> = File("src/inputs/day6.txt").readLines().map { line ->
        val re = Regex("[0-9]+")
        re.findAll(line).map { it.value.toLong() }.toList()
    }
    println("Part 1: ${Day6.part1(records)}")
    println("Part 2: ${Day6.part2(records)}")
}