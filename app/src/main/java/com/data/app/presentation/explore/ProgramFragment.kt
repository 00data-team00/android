package com.data.app.presentation.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.data.app.databinding.FragmentProgramBinding

class ProgramFragment : Fragment() {
    private var _binding: FragmentProgramBinding? = null
    private val binding: FragmentProgramBinding
        get() = requireNotNull(_binding) { "home fragment is null" }

    private val exploreViewModel: ExploreViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProgramBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setting()
    }

    private fun setting() {
        val contString =
            "<p style=\"line-height: 2;\">컴퓨터 기초</p><p style=\"line-height: 2;\">＊본 교육은 한국어로 진행됩니다</p><p style=\"line-height: 2;\">신청기간 : 2025.02.16.(일)~마감시까지</p><p style=\"line-height: 2;\">**선착순 마감</p><p style=\"line-height: 2;\">교육내용 : 윈도우, 인터넷활용 등 기초과정</p><p style=\"line-height: 2;\">교육대상 : 서울 거주 외국인주민 및 귀화자 9명</p><p style=\"line-height: 2;\">교육일시 : 2025.4.9.~5.28.(매주 수요일) 10:00~13:00</p><p style=\"line-height: 2;\">신청방법 : 방문접수(신분증 지참)</p><p style=\"line-height: 2;\">교육비 : 무료(단, 교재 개별 구매)</p><p style=\"line-height: 2;\">교육장소: 서울외국인주민센터 401호</p><p style=\"line-height: 2;\">(서울시 영등포구 도신로40)</p><p style=\"line-height: 2;\">문의 : 02-2229-4906, 임보람</p>"

        binding.wvProgram.settings.apply {
            javaScriptEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
            builtInZoomControls = false
            displayZoomControls = false
            textZoom = 120 // 글자 키우기 (100~150 사이 추천)
        }

        val cleanHtml = """
    <html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <style>
            body {
                font-family: 'Noto Sans KR', sans-serif;
                font-size: 16px;
                color: #222;
                padding: 16px;
                background-color: #ffffff;
                line-height: 1.8;
                word-break: break-word;
                overflow-wrap: break-word;
            }
            p, div, span {
                margin: 0;
                padding: 0;
                font-size: 16px;
                color: #222;
                line-height: 1.8;
            }
            a {
                color: #1E88E5;
                text-decoration: underline;
                word-break: break-word;
            }
            img {
                max-width: 100%;
                height: auto;
            }
        </style>
    </head>
    <body>
        $contString
    </body>
    </html>
""".trimIndent()


        binding.wvProgram.loadDataWithBaseURL(
            null,
            cleanHtml,
            "text/html",
            "utf-8",
            null
        )


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}