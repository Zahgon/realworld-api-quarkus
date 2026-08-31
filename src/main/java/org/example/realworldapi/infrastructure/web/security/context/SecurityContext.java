package org.example.realworldapi.infrastructure.web.security.context;

import java.security.Principal;

/**
 * Application owned replacement for {@code jakarta.ws.rs.core.SecurityContext}. Only the two
 * operations consumed by the application are declared.
 */
public interface SecurityContext {

  Principal getUserPrincipal();

  boolean isUserInRole(String role);
}
