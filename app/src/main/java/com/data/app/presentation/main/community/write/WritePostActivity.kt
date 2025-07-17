package com.data.app.presentation.main.community.write

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.TypedValue
import android.view.View
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import coil3.load
import coil3.request.transformations
import coil3.transform.RoundedCornersTransformation
import com.data.app.R
import com.data.app.data.shared_preferences.AppPreferences
import com.data.app.databinding.ActivityWritePostBinding
import com.data.app.extension.community.WritePostState
import com.data.app.presentation.main.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@AndroidEntryPoint
class WritePostActivity : BaseActivity() {
    private lateinit var binding: ActivityWritePostBinding
    private val writePostViewModel: WritePostViewModel by viewModels()
    private var writeComplete = false
    @Inject
    lateinit var appPreferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinds()
        setting()
    }

    private fun initBinds() {
        binding = ActivityWritePostBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setting() {
        writeContent()
        showGallerys()
        clickBack()
    }

    private fun showGallerys() {
        val writePostGalleryAdapter = WritePostGalleryAdapter(
            clickGalleryIcon = { openGallery() },
            clickImage = { uri ->
                Timber.d("uri: $uri")
                with(binding.ivGallery) {
                    visibility = View.VISIBLE


                    val radiusPx = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 16f, resources.displayMetrics
                    )

                    binding.ivGallery.load(uri) {
                        transformations(RoundedCornersTransformation(radiusPx))
                    }
                }
            }
        )
        binding.rvGallerys.adapter = writePostGalleryAdapter
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED -> {
                writePostGalleryAdapter.getList(loadRecentImages())
            }

            else -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            }
        }
    }

    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // 권한 허용되면 이미지 로딩
                val images = loadRecentImages()
                (binding.rvGallerys.adapter as? WritePostGalleryAdapter)?.getList(images)
            } else {
                // 거부된 경우
                Timber.w("사용자가 권한을 거부했습니다.")
            }
        }


    private val pickImageLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let {
                    with(binding.ivGallery) {
                        visibility = View.VISIBLE
                        val radiusPx = TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 16f, resources.displayMetrics
                        )

                        binding.ivGallery.load(it) {
                            transformations(RoundedCornersTransformation(radiusPx))
                        }
                    }
                }
            }
        }

    private fun openGallery() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        pickImageLauncher.launch(gallery)
    }

    private fun loadRecentImages(): List<Uri> {
        val imageList = mutableListOf<Uri>()

        // 갤러리 진입용 아이템을 위해 맨 앞에 dummy Uri (나중에 따로 처리)
        imageList.add(Uri.EMPTY)

        val projection = arrayOf(MediaStore.Images.Media._ID)
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        this.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

            var count = 0
            while (cursor.moveToNext() && count < 6) {
                val id = cursor.getLong(idColumn)
                val contentUri = Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toString()
                )
                Timber.d("uri: $contentUri")
                imageList.add(contentUri)
                count++
            }
        }

        return imageList
    }

    private fun writeContent() {
        uploadPost()

        with(binding) {
            etPostWrite.addTextChangedListener {
                val text = it.toString()
                val hasInput = text.isNotBlank()

                btnComplete.isSelected = hasInput
                btnComplete.isEnabled = hasInput  // 클릭 자체도 막고 싶다면 이 줄 추가
            }

            btnComplete.setOnClickListener {
                if (btnComplete.isSelected) {
                    if(writeComplete) return@setOnClickListener
                    val drawable = binding.ivGallery.drawable
                    if (drawable == null) {
                        Timber.d("이미지 없음!")
                    }

                    writePostViewModel.writePost(appPreferences.getAccessToken()!!, etPostWrite.text.toString(),getImage())
                }
            }
        }
    }

    private fun getImage(): MultipartBody.Part?{
        val drawable = binding.ivGallery.drawable
        if (drawable == null) {
            Timber.e("사진 없음!")
            return null
        }
        val bitmap = (drawable as BitmapDrawable).bitmap

        val file = File.createTempFile("upload_", ".jpg", cacheDir)
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()

        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", file.name, requestBody)
        return imagePart
    }

    private fun uploadPost() {
        lifecycleScope.launch {
            writePostViewModel.writePostState.collect { writePostState ->
                when (writePostState) {
                    is WritePostState.Success -> {
                        writeComplete=true
                        Timber.d("write post success")
                        setResult(Activity.RESULT_OK)
                        finish()
                        overridePendingTransition(R.anim.stay, R.anim.slide_out_right)
                    }

                    is WritePostState.Loading -> {
                        Timber.d("write post loading")
                    }

                    is WritePostState.Error -> {
                        Timber.e("write post error")
                    }
                }
            }
        }
    }

    private fun clickBack() {
        binding.btnBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.stay, R.anim.slide_out_right)
        }

        onBackPressedDispatcher.addCallback(this) {
            finish()
            overridePendingTransition(R.anim.stay, R.anim.slide_out_right)
        }
    }
}