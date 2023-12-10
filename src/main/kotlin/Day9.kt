import java.io.File

class Day9() {
    companion object {

        private fun reduce(history: List<Long>): MutableList<Long> {
            val next: MutableList<Long> = mutableListOf()
            (0..<history.size - 1).forEach {
                next.add(history[it + 1] - history[it])
            }
            return next
        }

        fun part1(histories: List<MutableList<Long>>): Long {
            var sum: Long = 0
            for (history in histories) {
                val entireHistory = mutableListOf(history)
                var curr: MutableList<Long> = history
                while (curr.any { it != 0L }) {
                    curr = reduce(curr)
                    entireHistory.add(curr)
                }
                curr.add(0)

                (entireHistory.size - 1 downTo 1).forEach {
                    entireHistory[it - 1].add(
                        entireHistory[it - 1].last + entireHistory[it].last
                    )
                }
                sum += entireHistory[0].last
            }
            return sum
        }

        fun part2(histories: List<MutableList<Long>>): Long {
            var sum: Long = 0
            for (history in histories) {
                val entireHistory = mutableListOf(history)
                var curr: MutableList<Long> = history
                while (curr.any { it != 0L }) {
                    curr = reduce(curr)
                    entireHistory.add(curr)
                }
                curr.add(0, 0)

                (entireHistory.size - 1 downTo 1).forEach {
                    entireHistory[it - 1].add(0,
                        entireHistory[it - 1].first - entireHistory[it].first
                    )
                }
                sum += entireHistory[0].first
            }
            return sum
        }
    }

}

fun main() {
    val histories: List<MutableList<Long>> =
        File("src/inputs/day9.txt").readLines().map { it -> it.split(" ").map { it.toLong() }.toMutableList() }
    println("Part 1: ${Day9.part1(histories)}")
    println("Part 2: ${Day9.part2(histories)}")
}