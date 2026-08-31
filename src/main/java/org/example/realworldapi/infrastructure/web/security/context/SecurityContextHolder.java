package org.example.realworldapi.infrastructure.web.security.context;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import org.example.realworldapi.infrastructure.web.security.annotation.Secured;
import org.springframework.web.method.HandlerMethod;

/**
 * Request scoped storage of the resolved {@link SecurityContext}, plus the {@link Secured}
 * resolution rules shared by the authentication and authorization interceptors.
 */
public final class SecurityContextHolder {

  private static final String ATTRIBUTE = SecurityContext.class.getName();

  private SecurityContextHolder() {}

  public static void set(HttpServletRequest request, SecurityContext securityContext) {
    request.setAttribute(ATTRIBUTE, securityContext);
  }

  public static SecurityContext get(HttpServletRequest request) {
    Object securityContext = request.getAttribute(ATTRIBUTE);
    return securityContext instanceof SecurityContext ? (SecurityContext) securityContext : null;
  }

  /**
   * Reproduces the JAX-RS {@code @NameBinding} activation of the original filters: they only ran
   * for resources annotated with {@link Secured} on the method or on the declaring class.
   */
  public static boolean isSecured(HandlerMethod handlerMethod) {
    return securedOf(handlerMethod) != null;
  }

  public static Secured securedOf(HandlerMethod handlerMethod) {
    Method method = handlerMethod.getMethod();
    Secured methodSecured = method.getAnnotation(Secured.class);
    if (methodSecured != null) {
      return methodSecured;
    }
    return handlerMethod.getBeanType().getAnnotation(Secured.class);
  }

  public static boolean isSecurityOptional(HandlerMethod handlerMethod) {
    Secured secured = securedOf(handlerMethod);
    return secured != null && secured.optional();
  }
}
