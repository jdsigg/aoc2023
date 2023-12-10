import java.io.File

// This solution isn't very pretty, but seems to work.

class Day7() {

    private enum class RANK(val weight: Int) {
        HIGH_CARD(0),
        ONE_PAIR(1),
        TWO_PAIR(2),
        THREE_OF_A_KIND(3),
        FULL_HOUSE(4),
        FOUR_OF_A_KIND(5),
        FIVE_OF_A_KIND(6)
    }

    private class Card(
        val hand: String,
        val bid: Long,
        val considerJokers: Boolean = false,
        var rank: RANK = RANK.HIGH_CARD
    ) : Comparable<Card> {

        private fun getRank(hand: String): RANK {
            if (isFiveOfAKind(hand)) return RANK.FIVE_OF_A_KIND
            if (isFourOfAKind(hand)) return RANK.FOUR_OF_A_KIND
            if (isFullHouse(hand)) return RANK.FULL_HOUSE
            if (isThreeOfAKind(hand)) return RANK.THREE_OF_A_KIND
            if (isTwoPair(hand)) return RANK.TWO_PAIR
            if (isOnePair(hand)) return RANK.ONE_PAIR
            // To note, if there is a joker, it is impossible to have a high card ranking.
            return RANK.HIGH_CARD
        }

        init {
            this.rank = getRank(this.hand)
        }

        private fun isFiveOfAKind(hand: String): Boolean {
            return if (this.considerJokers && hand.contains("J")) {
                val noJokers = hand.filter { it != 'J' }
                noJokers == "" || noJokers.toHashSet().size == 1
            } else {
                hand.toHashSet().size == 1
            }
        }

        private fun isFourOfAKind(hand: String): Boolean {
            val noJokers = hand.filter { it != 'J' }
            val numJokers = hand.length - noJokers.length
            return if (this.considerJokers && numJokers != 0) {
                val map: MutableMap<Char, Int> = mutableMapOf()
                noJokers.forEach { c ->
                    map[c] = map.getOrDefault(c, 0) + 1
                }
                map.values.max() + numJokers == 4
            } else {
                val map: MutableMap<Char, Int> = mutableMapOf()
                hand.forEach { c ->
                    map[c] = map.getOrDefault(c, 0) + 1
                }
                map.values.any { it == 4 }
            }
        }

        private fun isFullHouse(hand: String): Boolean {
            if (this.considerJokers && hand.contains('J')) {
                val noJokers = hand.filter { it != 'J' }
                val numJokers = hand.length - noJokers.length
                val map: MutableMap<Char, Int> = mutableMapOf()
                noJokers.forEach { c ->
                    map[c] = map.getOrDefault(c, 0) + 1
                }
                return (map.values.max() + map.values.min() + numJokers) == 5
            } else {
                val map: MutableMap<Char, Int> = mutableMapOf()
                hand.forEach { c ->
                    map[c] = map.getOrDefault(c, 0) + 1
                }

                val values = map.values.toList().sorted()
                return values.size == 2 && values.first == 2 && values.last == 3
            }
        }

        private fun isThreeOfAKind(hand: String): Boolean {
            if (this.considerJokers && hand.contains('J')) {
                val noJokers = hand.filter { it != 'J' }
                val numJokers = hand.length - noJokers.length
                val map: MutableMap<Char, Int> = mutableMapOf()
                noJokers.forEach { c ->
                    map[c] = map.getOrDefault(c, 0) + 1
                }

                return map.values.max() + numJokers == 3
            } else {
                val map: MutableMap<Char, Int> = mutableMapOf()
                hand.forEach { c ->
                    map[c] = map.getOrDefault(c, 0) + 1
                }

                return map.values.size == 3 && map.values.any { it == 3 }
            }
        }

        private fun isTwoPair(hand: String): Boolean {
            if (this.considerJokers && hand.contains('J')) {
                // We can never have two pair with a joker.
                // If we have 2+ jokers, at least we have three of a kind.
                // If we have 1 joker, we must have a hand like XXYZJ to make two pair. However, that would make three of a kind.
                // If we have 0 jokers, we wouldn't be in this check anyways.
                return false
            } else {

                val map: MutableMap<Char, Int> = mutableMapOf()
                hand.forEach { c ->
                    map[c] = map.getOrDefault(c, 0) + 1
                }

                return map.values.filter { it == 2 }.size == 2
            }
        }

        private fun isOnePair(hand: String): Boolean {
            if (this.considerJokers && hand.contains('J')) {
                // We can only ever have one pair if the entire string is unique and there is one joker.
                val noJokers = hand.filter { it != 'J' }
                return noJokers.toHashSet().size == 4
            }
            val map: MutableMap<Char, Int> = mutableMapOf()
            hand.forEach { c ->
                map[c] = map.getOrDefault(c, 0) + 1
            }

            return map.values.filter { it == 2 }.size == 1
        }

        private fun compareHands(h1: String, h2: String): Int {
            val rankList: MutableList<Char> =
                mutableListOf('2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A')
            if (this.considerJokers) {
                rankList.remove('J')
                rankList.add(0, 'J')
            }
            val ranks: Map<Char, Int> = rankList
                .mapIndexed { index, str ->
                    Pair(
                        str,
                        index
                    )
                }.toMap()

            h1.zip(h2).forEach { (l1, l2) ->
                val r1 = ranks[l1]!!
                val r2 = ranks[l2]!!
                if (r1 < r2) {
                    return -1
                } else if (r1 > r2) {
                    return 1
                }
            }
            return 0
        }

        override fun compareTo(other: Card): Int {
            return if (this.rank < other.rank) {
                -1
            } else if (this.rank > other.rank) {
                1
            } else {
                compareHands(this.hand, other.hand)
            }
        }

        override fun toString(): String {
            return "Hand: ${this.hand}, Bid: ${this.bid}, Rank: ${this.rank}"
        }
    }

    companion object {
        fun part1(lines: List<String>): Long {
            return lines.map {
                val (hand, bid) = it.split(" ")
                Card(hand, bid.toLong())
            }.sortedBy { it }.mapIndexed { index, card -> card.bid * (index + 1) }.reduce { a, b -> a + b }
        }

        fun part2(lines: List<String>): Long {
            return lines.map {
                val (hand, bid) = it.split(" ")
                Card(hand, bid.toLong(), considerJokers = true)
            }.sortedBy { it }.mapIndexed { index, card -> card.bid * (index + 1) }.reduce { a, b -> a + b }
        }
    }
}

fun main() {
    val lines: List<String> = File("src/inputs/day7.txt").readLines()
    println("Part 1: ${Day7.part1(lines)}")
    println("Part 2: ${Day7.part2(lines)}")
}