package org.example.realworldapi.unit.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.lang.reflect.Method;
import org.example.realworldapi.infrastructure.web.exception.UnauthorizedException;
import org.example.realworldapi.infrastructure.web.provider.TokenProvider;
import org.example.realworldapi.infrastructure.web.security.annotation.Secured;
import org.example.realworldapi.infrastructure.web.security.context.DecodedJWTSecurityContext;
import org.example.realworldapi.infrastructure.web.security.context.EmptySecurityContext;
import org.example.realworldapi.infrastructure.web.security.context.SecurityContextHolder;
import org.example.realworldapi.infrastructure.web.security.interceptor.AuthenticationInterceptor;
import org.example.realworldapi.infrastructure.web.security.profile.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;

public class AuthenticationInterceptorTest {

  @Secured({Role.USER})
  static class SecuredController {
    public void requiredSecurity() {}

    @Secured(value = {Role.USER}, optional = true)
    public void optionalSecurity() {}
  }

  static class PlainController {
    public void notSecured() {}
  }

  private TokenProvider tokenProvider;
  private AuthenticationInterceptor interceptor;
  private MockHttpServletRequest request;
  private MockHttpServletResponse response;

  @BeforeEach
  public void beforeEach() {
    tokenProvider = mock(TokenProvider.class);
    interceptor = new AuthenticationInterceptor(tokenProvider);
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
  }

  private HandlerMethod handlerMethod(Class<?> type, String methodName) throws Exception {
    Method method = type.getMethod(methodName);
    return new HandlerMethod(type.getDeclaredConstructor().newInstance(), method);
  }

  @Test
  public void shouldSkipWhenTheHandlerIsNotAHandlerMethod() throws Exception {
    assertTrue(interceptor.preHandle(request, response, new Object()));
    assertNull(SecurityContextHolder.get(request));
  }

  @Test
  public void shouldSkipWhenTheHandlerMethodIsNotSecured() throws Exception {
    assertTrue(interceptor.preHandle(request, response, handlerMethod(PlainController.class, "notSecured")));
    assertNull(SecurityContextHolder.get(request));
  }

  @Test
  public void shouldStoreADecodedJwtContextForAValidToken() throws Exception {
    DecodedJWT decodedJWT = mock(DecodedJWT.class);
    when(tokenProvider.verify("valid-token")).thenReturn(decodedJWT);
    request.addHeader(HttpHeaders.AUTHORIZATION, "Token valid-token");

    assertTrue(
        interceptor.preHandle(request, response, handlerMethod(SecuredController.class, "requiredSecurity")));
    assertInstanceOf(DecodedJWTSecurityContext.class, SecurityContextHolder.get(request));
  }

  @Test
  public void shouldRespondUnauthorizedWhenTheTokenCannotBeVerified() throws Exception {
    when(tokenProvider.verify(anyString())).thenThrow(new JWTVerificationException("invalid"));
    request.addHeader(HttpHeaders.AUTHORIZATION, "Token broken-token");

    assertFalse(
        interceptor.preHandle(request, response, handlerMethod(SecuredController.class, "requiredSecurity")));
    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    assertEquals(HttpStatus.UNAUTHORIZED.getReasonPhrase(), response.getContentAsString());
  }

  @Test
  public void shouldThrowUnauthorizedWhenTheHeaderIsMissingAndSecurityIsRequired() throws Exception {
    HandlerMethod handlerMethod = handlerMethod(SecuredController.class, "requiredSecurity");
    assertThrows(
        UnauthorizedException.class, () -> interceptor.preHandle(request, response, handlerMethod));
    assertInstanceOf(EmptySecurityContext.class, SecurityContextHolder.get(request));
  }

  @Test
  public void shouldThrowUnauthorizedWhenTheHeaderUsesAnotherScheme() throws Exception {
    request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer some-token");
    HandlerMethod handlerMethod = handlerMethod(SecuredController.class, "requiredSecurity");
    assertThrows(
        UnauthorizedException.class, () -> interceptor.preHandle(request, response, handlerMethod));
  }

  @Test
  public void shouldAllowAnonymousAccessWhenSecurityIsOptional() throws Exception {
    assertTrue(
        interceptor.preHandle(request, response, handlerMethod(SecuredController.class, "optionalSecurity")));
    assertInstanceOf(EmptySecurityContext.class, SecurityContextHolder.get(request));
  }
}
