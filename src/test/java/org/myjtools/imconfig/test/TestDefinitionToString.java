package org.myjtools.imconfig.test;

import org.junit.jupiter.api.Test;
import org.myjtools.imconfig.Config;
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


class TestDefinitionToString {


    @Test
    void testPrintDefinition()  {

        File file = Path.of("src","test","resources","definition.yaml").toFile();
        Config definitions = Config.withDefinitions(Config.loadDefinitions(file.toURI()));
        assertThat(definitions.getDefinitionsToString()).isEqualTo("""
                - defined.property.boolean: true | false
                - defined.property.enumeration: One of the following: red, yellow, orange
                - defined.property.map: Map of named datasource configurations. Each key is the datasource alias used in
                test steps to identify the target database connection.
                  Map with entries: url (Any text (required)), username (Any text (required)), password (Any text (required)), driver (Any text (required)), dialect (Any text (required)), schema (Any text), catalog (Any text)
                - defined.property.min-max-number: Integer number between 2 and 3
                - defined.property.regex-text: Text satisfying regex //A\\d\\dB//
                - defined.property.required: This is a test property that is required
                  Any text (required)
                - defined.property.with-default-value: This is a property with a default value
                  Any integer number [default: 5]""");



    }

}
