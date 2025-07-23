/*
 * @author Luis Iñesta Gelabert - luiinge@gmail.com
 */
package org.myjtools.imconfig.test;


import org.junit.jupiter.api.Test;
import org.myjtools.imconfig.Config;

import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


 class TestConfigurationFactoryDefinitions {

    private final Path definitionPath = Path.of("src", "test", "resources", "definition.yaml");


    @Test
     void testBuildEmptyConfigurationWithDefinitionFromURI() {
        var conf = Config.withDefinitions(Config.loadDefinitions(definitionPath.toUri()));
        assertConfiguration(conf);
    }

    @Test
     void testConfigurationValidation() {
        var conf = Config.ofMap(Map.of("defined.property.min-max-number", "6"));
        assertThat(conf.get("defined.property.min-max-number",Integer.class)).hasValue(6);
        conf = conf.append(Config.withDefinitions(Config.loadDefinitions(definitionPath.toUri())));
        assertThat(conf.get("defined.property.min-max-number",Integer.class)).hasValue(6);

        assertThat(conf.validations("defined.property.min-max-number"))
        .contains("Invalid value '6', expected: Integer number between 2 and 3");
    }

     @Test
     void testConfigurationValidationFromResource() {
         var conf = Config.ofMap(Map.of("defined.property.min-max-number", "6"));
         assertThat(conf.get("defined.property.min-max-number",Integer.class)).hasValue(6);
         conf = conf.append(Config.withDefinitions(Config.loadDefinitionsFromResource("definition.yaml", getClass().getClassLoader())));
         assertThat(conf.get("defined.property.min-max-number",Integer.class)).hasValue(6);

         assertThat(conf.validations("defined.property.min-max-number"))
                 .contains("Invalid value '6', expected: Integer number between 2 and 3");
     }


    private void assertConfiguration(Config conf) {
        assertThat(conf.getDefinitions()).hasSize(6);
        assertThat(conf.getDefinition("defined.property.required")).isNotEmpty();
        assertThat(conf.getDefinition("defined.property.with-default-value")).isNotEmpty();
        assertThat(conf.getDefinition("defined.property.regex-text")).isNotEmpty();
        assertThat(conf.getDefinition("defined.property.min-max-number")).isNotEmpty();
        assertThat(conf.getDefinition("defined.property.enumeration")).isNotEmpty();
        assertThat(conf.getDefinition("defined.property.boolean")).isNotEmpty();
        assertThat(conf.getDefinition("undefined.property")).isEmpty();
        assertThat(conf.get("defined.property.regex-text", String.class)).isEmpty();
        assertThat(conf.get("defined.property.with-default-value", Integer.class)).hasValue(5);
    }

}
