import java.io.File
import kotlin.math.pow

class Day4() {
    companion object {
        /** Return a set of numbers from a line of numbers, like " 1 12 14  5"
         *
         * To note: collecting as a set produces a correct answer, but poses two questions:
         *   1. What would it mean if there are two of the same winning number?
         *   2. What would it mean if we have two of the same number?
         *
         * In either case, it would probably mean we'd have to double count. However,
         * we don't check and the solution works as is, so either:
         *   1. Some cards contain duplicates (winning #'s or my #'s) in my input, and
         *   we are to treat them as if they weren't supposed to be there.
         *   2. There are no duplicates in my input.
         *
         * I'm going to assume it is #2 :D
         */
        private fun getNumbers(line: String): Set<Int> {
            return line.split(" ")
                .filter { it.isNotEmpty() }
                .map { it.toInt() }
                .toSet()
        }

        fun part1(lines: List<String>): Int {
            return lines.map { it.substring(it.indexOf(": ") + 2) }
                .map { numbers ->
                    // There should always be exactly two items here.
                    val (winning, mine) = numbers.split(" | ")
                    val matches = getNumbers(winning).intersect(getNumbers(mine))
                    if (matches.isEmpty()) 0 else 2.0.pow(matches.size - 1.0)
                }
                .map { it.toInt() }
                .reduce { a, b -> a + b }
        }

        fun part2(lines: List<String>): Int {
            // We only need to process each card once to see how many outputs they produce.
            // That is part 1 without the exponentiation / final reduction.
            val winningProductions: Array<Int> = lines.map { it.substring(it.indexOf(": ") + 2) }
                .map { numbers ->
                    // There should always be exactly two items here.
                    val (winning, mine) = numbers.split(" | ")
                    getNumbers(winning).intersect(getNumbers(mine)).size
                }.toTypedArray()
            // Now, assume we start with 1 card each.
            val myCards = IntArray(winningProductions.size) { 1 }
            // Then, iterate over each card and produce new cards based on the cached winning productions.
            myCards.forEachIndexed { i, numCards ->
                (i + 1..i + winningProductions[i]).forEach { j -> myCards[j] += numCards }
            }
            return myCards.reduce { a, b -> a + b }
        }
    }
}

fun main() {
    val lines: List<String> = File("src/inputs/day4.txt").readLines()
    println("Part 1: ${Day4.part1(lines)}")
    println("Part 2: ${Day4.part2(lines)}")
}