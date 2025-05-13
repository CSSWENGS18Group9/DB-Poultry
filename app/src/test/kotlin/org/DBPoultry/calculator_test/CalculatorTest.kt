package org.DBPoultry.calculator_test;

import org.DBPoultry.Calculator
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class CalculatorTest {
    @Test
    fun testAddition(){
        assertEquals(15, Calculator.addition(10, 5), "10 + 5 = 15")
    }

    @Test
    fun testSubtraction(){
        // this test should fail
        // since as per our code, if the result is negative
        // Calculator.subtraction will return 0
        assertEquals(-7, Calculator.subtraction(3, 10), "3 - 10 = -7")
        
        // this one will work
        assertEquals(0, Calculator.subtraction(3, 10), "3 - 10 = -7")
    }
}