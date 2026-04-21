package org.myjtools.imconfig.test;

import org.junit.jupiter.api.Test;
import org.myjtools.imconfig.PropertyDefinition;
import org.myjtools.imconfig.internal.PropertyDefinitionParser;
import org.myjtools.imconfig.types.EnumPropertyType;
import org.myjtools.imconfig.types.IntegerPropertyType;
import org.myjtools.imconfig.types.MapPropertyType;
import org.myjtools.imconfig.types.TextPropertyType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


class TestDefinitionParser {

    private final PropertyDefinitionParser parser = new PropertyDefinitionParser();


    @Test
    void testParseDefinitionFile() throws IOException {

        File file = Path.of("src","test","resources","definition.yaml").toFile();

        Map<String, PropertyDefinition> byName;
        try (InputStream inputStream = new FileInputStream(file)) {
            byName = parser.read(inputStream).stream()
                .collect(java.util.stream.Collectors.toMap(PropertyDefinition::property, d -> d));
        }

        var required = byName.get("defined.property.required");
        assertThat(required.description()).isEqualTo("This is a test property that is required");
        assertThat(required.required()).isTrue();
        assertThat(required.type()).isEqualTo("text");
        assertThat(required.propertyType()).isInstanceOf(TextPropertyType.class);

        var regexText = byName.get("defined.property.regex-text");
        assertThat(regexText.type()).isEqualTo("text");
        assertThat(regexText.propertyType()).isInstanceOf(TextPropertyType.class);
        assertThat(((TextPropertyType) regexText.propertyType()).pattern()).isEqualTo("A\\d\\dB");

        var withDefault = byName.get("defined.property.with-default-value");
        assertThat(withDefault.type()).isEqualTo("integer");
        assertThat(withDefault.description()).isEqualTo("This is a property with a default value");
        assertThat(withDefault.defaultValue()).hasValue("5");

        var minMax = byName.get("defined.property.min-max-number");
        assertThat(minMax.type()).isEqualTo("integer");
        assertThat(minMax.propertyType()).isInstanceOf(IntegerPropertyType.class);
        assertThat(((IntegerPropertyType) minMax.propertyType()).min()).isEqualTo(2L);
        assertThat(((IntegerPropertyType) minMax.propertyType()).max()).isEqualTo(3L);

        var enumeration = byName.get("defined.property.enumeration");
        assertThat(enumeration.type()).isEqualTo("enum");
        assertThat(enumeration.propertyType()).isInstanceOf(EnumPropertyType.class);
        assertThat(((EnumPropertyType) enumeration.propertyType()).values()).containsExactly("red", "yellow", "orange");

        var map = byName.get("defined.property.map");
        assertThat(map.type()).isEqualTo("map");
        assertThat(map.propertyType()).isInstanceOf(MapPropertyType.class);
        var mapEntries = ((MapPropertyType) map.propertyType()).entries();
        assertThat(mapEntries).containsKeys("url", "username", "password", "driver", "dialect", "schema", "catalog");
        assertThat(mapEntries.get("url").required()).isTrue();
        assertThat(mapEntries.get("schema").required()).isFalse();
    }

}
