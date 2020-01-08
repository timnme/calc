import com.calc.calculator.InvalidBracketsException
import com.calc.calculator.bracketsValid
import com.calc.calculator.simplifyBrackets
import com.calc.calculator.solve
import org.junit.Assert.assertEquals
import org.junit.Test

@Suppress("FunctionName")
internal class CalculatorTest {
    companion object {
        private const val DELTA = 0.00001
    }

    @Test
    fun solve_validatePowsAndSqrts() {
        assertEquals((27).toDouble(), "3^3".solve(), DELTA)
        assertEquals((-27).toDouble(), "-3^3".solve(), DELTA)
        assertEquals((3).toDouble(), "√9".solve(), DELTA)
        assertEquals((-3).toDouble(), "-√9".solve(), DELTA)
    }

    @Test
    fun solve_validateTimesAndDivs() {
        assertEquals((9).toDouble(), "3*3".solve(), DELTA)
        assertEquals((-9).toDouble(), "-3*3".solve(), DELTA)
        assertEquals((9).toDouble(), "-3*-3".solve(), DELTA)
        assertEquals((-9).toDouble(), "3*-3".solve(), DELTA)
        assertEquals((1).toDouble(), "3/3".solve(), DELTA)
        assertEquals((-1).toDouble(), "-3/3".solve(), DELTA)
        assertEquals((1).toDouble(), "-3/-3".solve(), DELTA)
        assertEquals(-0.01, "1/-100".solve(), DELTA)
    }

    @Test
    fun solve_validateAddsAndSubs() {
        assertEquals((6).toDouble(), "3++3".solve(), DELTA)
        assertEquals((6).toDouble(), "3+3".solve(), DELTA)
        assertEquals((0).toDouble(), "+3-3".solve(), DELTA)
        assertEquals((0).toDouble(), "-3+3".solve(), DELTA)
        assertEquals((-6).toDouble(), "-3-3".solve(), DELTA)
        assertEquals((0).toDouble(), "-3--3".solve(), DELTA)
        assertEquals((-6).toDouble(), "-3-+3".solve(), DELTA)
    }

    @Test
    fun solve_validateMixed() {
        assertEquals(1.toDouble(), "-1-4+6".solve(), DELTA)
        assertEquals(3.toDouble(), "1-4+6".solve(), DELTA)
        assertEquals((-23).toDouble(), "1-4*6".solve(), DELTA)
        assertEquals((-11).toDouble(), "1-4*6/2".solve(), DELTA)
        assertEquals((-3).toDouble(), "1-4*6/2+2^3".solve(), DELTA)
        assertEquals((-8).toDouble(), "1-4*6/2+√9".solve(), DELTA)
    }

    @Test
    fun bracketsValid_validate() {
        assertEquals(true, "(12+5)".bracketsValid())
        assertEquals(false, "((0+2)".bracketsValid())
        assertEquals(false, ")(0+2)".bracketsValid())
    }

    @Test
    fun simplifyBrackets_validate() {
        assertEquals("3.0", "(1+2)".simplifyBrackets())
        assertEquals("3.0", "((1+2))".simplifyBrackets())
        assertEquals("4.0+2", "((2-1)+(1+2))+2".simplifyBrackets())
    }

    @Test(expected = InvalidBracketsException::class)
    fun simplifyBrackets_validateThrows() {
        "(12+1".simplifyBrackets()
        "(12+1))".simplifyBrackets()
        ")12+1)".simplifyBrackets()
    }

    @Test
    fun solve_validateWithBrackets() {
        assertEquals(1.toDouble(), "-(1+4)+6".solve(), DELTA)
        assertEquals((-9).toDouble(), "1-(4+6)".solve(), DELTA)
        assertEquals((37).toDouble(), "-√16+(3^3)+(4*6)-(12+(1-1))+3*3/3-(6/(3*2))".solve(), DELTA)
    }
}
