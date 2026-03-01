package org.pramana.sdk;

import java.math.BigInteger;

/**
 * Number theory utilities for Gaussian arithmetic.
 */
public final class NumberTheory {

    private NumberTheory() {
        // utility class
    }

    /**
     * Tests whether a BigInteger is a positive prime using trial division
     * with the 6k +/- 1 optimization.
     */
    public static boolean isPrime(BigInteger n) {
        if (n.compareTo(BigInteger.TWO) < 0) return false;
        if (n.equals(BigInteger.TWO) || n.equals(BigInteger.valueOf(3))) return true;
        if (n.mod(BigInteger.TWO).equals(BigInteger.ZERO)) return false;
        if (n.mod(BigInteger.valueOf(3)).equals(BigInteger.ZERO)) return false;

        BigInteger i = BigInteger.valueOf(5);
        while (i.multiply(i).compareTo(n) <= 0) {
            if (n.mod(i).equals(BigInteger.ZERO)) return false;
            if (n.mod(i.add(BigInteger.TWO)).equals(BigInteger.ZERO)) return false;
            i = i.add(BigInteger.valueOf(6));
        }
        return true;
    }

    /**
     * Convenience overload for long values.
     */
    public static boolean isPrime(long n) {
        return isPrime(BigInteger.valueOf(n));
    }
}
