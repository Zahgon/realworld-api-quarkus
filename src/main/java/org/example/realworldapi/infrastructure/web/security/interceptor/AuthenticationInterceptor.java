package org.example.realworldapi.infrastructure.web.security.interceptor;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.example.realworldapi.infrastructure.web.exception.UnauthorizedException;
import org.example.realworldapi.infrastructure.web.provider.TokenProvider;
import org.example.realworldapi.infrastructure.web.security.context.DecodedJWTSecurityContext;
import org.example.realworldapi.infrastructure.web.security.context.EmptySecurityContext;
import org.example.realworldapi.infrastructure.web.security.context.SecurityContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@AllArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

  private static final String AUTHORIZATION_HEADER_PREFIX = "Token ";

  private final TokenProvider tokenProvider;

  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

    if (!(handler instanceof HandlerMethod handlerMethod)
        || !SecurityContextHolder.isSecured(handlerMethod)) {
      return true;
    }

    String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (authorizationHeader != null
        && authorizationHeader.startsWith(AUTHORIZATION_HEADER_PREFIX)) {

      String token = authorizationHeader.replace(AUTHORIZATION_HEADER_PREFIX, "");

      try {

        DecodedJWT decodedJWT = tokenProvider.verify(token);

        SecurityContextHolder.set(
            request, new DecodedJWTSecurityContext(decodedJWT, tokenProvider));

      } catch (JWTVerificationException ex) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.TEXT_PLAIN_VALUE);
        response.getWriter().write(HttpStatus.UNAUTHORIZED.getReasonPhrase());
        return false;
      }
    } else {

      SecurityContextHolder.set(request, new EmptySecurityContext());

      if (!SecurityContextHolder.isSecurityOptional(handlerMethod)) {
        throw new UnauthorizedException();
      }
    }

    return true;
  }
}
