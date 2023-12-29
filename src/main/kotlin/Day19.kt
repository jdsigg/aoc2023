import java.io.File

class Day19 {
    companion object {
        // We only have 'x', 'm', 'a', and 's'.
        const val INVALID_CATEGORY = 'Z'

        // Only valid conditions are '<' and '>'.
        const val INVALID_CONDITION = '='

        // For part 1.
        data class Rating(val x: Long, val m: Long, val a: Long, val s: Long)

        // For part 2.
        data class RangedRating(val x: IntRange, val m: IntRange, val a: IntRange, val s: IntRange)

        data class Rule(
            val category: Char = INVALID_CATEGORY,
            val condition: Char = INVALID_CONDITION,
            val gate: Long = 0,
            val destination: String
        ) {
            fun matches(rating: Rating): Boolean {
                if (this.category == INVALID_CATEGORY) {
                    // This is just a label and is a guaranteed match.
                    return true
                }

                // Otherwise, do the comparison.
                val comparison = when (this.category) {
                    'x' -> {
                        rating.x
                    }

                    'm' -> {
                        rating.m
                    }

                    'a' -> {
                        rating.a
                    }

                    else -> {
                        rating.s
                    }
                }

                return if (this.condition == '>') {
                    comparison > this.gate
                } else {
                    comparison < this.gate
                }
            }

            /**
             * Return a pair of matching / non-matching ranges of rules.
             *
             * The left rule, if any, is the matching rule.
             * The right rule, if any, is the non-matching rule.
             */
            fun matches(rating: RangedRating): Pair<RangedRating?, RangedRating?> {
                // x, m, a, and s ranges
                if (this.category == INVALID_CATEGORY) {
                    // This is just a label and is a guaranteed match.
                    return Pair(rating, null)
                }

                // Otherwise, do the comparison.
                when (this.category) {
                    'x' -> {
                        val (left, right) = split(rating.x, this.gate.toInt(), this.condition)
                        return Pair(
                            if (left == null) null else rating.copy(x = left),
                            if (right == null) null else rating.copy(x = right)
                        )
                    }

                    'm' -> {
                        val (left, right) = split(rating.m, this.gate.toInt(), this.condition)
                        return Pair(
                            if (left == null) null else rating.copy(m = left),
                            if (right == null) null else rating.copy(m = right)
                        )
                    }

                    'a' -> {
                        val (left, right) = split(rating.a, this.gate.toInt(), this.condition)
                        return Pair(
                            if (left == null) null else rating.copy(a = left),
                            if (right == null) null else rating.copy(a = right)
                        )
                    }

                    else -> {
                        val (left, right) = split(rating.s, this.gate.toInt(), this.condition)
                        return Pair(
                            if (left == null) null else rating.copy(s = left),
                            if (right == null) null else rating.copy(s = right)
                        )
                    }
                }
            }

            /**
             * Split a range into matching / non-matching parts.
             *
             * Given a range [a, b], a gating condition G, and a condition C, the following are possible:
             *
             * a. b < G, i.e. all values in [a, b] are less than G
             *    1. If C == '>', there are no matches
             *    2. If C == '<', every value in the range is a match
             *
             * b. G < a, i.e. all values in [a, b] are greater than G
             *    3. If C == '>', every value in the range is a match
             *    4. If C == '<', there are no matches
             *
             * c. a <= G <= b, i.e. [a, G) is less than G and (G, b] is greater than G
             *    5. If C == '>', (G, b] matches but [a, G] does not
             *    6. If C == '<', [a, G) matches but [G, b] does not
             *
             * The range of matching conditions, if any, are the first part of the returned pair.
             * The range of non-matching conditions, if any, are the second part of the returned pair.
             */
            private fun split(range: IntRange, G: Int, C: Char): Pair<IntRange?, IntRange?> {
                val a = range.first
                val b = range.last
                return if (C == '>') {
                    if (b < G) {
                        Pair(null, range)
                    } else if (a > G) {
                        Pair(range, null)
                    } else {
                        Pair((G + 1..b), (a..G))
                    }
                } else {
                    if (b < G) {
                        Pair(range, null)
                    } else if (a > G) {
                        Pair(null, range)
                    } else {
                        Pair(
                            (a..<G), (G..b)
                        )
                    }

                }
            }

        }

        private fun makeRule(rule: String): Rule {
            // Strings look like "x>N:label".
            val firstColon = rule.indexOf(':')
            return if (firstColon == -1) {
                // These are terminal / catch-all rules e.g. "A", "R", and "qqz"
                Rule(destination = rule)
            } else {
                Rule(
                    rule[0],
                    rule[1],
                    rule.substring(2, firstColon).toLong(),
                    rule.substring(firstColon + 1, rule.length)
                )
            }
        }

        /**
         * Return a map of workflow labels to the rules that apply to each label.
         */
        fun makeWorkflow(line: String): Pair<String, List<Rule>> {
            val firstSemicolon = line.indexOf('{')
            val label = line.substring(0, firstSemicolon)
            val rules = line.substring(firstSemicolon + 1, line.length - 1).split(',').map(this::makeRule)
            return Pair(label, rules)
        }

        /**
         * Return a Rating from a string.
         *
         * Ratings look like "{x=X,m=M,a=A,s=S}".
         */
        fun makeRating(line: String): Rating {
            val ratingMap: Map<String, Long> = line.substring(1, line.length - 1).split(',').associate {
                val (a, b) = it.split("=")
                Pair(a, b.toLong())
            }

            // Assume we always have a value for x, m, a, and s.
            return Rating(
                ratingMap["x"]!!, ratingMap["m"]!!, ratingMap["a"]!!, ratingMap["s"]!!
            )
        }

        fun part1(parsedWorkflows: Map<String, List<Rule>>, parsedRatings: List<Rating>): Long {
            val acceptedRatings: MutableList<Rating> = mutableListOf()
            for (rating in parsedRatings) {
                var currRules: List<Rule> = parsedWorkflows["in"]!!
                while (true) {
                    // Find the matching rule for this rating.
                    var nextRule = ""
                    for (rule in currRules) {
                        if (rule.matches(rating)) {
                            // We found a match
                            nextRule = rule.destination
                            break
                        }
                    }

                    if (nextRule == "A") {
                        acceptedRatings.add(rating)
                        break
                    }

                    if (nextRule == "R") {
                        break
                    }

                    currRules = parsedWorkflows[nextRule]!!
                }
            }

            return acceptedRatings.map { rating -> rating.x + rating.m + rating.a + rating.s }.reduce { a, b -> a + b }
        }

        fun part2(parsedWorkflows: Map<String, List<Rule>>): Long {
            val root = RangedRating(1..4000, 1..4000, 1..4000, 1..4000)
            val queue: MutableList<Pair<String, RangedRating>> = mutableListOf(Pair("in", root))
            val acceptedRanges: MutableList<RangedRating> = mutableListOf()

            while (queue.isNotEmpty()) {
                val (location, rating) = queue.removeFirst()!!
                val currRules = parsedWorkflows[location]!!

                // Take the current RangedRating and split it into a bunch of RangedRatings.
                var currRating = rating
                for (currRule in currRules) {
                    val match = currRule.matches(currRating)
                    // This range is a match and should either be accepted, rejected, or re-processed at the next location.
                    if (match.first != null) {
                        when (currRule.destination) {
                            "A" -> {
                                acceptedRanges.add(match.first!!)
                            }

                            "R" -> {
                                // Rejects are forgotten :(
                            }

                            else -> {
                                queue.add(Pair(currRule.destination, match.first!!))
                            }
                        }
                    }
                    // This range is not a match. If it exists, re-process now. Otherwise, we can stop this rule.
                    if (match.second == null) {
                        break
                    }
                    currRating = match.second!!
                }
            }

            return acceptedRanges.map { rangeRule ->
                // The number of combinations is the product of each range.
                rangeRule.x.count().toLong() * rangeRule.m.count().toLong() * rangeRule.a.count()
                    .toLong() * rangeRule.s.count().toLong()
            }.reduce { a, b -> a + b }
        }
    }
}

fun main() {
    val fContents: String = File("src/inputs/day19.txt").readText()
    val (workflows, ratings) = fContents.split("\r\n\r\n")
    val parsedWorkflows: Map<String, List<Day19.Companion.Rule>> =
        workflows.split("\r\n").associate(Day19::makeWorkflow)
    val parsedRatings: List<Day19.Companion.Rating> = ratings.split("\r\n").map(Day19::makeRating)

    println("Part 1: ${Day19.part1(parsedWorkflows, parsedRatings)}")
    println("Part 2: ${Day19.part2(parsedWorkflows)}")
}