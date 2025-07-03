package com.data.app.util

import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object TimeAgoFormatter {

    /**
     * ISO 8601 형식의 시간 문자열을 현재 시간 기준으로 "n시간 전" 또는 "n일 전"으로 변환합니다.
     * - 0분 ~ 1시간 59분 전: "1시간 전"
     * - 2시간 전 ~ 23시간 59분 전: "n시간 전"
     * - 24시간 이후: "n일 전"
     *
     * @param isoDateTimeString ISO 8601 형식의 UTC 시간 문자열 (예: "2025-07-02T15:05:52.606Z")
     * @return 변환된 시간 문자열 (예: "1시간 전", "5시간 전", "3일 전")
     *         파싱 실패 시 원본 문자열 반환
     */
    fun formatTimeAgo(isoDateTimeString: String): String {
        val patterns = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",  // 마이크로초 포함
            "yyyy-MM-dd'T'HH:mm:ss.SSS",     // 밀리초 포함
            "yyyy-MM-dd'T'HH:mm:ss"          // 초 단위까지
        )

        for (pattern in patterns) {
            try {
                val formatter = DateTimeFormatter.ofPattern(pattern)
                val localDateTime = LocalDateTime.parse(isoDateTimeString, formatter)
                val instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant()
                val now = Instant.now()
                val duration = Duration.between(instant, now)

                return when {
                    duration.isNegative -> "방금 전"
                    duration.toMinutes() < 120 -> "1시간 전"
                    duration.toHours() < 24 -> "${duration.toHours()}시간 전"
                    else -> "${duration.toDays()}일 전"
                }
            } catch (e: Exception) {
                continue // 다음 패턴으로 넘어감
            }
        }

        return isoDateTimeString // 모두 실패 시 원본 반환
    }

}