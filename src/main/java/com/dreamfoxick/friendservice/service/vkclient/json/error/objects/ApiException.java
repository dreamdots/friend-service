package com.dreamfoxick.friendservice.service.vkclient.json.error.objects;

import com.dreamfoxick.friendservice.service.vkclient.json.error.ErrorJsonMarker;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiException extends RuntimeException implements ErrorJsonMarker {

    @JsonProperty(value = "error_code")
    private int code;

    @JsonProperty(value = "error_msg")
    private String message;

    @JsonProperty(value = "method")
    private String method;
}
