import hr.algebra.MapMyPath.ui.dashboard.DashboardFragment
import org.junit.Test
import org.junit.Assert.*

class DashboardFragmentTest {

    @Test
    fun testCreateLiveFeed() {
        val dashboardFragment = DashboardFragment()
        dashboardFragment.createLiveFeedUspinjaca()


        assertNotNull(dashboardFragment.binding.webcamUspinjaca)

    }
}
