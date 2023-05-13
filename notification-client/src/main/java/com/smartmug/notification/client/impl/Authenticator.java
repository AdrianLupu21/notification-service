package com.smartmug.notification.client.impl;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.HttpHeaders;

public class Authenticator implements ClientRequestFilter {
    private final String token;

    public Authenticator(final String token) {
        this.token = token;
    }

    @Override
    public void filter(final ClientRequestContext requestContext){
        requestContext.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }
}
