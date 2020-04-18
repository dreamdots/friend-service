package com.dreamfoxick.friendservice.data.mongo.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@Document
@NoArgsConstructor
@EqualsAndHashCode(exclude = "ID")
@AllArgsConstructor
@JsonTypeName("tracked_user")
public class TrackedUserEntity implements EntityMarker<Integer> {

    @Id
    @JsonProperty(value = "tracked_ID")
    private Integer ID;

    @Field(name = "first_name")
    @JsonProperty(value = "first_name")
    private String firstName;

    @Field(name = "last_name")
    @JsonProperty(value = "last_name")
    private String lastName;

    @Field(name = "is_closed")
    @JsonProperty(value = "is_closed")
    private boolean closed;

    @Field(name = "deactivated")
    @JsonProperty(value = "deactivated")
    private String deactivated;

    @Field(name = "sex")
    @JsonProperty(value = "sex")
    private int sex;

    @Field(name = "friends")
    @JsonProperty(value = "friends")
    private List<FriendEntity> friends;
}
