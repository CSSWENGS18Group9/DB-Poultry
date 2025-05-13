package org.DBPoultry;

public class Calculator {
    public static int addition(int x, int y) {
        return x + y;
    }

    public static int subtraction(int x, int y) {
        if (x > y) {
            return x - y;
        } else {
            return 0;
        }
    }
}
