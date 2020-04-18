package com.dreamfoxick.friendservice.service.vkclient.json.common.objects;

import com.dreamfoxick.friendservice.service.vkclient.json.common.JsonMarker;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserTokenJson implements JsonMarker, Serializable {

    @JsonProperty(value = "user_id")
    private Integer ID;

    @JsonProperty(value = "access_token")
    private String accessToken;

    @JsonProperty(value = "expires_in")
    private Integer expires;
}
