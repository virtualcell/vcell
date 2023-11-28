package org.vcell.restq.models;

public record AuthCodeResponse (String access_token, String id_token, String refresh_token) {
}
