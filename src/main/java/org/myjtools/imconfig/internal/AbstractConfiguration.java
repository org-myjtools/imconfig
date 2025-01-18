/*
 * @author Luis Iñesta Gelabert - linesta@iti.es | luiinge@gmail.com
 */
package org.myjtools.imconfig.internal;


import org.myjtools.imconfig.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static java.util.stream.Collectors.toMap;


public abstract class AbstractConfiguration implements Config {

    protected final Map<String, PropertyDefinition> definitions;


    protected AbstractConfiguration(Map<String,PropertyDefinition> definitions) {
        this.definitions = definitions;
    }


    @Override
    public Config append(Config otherConfiguration) {
        return factory.merge(this, otherConfiguration);
    }


    @Override
    public Map<String, PropertyDefinition> getDefinitions() {
        return Collections.unmodifiableMap(definitions);
    }


    @Override
    public Optional<PropertyDefinition> getDefinition(String key) {
        return Optional.ofNullable(definitions.get(key));
    }


    @Override
    public boolean hasDefinition(String key) {
        return definitions.containsKey(key);
    }


    @Override
    public List<String> validations(String key) {
        return getDefinition(key).map(definition -> validations(key, definition)).orElseGet(List::of);
    }


    private List<String> validations(String key, PropertyDefinition definition) {
        List<String> values = definition.multivalue() ?
            getList(key, String.class) :
            get(key, String.class).map(List::of).orElseGet(List::of);
        return values
        .stream()
        .map(definition::validate)
        .flatMap(Optional::stream)
        .toList();
    }


    @Override
    public Map<String,List<String>> validations() {
        var invalidValues = keys()
            .map(key -> Map.entry(key, validations(key)))
            .filter(entry -> !entry.getValue().isEmpty());
        var missingValues = definitions.values().stream()
            .filter(PropertyDefinition::required)
            .filter(it->!this.hasProperty(it.property()))
            .map(it->Map.entry(
                it.property(),
                it.validate(null).map(List::of).orElseGet(List::of)
            ));
        return Stream.concat(invalidValues,missingValues)
            .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }


    @Override
    public Config validate() {
        var validations = validations();
        if (!validations.isEmpty()) {
           var message = validations.entrySet().stream()
               .map(entry -> String.format(
                   "%s : %s",
                   entry.getKey(),
                   String.join("\n"+(" ").repeat(entry.getKey().length() + 3),  entry.getValue())
               ))
               .collect(Collectors.joining("\n\t", "The configuration contains one or more invalid values:\n\t",""));
           throw new ConfigException(message);
        }
        return this;
    }


    @Override
    public Config accordingDefinitions(Collection<PropertyDefinition> definitions) {
        return factory.merge(this, factory.withDefinitions(definitions));
    }



    @Override
    public String getDefinitionsToString() {
       return getDefinitions().values().stream()
           .map(PropertyDefinition::toString)
           .collect(Collectors.joining("\n"));
    }



}
