import java.io.File

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
         * A B C means [B, B + C - 1] maps to [A, A + C - 1]
         *
         * Therefore:
         * - Split the string by new lines
         * - Remove
         */
        private fun parseRange(map: String): List<Pair<Range, Range>> {
            val ranges = map.split("\r\n")
            return (1..<ranges.size).map { i ->
                val (dest, source, range) = ranges[i].split(" ").map { it.toLong() }
                Pair(
                    Range(source, source + range - 1),
                    Range(dest, dest + range - 1)
                )
            }
        }

        private fun parseSeeds(seeds: String): List<Long> {
            // "seeds: s1 s2 s3 ... sN
            return seeds.substring(7).split(" ").map { it.toLong() }
        }

        fun part1(fContents: String): Long {
            val spContents = fContents.split("\r\n\r\n")
            val seeds = parseSeeds(spContents[0])
            val maps = (1..<spContents.size).map { parseRange(spContents[it]) }
            return seeds.minOfOrNull { seed ->
                var translation = seed
                maps.forEach { map ->
                    val match = map.filter { (it.first.start..it.first.end).contains(translation) }
                    if (match.isNotEmpty()) {
                        // Assume there can only be one match.
                        val rangePair = match.first
                        translation = (translation - rangePair.first.start) + rangePair.second.start
                    }
                }
                translation
            }!!
        }

        private fun makeSeedRanges(seeds: List<Long>): List<Range> {
            // A, B, C, D becomes [A, A + B - 1], [C, C + D - 1], etc.
            return (seeds.indices step 2).map { Range(seeds[it], seeds[it] + seeds[it + 1] - 1) }
        }

        /**
         * Calculate the overlap of two ranges.
         *
         * For two ranges, [A, B] and [C, D], there are 6 possible combinations.
         * - If A is less than C
         *   - B < C
         *   - C <= B <= D
         *   - D < B
         * - If C <= A <= D
         *   - B <= D
         *   - D < B
         * - If D < A
         *   - D < B
         */
        private fun findOverlap(r1: Range, r2: Range): List<List<Long>> {
            var left: List<Long> = listOf()
            var middle: List<Long> = listOf()
            var right: List<Long> = listOf()

            if (r1.start < r2.start) {
                if (r1.end < r2.start) {
                    left = listOf(r1.start, r1.end)
                } else if ((r2.start..r2.end).contains(r1.end)) {
                    left = listOf(r1.start, r2.start - 1)
                    middle = listOf(r2.start, r1.end)
                } else {
                    left = listOf(r1.start, r2.start - 1)
                    middle = listOf(r2.start, r2.end)
                    right = listOf(r2.end + 1, r1.end)
                }
            } else if ((r2.start..r2.end).contains(r1.start)) {
                if (r1.end <= r2.end) {
                    middle = listOf(r1.start, r1.end)
                } else {
                    middle = listOf(r1.start, r2.end)
                    right = listOf(r2.end + 1, r1.end)
                }
            } else {
                right = listOf(r1.start, r1.end)
            }

            return listOf(left, middle, right)
        }

        fun part2(fContents: String): Long {
            val spContents = fContents.split("\r\n\r\n")
            val seedRanges = makeSeedRanges(parseSeeds(spContents[0]))
            val maps = (1..<spContents.size).map { parseRange(spContents[it]).sortedBy { r -> r.first.start } }

            // A list of all ranges after they've been translated.
            val finalRanges: MutableList<Range> = mutableListOf()
            // A dynamic list of ranges that need translated at each step.
            val toTranslate: MutableList<Range> = mutableListOf()
            for (seedRange in seedRanges) {
                // At the start, a range of seeds needs translated.
                // However, at the next level, it is possible that a number of soils will need translated.
                toTranslate.add(seedRange)
                for (map in maps) {
                    // For a given map, translate every item that needs translated.
                    val next: MutableList<Range> = mutableListOf()
                    for (r in toTranslate) {
                        var curr = r
                        // Each mapping is sorted in a given map by the start value.
                        for (mapping in map) {
                            val (left, middle, right) = findOverlap(curr, mapping.first)
                            if (left.isNotEmpty()) {
                                // Left ranges just carry over to the next map.
                                next.add(Range(left.first, left.last))
                            }
                            if (middle.isNotEmpty()) {
                                // Middle ranges get translated and carry over to the next map.
                                next.add(
                                    Range(
                                        middle.first - mapping.first.start + mapping.second.start,
                                        middle.last - mapping.first.start + mapping.second.start
                                    )
                                )
                            }
                            if (right.isNotEmpty()) {
                                // Right ranges need translated again.
                                curr = Range(right.first, right.last)
                            } else {
                                // Move on.
                                break
                            }
                        }
                    }
                    // By the end of this, we know what we need to feed into the next map.
                    toTranslate.clear()
                    toTranslate.addAll(next)
                }
                // toTranslate represents the translations done at the final level.
                finalRanges.addAll(toTranslate)
                toTranslate.clear()
            }

            // We have a collection of location ranges. We want the lowest one.
            return finalRanges.sortedBy { r -> r.start }[0].start;
        }
    }
}

fun main() {
    val fContents: String = File("src/inputs/day5.txt").readText()
    println("Part 1: ${Day5.part1(fContents)}")
    println("Part 2: ${Day5.part2(fContents)}")
}