package org.myjtools.imconfig.internal;


import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.myjtools.imconfig.ConfigException;
import org.myjtools.imconfig.PropertyDefinition;
import org.myjtools.imconfig.types.internal.PropertyTypeFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class PropertyDefinitionParser {

    private final YAMLMapper yaml = new YAMLMapper();
    private final PropertyTypeFactory typeFactory = new PropertyTypeFactory();


    @SuppressWarnings("unchecked")
    Collection<PropertyDefinition> read (Reader reader) {
        try {
            Map<String, Map<String, Object>> map = yaml.readValue(reader, HashMap.class);
            return map.entrySet().stream().map(this::parseDefinition).toList();
        } catch (IOException e) {
            throw new ConfigException(e);
        }
    }


    @SuppressWarnings("unchecked")
    public Collection<PropertyDefinition> read(InputStream inputStream) {
        try {
            Map<String, Map<String, Object>> map = yaml.readValue(inputStream, HashMap.class);
            return map.entrySet().stream().map(this::parseDefinition).toList();
        } catch (IOException e) {
            throw new ConfigException(e);
        }
    }



    @SuppressWarnings("unchecked")
    private PropertyDefinition parseDefinition(Entry<String, Map<String, Object>> entry) {
        try {
            var definition = entry.getValue();
            return PropertyDefinition.builder()
                .property(entry.getKey())
                .description((String) definition.get("description"))
                .required((Boolean)definition.get("required"))
                .defaultValue(toString(definition.get("defaultValue")))
                .propertyType(typeFactory.create(
                    (String)definition.get("type"),
                    (Map<String, Object>) definition.get("constraints")
                ))
                .build();
        } catch (RuntimeException e) {
            throw new ConfigException(
                "Bad configuration of property '"+entry.getKey()+"' : "+e.getMessage(), e
            );
        }
    }


    private String toString(Object object) {
        return object == null ? null : object.toString();
    }


}
