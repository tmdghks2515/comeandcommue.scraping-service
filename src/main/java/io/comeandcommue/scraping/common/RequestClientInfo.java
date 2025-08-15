package io.comeandcommue.scraping.common;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RequestClientInfo {

    private RequestClientInfo() {}

    public static String resolveClientIp(HttpServletRequest req) {
        // 신뢰 가능한 프록시 뒤라면 아래 헤더들이 세팅됨
        String xff = header(req, "X-Forwarded-For");
        if (xff != null) {
            // "client, proxy1, proxy2" 형태 → 첫 번째가 실제 클라이언트
            int idx = xff.indexOf(',');
            return (idx > 0 ? xff.substring(0, idx) : xff).trim();
        }
        String realIp = header(req, "X-Real-IP");
        if (realIp != null) return realIp;

        String cfIp = header(req, "CF-Connecting-IP"); // Cloudflare
        if (cfIp != null) return cfIp;

        String fwd = header(req, "Forwarded"); // RFC 7239: for=...
        if (fwd != null) {
            Matcher m = Pattern.compile("for=\"?([^;,\"]+)").matcher(fwd);
            if (m.find()) return m.group(1);
        }
        return req.getRemoteAddr();
    }

    public static String resolveUserAgent(HttpServletRequest req) {
        return header(req, "User-Agent");
    }

    public static DeviceType resolveDeviceType(HttpServletRequest req) {
        // 1) 앱/프론트에서 의도적으로 보내주는 헤더가 있으면 우선 사용
        String override = header(req, "X-Device-Type"); // ANDROID/IOS/WEB
        if (override != null) {
            try {
                return DeviceType.valueOf(override.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ignore) { /* fall through */ }
        }

        // 2) User-Agent 기반 추정 (간단/실용)
        String ua = header(req, "User-Agent");
        if (ua == null) return DeviceType.WEB;
        String l = ua.toLowerCase(Locale.ROOT);

        if (l.contains("android")) return DeviceType.ANDROID;
        if (l.contains("iphone") || l.contains("ipad") || l.contains("ipod")
                || l.contains("crios") /* Chrome on iOS */
                || l.contains("cfnetwork") /* iOS 네트워크 스택 */) {
            return DeviceType.IOS;
        }
        return DeviceType.WEB;
    }

    private static String header(HttpServletRequest req, String name) {
        String v = req.getHeader(name);
        return (v == null || v.isBlank()) ? null : v;
    }
}
