package com.pragma.plazoleta.domain.spi;

import java.util.UUID;


public interface ISecurityContextPort {
    String getRoleOfUserAutenticated();
    UUID getUserIdOfUserAutenticated();
}
