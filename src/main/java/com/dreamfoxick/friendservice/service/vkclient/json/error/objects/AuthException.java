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
public class AuthException extends RuntimeException implements ErrorJsonMarker {

    @JsonProperty(value = "error")
    private String error;

    @JsonProperty(value = "error_description")
    private String errorDescription;
}
