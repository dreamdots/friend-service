package com.dreamfoxick.friendservice.service.vkclient.json.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemsObject<R extends JsonMarker> implements JsonMarker, Serializable {

    @JsonProperty(value = "count")
    private Integer count;

    @JsonProperty(value = "items")
    private R[] items;
}
