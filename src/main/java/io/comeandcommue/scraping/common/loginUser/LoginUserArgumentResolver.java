package io.comeandcommue.scraping.common.loginUser;

import io.comeandcommue.scraping.common.enums.DeviceType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {
    private final JwtTokenUtil jwt;

    @Override public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginUser.class)
                && parameter.getParameterType().isAssignableFrom(UserPrincipal.class);
    }

    @Override public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer container,
            NativeWebRequest request,
            WebDataBinderFactory factory
    ) {
        var req = (HttpServletRequest) request.getNativeRequest();
        String token = extractCookie(req, "__auth_token_");
        if (token == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        return toPrincipal(token, req);
    }

    private static String extractCookie(HttpServletRequest req, String name) {
        Cookie[] cookies = req.getCookies();
        if (cookies == null) return null;
        return Arrays.stream(cookies)
                .filter(c -> name.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    private UserPrincipal toPrincipal(String authToken, HttpServletRequest req) {
        String ip = RequestClientInfo.resolveClientIp(req);
        String ua = RequestClientInfo.resolveUserAgent(req);
        DeviceType dt = RequestClientInfo.resolveDeviceType(req);
        return new UserPrincipal(jwt.extractSubject(authToken), jwt.extractNickname(authToken), ip, dt, ua);
    }
}
