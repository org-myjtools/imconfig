package org.myjtools.imconfig.types;

import org.myjtools.imconfig.PropertyType;

import java.util.regex.Pattern;
public class TextPropertyType implements PropertyType {


    private final Pattern pattern;

    public TextPropertyType(String pattern) {
        this.pattern = (pattern == null ? null : Pattern.compile(pattern));
    }

    @Override
    public String name() {
        return "text";
    }

    @Override
    public boolean accepts(String value) {
        return pattern == null || pattern.matcher(value).matches();
    }

    @Override
    public String hint() {
        return pattern == null ? "Any text" : "Text satisfying regex //"+pattern+"//";
    }

    public String pattern() {
        return this.pattern.pattern();
    }
}