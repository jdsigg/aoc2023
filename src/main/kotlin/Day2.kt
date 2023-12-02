import java.io.File
import kotlin.math.max

class Day2() {
    companion object {
        fun part1(lines: List<String>): Int {
            return lines.map { line ->
                val colonInd: Int = line.indexOf(":")
                // Game gameId:
                var gameId: Int = line.substring(5, colonInd).toInt()

                val games = line.substring(colonInd + 2).split("; ")
                game@ for (game in games) {
                    val draws: List<String> = game.split(", ")
                    for (draw in draws) {
                        val (num, color) = draw.split(" ")
                        if ((color == "red" && num.toInt() > 12) || (color == "green" && num.toInt() > 13) || (color == "blue" && num.toInt() > 14)) {
                            gameId = 0
                            break@game
                        }
                    }
                }
                gameId
            }.reduce { a, b -> a + b }
        }

        fun part2(lines: List<String>): Int {
            return lines.map { line ->
                val colonInd: Int = line.indexOf(":")
                val maxes: MutableMap<String, Int> = mutableMapOf("red" to 0, "green" to 0, "blue" to 0)
                // Game gameId: [num1 color1, num2 color2; ...]
                line.substring(colonInd + 2).split("; ").forEach { game ->
                    game.split(", ").forEach { draw ->
                        // num1, color1
                        val (num, color) = draw.split(" ")
                        maxes[color] = max(num.toInt(), maxes[color]!!)
                    }
                }
                maxes.values.reduce { a, b -> a * b }
            }.reduce { a, b -> a + b }
        }
    }
}

fun main() {
    val lines: List<String> = File("src/inputs/day2.txt").readLines()
    println("Part 1: ${Day2.part1(lines)}")
    println("Part 2: ${Day2.part2(lines)}")
}