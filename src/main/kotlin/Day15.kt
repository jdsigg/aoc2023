import java.io.File

class Day15 {
    companion object {

        private fun HASH(str: String): Int {
            return str.map { it.code }.fold(0) { a, b -> ((a + b) * 17) % 256 }
        }

        fun part1(steps: List<String>): Int {
            return steps.map { HASH(it) }.reduce { a, b -> a + b }
        }

        private fun handleRemoval(step: String, boxes: MutableMap<Int, LinkedHashMap<String, String>>) {
            val label = step.dropLast(1);
            val box = HASH(label)
            boxes[box]?.remove(label)
        }

        private fun handleAddition(step: String, boxes: MutableMap<Int, LinkedHashMap<String, String>>) {
            val (label, lens) = step.split('=')
            val box = HASH(label)
            if (boxes.contains(box)) {
                boxes[box]!![label] = lens
            } else {
                boxes[box] = linkedMapOf(label to lens)
            }
        }

        private fun handleStep(step: String, boxes: MutableMap<Int, LinkedHashMap<String, String>>) {
            if (step.endsWith('-')) {
                handleRemoval(step, boxes)
            } else {
                handleAddition(step, boxes)
            }
        }

        fun part2(steps: List<String>): Int {
            val boxes: MutableMap<Int, LinkedHashMap<String, String>> = mutableMapOf()
            steps.forEach { step -> handleStep(step, boxes) }
            return boxes.flatMap { (k, v) ->
                v.toList().mapIndexed { i, (_, f) ->
                    (k + 1) * (i + 1) * f.toInt()
                }
            }.reduce { a, b -> a + b }
        }
    }
}

fun main() {
    val steps: List<String> = File("src/inputs/day15.txt").readText().trim().split(",")
    println("Part 1: ${Day15.part1(steps)}")
    println("Part 2: ${Day15.part2(steps)}")
}