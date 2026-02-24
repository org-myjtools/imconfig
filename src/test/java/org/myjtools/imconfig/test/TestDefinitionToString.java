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

    private final PropertyDefinitionParser parser = new PropertyDefinitionParser();


    @Test
    void testPrintDefinition() throws IOException {

        File file = Path.of("src","test","resources","definition.yaml").toFile();
        Config definitions = Config.withDefinitions(Config.loadDefinitions(file.toURI()));
        System.out.println(definitions.getDefinitionsToString());



    }

}
