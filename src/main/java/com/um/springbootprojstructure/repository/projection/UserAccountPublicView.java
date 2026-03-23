package com.um.springbootprojstructure.repository.projection;

import java.util.UUID;

public interface UserAccountPublicView {
    UUID getId();
    String getUsername();
    String getEmail();
    boolean isActive();
}
