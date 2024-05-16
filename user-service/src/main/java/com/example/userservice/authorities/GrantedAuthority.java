package com.example.userservice.authorities;

import java.io.Serializable;

public interface GrantedAuthority extends Serializable {
    String getAuthority();
}
