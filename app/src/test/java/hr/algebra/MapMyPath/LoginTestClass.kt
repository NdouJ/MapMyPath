package hr.algebra.MapMyPath
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4



@RunWith(JUnit4::class)
class LoginTestClass {

    @Test
    fun testUsernameNotEmpty() {
        val username = "TestUsername"
        assertTrue(username.isNotEmpty())
    }

    @Test
    fun testPasswordCriteria() {
        val password = "TestPassw0rd"
        assertTrue(password.isNotEmpty())
        assertTrue(password.length > 8)
        assertTrue(password.matches(Regex(".*[A-Z].*")))
        assertTrue(password.matches(Regex(".*\\d.*")))
    }


}