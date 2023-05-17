package hr.algebra.MapMyPath.ui.home

import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import hr.algebra.MapMyPath.databinding.FragmentHomeBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.Manifest.permission.CAMERA
import android.Manifest.permission.INTERNET
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible

class HomeFragment : Fragment() {


    companion object{
        private const val CAMERA_PERMISION_CODE =1
        private const val CAMERA_REQUEST_CODE = 2
    }
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.tvGreeting
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        binding.ivShare.setOnClickListener{
            shareThumbnailViaIntent()
        }

        binding.floatingCamera.setOnClickListener {
            if(
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED

            ){
                takePhoto()

            }else{
ActivityCompat.requestPermissions(
    requireActivity(),
    arrayOf(android.Manifest.permission.CAMERA),
    CAMERA_PERMISION_CODE
)


            }

            }


        return root
    }

    private fun shareThumbnailViaIntent() {
        // Create an intent to share the thumbnail
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/jpeg"

        // Add the thumbnail as an attachment
        val bitmap = (binding.imageView.drawable as BitmapDrawable).bitmap
        val path = MediaStore.Images.Media.insertImage(
            requireContext().contentResolver,
            bitmap,
            "mapMyPathZagreb",
            null
        )
        val thumbnailUri = Uri.parse(path)
        shareIntent.putExtra(Intent.EXTRA_STREAM, thumbnailUri)


        startActivity(Intent.createChooser(shareIntent, "Share Thumbnail"))
    }

    private fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ( resultCode==Activity.RESULT_OK){
            if(requestCode== CAMERA_REQUEST_CODE){
                val thumBnail: Bitmap = data!!.extras!!.get("data") as Bitmap
                binding.imageView.setImageBitmap(thumBnail)

                binding.ivShare.isVisible=true;
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}