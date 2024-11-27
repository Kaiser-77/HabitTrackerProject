package com.project.habit_tracker_app.auth.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

//    @NotBlank(message = "Name field can't be blank")
//    private String name;

    @NotBlank(message = "UserName field can't be blank")
    @Column(unique = true, nullable = false, length = 50)
    @Size(min = 3, max = 50)
    private String userName;

    @NotBlank(message = "Email field can't be blank")
    @Column(unique = true)
    @Email(message = "Please enter email in proper format")
    private String email;

    @NotBlank(message = "Password field can't be blank")
    @Size(min = 5, message = "atleast 5 characters")
    private String password;

    @OneToOne(mappedBy = "user",cascade = CascadeType.REMOVE, orphanRemoval = true)
    private RefreshToken refreshToken;

    @OneToOne(mappedBy = "user",cascade = CascadeType.REMOVE, orphanRemoval = true)
    private ForgotPassword forgotPassword;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getUserName() {
        return userName;
    }

//    --------------------------------------------------------


}

