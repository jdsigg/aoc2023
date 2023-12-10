import java.io.File

class Day8() {
    companion object {
        fun part1(instructions: String, graph: Map<String, Pair<String, String>>): Int {
            var moveCount = 0
            var curr = "AAA"
            while (curr != "ZZZ") {
                val options = graph[curr]!!
                val index = moveCount % instructions.length
                val next = if (instructions[index] == 'L') options.first else options.second
                curr = next
                moveCount++
            }
            return moveCount
        }


        // Took the following three functions (gcd, lcm, lcm) from the internet :)
        private fun gcd(a: Long, b: Long): Long {
            var aTemp = a
            var bTemp = b
            while (bTemp > 0) {
                val temp = bTemp
                bTemp = aTemp % bTemp
                aTemp = temp
            }
            return aTemp
        }

        private fun lcm(a: Long, b: Long): Long {
            return a * (b / gcd(a, b))
        }

        private fun lcm(input: List<Long>): Long {
            var result = input[0]
            for (i in 1..<input.size) {
                result = lcm(result, input[i])
            }
            return result
        }

        /**
         *  For my input, each location that ends with A is as such:
         *    QKA, VMA, AAA, RKA, LBA, and JMA
         *
         *  When traversing the graph, each location cycles in the following moves, respectively:
         *    (QXZ) 12169, (MTZ) 20093, (ZZZ) 20659, (NDZ) 22357, (GPZ) 13301, and (VHZ) 18961
         *
         *  To find when they all overlap, we need to calculate the LCM of these numbers.
         */
        fun part2(instructions: String, graph: Map<String, Pair<String, String>>): Long {
            val endsWithA = graph.keys.filter { it.endsWith("A") }.toList()
            val moveCounts: MutableList<Long> = mutableListOf()
            for (loc in endsWithA) {
                var moveCount: Long = 0
                var curr = loc
                while (!curr.endsWith("Z")) {
                    val index = (moveCount % instructions.length).toInt()
                    val options = graph[curr]!!
                    curr = if (instructions[index] == 'L') options.first else options.second
                    moveCount++
                }
                moveCounts.add(moveCount)
            }
            return lcm(moveCounts)
        }
    }
}

fun main() {
    val (instructions, graph) = File("src/inputs/day8.txt").readText().split("\r\n\r\n")
    val pGraph = graph.split("\r\n").associate { line ->
        val (source, dest) = line.split(" = ")
        val (left, right) = dest.substring(1, dest.length - 1).split(", ")
        Pair(source, Pair(left, right))
    }
    println("Part 1: ${Day8.part1(instructions, pGraph)}")
    println("Part 2: ${Day8.part2(instructions, pGraph)}")
}
