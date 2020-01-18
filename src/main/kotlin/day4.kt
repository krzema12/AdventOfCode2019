fun main() {
    val possiblePasswordsForPart1 = crackPasswordUsingBruteForce(
        from = 124075, to = 580769,  passwordLegalityPredicate = ::isPasswordLegalForPart1)
    println(possiblePasswordsForPart1.size)
    val possiblePasswordsForPart2 = crackPasswordUsingBruteForce(
        from = 124075, to = 580769,  passwordLegalityPredicate = ::isPasswordLegalForPart2)
    println(possiblePasswordsForPart2.size)
}

fun crackPasswordUsingBruteForce(from: Int, to: Int, passwordLegalityPredicate: (String) -> Boolean): List<String> {
    return (from..to)
        .map { it.toString() }
        .filter { passwordLegalityPredicate(it) }
}

fun isPasswordLegalForPart1(password: String) =
    twoAdjacentDigitsAreTheSame(password) && digitsDoNotDecrease(password)

fun isPasswordLegalForPart2(password: String) =
    twoAdjacentDigitsAreTheSame(password) && digitsDoNotDecrease(password) &&
            twoAdjacentMatchingDigitsAreNotPartOfLargerGroup(password)

fun twoAdjacentMatchingDigitsAreNotPartOfLargerGroup(password: String): Boolean {
    val countedSortedCharacters = password
        .groupBy { it }
        .mapValues { it.value.size }
        .entries
        .sortedByDescending { it.value }

    val allTwoAdjacentMatchingDigits = password
        .zipWithNext()
        .filter { (a, b) -> a == b }
        .map { (a, _) -> a }
        .toSet()

    val largerGroupsOfMatchingDigits = countedSortedCharacters
        .filter { it.value == countedSortedCharacters.first().value }
        .map { it.key }
    val noLargerGroup = countedSortedCharacters.all { it.value == countedSortedCharacters.first().value }

    return noLargerGroup || password
        .zipWithNext()
        .any { (a, b) -> a == b && !largerGroupsOfMatchingDigits.contains(a)}

//    return noLargerGroup
//            || allTwoAdjacentMatchingDigits.any { !largerGroupsOfMatchingDigits.contains(it) }

//    return countedSortedCharacters.size == 1
//            || (countedSortedCharacters.size > 1 && countedSortedCharacters[0].value > 1 && countedSortedCharacters[1].value > 1)

//    if (countedSortedCharacters.first().value == countedSortedCharacters.last().value) {
//        return true
//    }
//
//    val characterFromLargerGroup = countedSortedCharacters.first().key
//
//    val test = password
//        .zipWithNext()
//        .any { (a, b) -> a == b && a != characterFromLargerGroup }
//
//    return test
}

fun twoAdjacentDigitsAreTheSame(password: String): Boolean {
    return password
        .zipWithNext()
        .any { (a, b) -> a == b }
}

fun digitsDoNotDecrease(password: String): Boolean {
    return password
        .zipWithNext()
        .all { (a, b) -> a.toInt() <= b.toInt() }
}
