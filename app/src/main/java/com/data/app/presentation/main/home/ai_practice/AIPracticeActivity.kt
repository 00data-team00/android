package com.data.app.presentation.main.home.ai_practice

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.data.app.R
import com.data.app.databinding.ActivityAiPracticeBinding
import com.data.app.presentation.main.BaseActivity
import com.data.app.presentation.main.home.ai_practice.ai_chat.AIChatActivity
import com.data.app.presentation.main.home.ai_practice.previous_practice.PreviousPracticeActivity
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class AIPracticeActivity : BaseActivity() {
    private lateinit var binding: ActivityAiPracticeBinding

    private lateinit var aiAdapter: AIPracticeAdapter
    private val aiPracticeViewModel: AIPracticeViewModel by viewModels()

    private var currentTab = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinds()
        setting()
    }

    private fun initBinds() {
        binding = ActivityAiPracticeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setting() {
        val token = intent?.getStringExtra("accessToken")

        showList(token)
        clickPracticeRecord(token)
        clickBack()
    }

    private fun showList(token: String?) {
        aiAdapter = AIPracticeAdapter(this, clickPractice = { id->
            val intent = Intent(this, AIChatActivity::class.java)
            intent.putExtra("accessToken", token)
            intent.putExtra("topicId", id)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
        )
        binding.rvAiPractice.adapter = aiAdapter

        aiPracticeViewModel.essentialTopics.observe(this) { essential ->
            Timber.d("Observed essential topic list: size=${essential.size}")
            aiAdapter.getList(essential)

        }
        //aiAdapter.getList(aiPracticeViewModel.mockDailyList)

        aiPracticeViewModel.getAiTopics()
        setupTabs()
    }

    private fun setupTabs() {
        binding.tlAiPractice.apply {
            addTab(newTab().setText(getString(R.string.ai_practice_daily)))
            addTab(newTab().setText(getString(R.string.ai_practice_culture)))
            addTab(newTab().setText(getString(R.string.ai_practice_job)))
        }

        applyTabMargins()

        binding.tlAiPractice.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentTab = tab?.position ?: 0
                when (tab?.position) {
                    0 -> {
                        aiAdapter.changeCategory("essential")
                        aiAdapter.getList(aiPracticeViewModel.essentialTopics.value ?: emptyList())
                    }
                    1 -> {
                        aiAdapter.changeCategory("culture")
                        aiAdapter.getList(aiPracticeViewModel.cultureTopics.value ?: emptyList())
                    }
                    2 -> {
                        aiAdapter.changeCategory("work")
                        aiAdapter.getList(aiPracticeViewModel.businessTopics.value ?: emptyList())
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun applyTabMargins() {
        binding.tlAiPractice.post {
            val tabLayout = binding.tlAiPractice.getChildAt(0) as? ViewGroup ?: return@post
            for (i in 0 until tabLayout.childCount) {
                val tab = tabLayout.getChildAt(i)
                val layoutParams = tab.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.marginStart = 6.dpToPx()
                layoutParams.marginEnd = 8.dpToPx()
                tab.layoutParams = layoutParams
                tab.requestLayout()
            }
        }
    }

    private fun Int.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }

    private fun clickPracticeRecord(token: String?) {
        binding.btnShowPreviousPractice.setOnClickListener {
            val intent = Intent(this, PreviousPracticeActivity::class.java)
            intent.putExtra("accessToken", token)
            startActivity(intent)
            this.overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
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