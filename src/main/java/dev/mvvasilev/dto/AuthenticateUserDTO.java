package dev.mvvasilev.dto;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.Objects;

public class AuthenticateUserDTO {

    @NotEmpty
    @Email
    private String email;

    @NotEmpty
    @Length(min = 8, max = 32)
    private String rawPassword;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRawPassword() {
        return rawPassword;
    }

    public void setRawPassword(String rawPassword) {
        this.rawPassword = rawPassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthenticateUserDTO that = (AuthenticateUserDTO) o;
        return Objects.equals(email, that.email) &&
                Objects.equals(rawPassword, that.rawPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, rawPassword);
    }
}
