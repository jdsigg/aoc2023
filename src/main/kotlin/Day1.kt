import java.io.File

class Day1() {
    companion object {
        fun part1(lines: List<String>): Int {
            return lines.map { line ->
                val cvs: List<Int> = line.split("").mapNotNull { c -> c.toIntOrNull() }
                "${cvs.first}${cvs.last}".toInt()
            }.reduce { a, b -> a + b }
        }
        fun part2(lines: List<String>): Int {
            val digits: Map<String, Int> = mapOf(
                "one" to 1,
                "two" to 2,
                "three" to 3,
                "four" to 4,
                "five" to 5,
                "six" to 6,
                "seven" to 7,
                "eight" to 8,
                "nine" to 9
            )

            return lines.map { line ->
                val cvs: MutableList<Int> = mutableListOf()
                val re = Regex("[1-9]|one|two|three|four|five|six|seven|eight|nine")
                re.findAll(line).map { it.value }
                    .forEach { match ->
                        digits[match]?.let { cvs.add(it) } ?: run { cvs.add(match.toInt()) }
                    }
                "${cvs.first}${cvs.last}".toInt()
            }.reduce { a, b -> a + b }
        }
    }
}

fun main() {
    val lines: List<String> = File("src/inputs/day1.txt").readLines()
    println("Part 1: ${Day1.part1(lines)}")
    println("Part 2: ${Day1.part2(lines)}")
}