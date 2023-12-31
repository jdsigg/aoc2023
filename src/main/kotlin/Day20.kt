import java.io.File

enum class Pulse {
    HIGH, LOW
}

enum class State {
    ON, OFF
}

class Day20 {
    companion object {
        fun part1(moduleConfig: List<String>): Long {
            // All module labels to their children labels.
            val moduleGraph: MutableMap<String, List<String>> = mutableMapOf()

            // Conjunction modules have to remember pulses from their parents.
            val conjunctionModules: MutableMap<String, MutableMap<String, Pulse>> = mutableMapOf()

            // Flip-flop states.
            val flipFlopStates: MutableMap<String, State> = mutableMapOf()

            // Create links from each module to their children.
            moduleConfig.forEach { line ->
                val (source, destinations) = line.split(" -> ")
                val children = destinations.split(", ")

                if (source == "broadcaster") {
                    moduleGraph[source] = children
                } else {
                    // We have either a conjunction or flip-flop module.
                    val type = source[0]
                    val sourceLabel = source.substring(1)

                    if (type == '&') {
                        conjunctionModules[sourceLabel] = mutableMapOf()
                    } else {
                        flipFlopStates[source] = State.OFF
                    }
                    moduleGraph[sourceLabel] = children
                }
            }

            // Re-process the input, associating conjunction modules to their parents.
            moduleConfig.forEach { line ->
                val (source, destinations) = line.split(" -> ")
                val children = destinations.split(", ")

                children.forEach { child ->
                    if (conjunctionModules.containsKey(child)) {
                        // Substring is fine here; broadcaster is only linked to flip-flops.
                        conjunctionModules[child]!![source.substring(1)] = Pulse.LOW
                    }
                }
            }

            // Keep track of LOW / HIGH pulses.
            var highPulses = 0L
            var lowPulses = 0L

            (1..1000).forEach { _ ->
                // Start by pushing the button (send a LOW to broadcaster)
                lowPulses++
                val toProcess: MutableList<Pair<Pulse, String>> = mutableListOf(Pair(Pulse.LOW, "broadcaster"))
                while (toProcess.isNotEmpty()) {
                    val nextBatch: MutableList<Pair<Pulse, String>> = mutableListOf()

                    toProcess.forEach { (pulse, source) ->
                        val children = moduleGraph[source]
                        if (source == "broadcaster") {
                            // broadcaster just propagates to children
                            children!!.forEach { child -> nextBatch.add(Pair(pulse, child)) }
                        } else if (children != null) {
                            // The source is either a conjunction or a flip-flop.
                            if (conjunctionModules.containsKey(source)) {
                                // Conjunction modules propagate LOW if all parents were HIGH.
                                val propagatedPulse =
                                    if (conjunctionModules[source]!!.values.all { it == Pulse.HIGH }) {
                                        Pulse.LOW
                                    } else {
                                        Pulse.HIGH
                                    }
                                children.forEach { child ->
                                    // We could be propagating to another conjunction.
                                    if (conjunctionModules.containsKey(child)) {
                                        conjunctionModules[child]!![source] = propagatedPulse
                                    }
                                    nextBatch.add(Pair(propagatedPulse, child))
                                }
                            } else {
                                // Flip-flops only change state when they receive a low pulse.
                                if (pulse == Pulse.LOW) {
                                    if (flipFlopStates[source] == State.ON) {
                                        // The flip-flop is turning off and sending a LOW.
                                        flipFlopStates[source] = State.OFF
                                        children.forEach { child ->
                                            if (conjunctionModules.containsKey(child)) {
                                                conjunctionModules[child]!![source] = Pulse.LOW
                                            }
                                            nextBatch.add(Pair(Pulse.LOW, child))
                                        }
                                    } else {
                                        // The flip-flop is turning on and sending a high.
                                        flipFlopStates[source] = State.ON
                                        children.forEach { child ->
                                            if (conjunctionModules.containsKey(child)) {
                                                conjunctionModules[child]!![source] = Pulse.HIGH
                                            }
                                            nextBatch.add(Pair(Pulse.HIGH, child))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    lowPulses += nextBatch.count { it.first == Pulse.LOW }
                    highPulses += nextBatch.count { it.first == Pulse.HIGH }
                    toProcess.clear()
                    toProcess.addAll(nextBatch)
                }
            }
            return lowPulses * highPulses
        }
    }
}

fun main() {
    val moduleConfig: List<String> = File("src/inputs/day20.txt").readLines()
    println("Part 1: ${Day20.part1(moduleConfig)}")
}