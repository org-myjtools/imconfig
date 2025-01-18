package org.myjtools.imconfig.test;


import org.junit.jupiter.api.Test;
import org.myjtools.imconfig.Config;
import org.myjtools.imconfig.ConfigException;
import org.myjtools.imconfig.PropertyDefinition;

import java.util.List;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

 class TestPropertyDefinitions {

    @Test
     void testRequiredPropertyDoNotAcceptEmptyOrNullValues() {
        var definition = PropertyDefinition.builder()
            .property("test")
            .required()
            .textType()
            .build();
        assertThat(definition.validate("")).isNotEmpty();
        assertThat(definition.validate(null)).isNotEmpty();
    }


    @Test
     void testTextWithoutPatternAcceptsAnyValue() {
        var definition = PropertyDefinition.builder()
            .property("test")
            .textType()
            .build();
        assertThat(definition.validate("")).isEmpty();
        assertThat(definition.validate("dasdjhsajkdhsakjd")).isEmpty();
        assertThat(definition.validate("12312321123")).isEmpty();
        assertThat(definition.validate(")(*%^&%*^&%^&%#*&^)&*(&()*&)(*&")).isEmpty();
    }



    @Test
     void testTextWithInvalidPatternThrowsException() {
        var builder = PropertyDefinition.builder().property("test");
        assertThatCode(()->builder.textType("A\\d{*\\dB"))
        .isExactlyInstanceOf(PatternSyntaxException.class)
        .hasMessageStartingWith("Illegal");
    }



    @Test
     void testTextWithPatternAcceptsOnlyMatchingValues() {
        var definition = PropertyDefinition.builder("test")
            .textType("A\\d\\dB")
            .build();
        assertThat(definition.validate("")).isEmpty();
        assertThat(definition.validate("dasdjhsajkdhsakjd")).isNotEmpty();
        assertThat(definition.validate("A1B")).isNotEmpty();
        assertThat(definition.validate("A12B")).isEmpty();
    }


    @Test
     void testIntegerOnlyAcceptsNumericIntegerValue() {
        var definition = PropertyDefinition.builder()
            .property("test")
            .integerType()
            .build();
        assertThat(definition.validate("")).isEmpty();
        assertThat(definition.validate("dasdjhsajkdhsakjd")).isNotEmpty();
        assertThat(definition.validate("12.65")).isNotEmpty();
        assertThat(definition.validate("13")).isEmpty();
    }


    @Test
     void testIntegerWithoutBoundsAcceptsAnyValue() {
        var definition = PropertyDefinition.builder()
            .property("test")
            .integerType()
            .build();
        assertThat(definition.validate("")).isEmpty();
        assertThat(definition.validate("-32")).isEmpty();
        assertThat(definition.validate("0")).isEmpty();
        assertThat(definition.validate("1")).isEmpty();
        assertThat(definition.validate("43247329874239874")).isEmpty();
    }


    @Test
     void testIntegerWithInvalidBoundsThrowsException() {
        var builder = PropertyDefinition.builder().property("test");
        assertThatCode(()->builder.integerType(6,3))
        .isExactlyInstanceOf(IllegalArgumentException.class)
        .hasMessage("Minimum value cannot be greater than maximum value");
    }


    @Test
     void testIntegerWithBoundsOnlyAcceptsValuesInRange() {
        var definition = PropertyDefinition.builder()
            .property("test")
            .integerType(3,6)
            .build();
        assertThat(definition.validate("")).isEmpty();
        assertThat(definition.validate("2")).isNotEmpty();
        assertThat(definition.validate("3")).isEmpty();
        assertThat(definition.validate("6")).isEmpty();
        assertThat(definition.validate("7")).isNotEmpty();
    }


    @Test
     void testDecimalOnlyAcceptsNumericValues() {
        var definition = PropertyDefinition.builder()
            .property("test")
            .decimalType()
            .build();
        assertThat(definition.validate("")).isEmpty();
        assertThat(definition.validate("dasdjhsajkdhsakjd")).isNotEmpty();
        assertThat(definition.validate("12.65")).isEmpty();
        assertThat(definition.validate("13")).isEmpty();
    }


    @Test
     void testDecimalWithoutBoundsAcceptsAnyValue() {
        var definition = PropertyDefinition.builder()
            .property("test")
            .decimalType()
            .build();
        assertThat(definition.validate("")).isEmpty();
        assertThat(definition.validate("-32.0")).isEmpty();
        assertThat(definition.validate("0.0")).isEmpty();
        assertThat(definition.validate("1.7")).isEmpty();
        assertThat(definition.validate("43247329874239874.4243242343243")).isEmpty();
    }


    @Test
     void testDecimalWithInvalidBoundsThrowsException() {
        var builder = PropertyDefinition.builder().property("test");
        assertThatCode(()->builder.decimalType(4.6,4.3))
        .isExactlyInstanceOf(IllegalArgumentException.class)
        .hasMessage("Minimum value cannot be greater than maximum value");
    }


    @Test
     void testDecimalWithBoundsOnlyAcceptsValuesInRange() {
        var definition = PropertyDefinition.builder()
            .property("test")
            .decimalType(4.3,4.6)
            .build();
        assertThat(definition.validate("")).isEmpty();
        assertThat(definition.validate("4.2")).isNotEmpty();
        assertThat(definition.validate("4.3")).isEmpty();
        assertThat(definition.validate("4.6")).isEmpty();
        assertThat(definition.validate("4.7")).isNotEmpty();
    }


    @Test
     void testEnumOnlyAcceptsValuesInList() {
        var definition = PropertyDefinition.builder()
            .property("test")
            .enumType("hello","goodbay")
            .build();
        assertThat(definition.validate("")).isEmpty();
        assertThat(definition.validate("hello")).isEmpty();
        assertThat(definition.validate("HELLO")).isEmpty();
        assertThat(definition.validate("Hello")).isEmpty();
        assertThat(definition.validate("goodbay")).isEmpty();
        assertThat(definition.validate("GOODBAY")).isEmpty();
        assertThat(definition.validate("Goodbay")).isEmpty();
        assertThat(definition.validate("later")).isNotEmpty();
    }


    @Test
     void testBooleanOnlyAcceptsTrueOrFalse() {
        var definition = PropertyDefinition.builder()
            .property("test")
            .booleanType()
            .build();
        assertThat(definition.validate("")).isEmpty();
        assertThat(definition.validate("true")).isEmpty();
        assertThat(definition.validate("false")).isEmpty();
        assertThat(definition.validate("other")).isNotEmpty();
    }


    @Test
     void testRequiredPropertyNotAcceptEmptyOrNullValue() {
        var definition = PropertyDefinition.builder()
                .property("test")
                .textType()
                .required()
                .build();
        assertThat(definition.validate("")).isNotEmpty();
        assertThat(definition.validate(null)).isNotEmpty();
        assertThat(definition.validate("x")).isEmpty();
    }



    private List<PropertyDefinition> definitions() {
        return List.of(
            PropertyDefinition.builder()
                .property("pA")
                .description("This is a required boolean property")
                .required()
                .booleanType()
                .build(),
            PropertyDefinition.builder()
                .property("pB")
                .description("This is a enum property with default value")
                .defaultValue("two")
                .enumType("one", "two", "three")
                .build(),
            PropertyDefinition.builder()
                .property("pC")
                .description("This is decimal property with no bound")
                .decimalType()
                .build(),
            PropertyDefinition.builder()
                .property("pD")
                .description("This is a integer property with bounds")
                .integerType(10,15)
                .build(),
            PropertyDefinition.builder()
                .property("pE")
                .description("This is a enum property multivalue")
                .multivalue()
                .enumType("one", "two", "three")
                .build()
        );
    }


    @Test
    void testDefinitionToString() {
        List<PropertyDefinition> definitions = definitions();
        var configuration = Config.withDefinitions(definitions)
            .append(Config.ofMap(Map.of("pE", "undefined property")));

        assertThat(configuration.getDefinitionsToString()).isEqualTo(
                """
                        - pA: This is a required boolean property
                          true | false (required)
                        - pB: This is a enum property with default value
                          One of the following: one, two, three [default: two]
                        - pC: This is decimal property with no bound
                          Any decimal number
                        - pD: This is a integer property with bounds
                          Integer number between 10 and 15
                        - pE: This is a enum property multivalue
                          List of one of the following: one, two, three"""
        );

    }




    @Test
     void validationsOfConfiguration() {
        var definitions = definitions();
        var configuration = Config.withDefinitions(definitions)
            .append(Config.ofMap(Map.of(
            "pB","four",
            "pD", "20"
            )));
        assertThat(configuration.validations()).containsExactly(
            Map.entry("pA",List.of("Property is required but not present")),
            Map.entry("pB",List.of("Invalid value 'four', expected: One of the following: one, two, three [default: two]")),
            Map.entry("pD",List.of("Invalid value '20', expected: Integer number between 10 and 15"))
        );
        assertThatCode(configuration::validate)
            .isInstanceOf(ConfigException.class)
            .hasMessage("""
                The configuration contains one or more invalid values:
                \tpA : Property is required but not present
                \tpB : Invalid value 'four', expected: One of the following: one, two, three [default: two]
                \tpD : Invalid value '20', expected: Integer number between 10 and 15"""
            );
    }


    @Test
     void validationsOfMultivaluedConfigurationProperty() {
        var definitions = definitions();
        Config.factory.multivalueSeparator(',');
        var configuration = Config.withDefinitions(definitions)
            .append(Config.ofMap(Map.of("pE","one,two,five,six")));
        assertThat(configuration.validations()).containsExactly(
            Map.entry("pA",List.of("Property is required but not present")),
            Map.entry("pE",List.of(
                "Invalid value 'five', expected: One of the following: one, two, three",
                "Invalid value 'six', expected: One of the following: one, two, three"
            ))
        );
    }
}
