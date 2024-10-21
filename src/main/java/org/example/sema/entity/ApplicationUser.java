package org.example.sema.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@AllArgsConstructor
@Getter
@Setter
@Document(indexName = "application_user")
@NoArgsConstructor
public class ApplicationUser implements UserDetails {

    @Id
    private String id;

    @Field(type = FieldType.Text, index = false)
    @JsonIgnore
    private String password;

    @Field(type = FieldType.Keyword)
    private String username;

    @Field(type = FieldType.Keyword)
    private String email;

    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private Date createdAt = new Date();

    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private Date updatedAt = new Date();

    @Field(type = FieldType.Nested)
    @JsonBackReference
    private Set<String> deviceIds = new HashSet<>();

    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return "ApplicationUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }
}
