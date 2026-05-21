package medisync.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateUtils {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private DateUtils() {

    }

    public static String formatDate(LocalDateTime dateTime) {
        // LocalDateTime을 String으로 변환
        return dateTime.format(FORMATTER);
    }

    public static LocalDateTime parseDate(String dateStr) {
        // String을 LocalDateTime으로 변환
        return LocalDateTime.parse(dateStr, FORMATTER);
    }
}
