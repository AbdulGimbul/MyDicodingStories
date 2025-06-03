package com.abdl.mydicodingstories.ui

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.abdl.mydicodingstories.R
import com.abdl.mydicodingstories.databinding.ActivityAddStoryBinding
import com.abdl.mydicodingstories.utils.rotateBitmap
import com.abdl.mydicodingstories.utils.uriToFile
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@AndroidEntryPoint
class AddStoryActivity : BaseActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private val mainViewModel: MainViewModel by viewModels()
    private var getFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = getString(R.string.add_story)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.root.updatePadding(
                left = insets.left,
                top = insets.top,
                right = insets.right,
                bottom = insets.bottom
            )
            WindowInsetsCompat.CONSUMED
        }

        if (!allPermissionGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        mainViewModel.isLoading.observe(this) {
            if (it == true) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        mainViewModel.errorMessage.observe(this) {
            if (it == "Error : \"description\" is not allowed to be empty") {
                Toast.makeText(this, R.string.description_empty_error_toast, Toast.LENGTH_SHORT)
                    .show()
            } else if (it == "Error : Payload content length greater than maximum allowed: 1000000") {
                Toast.makeText(this, R.string.image_too_large_error_toast, Toast.LENGTH_LONG)
                    .show()
            }
        }

        binding.cameraXButton.setOnClickListener { startCameraX() }
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.uploadButton.setOnClickListener { uploadImage() }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionGranted()) {
                Toast.makeText(
                    this,
                    R.string.permission_not_granted,
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@AddStoryActivity)
            getFile = myFile
            binding.previewImageView.setImageURI(selectedImg)
        }
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile: File? = it.data?.let { intentData ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intentData.getSerializableExtra("picture", File::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intentData.getSerializableExtra("picture") as? File
                }
            }
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            getFile = myFile
            val result = rotateBitmap(
                BitmapFactory.decodeFile(getFile?.path),
                isBackCamera
            )

            binding.previewImageView.setImageBitmap(result)
        }
    }

    private fun uploadImage() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)

            val storyDetail = binding.edtDescription.text.toString()

            val description =
                storyDetail.toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            mainViewModel.postStory(imageMultipart, description)

            mainViewModel.postStoryResponse.observe(this) { responseBody ->
                if (responseBody != null && !responseBody.error) {
                    Toast.makeText(
                        this@AddStoryActivity,
                        R.string.story_uploaded_successfully,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    Intent(this@AddStoryActivity, MainActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(it)
                    }
                }
            }
        } else {
            Toast.makeText(
                this@AddStoryActivity,
                R.string.please_select_image_first,
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    private fun reduceFileImage(file: File): File {
        val MAX_FILE_SIZE_BYTES = 1 * 1024 * 1024

        if (file.length() <= MAX_FILE_SIZE_BYTES) {
            return file
        }

        var bitmap: Bitmap? = null
        try {
            bitmap = BitmapFactory.decodeFile(file.path)
            if (bitmap == null) {
                Toast.makeText(this, R.string.failed_to_decode_image_toast, Toast.LENGTH_SHORT).show()
                return file
            }

            var quality = 90
            val minQuality = 10
            val step = 5

            val outputDir = cacheDir
            val outputFile = File(outputDir, "compressed_${System.currentTimeMillis()}_${file.name}")

            val byteArrayOutputStream = ByteArrayOutputStream()

            while (quality >= minQuality) {
                byteArrayOutputStream.reset()
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
                val compressedSize = byteArrayOutputStream.size()

                if (compressedSize <= MAX_FILE_SIZE_BYTES) {
                    FileOutputStream(outputFile).use { fos ->
                        fos.write(byteArrayOutputStream.toByteArray())
                    }
                    return outputFile
                }
                quality -= step
            }

            FileOutputStream(outputFile).use { fos ->
                fos.write(byteArrayOutputStream.toByteArray())
            }

            if (outputFile.length() > MAX_FILE_SIZE_BYTES) {
                Toast.makeText(this, R.string.could_not_compress_toast, Toast.LENGTH_LONG).show()
            }
            return outputFile

        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            Toast.makeText(this, R.string.image_too_large_to_process_toast, Toast.LENGTH_LONG).show()
            return file
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error processing image: ${e.message}", Toast.LENGTH_SHORT).show()
            return file
        } finally {
            bitmap?.recycle()
        }
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}