import java.io.File
import kotlin.math.abs

class Day18 {
    companion object {
        fun solve(instructions: List<Pair<String, Long>>): Long {
            // There are two useful formulas we will leverage here.

            // The first is the Shoelace formula: https://en.wikipedia.org/wiki/Shoelace_formula
            // This calculates the area of a "simple" polygon.

            // The formula requires a list of coordinates as input, which we can generate from the provided
            // instruction list.
            var curr: Pair<Long, Long> = Pair(0L, 0L)
            val points: MutableList<Pair<Long, Long>> = mutableListOf(curr)
            var totalPoints = 0L
            for ((direction, length) in instructions) {
                totalPoints += length
                var (newX, newY) = curr
                when (direction) {
                    "R" -> {
                        newX += length
                    }

                    "D" -> {
                        newY -= length
                    }

                    "L" -> {
                        newX -= length
                    }

                    "U" -> {
                        newY += length
                    }
                }
                curr = Pair(newX, newY)
                points.add(curr)
            }
            // To note: The shoelace formula requires "wrapping around" back to the start of the polygon.
            // To do this, we'd add (0, 0) to the end of our points list. However, don't do so here as the input already
            // takes care of that for us.

            // Once we have all the points, the formula is:
            // area = abs((x1y2 - y1x2) + (x2y3 - y2x3) + ... + (xNy1 - yNx1)) / 2
            var area: Long = 0
            (0..<points.size - 1).forEach { i ->
                val (x, y) = points[i]
                val (xNext, yNext) = points[i + 1]
                area += (x * yNext - y * xNext)
            }
            area = abs(area) / 2

            // The second is Pick's theorem: https://en.wikipedia.org/wiki/Pick%27s_theorem
            // This calculates the area of a polygon given the number of interior and boundary points.
            // An interior point is entirely inside a given polygon.
            // A boundary point lies on the outside of a given polygon.

            // The formula: area = numInterior + numBoundary / 2 - 1
            // We are interested in (numInterior + numBoundary)
            // We know numBoundary, as we count it when calculating the area

            // Therefore...
            return (area - 0.5 * totalPoints + 1L).toLong() + totalPoints
        }
    }
}

fun main() {
    val digPlan: List<String> = File("src/inputs/day18.txt").readLines()

    val instructions1 = digPlan.map {
        val (direction, length, _) = it.split(" ")
        Pair(direction, length.toLong())
    }
    println("Part 1: ${Day18.solve(instructions1)}")

    val instructions2 = digPlan.map {
        val (_, _, hex) = it.split(" ")
        val length = hex.substring(2, 7).toLong(16)
        val direction = listOf('R', 'D', 'L', 'U')[hex[hex.length - 2].digitToInt()].toString()
        Pair(direction, length)
    }
    println("Part 2: ${Day18.solve(instructions2)}")
}