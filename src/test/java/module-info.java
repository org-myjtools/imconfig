module org.myjtools.imconfig.test {
    requires org.myjtools.imconfig;
    requires org.assertj.core;
    requires org.junit.jupiter.engine;
    requires org.junit.jupiter.params;

    opens org.myjtools.imconfig.test to org.junit.platform.commons, org.myjtools.imconfig;
    exports org.myjtools.imconfig.test to org.myjtools.imconfig;
}