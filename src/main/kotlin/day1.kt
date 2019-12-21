fun main() {
    val moduleMasses = readTextFileFromResourcesForDay(1).split("\n").map(String::toInt)
    println(fuelForModulesNotTakingFuelMassIntoConsideration(moduleMasses))
    println(fuelForModulesTakingFuelMassIntoConsideration(moduleMasses))
}

fun fuelForModulesNotTakingFuelMassIntoConsideration(moduleMasses: List<Int>) =
    moduleMasses
        .map { fuelForMass(it) }
        .sum()

fun fuelForModulesTakingFuelMassIntoConsideration(moduleMasses: List<Int>) =
    moduleMasses
        .map { fuelForMassRecursive(it) }
        .sum()

tailrec fun fuelForMassRecursive(mass: Int, accumulator: Int = 0): Int {
    val firstLevelFuelMass = fuelForMass(mass)

    if (firstLevelFuelMass <= 0) {
        return accumulator
    }

    return fuelForMassRecursive(firstLevelFuelMass, firstLevelFuelMass + accumulator)
}

fun fuelForMass(mass: Int) =
    mass/3 - 2
