package com.dreamfoxick.friendservice.service.vkclient.json.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExecuteObject<E extends RuntimeException> {

    private Object[] response;

    private E[] errors;
}
