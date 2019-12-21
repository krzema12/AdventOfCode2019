fun main() {
    val program = readTextFileFromResourcesForDay(2).split(",").map(String::toInt)
    println("Answer to part 1: ${getProgramOutputFor(program, noun = 12, verb = 2)}")
    println("Answer to part 2: ${getAnswerToPart2(program)}")
}

fun getAnswerToPart2(program: IntcodeProgram): Int {
    val possibleCombinations = (0..99).flatMap { noun ->
        (0..99).map { verb ->
            Pair(noun, verb)
        }
    }
    val nounVerbPairOfInterest = possibleCombinations.map { nounVerbPair ->
        val programOutput = getProgramOutputFor(program, nounVerbPair.first, nounVerbPair.second)
        Pair(nounVerbPair, programOutput)
    }.first { (_, programOutput) ->
        programOutput == 19690720
    }.first

    return 100*nounVerbPairOfInterest.first + nounVerbPairOfInterest.second
}

fun getProgramOutputFor(program: IntcodeProgram, noun: Int, verb: Int): Int {
    val programAfterInitialModification = replaceInitialPositions(program, noun = noun, verb = verb)
    val programAfterExecution = executeAndReturnModified(programAfterInitialModification)
    return programAfterExecution[0]
}

tailrec fun executeAndReturnModified(program: IntcodeProgram, position: Int = 0): IntcodeProgram {
    val opcode = program[position]

    if (opcode == 99) {
        return program
    }

    val firstArgument = program[program[position + 1]]
    val secondArgument = program[program[position + 2]]
    val targetPosition = program[position + 3]
    val modified = program.toMutableList().apply {
        val operationResult = when(opcode) {
            1 -> firstArgument + secondArgument
            2 -> firstArgument * secondArgument
            else -> {
                throw IllegalStateException("Something went wrong!")
            }
        }
        this[targetPosition] = operationResult
    }

    return executeAndReturnModified(modified, position + 4)
}

fun replaceInitialPositions(initialProgram: IntcodeProgram, noun: Int, verb: Int) =
    initialProgram.toMutableList().apply {
        this[1] = noun
        this[2] = verb
    }

typealias IntcodeProgram = List<Int>
