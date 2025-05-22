package org.vcell.restclient;

import java.net.http.HttpHeaders;

public class RuntimeApiException extends RuntimeException{
    public final String message;
    public final int httpCode;
    public final String path;
    public final HttpHeaders headers;

    public RuntimeApiException(String message, int httpCode, String path, HttpHeaders headers){
        this.message = message; this.httpCode = httpCode; this.path = path; this.headers = headers;
    }
}
