package org.myjtools.imconfig.test;

import org.myjtools.imconfig.ConfigClass;
import org.myjtools.imconfig.ConfigProperty;

@ConfigClass
public class TestConfigClass {

    @ConfigProperty("configClass.number")
    private Integer number;

    @ConfigProperty("configClass.string")
    private String string;

    public Integer number() {
        return number;
    }

    public String string() {
        return string;
    }
}
