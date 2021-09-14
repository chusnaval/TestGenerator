package org.chusnaval.etg;

/**
 *
 */
public class ValueFinderService {

    /**
     * Default constructor private to make static class
     */
    private ValueFinderService() {
        super();
    }

    /**
     * Return a test value based on parameter type
     *
     * @param type type of class member
     * @return a valid value test
     */
    public static Object getValueByType(String type) {
        switch (type) {
            case "int":
            case "byte":
            case "short":
            case "Integer":
                return "TESTING_INTEGER";
            case "Long":
            case "long":
                return "TESTING_LONG";
            case "float":
            case "double":
            case "Float":
            case "Double":
                return "TESTING_FLOAT";
            case "boolean":
            case "Boolean":
                return true;
            case "String":
                return "TESTING_STRING";
            case "Date":
                return "TESTING_DATE";
            default:
                return null;
        }
    }
}
