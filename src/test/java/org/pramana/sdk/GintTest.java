package org.pramana.sdk;

import org.junit.jupiter.api.Test;
import java.math.BigInteger;
import static org.junit.jupiter.api.Assertions.*;

class GintTest {

    // ── Construction ──

    @Test
    void testDefaultConstructor() {
        Gint z = new Gint();
        assertEquals(BigInteger.ZERO, z.real());
        assertEquals(BigInteger.ZERO, z.imag());
    }

    @Test
    void testRealOnly() {
        Gint z = new Gint(5);
        assertEquals(BigInteger.valueOf(5), z.real());
        assertEquals(BigInteger.ZERO, z.imag());
    }

    @Test
    void testBothParts() {
        Gint z = new Gint(3, 4);
        assertEquals(BigInteger.valueOf(3), z.real());
        assertEquals(BigInteger.valueOf(4), z.imag());
    }

    @Test
    void testStaticOf() {
        Gint z = Gint.of(7, -2);
        assertEquals(BigInteger.valueOf(7), z.real());
        assertEquals(BigInteger.valueOf(-2), z.imag());
    }

    // ── Properties ──

    @Test
    void testConjugate() {
        Gint z = new Gint(3, 4);
        Gint conj = z.conjugate();
        assertEquals(new Gint(3, -4), conj);
    }

    @Test
    void testNorm() {
        Gint z = new Gint(3, 4);
        assertEquals(BigInteger.valueOf(25), z.norm());
    }

    @Test
    void testIsUnit() {
        assertTrue(Gint.ONE.isUnit());
        assertTrue(Gint.MINUS_ONE.isUnit());
        assertTrue(Gint.I.isUnit());
        assertTrue(Gint.I.negate().isUnit());
        assertFalse(new Gint(2, 0).isUnit());
    }

    @Test
    void testIsZero() {
        assertTrue(Gint.ZERO.isZero());
        assertFalse(Gint.ONE.isZero());
    }

    @Test
    void testIsReal() {
        assertTrue(new Gint(5).isReal());
        assertFalse(new Gint(3, 4).isReal());
    }

    @Test
    void testIsPurelyImaginary() {
        assertTrue(Gint.I.isPurelyImaginary());
        assertFalse(new Gint(1, 1).isPurelyImaginary());
        assertFalse(Gint.ZERO.isPurelyImaginary());
    }

    // ── Arithmetic ──

    @Test
    void testAdd() {
        Gint a = new Gint(3, 4);
        Gint b = new Gint(1, 2);
        assertEquals(new Gint(4, 6), a.add(b));
    }

    @Test
    void testSubtract() {
        Gint a = new Gint(3, 4);
        Gint b = new Gint(1, 2);
        assertEquals(new Gint(2, 2), a.subtract(b));
    }

    @Test
    void testMultiply() {
        // (3+4i)(1+2i) = (3-8) + (6+4)i = -5 + 10i
        Gint a = new Gint(3, 4);
        Gint b = new Gint(1, 2);
        assertEquals(new Gint(-5, 10), a.multiply(b));
    }

    @Test
    void testNegate() {
        Gint z = new Gint(3, -4);
        assertEquals(new Gint(-3, 4), z.negate());
    }

    @Test
    void testPow() {
        Gint z = new Gint(1, 1); // 1+i
        // (1+i)^2 = 2i
        assertEquals(new Gint(0, 2), z.pow(2));
        // (1+i)^0 = 1
        assertEquals(Gint.ONE, z.pow(0));
    }

    @Test
    void testPowNegativeThrows() {
        assertThrows(IllegalArgumentException.class, () -> Gint.pow(Gint.ONE, -1));
    }

    @Test
    void testIncrementDecrement() {
        Gint z = new Gint(3, 4);
        assertEquals(new Gint(4, 4), z.increment());
        assertEquals(new Gint(2, 4), z.decrement());
    }

    // ── Floor Division & Modulo ──

    @Test
    void testFloorDiv() {
        Gint a = new Gint(7, 3);
        Gint b = new Gint(2, 1);
        Gint q = Gint.floorDiv(a, b);
        // q * b should be close to a
        assertNotNull(q);
    }

    @Test
    void testModifiedDivmod() {
        Gint a = new Gint(7, 3);
        Gint b = new Gint(2, 1);
        Gint[] result = Gint.modifiedDivmod(a, b);
        Gint q = result[0];
        Gint r = result[1];
        // Verify a = b*q + r
        assertEquals(a, b.multiply(q).add(r));
        // Verify r.norm < b.norm / 2 (the modified divmod guarantee)
        // r.norm * 2 < b.norm
    }

    @Test
    void testModifiedDivmodByZeroThrows() {
        assertThrows(ArithmeticException.class, () -> Gint.modifiedDivmod(Gint.ONE, Gint.ZERO));
    }

    // ── Number Theory ──

    @Test
    void testGcd() {
        Gint a = new Gint(3, 1);
        Gint b = new Gint(1, 2);
        Gint g = Gint.gcd(a, b);
        assertNotNull(g);
        assertFalse(g.isZero());
    }

    @Test
    void testGcdBothZeroThrows() {
        assertThrows(IllegalArgumentException.class, () -> Gint.gcd(Gint.ZERO, Gint.ZERO));
    }

    @Test
    void testXgcd() {
        Gint alpha = new Gint(3, 1);
        Gint beta = new Gint(1, 2);
        Gint[] result = Gint.xgcd(alpha, beta);
        Gint gcd = result[0];
        Gint x = result[1];
        Gint y = result[2];
        // Verify gcd = alpha*x + beta*y
        assertEquals(gcd, alpha.multiply(x).add(beta.multiply(y)));
    }

    @Test
    void testIsGaussianPrime() {
        // 3 is a Gaussian prime (3 ≡ 3 mod 4)
        assertTrue(Gint.isGaussianPrime(new Gint(3, 0)));
        // 7 is a Gaussian prime (7 ≡ 3 mod 4)
        assertTrue(Gint.isGaussianPrime(new Gint(7, 0)));
        // 2 is NOT a Gaussian prime (2 = -i(1+i)^2)
        assertFalse(Gint.isGaussianPrime(new Gint(2, 0)));
        // 5 is NOT a Gaussian prime (5 = (2+i)(2-i), and 5 ≡ 1 mod 4)
        assertFalse(Gint.isGaussianPrime(new Gint(5, 0)));
        // 1+i has norm 2 which is prime → Gaussian prime
        assertTrue(Gint.isGaussianPrime(new Gint(1, 1)));
        // 2+i has norm 5 which is prime → Gaussian prime
        assertTrue(Gint.isGaussianPrime(new Gint(2, 1)));
    }

    @Test
    void testIsRelativelyPrime() {
        // 3 and 2 are relatively prime in Z[i]
        assertTrue(Gint.isRelativelyPrime(new Gint(3, 0), new Gint(0, 2)));
        // 2+2i and 2 share factor 1+i, so not relatively prime
        assertFalse(Gint.isRelativelyPrime(new Gint(2, 2), new Gint(2, 0)));
    }

    @Test
    void testCongruentModulo() {
        // 5 ≡ 2 (mod 3)
        assertTrue(Gint.congruentModulo(new Gint(5), new Gint(2), new Gint(3)));
        assertFalse(Gint.congruentModulo(new Gint(5), new Gint(1), new Gint(3)));
    }

    @Test
    void testNormsDivide() {
        Gint a = new Gint(2, 1); // norm = 5
        Gint b = new Gint(1, 0); // norm = 1
        BigInteger result = Gint.normsDivide(a, b);
        assertEquals(BigInteger.valueOf(5), result);
    }

    // ── Associates ──

    @Test
    void testAssociates() {
        Gint z = new Gint(3, 2);
        Gint[] assoc = z.associates();
        assertEquals(3, assoc.length);
        assertEquals(z.negate(), assoc[0]);
    }

    @Test
    void testIsAssociate() {
        Gint z = new Gint(3, 2);
        assertTrue(z.isAssociate(z.negate()));
        assertTrue(z.isAssociate(z.multiply(Gint.I)));
        assertFalse(z.isAssociate(new Gint(1, 1)));
    }

    // ── Conversion ──

    @Test
    void testToGauss() {
        Gint z = new Gint(3, 4);
        Gauss g = z.toGauss();
        assertEquals(BigInteger.valueOf(3), g.a());
        assertEquals(BigInteger.ONE, g.b());
        assertEquals(BigInteger.valueOf(4), g.c());
        assertEquals(BigInteger.ONE, g.d());
    }

    @Test
    void testToArray() {
        Gint z = new Gint(3, 4);
        BigInteger[] arr = z.toArray();
        assertEquals(BigInteger.valueOf(3), arr[0]);
        assertEquals(BigInteger.valueOf(4), arr[1]);
    }

    @Test
    void testFromArray() {
        Gint z = Gint.fromArray(new BigInteger[]{ BigInteger.valueOf(5), BigInteger.valueOf(6) });
        assertEquals(new Gint(5, 6), z);
    }

    @Test
    void testFromArrayWrongLength() {
        assertThrows(IllegalArgumentException.class, () -> Gint.fromArray(new BigInteger[]{ BigInteger.ONE }));
    }

    // ── Equality & Comparison ──

    @Test
    void testEquals() {
        assertEquals(new Gint(3, 4), new Gint(3, 4));
        assertNotEquals(new Gint(3, 4), new Gint(4, 3));
    }

    @Test
    void testCompareTo() {
        assertTrue(new Gint(1, 0).compareTo(new Gint(2, 0)) < 0);
        assertTrue(new Gint(2, 0).compareTo(new Gint(1, 0)) > 0);
        assertEquals(0, new Gint(3, 4).compareTo(new Gint(3, 4)));
        // Same real, compare by imaginary
        assertTrue(new Gint(3, 1).compareTo(new Gint(3, 2)) < 0);
    }

    // ── String ──

    @Test
    void testToString() {
        assertEquals("3 + 4i", new Gint(3, 4).toString());
        assertEquals("5", new Gint(5, 0).toString());
        assertEquals("i", Gint.I.toString());
        assertEquals("-i", Gint.I.negate().toString());
        assertEquals("3 - 2i", new Gint(3, -2).toString());
        assertEquals("3 + i", new Gint(3, 1).toString());
        assertEquals("3 - i", new Gint(3, -1).toString());
    }

    @Test
    void testToRawString() {
        assertEquals("(3, 4)", new Gint(3, 4).toRawString());
    }

    // ── Pramana Identity ──

    @Test
    void testPramanaKey() {
        assertEquals("3,1,4,1", new Gint(3, 4).pramanaKey());
    }

    @Test
    void testPramanaString() {
        assertEquals("pra:num:3,1,4,1", new Gint(3, 4).pramanaString());
    }

    @Test
    void testPramanaIdDeterministic() {
        assertEquals(new Gint(3, 4).pramanaId(), new Gint(3, 4).pramanaId());
    }

    @Test
    void testPramanaIdMatchesGauss() {
        Gint z = new Gint(3, 4);
        Gauss g = z.toGauss();
        assertEquals(z.pramanaId(), g.pramanaId());
    }

    // ── Static Constants ──

    @Test
    void testConstants() {
        assertEquals(new Gint(0, 0), Gint.ZERO);
        assertEquals(new Gint(1, 0), Gint.ONE);
        assertEquals(new Gint(-1, 0), Gint.MINUS_ONE);
        assertEquals(new Gint(0, 1), Gint.I);
    }

    @Test
    void testUnits() {
        Gint[] units = Gint.units();
        assertEquals(4, units.length);
        for (Gint u : units) {
            assertTrue(u.isUnit());
        }
    }

    @Test
    void testTwo() {
        assertEquals(new Gint(1, 1), Gint.two());
    }

    @Test
    void testEye() {
        assertEquals(Gint.I, Gint.eye());
    }
}
