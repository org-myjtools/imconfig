package org.myjtools.imconfig.test;

import org.junit.jupiter.api.Test;
import org.myjtools.imconfig.PropertyDefinition;
import org.myjtools.imconfig.internal.PropertyDefinitionParser;
import org.myjtools.imconfig.types.EnumPropertyType;
import org.myjtools.imconfig.types.IntegerPropertyType;
import org.myjtools.imconfig.types.TextPropertyType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;


class TestDefinitionParser {

    private final PropertyDefinitionParser parser = new PropertyDefinitionParser();


    @Test
    void testParseDefinitionFile() throws IOException {

        File file = Path.of("src","test","resources","definition.yaml").toFile();

        PropertyDefinition definition;
        try (InputStream inputStream = new FileInputStream(file)) {
            var contents = parser.read(inputStream).iterator();

            definition = contents.next();
            assertThat(definition.property()).isEqualTo("defined.property.required");
            assertThat(definition.description()).isEqualTo("This is a test property that is required");
            assertThat(definition.required()).isTrue();
            assertThat(definition.type()).isEqualTo("text");
            assertThat(definition.propertyType()).isInstanceOf(TextPropertyType.class);

            definition = contents.next();
            assertThat(definition.property()).isEqualTo("defined.property.regex-text");
            assertThat(definition.type()).isEqualTo("text");
            assertThat(definition.propertyType()).isInstanceOf(TextPropertyType.class);
            assertThat(((TextPropertyType)definition.propertyType()).pattern()).isEqualTo("A\\d\\dB");

            definition = contents.next();
            assertThat(definition.type()).isEqualTo("integer");
            assertThat(definition.property()).isEqualTo("defined.property.with-default-value");
            assertThat(definition.description()).isEqualTo("This is a property with a default value");
            assertThat(definition.defaultValue()).hasValue("5");

            definition = contents.next();
            assertThat(definition.type()).isEqualTo("integer");
            assertThat(definition.property()).isEqualTo("defined.property.min-max-number");
            assertThat(definition.propertyType()).isInstanceOf(IntegerPropertyType.class);
            assertThat(((IntegerPropertyType)definition.propertyType()).min()).isEqualTo(2L);
            assertThat(((IntegerPropertyType)definition.propertyType()).max()).isEqualTo(3L);

            definition = contents.next();
            assertThat(definition.type()).isEqualTo("enum");
            assertThat(definition.property()).isEqualTo("defined.property.enumeration");
            assertThat(definition.propertyType()).isInstanceOf(EnumPropertyType.class);
            assertThat(((EnumPropertyType)definition.propertyType()).values()).containsExactly("red","yellow","orange");

        }
    }

}
