/**
 This module provides a simple interface in order to load and consume configurations,
 which are mainly a set of valued properties that can be parsed from a wide range of sources
 (such as JSON, YAML or .properties files, Map and Properties objects, or even plain pairs of
 values) to specific Java types.
 <p>
 The primary focus of the module is null-safety, immutability, and fluency.
 */
open module org.myjtools.imconfig {

    exports org.myjtools.imconfig;
    exports org.myjtools.imconfig.internal;
    exports org.myjtools.imconfig.types;

    requires org.apache.commons.configuration2;
    requires com.fasterxml.jackson.dataformat.yaml;
    requires com.fasterxml.jackson.dataformat.xml;

}