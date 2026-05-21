package medisync.common.util;

public final class StringUtils {
    private StringUtils() {}

    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static String maskEmail(String email) {
        if (isBlank(email)) return email;
        int index = email.indexOf("@");
        if (index == -1) return email;

        String firstEmail = email.substring(0, 2);
        String masking = "**";
        String domain = email.substring(index);
        return firstEmail + masking + domain;
    }
}
