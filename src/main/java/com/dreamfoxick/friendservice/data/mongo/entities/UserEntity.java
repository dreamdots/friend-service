package com.dreamfoxick.friendservice.data.mongo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@Document(collection = "users")
@NoArgsConstructor
@EqualsAndHashCode(exclude = "ID")
@AllArgsConstructor
@JsonTypeName("user")
public class UserEntity implements EntityMarker<Integer> {

    @Id
    @JsonProperty(value = "ID")
    private Integer ID;

    @Field(name = "first_name")
    @JsonProperty(value = "first_name")
    private String firstName;

    @Field(name = "last_name")
    @JsonProperty(value = "last_name")
    private String lastName;

    @Field(name = "sex")
    @JsonProperty(value = "sex")
    private int sex;

    @Field(name = "tracked_users")
    @JsonProperty(value = "tracked_users")
    private List<TrackedUserEntity> trackedUsers;

    @JsonIgnore
    @Field(name = "token")
    private TokenEntity token;
}
