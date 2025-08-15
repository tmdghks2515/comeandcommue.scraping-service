package io.comeandcommue.scraping.common;

public record UserPrincipal(
        String id,
        String nickname,
        String ipAddr,
        DeviceType deviceType,
        String userAgent
) { }
