package com.data.app.presentation.main.community.write

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.TypedValue
import android.view.View
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import coil3.load
import coil3.request.transformations
import coil3.transform.RoundedCornersTransformation
import com.data.app.R
import com.data.app.databinding.ActivityWritePostBinding
import com.data.app.presentation.main.BaseActivity
import timber.log.Timber

class WritePostActivity : BaseActivity() {
    private lateinit var binding: ActivityWritePostBinding

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