package com.dreamfoxick.friendservice.service.vkclient.json.common.objects;

import com.dreamfoxick.friendservice.service.vkclient.json.common.JsonMarker;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScreenNameJson implements JsonMarker, Serializable {

    @JsonProperty(value = "object_id")
    private Integer id;

    @JsonProperty(value = "type")
    private String type;
}
