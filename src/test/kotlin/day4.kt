import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Day4Test {
    @Test
    fun assertPartTwoHelperFunction() =
        assertTrue(twoAdjacentMatchingDigitsAreNotPartOfLargerGroup("112233"))

    @Test
    fun assertPartTwoHelperFunction2() =
        assertFalse(twoAdjacentMatchingDigitsAreNotPartOfLargerGroup("123444"))

    @Test
    fun assertPartTwoHelperFunction3() =
        assertTrue(twoAdjacentMatchingDigitsAreNotPartOfLargerGroup("111122"))

    @Test
    fun assertPartTwoHelperFunction4() =
        assertTrue(twoAdjacentMatchingDigitsAreNotPartOfLargerGroup("111111"))
}
