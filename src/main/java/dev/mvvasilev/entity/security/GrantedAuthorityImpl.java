package dev.mvvasilev.entity.security;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class GrantedAuthorityImpl implements GrantedAuthority {

    private String authority;

    public GrantedAuthorityImpl() {
    }

    public GrantedAuthorityImpl(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GrantedAuthorityImpl that = (GrantedAuthorityImpl) o;
        return Objects.equals(authority, that.authority);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authority);
    }
}
