package org.example.sema.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Setter
@Entity
@Table(
        name = "application_user",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "password")
        }
)
@NoArgsConstructor
public class ApplicationUser implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Getter
    @Column(unique=true, nullable = false)
    @JsonIgnore
    private String password;

    @Getter
    @Column(unique=true, nullable = false)
    private String username;

    @Getter
    @Column(length = 100, nullable = false)
    private String email;

    @Getter
    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;

    @Getter
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    @ManyToMany
    @JoinTable(
            name = "user_device",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "device_id")
    )
    @Getter
    @Setter
    private Set<Device> devices = new HashSet<>();

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    public boolean isAccountNonExpired() {
        return true;
    }

    public boolean isAccountNonLocked() {
        return true;
    }

    public boolean isCredentialsNonExpired() {
        return true;
    }

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
