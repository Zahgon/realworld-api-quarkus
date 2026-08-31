package org.example.realworldapi.infrastructure.web.security.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.example.realworldapi.infrastructure.web.exception.ForbiddenException;
import org.example.realworldapi.infrastructure.web.security.annotation.Secured;
import org.example.realworldapi.infrastructure.web.security.context.SecurityContext;
import org.example.realworldapi.infrastructure.web.security.context.SecurityContextHolder;
import org.example.realworldapi.infrastructure.web.security.profile.Role;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthorizationInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {

    if (!(handler instanceof HandlerMethod handlerMethod)
        || !SecurityContextHolder.isSecured(handlerMethod)) {
      return true;
    }

    if (!SecurityContextHolder.isSecurityOptional(handlerMethod)) {

      SecurityContext securityContext = SecurityContextHolder.get(request);

      List<Role> classRoles = extractRoles(handlerMethod.getBeanType());
      List<Role> methodRoles = extractRoles(handlerMethod.getMethod());

      try {
        if (methodRoles.isEmpty()) {
          checkPermissions(classRoles, securityContext);
        } else {
          checkPermissions(methodRoles, securityContext);
        }
      } catch (Exception ex) {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        return false;
      }
    }

    return true;
  }

  private void checkPermissions(List<Role> allowedRoles, SecurityContext securityContext) {
    if (!isAccessAllowed(allowedRoles, securityContext)) {
      throw new ForbiddenException();
    }
  }

  private boolean isAccessAllowed(List<Role> allowedRoles, SecurityContext securityContext) {
    for (Role allowedRole : allowedRoles) {
      if (securityContext.isUserInRole(allowedRole.name())) {
        return true;
      }
    }
    return false;
  }

  private List<Role> extractRoles(AnnotatedElement annotatedElement) {
    if (annotatedElement == null) {
      return new LinkedList<>();
    } else {
      Secured secured = annotatedElement.getAnnotation(Secured.class);
      if (secured == null) {
        return new LinkedList<>();
      } else {
        return Arrays.asList(secured.value());
      }
    }
  }
}
