package org.example.realworldapi.unit.security;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Arrays;
import org.example.realworldapi.infrastructure.web.security.annotation.Secured;
import org.example.realworldapi.infrastructure.web.security.context.SecurityContext;
import org.example.realworldapi.infrastructure.web.security.context.SecurityContextHolder;
import org.example.realworldapi.infrastructure.web.security.interceptor.AuthorizationInterceptor;
import org.example.realworldapi.infrastructure.web.security.profile.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;

class AuthorizationInterceptorTest {

  private AuthorizationInterceptor authorizationInterceptor;
  private MockHttpServletRequest request;
  private MockHttpServletResponse response;

  @BeforeEach
  void beforeEach() {
    authorizationInterceptor = new AuthorizationInterceptor();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
  }

  @Test
  void shouldSkipWhenHandlerIsNotAHandlerMethod() throws Exception {
    assertThat(authorizationInterceptor.preHandle(request, response, new Object())).isTrue();
  }

  @Test
  void shouldSkipWhenHandlerMethodIsNotSecured() throws Exception {
    HandlerMethod handlerMethod = handlerMethod(new UnsecuredController(), "unsecured");
    assertThat(authorizationInterceptor.preHandle(request, response, handlerMethod)).isTrue();
  }

  @Test
  void shouldSkipRoleCheckWhenSecurityIsOptional() throws Exception {
    HandlerMethod handlerMethod = handlerMethod(new OptionalController(), "optional");

    assertThat(authorizationInterceptor.preHandle(request, response, handlerMethod)).isTrue();
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);
  }

  @Test
  void shouldAllowAccessWhenMethodRoleMatches() throws Exception {
    SecurityContextHolder.set(request, securityContextWithRoles(Role.USER));
    HandlerMethod handlerMethod = handlerMethod(new RolesController(), "userOnly");

    assertThat(authorizationInterceptor.preHandle(request, response, handlerMethod)).isTrue();
  }

  @Test
  void shouldRejectWithForbiddenWhenMethodRoleDoesNotMatch() throws Exception {
    SecurityContextHolder.set(request, securityContextWithRoles(Role.USER));
    HandlerMethod handlerMethod = handlerMethod(new RolesController(), "adminOnly");

    assertThat(authorizationInterceptor.preHandle(request, response, handlerMethod)).isFalse();
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_FORBIDDEN);
  }

  @Test
  void shouldFallBackToClassRolesWhenMethodDeclaresNone() throws Exception {
    SecurityContextHolder.set(request, securityContextWithRoles(Role.ADMIN));
    HandlerMethod handlerMethod = handlerMethod(new ClassRolesController(), "inheritsClassRoles");

    assertThat(authorizationInterceptor.preHandle(request, response, handlerMethod)).isTrue();
  }

  @Test
  void shouldRejectWhenClassRolesDoNotMatch() throws Exception {
    SecurityContextHolder.set(request, securityContextWithRoles(Role.USER));
    HandlerMethod handlerMethod = handlerMethod(new ClassRolesController(), "inheritsClassRoles");

    assertThat(authorizationInterceptor.preHandle(request, response, handlerMethod)).isFalse();
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_FORBIDDEN);
  }

  @Test
  void shouldRejectWhenSecurityContextIsAbsent() throws Exception {
    HandlerMethod handlerMethod = handlerMethod(new RolesController(), "userOnly");

    assertThat(authorizationInterceptor.preHandle(request, response, handlerMethod)).isFalse();
    assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_FORBIDDEN);
  }

  private SecurityContext securityContextWithRoles(Role... roles) {
    return new SecurityContext() {

      @Override
      public Principal getUserPrincipal() {
        return () -> "principal";
      }

      @Override
      public boolean isUserInRole(String role) {
        return Arrays.stream(roles).anyMatch(current -> current.name().equals(role));
      }
    };
  }

  private HandlerMethod handlerMethod(Object bean, String methodName) throws NoSuchMethodException {
    return new HandlerMethod(bean, bean.getClass().getMethod(methodName));
  }

  static class UnsecuredController {
    public void unsecured() {}
  }

  static class OptionalController {
    @Secured(optional = true)
    public void optional() {}
  }

  static class RolesController {

    @Secured({Role.USER})
    public void userOnly() {}

    @Secured({Role.ADMIN})
    public void adminOnly() {}
  }

  @Secured({Role.ADMIN})
  static class ClassRolesController {
    @Secured
    public void inheritsClassRoles() {}
  }
}
