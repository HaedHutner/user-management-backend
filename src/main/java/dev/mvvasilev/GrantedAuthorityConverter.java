package dev.mvvasilev;

import dev.mvvasilev.entity.security.GrantedAuthorityImpl;

import javax.persistence.AttributeConverter;

public class GrantedAuthorityConverter implements AttributeConverter<GrantedAuthorityImpl, String> {
    @Override
    public String convertToDatabaseColumn(GrantedAuthorityImpl attribute) {
        return attribute.getAuthority();
    }

    @Override
    public GrantedAuthorityImpl convertToEntityAttribute(String dbData) {
        return new GrantedAuthorityImpl(dbData);
    }
}
