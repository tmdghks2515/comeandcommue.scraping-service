package io.comeandcommue.scraping.common.loginUser;

import io.comeandcommue.scraping.common.enums.DeviceType;

public record UserPrincipal(
        String id,
        String nickname,
        String ipAddr,
        DeviceType deviceType,
        String userAgent
) { }
