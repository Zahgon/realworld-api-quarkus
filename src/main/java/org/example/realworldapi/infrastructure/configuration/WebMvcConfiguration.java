package org.example.realworldapi.infrastructure.configuration;

import java.util.List;
import lombok.AllArgsConstructor;
import org.example.realworldapi.infrastructure.web.security.interceptor.AuthenticationInterceptor;
import org.example.realworldapi.infrastructure.web.security.interceptor.AuthorizationInterceptor;
import org.example.realworldapi.infrastructure.web.security.resolver.SecurityContextArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Registers the Spring MVC replacements for the JAX-RS request filters and for the
 * {@code @Context SecurityContext} injection point. The authentication interceptor is registered
 * before the authorization one, mirroring the {@code Priorities.AUTHENTICATION} /
 * {@code Priorities.AUTHORIZATION} ordering of the original providers.
 */
@Configuration
@AllArgsConstructor
public class WebMvcConfiguration implements WebMvcConfigurer {

  private final AuthenticationInterceptor authenticationInterceptor;
  private final AuthorizationInterceptor authorizationInterceptor;
  private final SecurityContextArgumentResolver securityContextArgumentResolver;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(authenticationInterceptor).order(0);
    registry.addInterceptor(authorizationInterceptor).order(1);
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(securityContextArgumentResolver);
  }
}
