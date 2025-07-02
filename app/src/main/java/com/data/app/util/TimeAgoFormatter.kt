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
        return try {
            // 1. ISO 8601 문자열을 Instant 객체로 파싱 (UTC 기준)
            val instant = Instant.parse(isoDateTimeString)

            // 2. Instant를 시스템 기본 시간대의 LocalDateTime으로 변환 (선택 사항, 필요에 따라)
            //    현재 시간과의 차이를 계산할 때는 UTC Instant를 그대로 사용해도 무방합니다.
            // val pastDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

            // 3. 현재 시간 (UTC Instant)
            val now = Instant.now()

            // 4. 두 시간 사이의 차이 계산
            val duration = Duration.between(instant, now)

            when {
                duration.isNegative -> {
                    // 과거 시간이 현재 시간보다 미래인 경우 (데이터 오류 등)
                    // 여기서는 간단히 "방금 전" 또는 특정 문자열을 반환하거나,
                    // 파싱된 시간을 그대로 보여줄 수도 있습니다.
                    "방금 전" // 또는 적절한 처리
                }
                duration.seconds < 120 * 60 -> { // 0초 ~ 1시간 59분 59초 (119분 59초) -> 2시간 미만
                    "1시간 전"
                }
                duration.toHours() < 24 -> { // 2시간 ~ 23시간
                    "${duration.toHours()}시간 전"
                }
                else -> { // 24시간 이상
                    // ChronoUnit.DAYS.between()은 날짜 경계를 기준으로 일 수를 계산합니다.
                    // 정확한 "만 n일"을 원하면 duration.toDays()를 사용할 수 있습니다.
                    // 여기서는 단순하게 duration.toDays()를 사용합니다.
                    val days = duration.toDays()
                    if (days <= 0) "1일 전" else "${days}일 전" // 만약 정확히 24시간 차이라도 1일 전으로 표시
                }
            }
        } catch (e: Exception) {
            // DateTimeParseException 등 발생 시 원본 문자열 반환 또는 오류 로깅
            // Timber.e(e, "Failed to parse or format time: $isoDateTimeString")
            isoDateTimeString // 파싱 실패 시 원본 반환
        }
    }
}