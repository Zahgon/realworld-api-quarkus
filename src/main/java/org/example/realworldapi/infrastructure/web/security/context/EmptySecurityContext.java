package org.example.realworldapi.infrastructure.web.security.context;

import java.security.Principal;

public class EmptySecurityContext implements SecurityContext {
  @Override
  public Principal getUserPrincipal() {
    return null;
  }

  @Override
  public boolean isUserInRole(String s) {
    return false;
  }
}
