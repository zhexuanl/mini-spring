package org.minis.test;

public class BaseService {

    private TestService testService;

    public BaseService() {
    }

    public TestService getTestService() {
        return testService;
    }

    public void setTestService(TestService testService) {
        this.testService = testService;
    }
}
