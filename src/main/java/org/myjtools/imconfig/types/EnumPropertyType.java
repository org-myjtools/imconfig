package org.myjtools.imconfig.types;



import org.myjtools.imconfig.PropertyType;

import java.util.List;

public class EnumPropertyType implements PropertyType {

    private final List<String> values;

    public EnumPropertyType(List<String> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("Enumeration values cannot be empty");
        }
        this.values = values.stream().map(String::toLowerCase).toList();
    }

    @Override
    public String name() {
        return "enum";
    }

    public List<String> values() {
        return values;
    }

    @Override
    public String hint() {
        return "One of the following: "+ String.join(", ", values);
    }

    @Override
    public boolean accepts(String value) {
        return values.contains(value.toLowerCase());
    }

}