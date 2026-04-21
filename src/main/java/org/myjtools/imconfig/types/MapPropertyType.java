package org.myjtools.imconfig.types;

import org.myjtools.imconfig.PropertyDefinition;
import org.myjtools.imconfig.PropertyType;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MapPropertyType implements PropertyType {

    private final Map<String, PropertyDefinition> entries;

    public MapPropertyType(Map<String, PropertyDefinition> entries) {
        this.entries = Collections.unmodifiableMap(new LinkedHashMap<>(entries));
    }

    @Override
    public String name() {
        return "map";
    }

    @Override
    public boolean accepts(String value) {
        return true;
    }

    @Override
    public String hint() {
        return "Map with entries: " + entries.entrySet().stream()
            .map(e -> e.getKey() + " (" + e.getValue().hint() + ")")
            .collect(Collectors.joining(", "));
    }

    public Map<String, PropertyDefinition> entries() {
        return entries;
    }
}