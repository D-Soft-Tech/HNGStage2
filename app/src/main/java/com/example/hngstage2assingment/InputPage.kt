package com.example.hngstage2assingment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import coil.load
import com.example.hngstage2assingment.AppConstant.CAMERA_PERM_REQUEST_CODE
import com.example.hngstage2assingment.AppConstant.STORAGE_PERM_REQUEST_CODE
import com.example.hngstage2assingment.AppConstant.checkForPermission
import com.example.hngstage2assingment.AppConstant.getImageLoader
import com.example.hngstage2assingment.AppConstant.requestForPermission
import com.example.hngstage2assingment.AppConstant.validateInputs
import com.example.hngstage2assingment.databinding.FragmentInputPageBinding
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class InputPage : Fragment(), EasyPermissions.PermissionCallbacks {
    private var _binding: FragmentInputPageBinding? = null
    private val binding get() = _binding!!
    private lateinit var userImage: ImageView
    private lateinit var camera: ImageView
    private lateinit var gallery: ImageView
    private lateinit var firstName: TextView
    private lateinit var lastName: TextView
    private lateinit var submitBtn: Button
    private var currentBitmapInUserImageView: Bitmap? = null // To keep track of the image in the user profile image's image view

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentInputPageBinding.inflate(inflater, container, false)
        val view = binding.root

        userImage = binding.userImage
        camera = binding.captureImageImg
        gallery = binding.selectImageFromGalleryImg
        firstName = binding.firstNameEt
        lastName = binding.lastNameEt
        submitBtn = binding.submitBtn

        val cameraLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                val pictureTakenWithCameraAsBitmap = activityResult.data?.extras?.get("data") as Bitmap

                currentBitmapInUserImageView = pictureTakenWithCameraAsBitmap
                // use coil to load the picture into the user profile picture image view
                getImageLoader(userImage, pictureTakenWithCameraAsBitmap)
            }
        }

        val action = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            val imageInBitmapForm = MediaStore.Images.Media.getBitmap(
                activity?.contentResolver, uri
            )
            currentBitmapInUserImageView = imageInBitmapForm
            drawImageUsingCoil(userImage, imageInBitmapForm)
        }

        // click the gallery icon to select an image from the photo gallery
        gallery.setOnClickListener {
            if (
                checkForPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                action.launch("image/*")
            } else {
                requestForPermission(
                    requireActivity(),
                    getString(R.string.permission_rationale),
                    STORAGE_PERM_REQUEST_CODE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
        }

        // click the camera icon to capture your picture
        camera.setOnClickListener {
            if (
                checkForPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                )
            ) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                cameraLauncher.launch(intent)
            } else {
                requestForPermission(
                    requireActivity(),
                    getString(R.string.camera_permission_rationale),
                    CAMERA_PERM_REQUEST_CODE,
                    Manifest.permission.CAMERA
                )
            }
        }

        submitBtn.setOnClickListener {
            // validate the input
            if (
                currentBitmapInUserImageView?.let { validateInputs(firstName.text.toString(), lastName.text.toString(), it) } == true
            ) {
                // Build a user object from the supplied details in the input fields
                val curUser = currentBitmapInUserImageView?.let { UserClass(firstName.text.toString(), lastName.text.toString(), it) }
//                val action = InputPageFragmentD
            }
        }

        return view
    }

    private fun drawImageUsingCoil(imageView: ImageView, imageBitmap: Bitmap) {
        imageView.load(imageBitmap) {
            crossfade(true)
            crossfade(1000)
            placeholder(R.drawable.image_loader_place_holder)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        when {
            requestCode == STORAGE_PERM_REQUEST_CODE -> {
                Toast.makeText(requireContext(), getString(R.string.storage_permission_granted), Toast.LENGTH_SHORT).show()
            }
            requestCode == CAMERA_PERM_REQUEST_CODE -> {
                Toast.makeText(requireContext(), getString(R.string.camera_permission_granted), Toast.LENGTH_SHORT).show()
            }
            perms.size > 1 -> {
                Toast.makeText(requireContext(), getString(R.string.permission_granted), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(requireActivity(), perms)) {
            AppSettingsDialog.Builder(requireActivity()).build().show()
        } else {
            Toast.makeText(requireContext(), getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
