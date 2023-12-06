import java.io.File

// These are needed for extensive destructuring.
operator fun <T> List<T>.component6(): T = get(5)
operator fun <T> List<T>.component7(): T = get(6)
operator fun <T> List<T>.component8(): T = get(7)

class Day5() {
    private data class Range(val start: Long, val end: Long)
    companion object {

        /**
         * Parse ranges from a map string.
         *
         * Each range looks something like:
         * "x-to-y map:\r\n
         * A B C\r\n
         * D E F\r\n
         * ...\r\n
         * X Y Z
         *
         * Split the string, remove the first index, and parse each line as a Range object.
         */
        private fun parseRange(map: String): List<Range> {
            val ranges = map.split("\r\n")
            return (1..<ranges.size).map { i ->
                val (dest, source, range) = ranges[i].split(" ").map { it.toLong() }
                Range(source + range - 1L, dest + range - 1L)
            }
        }

        private fun parseSeeds(seeds: String): List<Long> {
            // "seeds: s1 s2 s3 ... sN
            return seeds.substring(7).split(" ").map { it.toLong() }
        }

        fun part1(fContents: String): Int {
            val spContents = fContents.split("\r\n\r\n")
            val seeds = parseSeeds(spContents[0])
            val maps = (1..<spContents.size).map { parseRange(spContents[it]) }
            return seeds.map { seed ->
                var translation = seed
                maps.forEach{ map ->
                    val match = map.any {  }
                }
                // Grab a map
                // Try to find a range we fit in
                // If there's no range, translate the value to itself
            }.min { it }
        }

        fun part2() {

        }
    }
}

fun main() {
    val fContents: String = File("src/inputs/day5.txt").readText()
    println("Part 1: ${Day5.part1(fContents)}")
//    println("Part 2: ${Day5.part2(lines)}")
}