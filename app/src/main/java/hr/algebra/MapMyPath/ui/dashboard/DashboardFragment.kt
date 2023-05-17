package hr.algebra.MapMyPath.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso
import hr.algebra.MapMyPath.databinding.FragmentDashboardBinding
import com.squareup.picasso.Callback



private const val WEBCAM_IMAGE_URL_USPINJACA = "https://images-webcams.windy.com/57/1384996057/current/full/1384996057.jpg"
private const val WEBCAM_IMAGE_URL_TRG = "https://images-webcams.windy.com/09/1599596709/current/full/1599596709.jpg"
private const val WEBCAM_IMAGE_URL_JARUN = "https://images-webcams.windy.com/08/1384995908/current/full/1384995908.jpg"

class DashboardFragment : Fragment() {

     var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
     val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        dashboardViewModel.text.observe(viewLifecycleOwner) {

        }
            createLiveFeedUspinjaca()

        createLiveFeedRest()
        return root
    }

    public fun createLiveFeedUspinjaca() {
        val webCamUspinjaca = binding.webcamUspinjaca
        Picasso.get().load(WEBCAM_IMAGE_URL_USPINJACA).into(webCamUspinjaca, object : Callback {
            override fun onSuccess() {
                // Image loaded successfully
            }

            override fun onError(e: Exception?) {
                e?.printStackTrace()
            }
        })
    }

    public fun createLiveFeedRest() {



        val webCamTrg = binding.webcamTrgBanaJelacica
        Picasso.get().load(WEBCAM_IMAGE_URL_TRG).into(webCamTrg, object : Callback {
            override fun onSuccess() {
                // Image loaded successfully
            }

            override fun onError(e: Exception?) {
                e?.printStackTrace()
            }
        })



        val webCamJarun = binding.webcamJarun
        Picasso.get().load(WEBCAM_IMAGE_URL_JARUN).into(webCamJarun, object : Callback {
            override fun onSuccess() {
                // Image loaded successfully
            }

            override fun onError(e: Exception?) {
                e?.printStackTrace()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}