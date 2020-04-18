package com.dreamfoxick.friendservice.data.mongo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@Builder
@ToString
@Document
@NoArgsConstructor
@EqualsAndHashCode(exclude = "ID")
@AllArgsConstructor
@JsonTypeName("token")
public class TokenEntity implements EntityMarker<Integer> {

    @Id
    @JsonIgnore
    private Integer ID;

    @JsonIgnore
    @Field(name = "token")
    private String token;

    @JsonIgnore
    @Field(name = "salt")
    private byte[] salt;

    @Field(name = "expires_in")
    @JsonProperty(value = "expires_in")
    private Integer expires;
}
