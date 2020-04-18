package com.dreamfoxick.friendservice.service.vkclient.json.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArrayObject<R extends JsonMarker> {

    @JsonProperty(value = "response")
    private R[] response;
}
