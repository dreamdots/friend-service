package com.dreamfoxick.friendservice.service.vkclient.json.common.objects;

import com.dreamfoxick.friendservice.service.vkclient.json.common.JsonMarker;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserJson implements JsonMarker {

    @Id
    @JsonProperty(value = "id")
    private Integer ID;

    @JsonProperty(value = "first_name")
    private String firstName;

    @JsonProperty(value = "last_name")
    private String lastName;

    @JsonProperty(value = "is_closed")
    private boolean closed;

    @JsonProperty(value = "deactivated")
    private String deactivated;

    @JsonProperty(value = "sex")
    private int sex;
}
