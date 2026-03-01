package org.pramana.sdk;

import org.junit.jupiter.api.Test;
import java.math.BigInteger;
import static org.junit.jupiter.api.Assertions.*;

class NumberTheoryTest {

    @Test
    void testSmallPrimes() {
        assertTrue(NumberTheory.isPrime(2));
        assertTrue(NumberTheory.isPrime(3));
        assertTrue(NumberTheory.isPrime(5));
        assertTrue(NumberTheory.isPrime(7));
        assertTrue(NumberTheory.isPrime(11));
        assertTrue(NumberTheory.isPrime(13));
    }

    @Test
    void testNonPrimes() {
        assertFalse(NumberTheory.isPrime(0));
        assertFalse(NumberTheory.isPrime(1));
        assertFalse(NumberTheory.isPrime(4));
        assertFalse(NumberTheory.isPrime(6));
        assertFalse(NumberTheory.isPrime(9));
        assertFalse(NumberTheory.isPrime(15));
        assertFalse(NumberTheory.isPrime(100));
    }

    @Test
    void testLargerPrimes() {
        assertTrue(NumberTheory.isPrime(97));
        assertTrue(NumberTheory.isPrime(101));
        assertTrue(NumberTheory.isPrime(BigInteger.valueOf(7919)));
    }

    @Test
    void testNegative() {
        assertFalse(NumberTheory.isPrime(-1));
        assertFalse(NumberTheory.isPrime(-7));
    }
}
