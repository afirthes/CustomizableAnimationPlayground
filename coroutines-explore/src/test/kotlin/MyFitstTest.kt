import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Test

class MyFirstTest {

    @Test
    fun firstTest() = runBlocking {
        myFoo()
        assertEquals(1,1)
    }
}
