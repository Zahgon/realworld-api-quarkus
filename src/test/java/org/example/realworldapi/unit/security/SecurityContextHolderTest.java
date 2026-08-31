package org.example.realworldapi.unit.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import org.example.realworldapi.infrastructure.web.security.annotation.Secured;
import org.example.realworldapi.infrastructure.web.security.context.EmptySecurityContext;
import org.example.realworldapi.infrastructure.web.security.context.SecurityContext;
import org.example.realworldapi.infrastructure.web.security.context.SecurityContextHolder;
import org.example.realworldapi.infrastructure.web.security.profile.Role;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.method.HandlerMethod;

public class SecurityContextHolderTest {

  @Secured({Role.ADMIN})
  static class SecuredController {
    public void inheritsClassAnnotation() {}

    @Secured(value = {Role.USER}, optional = true)
    public void optionalMethod() {}
  }

  static class PlainController {
    public void notSecured() {}
  }

  private HandlerMethod handlerMethod(Class<?> type, String methodName) throws Exception {
    Method method = type.getMethod(methodName);
    return new HandlerMethod(type.getDeclaredConstructor().newInstance(), method);
  }

  @Test
  public void shouldStoreAndReadTheSecurityContextOnTheRequest() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    SecurityContext securityContext = new EmptySecurityContext();
    SecurityContextHolder.set(request, securityContext);
    assertSame(securityContext, SecurityContextHolder.get(request));
  }

  @Test
  public void shouldReturnNullWhenNoSecurityContextWasStored() {
    assertNull(SecurityContextHolder.get(new MockHttpServletRequest()));
  }

  @Test
  public void shouldReturnNullWhenTheStoredAttributeIsNotASecurityContext() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setAttribute(SecurityContext.class.getName(), "not-a-security-context");
    assertNull(SecurityContextHolder.get(request));
  }

  @Test
  public void shouldResolveTheClassLevelAnnotationWhenTheMethodHasNone() throws Exception {
    HandlerMethod handlerMethod = handlerMethod(SecuredController.class, "inheritsClassAnnotation");
    assertTrue(SecurityContextHolder.isSecured(handlerMethod));
    assertFalse(SecurityContextHolder.isSecurityOptional(handlerMethod));
  }

  @Test
  public void shouldPreferTheMethodLevelAnnotation() throws Exception {
    HandlerMethod handlerMethod = handlerMethod(SecuredController.class, "optionalMethod");
    assertTrue(SecurityContextHolder.isSecured(handlerMethod));
    assertTrue(SecurityContextHolder.isSecurityOptional(handlerMethod));
  }

  @Test
  public void shouldNotBeSecuredWhenNeitherMethodNorClassIsAnnotated() throws Exception {
    HandlerMethod handlerMethod = handlerMethod(PlainController.class, "notSecured");
    assertFalse(SecurityContextHolder.isSecured(handlerMethod));
    assertFalse(SecurityContextHolder.isSecurityOptional(handlerMethod));
  }
}
