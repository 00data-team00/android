package com.data.app.presentation.main.my

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.CircleCropTransformation
import com.data.app.BuildConfig
import com.data.app.R
import com.data.app.data.shared_preferences.AppPreferences
import com.data.app.databinding.FragmentMyBinding
import com.data.app.extension.EditProfileState
import com.data.app.extension.MyState
import com.data.app.presentation.login.LoginActivity
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class MyFragment : Fragment() {
    private var _binding: FragmentMyBinding? = null
    private val binding: FragmentMyBinding
        get() = requireNotNull(_binding) { "home fragment is null" }
    private val myViewModel: MyViewModel by viewModels()
    private lateinit var myAdapter: _root_ide_package_.com.data.app.presentation.main.my.MyAdapter
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>

    companion object {
        private const val GALLERY_PERMISSION_CODE = 101
    }

    @Inject
    lateinit var appPreferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 갤러리에서 이미지 선택하기 위한 launcher
        galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val selectedImageUri = result.data?.data
                    selectedImageUri?.let { uri ->
                        Timber.d("갤러리에서 선택된 URI: $uri")
                        startCrop(uri)  // 선택된 이미지 크롭 시작
                    }
                }
            }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setting()
    }

    private fun setting() {
        showPosts()
        makeList()
        clickFollow()
        clickQuit()
    }

    private fun showPosts() {
        lifecycleScope.launch {
            myViewModel.myState.collect { myState ->
                when (myState) {
                    is MyState.Success -> {
                        Timber.d("myState is success")
                        showProfile()
                        myAdapter =
                            _root_ide_package_.com.data.app.presentation.main.my.MyAdapter(clickPost = { post ->
                                val action =
                                    MyFragmentDirections.actionMyFragmentToMyPostDetailFragment(post)
                                findNavController().navigate(action)
                            })
                        binding.rvPosts.adapter = myAdapter
                        myAdapter.getList(myState.response)
                    }

                    is MyState.Loading -> {
                        Timber.d("myState is loading")
                    }

                    is MyState.Error -> {
                        Timber.d("myState is error")
                    }
                }
            }
        }

        binding.tvId.text = "kkuming"
    }

    private fun showProfile() {
        lifecycleScope.launch {
            myViewModel.myProfileState.collect { myProfileState ->
                when (myProfileState) {
                    is com.data.app.extension.MyProfileState.Success -> {
                        Timber.d("myProfileState is success")
                        with(binding) {
                            val imageUrl = BuildConfig.BASE_URL.removeSuffix("/") + myProfileState.response.profileImage
                            // val resourceId = resources.getIdentifier("ic_profile", "drawable", requireContext().packageName)
                            ivProfile.load(imageUrl) {
                                transformations(CircleCropTransformation())
                                placeholder(R.drawable.ic_profile)
                                fallback(R.drawable.ic_profile) // profile이 null일 때 기본 이미지 표시
                            }
                            tvName.text = myProfileState.response.name
                            tvCountry.text = myProfileState.response.nationNameKo
                            tvPostCount.text = myProfileState.response.postCount.toString()
                            tvFollowerCount.text = myProfileState.response.followerCount.toString()
                            tvFollowingCount.text = myProfileState.response.postCount.toString()

                            btnEdit.setOnClickListener {
                                Timber.d("편집 버튼 클릭됨!")
                                checkGalleryPermissionAndOpenPicker()
                            }
                        }
                    }

                    is com.data.app.extension.MyProfileState.Loading -> {
                        Timber.d("myProfileState is loading")
                    }

                    is com.data.app.extension.MyProfileState.Error -> {
                        Timber.d("myProfileState is error")
                    }
                }
            }
        }

        myViewModel.getProfile(appPreferences.getAccessToken()!!)
    }

    private fun checkGalleryPermissionAndOpenPicker() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(permission), GALLERY_PERMISSION_CODE)
        } else {
            openGalleryPicker()
        }
    }

    private fun openGalleryPicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        galleryLauncher.launch(intent)
    }


    private fun startCrop(uri: Uri) {
        val destinationUri = Uri.fromFile(
            File(
                requireContext().cacheDir,
                "cropped_${System.currentTimeMillis()}.jpg"
            )
        )

        val options = UCrop.Options().apply {
            setCircleDimmedLayer(true)     // ✅ 원형 마스크
            setShowCropFrame(false)        // ❗ 프레임 안 보이게
            setShowCropGrid(false)         // ❗ 그리드 숨기기
            setHideBottomControls(true)    // ✅ 비율 고정이면 하단 버튼 숨기기 좋음
            setToolbarTitle("프로필 사진 자르기")
        }

        // UCrop 실행
        UCrop.of(uri, destinationUri)
            .withAspectRatio(1f, 1f) // ✅ 1:1 비율 고정
            .withOptions(options)
            .start(requireContext(), this)
    }

    private fun makeList() {
        myViewModel.getPosts()
    }

    private fun clickQuit() {
        binding.btnMenu.setOnClickListener {
            val dropdownInflater = LayoutInflater.from(context)
            val popupView = dropdownInflater.inflate(R.layout.dropdown_menu, null)

            val popupWindow = PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
            )

            popupWindow.showAsDropDown(binding.btnMenu, -175, 10)

            popupView.findViewById<TextView>(R.id.tv_logout).setOnClickListener {
                // 로그아웃시 필요한 기능 (사용자 DB정보 없애기 등)


                // dialog 로 한번 더 물어보기
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("로그아웃 확인")
                builder.setMessage("정말 로그아웃 하시겠습니까?")
                builder.setPositiveButton("예") { dialog, _ ->
                    dialog.dismiss()
                    popupWindow.dismiss()

                    // 로그인 정보 삭제
                    appPreferences.clearAccessToken() // AppPreferences에 정의된 토큰 삭제 메서드 호출
                    Timber.d("Access token cleared.")

                    // 화면 전환 (로그인 화면으로)
                    val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)

                    Toast.makeText(context, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                }
                builder.setNegativeButton("아니오") { dialog, _ ->
                    dialog.dismiss()
                    popupWindow.dismiss()
                }

                val dialog = builder.create()
                dialog.show()

                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
            }

            popupView.findViewById<TextView>(R.id.tv_quit).setOnClickListener {
                // Overlay 컨테이너를 보이게 설정
                val overlayContainer =
                    requireActivity().findViewById<FragmentContainerView>(R.id.fcv_quit)
                overlayContainer.visibility = View.VISIBLE

                // FragmentB 생성
                val fragmentB = QuitFragment()

                // MainActivity의 supportFragmentManager를 사용하여 fcv_overlay에 replace
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fcv_quit, fragmentB)
                    .addToBackStack(null) // 필요 시 뒤로가기 지원
                    .commit()

                popupWindow.dismiss()
            }
        }
    }

    private fun clickFollow() {
        listOf(
            binding.vFollower to "follower",
            binding.vFollowing to "following"
        ).forEach { (view, title) ->
            view.setOnClickListener {
                val action = MyFragmentDirections.actionMyFragmentToFollowFragment(title)
                findNavController().navigate(action)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == GALLERY_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGalleryPicker()
            } else {
                Toast.makeText(requireContext(), "갤러리 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Timber.d("resultCode: $resultCode, requestCode: $requestCode")
        if (requestCode == UCrop.REQUEST_CROP && resultCode == Activity.RESULT_OK) {
            val resultUri = UCrop.getOutput(data!!)  // 크롭된 이미지 URI 받기
            resultUri?.let {
                Timber.d("크롭된 이미지 URI: $it")

                lifecycleScope.launch {
                    myViewModel.editProfileState.collect { state ->
                        when (state) {
                            is EditProfileState.Success -> {
                                // 크롭된 이미지를 ivProfile에 원형으로 로드
                                binding.ivProfile.load(it) {
                                    transformations(CircleCropTransformation()) // 원형 크롭
                                    placeholder(R.drawable.ic_profile)  // 로딩 중 이미지
                                    error(R.drawable.ic_profile)  // 오류 시 이미지
                                }
                            }

                            is EditProfileState.Loading -> {}
                            is EditProfileState.Error -> {
                                Timber.e("크롭 에러 발생: ${state.message}")
                                binding.ivProfile.load(R.drawable.ic_profile) {
                                    transformations(CircleCropTransformation())
                                }
                            }
                        }
                    }
                }

                myViewModel.editProfile(appPreferences.getAccessToken()!!, createImagePart(it))
            }
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == UCrop.RESULT_ERROR) {
            val error = UCrop.getError(data!!)
            Timber.e("크롭 에러 발생: $error")
            Toast.makeText(requireContext(), "이미지 크롭에 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createImagePart(uri: Uri): MultipartBody.Part {
        val contentResolver = requireContext().contentResolver

        val inputStream = contentResolver.openInputStream(uri)
            ?: throw IllegalStateException("InputStream 열기 실패")

        val tempFile = File.createTempFile("profile_", ".jpg", requireContext().cacheDir)
        tempFile.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }

        val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("image", tempFile.name, requestFile)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}