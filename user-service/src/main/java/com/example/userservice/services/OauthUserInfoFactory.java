package com.example.userservice.services;


import com.example.commonmodule.enums.AuthProvider;

@FunctionalInterface
public interface OauthUserInfoFactory {
    OauthUserInfoHandler getOauthUserInfoHandler(AuthProvider provider, HandleUserProvider handleUserProvider);

}
