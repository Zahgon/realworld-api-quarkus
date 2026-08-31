package org.example.realworldapi.infrastructure.web.security.resolver;

import jakarta.servlet.http.HttpServletRequest;
import org.example.realworldapi.infrastructure.web.security.context.EmptySecurityContext;
import org.example.realworldapi.infrastructure.web.security.context.SecurityContext;
import org.example.realworldapi.infrastructure.web.security.context.SecurityContextHolder;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/** Spring MVC replacement for the JAX-RS {@code @Context SecurityContext} injection point. */
@Component
public class SecurityContextArgumentResolver implements HandlerMethodArgumentResolver {

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return SecurityContext.class.equals(parameter.getParameterType());
  }

  @Override
  public Object resolveArgument(
      MethodParameter parameter,
      ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest,
      WebDataBinderFactory binderFactory) {

    HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
    SecurityContext securityContext =
        request != null ? SecurityContextHolder.get(request) : null;
    return securityContext != null ? securityContext : new EmptySecurityContext();
  }
}
