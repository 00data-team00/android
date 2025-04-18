package com.data.app.presentation.explore

import androidx.lifecycle.ViewModel
import com.data.app.R
import com.data.app.data.DeadLine
import com.data.app.data.Program

class ExploreViewModel:ViewModel() {
    val mockDeadlineList = listOf(
        DeadLine(
            image = R.drawable.ic_image,
            deadline = "~ APR 25",
            price = "무료",
            title = "외국인을 위한 한글 입문반",
            address = "중구"
        ),
        DeadLine(
            image = R.drawable.ic_image,
            deadline = "~ APR 30",
            price = "₩5,000",
            title = "기초 회화를 위한 한글 수업",
            address = "영등포구"
        ),
        DeadLine(
            image = R.drawable.ic_image,
            deadline = "~ MAY 05",
            price = "무료",
            title = "한글과 한국문화 체험 교실",
            address = "성동구"
        ),
        DeadLine(
            image = R.drawable.ic_image,
            deadline = "~ MAY 12",
            price = "₩3,000",
            title = "외국인 유학생 대상 한글 쓰기 강의",
            address = "관악구"
        ),
        DeadLine(
            image = R.drawable.ic_image,
            deadline = "~ MAY 20",
            price = "무료",
            title = "한글로 배우는 일상 표현",
            address = "서대문구"
        ),
        DeadLine(
            image = R.drawable.ic_image,
            deadline = "~ MAY 28",
            price = "₩7,000",
            title = "한국어 초급자 한글 집중 캠프",
            address = "강남구"
        )
    )

    val freeProgramList = listOf(
        Program(
            title = "[무료] 외국인을 위한 한글 입문반",
            date = "2025-04-25",
            image = R.drawable.ic_image
        ),
        Program(
            title = "[무료] 한글과 한국문화 체험 교실",
            date = "2025-04-30",
            image = R.drawable.ic_image
        ),
        Program(
            title = "[무료] 일상생활 한글 표현 배우기",
            date = "2025-05-06",
            image = R.drawable.ic_image
        ),
        Program(
            title = "[무료] 한글 타자 및 기본 문서 작성",
            date = "2025-03-12",
            image = R.drawable.ic_image
        ),
        Program(
            title = "[무료] 어린이와 함께하는 한글 놀이터",
            date = "2025-03-20",
            image = R.drawable.ic_image
        ),
        Program(
            title = "[무료] 외국인 대상 한글 발음 특강",
            date = "2025-03-01",
            image = R.drawable.ic_image
        )
    )

    val paidProgramList = listOf(
        Program(
            title = "기초 회화를 위한 한글 수업",
            date = "2025-04-28",
            image = R.drawable.ic_image
        ),
        Program(
            title = "외국인 유학생 대상 한글 쓰기 강의",
            date = "2025-05-04",
            image = R.drawable.ic_image
        ),
        Program(
            title = "한국어 초급자 집중 캠프",
            date = "2025-05-10",
            image = R.drawable.ic_image
        ),
        Program(
            title = "한글 뉴스 읽기와 시사 토론",
            date = "2025-05-18",
            image = R.drawable.ic_image
        ),
        Program(
            title = "TOPIK 1 대비반 (기초)",
            date = "2025-03-26",
            image = R.drawable.ic_image
        ),
        Program(
            title = "한국 드라마로 배우는 실용 한글",
            date = "2025-03-03",
            image = R.drawable.ic_image
        )
    )

}