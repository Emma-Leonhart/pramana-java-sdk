package org.pramana.sdk;

import org.junit.jupiter.api.Test;
import java.math.BigInteger;
import static org.junit.jupiter.api.Assertions.*;

class GaussTest {

    // ── Construction ──

    @Test
    void testFullConstructor() {
        Gauss g = new Gauss(3, 2, 1, 4);
        assertEquals(BigInteger.valueOf(3), g.a());
        assertEquals(BigInteger.valueOf(2), g.b());
        assertEquals(BigInteger.valueOf(1), g.c());
        assertEquals(BigInteger.valueOf(4), g.d());
    }

    @Test
    void testNormalization() {
        // 2/4 should normalize to 1/2
        Gauss g = new Gauss(2, 4, 0, 1);
        assertEquals(BigInteger.ONE, g.a());
        assertEquals(BigInteger.TWO, g.b());
    }

    @Test
    void testNegativeDenominator() {
        // a/-b normalizes to -a/b
        Gauss g = new Gauss(3, -2, 0, 1);
        assertEquals(BigInteger.valueOf(-3), g.a());
        assertEquals(BigInteger.TWO, g.b());
    }

    @Test
    void testZeroNumerator() {
        Gauss g = new Gauss(0, 5, 0, 3);
        assertEquals(BigInteger.ZERO, g.a());
        assertEquals(BigInteger.ONE, g.b());
    }

    @Test
    void testZeroDenominatorThrows() {
        assertThrows(ArithmeticException.class, () -> new Gauss(1, 0, 0, 1));
    }

    @Test
    void testIntegerConstructor() {
        Gauss g = new Gauss(3, 4);
        assertEquals(BigInteger.valueOf(3), g.a());
        assertEquals(BigInteger.ONE, g.b());
        assertEquals(BigInteger.valueOf(4), g.c());
        assertEquals(BigInteger.ONE, g.d());
    }

    @Test
    void testSingleValueConstructor() {
        Gauss g = new Gauss(5);
        assertEquals(BigInteger.valueOf(5), g.a());
        assertTrue(g.isReal());
        assertTrue(g.isInteger());
    }

    // ── Classification ──

    @Test
    void testIsReal() {
        assertTrue(new Gauss(3, 2, 0, 1).isReal());
        assertFalse(new Gauss(3, 2, 1, 4).isReal());
    }

    @Test
    void testIsPurelyImaginary() {
        assertTrue(Gauss.I.isPurelyImaginary());
        assertFalse(Gauss.ONE.isPurelyImaginary());
        assertFalse(Gauss.ZERO.isPurelyImaginary());
    }

    @Test
    void testIsInteger() {
        assertTrue(new Gauss(5).isInteger());
        assertFalse(new Gauss(1, 2, 0, 1).isInteger());
        assertFalse(new Gauss(1, 1, 1, 1).isInteger()); // has imaginary part
    }

    @Test
    void testIsGaussianInteger() {
        assertTrue(new Gauss(3, 4).isGaussianInteger());
        assertFalse(new Gauss(1, 2, 0, 1).isGaussianInteger());
    }

    @Test
    void testIsZero() {
        assertTrue(Gauss.ZERO.isZero());
        assertFalse(Gauss.ONE.isZero());
    }

    @Test
    void testIsOne() {
        assertTrue(Gauss.ONE.isOne());
        assertFalse(Gauss.ZERO.isOne());
    }

    @Test
    void testIsPositive() {
        assertTrue(new Gauss(3).isPositive());
        assertFalse(new Gauss(-3).isPositive());
        assertFalse(new Gauss(3, 4).isPositive()); // complex
    }

    @Test
    void testIsNegative() {
        assertTrue(new Gauss(-3).isNegative());
        assertFalse(new Gauss(3).isNegative());
    }

    // ── Arithmetic ──

    @Test
    void testAdd() {
        Gauss a = new Gauss(1, 2, 0, 1); // 1/2
        Gauss b = new Gauss(1, 3, 0, 1); // 1/3
        Gauss result = a.add(b);
        assertEquals(BigInteger.valueOf(5), result.a());
        assertEquals(BigInteger.valueOf(6), result.b());
    }

    @Test
    void testSubtract() {
        Gauss a = new Gauss(1, 2, 0, 1); // 1/2
        Gauss b = new Gauss(1, 3, 0, 1); // 1/3
        Gauss result = a.subtract(b);
        assertEquals(BigInteger.ONE, result.a());
        assertEquals(BigInteger.valueOf(6), result.b());
    }

    @Test
    void testMultiply() {
        // (1/2 + i)(1/3 + 2i) = (1/6 - 2) + (2/2 + 1/3)i = -11/6 + 2/3 i... wait
        // Let me use simpler: (2+3i)(4+5i) = (8-15) + (10+12)i = -7 + 22i
        Gauss a = new Gauss(2, 3);
        Gauss b = new Gauss(4, 5);
        Gauss result = a.multiply(b);
        assertEquals(new Gauss(-7, 22), result);
    }

    @Test
    void testMultiplyFractions() {
        // (1/2)(1/3) = 1/6
        Gauss a = new Gauss(1, 2, 0, 1);
        Gauss b = new Gauss(1, 3, 0, 1);
        Gauss result = a.multiply(b);
        assertEquals(BigInteger.ONE, result.a());
        assertEquals(BigInteger.valueOf(6), result.b());
    }

    @Test
    void testDivide() {
        // (4+2i) / (1+i) = (4+2i)(1-i) / (1+1) = (4+2+2i-4i) / 2 = (6-2i)/2 = 3-i
        Gauss a = new Gauss(4, 2);
        Gauss b = new Gauss(1, 1);
        Gauss result = a.divide(b);
        assertEquals(new Gauss(3, -1), result);
    }

    @Test
    void testDivideByZeroThrows() {
        assertThrows(ArithmeticException.class, () -> Gauss.ONE.divide(Gauss.ZERO));
    }

    @Test
    void testNegate() {
        Gauss g = new Gauss(3, 2, 1, 4);
        Gauss neg = g.negate();
        assertEquals(BigInteger.valueOf(-3), neg.a());
        assertEquals(BigInteger.TWO, neg.b());
        assertEquals(BigInteger.valueOf(-1), neg.c());
        assertEquals(BigInteger.valueOf(4), neg.d());
    }

    @Test
    void testPow() {
        // (1+i)^2 = 2i
        Gauss z = new Gauss(1, 1);
        Gauss result = z.pow(2);
        assertEquals(new Gauss(0, 2), result);
    }

    @Test
    void testPowZero() {
        assertEquals(Gauss.ONE, new Gauss(5, 3).pow(0));
    }

    @Test
    void testPowNegative() {
        // (2)^-1 = 1/2
        Gauss result = new Gauss(2).pow(-1);
        assertEquals(BigInteger.ONE, result.a());
        assertEquals(BigInteger.TWO, result.b());
    }

    @Test
    void testMod() {
        // 7/2 mod 3 = 1/2 (since 7/2 = 1*3 + 1/2)
        Gauss a = new Gauss(7, 2, 0, 1);
        Gauss b = new Gauss(3);
        Gauss result = a.mod(b);
        assertEquals(BigInteger.ONE, result.a());
        assertEquals(BigInteger.TWO, result.b());
    }

    @Test
    void testModComplexThrows() {
        assertThrows(UnsupportedOperationException.class, () -> Gauss.I.mod(Gauss.ONE));
    }

    // ── Complex Properties ──

    @Test
    void testConjugate() {
        Gauss g = new Gauss(3, 2, 1, 4);
        Gauss conj = g.conjugate();
        assertEquals(g.a(), conj.a());
        assertEquals(g.b(), conj.b());
        assertEquals(g.c().negate(), conj.c());
        assertEquals(g.d(), conj.d());
    }

    @Test
    void testMagnitudeSquared() {
        // |3+4i|² = 9+16 = 25
        Gauss g = new Gauss(3, 4);
        Gauss msq = g.magnitudeSquared();
        assertEquals(new Gauss(25), msq);
    }

    @Test
    void testMagnitudeSquaredFraction() {
        // |1/2 + 1/3 i|² = 1/4 + 1/9 = 13/36
        Gauss g = new Gauss(1, 2, 1, 3);
        Gauss msq = g.magnitudeSquared();
        assertEquals(BigInteger.valueOf(13), msq.a());
        assertEquals(BigInteger.valueOf(36), msq.b());
    }

    @Test
    void testNorm() {
        Gauss g = new Gauss(3, 4);
        assertEquals(g.magnitudeSquared(), g.norm());
    }

    @Test
    void testReciprocal() {
        // 1/(2+i) = (2-i)/5 = 2/5 - 1/5 i
        Gauss g = new Gauss(2, 1);
        Gauss inv = g.reciprocal();
        assertEquals(BigInteger.TWO, inv.a());
        assertEquals(BigInteger.valueOf(5), inv.b());
        assertEquals(BigInteger.valueOf(-1), inv.c());
        assertEquals(BigInteger.valueOf(5), inv.d());
    }

    @Test
    void testRealPart() {
        Gauss g = new Gauss(3, 2, 1, 4);
        Gauss rp = g.realPart();
        assertEquals(g.a(), rp.a());
        assertEquals(g.b(), rp.b());
        assertTrue(rp.isReal());
    }

    @Test
    void testImaginaryPart() {
        Gauss g = new Gauss(3, 2, 1, 4);
        Gauss ip = g.imaginaryPart();
        assertEquals(g.c(), ip.a());
        assertEquals(g.d(), ip.b());
        assertTrue(ip.isReal());
    }

    // ── Associates ──

    @Test
    void testAssociates() {
        Gauss g = new Gauss(3, 2);
        Gauss[] assoc = g.associates();
        assertEquals(3, assoc.length);
        assertEquals(g.negate(), assoc[0]);
    }

    @Test
    void testIsAssociate() {
        Gauss g = new Gauss(3, 2);
        assertTrue(g.isAssociate(g.negate()));
        assertTrue(g.isAssociate(g.multiply(Gauss.I)));
        assertFalse(g.isAssociate(new Gauss(1, 1)));
    }

    // ── Math Functions ──

    @Test
    void testAbs() {
        Gauss neg = new Gauss(-5);
        assertEquals(new Gauss(5), Gauss.abs(neg));
    }

    @Test
    void testSign() {
        assertEquals(1, Gauss.sign(new Gauss(5)));
        assertEquals(-1, Gauss.sign(new Gauss(-5)));
        assertEquals(0, Gauss.sign(Gauss.ZERO));
    }

    @Test
    void testFloor() {
        // floor(7/2) = 3
        assertEquals(new Gauss(3), Gauss.floor(new Gauss(7, 2, 0, 1)));
        // floor(-7/2) = -4
        assertEquals(new Gauss(-4), Gauss.floor(new Gauss(-7, 2, 0, 1)));
    }

    @Test
    void testCeiling() {
        // ceiling(7/2) = 4
        assertEquals(new Gauss(4), Gauss.ceiling(new Gauss(7, 2, 0, 1)));
        // ceiling(-7/2) = -3
        assertEquals(new Gauss(-3), Gauss.ceiling(new Gauss(-7, 2, 0, 1)));
    }

    @Test
    void testTruncate() {
        // truncate(7/2) = 3
        assertEquals(new Gauss(3), Gauss.truncate(new Gauss(7, 2, 0, 1)));
        // truncate(-7/2) = -3 (toward zero)
        assertEquals(new Gauss(-3), Gauss.truncate(new Gauss(-7, 2, 0, 1)));
    }

    @Test
    void testMin() {
        Gauss a = new Gauss(1);
        Gauss b = new Gauss(2);
        assertEquals(a, Gauss.min(a, b));
    }

    @Test
    void testMax() {
        Gauss a = new Gauss(1);
        Gauss b = new Gauss(2);
        assertEquals(b, Gauss.max(a, b));
    }

    @Test
    void testClamp() {
        Gauss min = new Gauss(0);
        Gauss max = new Gauss(10);
        assertEquals(new Gauss(5), Gauss.clamp(new Gauss(5), min, max));
        assertEquals(min, Gauss.clamp(new Gauss(-1), min, max));
        assertEquals(max, Gauss.clamp(new Gauss(15), min, max));
    }

    // ── String Representations ──

    @Test
    void testToString() {
        assertEquals("0", Gauss.ZERO.toString());
        assertEquals("1", Gauss.ONE.toString());
        assertEquals("-1", Gauss.MINUS_ONE.toString());
        assertEquals("i", Gauss.I.toString());
        assertEquals("3 + 4i", new Gauss(3, 4).toString());
    }

    @Test
    void testToStringMixedFraction() {
        // 3/2 = "1 & 1/2"
        Gauss g = new Gauss(3, 2, 0, 1);
        assertEquals("1 & 1/2", g.toString());
    }

    @Test
    void testToStringProperFraction() {
        assertEquals("1/3", new Gauss(1, 3, 0, 1).toString());
    }

    @Test
    void testToRawString() {
        assertEquals("<3,2,1,4>", new Gauss(3, 2, 1, 4).toRawString());
    }

    @Test
    void testToImproperFractionString() {
        Gauss g = new Gauss(3, 2, 0, 1);
        assertEquals("3/2", g.toImproperFractionString());
    }

    // ── Parsing ──

    @Test
    void testParse() {
        Gauss g = Gauss.parse("3,2,1,4");
        assertEquals(BigInteger.valueOf(3), g.a());
        assertEquals(BigInteger.TWO, g.b());
        assertEquals(BigInteger.ONE, g.c());
        assertEquals(BigInteger.valueOf(4), g.d());
    }

    @Test
    void testParseInvalid() {
        assertThrows(IllegalArgumentException.class, () -> Gauss.parse("1,2,3"));
    }

    @Test
    void testFromPramana() {
        Gauss g = Gauss.fromPramana("pra:num:3,2,1,4");
        assertEquals(BigInteger.valueOf(3), g.a());
    }

    @Test
    void testTryParse() {
        assertNotNull(Gauss.tryParse("1,1,0,1"));
        assertNull(Gauss.tryParse("invalid"));
    }

    // ── From Double ──

    @Test
    void testFromDouble() {
        Gauss g = Gauss.fromDouble(0.5);
        assertEquals(BigInteger.ONE, g.a());
        assertEquals(BigInteger.TWO, g.b());
    }

    @Test
    void testFromDoubleComplex() {
        Gauss g = Gauss.fromDouble(0.5, 0.25);
        assertEquals(BigInteger.ONE, g.a());
        assertEquals(BigInteger.TWO, g.b());
        assertEquals(BigInteger.ONE, g.c());
        assertEquals(BigInteger.valueOf(4), g.d());
    }

    @Test
    void testFromDoubleNanThrows() {
        assertThrows(IllegalArgumentException.class, () -> Gauss.fromDouble(Double.NaN));
    }

    // ── Equality & Comparison ──

    @Test
    void testEquals() {
        assertEquals(new Gauss(1, 2, 3, 4), new Gauss(1, 2, 3, 4));
        // Normalization: 2/4 = 1/2
        assertEquals(new Gauss(1, 2, 0, 1), new Gauss(2, 4, 0, 1));
        assertNotEquals(new Gauss(1, 2, 0, 1), new Gauss(1, 3, 0, 1));
    }

    @Test
    void testCompareTo() {
        // 1/2 < 1
        assertTrue(new Gauss(1, 2, 0, 1).compareTo(Gauss.ONE) < 0);
        assertTrue(Gauss.ONE.compareTo(new Gauss(1, 2, 0, 1)) > 0);
        assertEquals(0, Gauss.ONE.compareTo(Gauss.ONE));
    }

    // ── Conversion ──

    @Test
    void testToGint() {
        Gauss g = new Gauss(3, 4);
        Gint z = g.toGint();
        assertEquals(BigInteger.valueOf(3), z.real());
        assertEquals(BigInteger.valueOf(4), z.imag());
    }

    @Test
    void testToGintNonIntegerThrows() {
        assertThrows(ArithmeticException.class, () -> new Gauss(1, 2, 0, 1).toGint());
    }

    @Test
    void testToDoubleArray() {
        double[] arr = new Gauss(1, 2, 1, 4).toDoubleArray();
        assertEquals(0.5, arr[0], 1e-10);
        assertEquals(0.25, arr[1], 1e-10);
    }

    // ── Pramana Identity ──

    @Test
    void testPramanaKey() {
        assertEquals("3,2,1,4", new Gauss(3, 2, 1, 4).pramanaKey());
    }

    @Test
    void testPramanaString() {
        assertEquals("pra:num:3,2,1,4", new Gauss(3, 2, 1, 4).pramanaString());
    }

    @Test
    void testPramanaIdDeterministic() {
        Gauss g1 = new Gauss(3, 2, 1, 4);
        Gauss g2 = new Gauss(3, 2, 1, 4);
        assertEquals(g1.pramanaId(), g2.pramanaId());
    }

    @Test
    void testPramanaIdVersion5() {
        assertEquals(5, new Gauss(1).pramanaId().version());
    }

    @Test
    void testPramanaUrl() {
        String url = new Gauss(3, 2, 1, 4).pramanaUrl();
        assertTrue(url.startsWith("https://pramana.dev/entity/pra:num:"));
    }

    @Test
    void testPramanaHashUrl() {
        String url = new Gauss(3, 2, 1, 4).pramanaHashUrl();
        assertTrue(url.startsWith("https://pramana.dev/entity/"));
    }

    // ── Static Constants ──

    @Test
    void testConstants() {
        assertTrue(Gauss.ZERO.isZero());
        assertTrue(Gauss.ONE.isOne());
        assertTrue(Gauss.MINUS_ONE.isNegative());
        assertTrue(Gauss.I.isPurelyImaginary());
    }

    @Test
    void testEye() {
        assertEquals(Gauss.I, Gauss.eye());
    }

    @Test
    void testGaussUnits() {
        Gauss[] units = Gauss.gaussUnits();
        assertEquals(4, units.length);
    }
}
