package org.example.realworldapi.unit.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.security.Principal;
import org.example.realworldapi.infrastructure.web.security.context.EmptySecurityContext;
import org.example.realworldapi.infrastructure.web.security.context.SecurityContext;
import org.example.realworldapi.infrastructure.web.security.context.SecurityContextHolder;
import org.example.realworldapi.infrastructure.web.security.resolver.SecurityContextArgumentResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

class SecurityContextArgumentResolverTest {

  private SecurityContextArgumentResolver securityContextArgumentResolver;
  private MockHttpServletRequest request;
  private ServletWebRequest webRequest;

  @BeforeEach
  void beforeEach() {
    securityContextArgumentResolver = new SecurityContextArgumentResolver();
    request = new MockHttpServletRequest();
    webRequest = new ServletWebRequest(request);
  }

  @Test
  void shouldSupportSecurityContextParameter() throws NoSuchMethodException {
    assertThat(securityContextArgumentResolver.supportsParameter(parameter("withContext"))).isTrue();
  }

  @Test
  void shouldNotSupportOtherParameterTypes() throws NoSuchMethodException {
    assertThat(securityContextArgumentResolver.supportsParameter(parameter("withString")))
        .isFalse();
  }

  @Test
  void shouldResolveStoredSecurityContext() throws Exception {
    SecurityContext storedSecurityContext = securityContext();
    SecurityContextHolder.set(request, storedSecurityContext);

    Object resolved =
        securityContextArgumentResolver.resolveArgument(
            parameter("withContext"), null, webRequest, null);

    assertThat(resolved).isSameAs(storedSecurityContext);
  }

  @Test
  void shouldResolveEmptySecurityContextWhenNothingStored() throws Exception {
    Object resolved =
        securityContextArgumentResolver.resolveArgument(
            parameter("withContext"), null, webRequest, null);

    assertThat(resolved).isInstanceOf(EmptySecurityContext.class);
  }

  private SecurityContext securityContext() {
    return new SecurityContext() {

      @Override
      public Principal getUserPrincipal() {
        return () -> "principal";
      }

      @Override
      public boolean isUserInRole(String role) {
        return true;
      }
    };
  }

  private MethodParameter parameter(String methodName) throws NoSuchMethodException {
    Class<?> parameterType =
        "withContext".equals(methodName) ? SecurityContext.class : String.class;
    return new MethodParameter(
        SampleController.class.getMethod(methodName, parameterType), 0);
  }

  static class SampleController {

    public void withContext(SecurityContext securityContext) {}

    public void withString(String value) {}
  }
}
