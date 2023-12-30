package org.minis.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AServiceImpl implements AService {

    private final Logger logger = LogManager.getLogger(AServiceImpl.class);

    private String name;
    private int level;
    private String property1;
    private String property2;

    private BaseService ref1;

    public AServiceImpl() {
    }

    public AServiceImpl(String name, int level) {
        logger.debug("Called AServiceImpl constructor");
        this.name = name;
        this.level = level;

    }

    @Override
    public void hello() {
        System.out.println("hello");
    }

    public String getProperty1() {
        return property1;
    }

    public void setProperty1(String property1) {
        this.property1 = property1;
    }

    public String getProperty2() {
        return property2;
    }

    public void setProperty2(String property2) {
        this.property2 = property2;
    }

    public void setRef1(BaseService ref1) {
        this.ref1 = ref1;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }
}
