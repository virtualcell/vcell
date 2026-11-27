package org.vcell.api.dto;

import java.util.List;
/** RFC 9457 Problem Details (style guide 9.1). */
public record Problem(String type, String title, int status, String detail, String instance, String requestId,
                      List<FieldError> errors) { }
