package com.dreamfoxick.friendservice.service.vkclient.json.error;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SingleError<R extends RuntimeException> {

    @JsonProperty(value = "error")
    private R error;
}
