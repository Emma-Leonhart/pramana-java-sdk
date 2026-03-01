package org.pramana.sdk;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a Gaussian rational number (Q[i]) — a complex number whose real and
 * imaginary parts are both rational, stored as the normalized vector
 * &lt;A, B, C, D&gt; representing A/B + (C/D)i.
 *
 * <p>Fractions are always kept in lowest terms with positive denominators.
 * This class is immutable and uses {@link BigInteger} for all components.</p>
 */
public final class Gauss implements Comparable<Gauss> {

    /** Zero. */
    public static final Gauss ZERO = new Gauss(BigInteger.ZERO, BigInteger.ONE, BigInteger.ZERO, BigInteger.ONE);
    /** One. */
    public static final Gauss ONE = new Gauss(BigInteger.ONE, BigInteger.ONE, BigInteger.ZERO, BigInteger.ONE);
    /** Negative one. */
    public static final Gauss MINUS_ONE = new Gauss(BigInteger.ONE.negate(), BigInteger.ONE, BigInteger.ZERO, BigInteger.ONE);
    /** The imaginary unit i. */
    public static final Gauss I = new Gauss(BigInteger.ZERO, BigInteger.ONE, BigInteger.ONE, BigInteger.ONE);

    private final BigInteger a; // real numerator
    private final BigInteger b; // real denominator (always > 0)
    private final BigInteger c; // imaginary numerator
    private final BigInteger d; // imaginary denominator (always > 0)

    // ── Constructors ──

    /**
     * Creates a Gauss representing a/b + (c/d)i.
     * Fractions are normalized to lowest terms with positive denominators.
     */
    public Gauss(BigInteger a, BigInteger b, BigInteger c, BigInteger d) {
        if (b.signum() == 0) throw new ArithmeticException("Real denominator cannot be zero");
        if (d.signum() == 0) throw new ArithmeticException("Imaginary denominator cannot be zero");

        BigInteger[] realNorm = normalize(a, b);
        BigInteger[] imagNorm = normalize(c, d);
        this.a = realNorm[0];
        this.b = realNorm[1];
        this.c = imagNorm[0];
        this.d = imagNorm[1];
    }

    /** Creates a Gauss from integer real and imaginary parts. */
    public Gauss(BigInteger real, BigInteger imaginary) {
        this(real, BigInteger.ONE, imaginary, BigInteger.ONE);
    }

    /** Creates a real Gauss from a single integer. */
    public Gauss(BigInteger value) {
        this(value, BigInteger.ONE, BigInteger.ZERO, BigInteger.ONE);
    }

    /** Convenience: creates from long components. */
    public Gauss(long a, long b, long c, long d) {
        this(BigInteger.valueOf(a), BigInteger.valueOf(b), BigInteger.valueOf(c), BigInteger.valueOf(d));
    }

    /** Convenience: creates from long real and imaginary. */
    public Gauss(long real, long imaginary) {
        this(BigInteger.valueOf(real), BigInteger.ONE, BigInteger.valueOf(imaginary), BigInteger.ONE);
    }

    /** Convenience: creates a real Gauss from a long. */
    public Gauss(long value) {
        this(BigInteger.valueOf(value), BigInteger.ONE, BigInteger.ZERO, BigInteger.ONE);
    }

    // ── Normalization ──

    private static BigInteger[] normalize(BigInteger num, BigInteger den) {
        if (den.signum() == 0) throw new ArithmeticException("Denominator cannot be zero");
        if (num.signum() == 0) return new BigInteger[]{ BigInteger.ZERO, BigInteger.ONE };
        if (den.signum() < 0) {
            num = num.negate();
            den = den.negate();
        }
        BigInteger gcd = num.abs().gcd(den);
        return new BigInteger[]{ num.divide(gcd), den.divide(gcd) };
    }

    // ── Accessors ──

    /** Real numerator (A in A/B). */
    public BigInteger a() { return a; }
    /** Real denominator (B in A/B), always positive. */
    public BigInteger b() { return b; }
    /** Imaginary numerator (C in C/D). */
    public BigInteger c() { return c; }
    /** Imaginary denominator (D in C/D), always positive. */
    public BigInteger d() { return d; }

    // Aliases matching the spec
    public BigInteger realNumerator() { return a; }
    public BigInteger realDenominator() { return b; }
    public BigInteger imagNumerator() { return c; }
    public BigInteger imagDenominator() { return d; }

    // ── Classification Properties ──

    /** True if imaginary part is zero. */
    public boolean isReal() { return c.signum() == 0; }

    /** True if real part is zero and imaginary is nonzero. */
    public boolean isPurelyImaginary() { return a.signum() == 0 && c.signum() != 0; }

    /** True if this is an integer (real with denominator 1). */
    public boolean isInteger() { return b.equals(BigInteger.ONE) && c.signum() == 0; }

    /** True if both parts are integers (denominators are 1). */
    public boolean isGaussianInteger() { return b.equals(BigInteger.ONE) && d.equals(BigInteger.ONE); }

    /** True if zero. */
    public boolean isZero() { return a.signum() == 0 && c.signum() == 0; }

    /** True if this equals 1. */
    public boolean isOne() { return a.equals(BigInteger.ONE) && b.equals(BigInteger.ONE) && c.signum() == 0; }

    /** True if negative real. */
    public boolean isNegative() { return isReal() && a.signum() < 0; }

    /** True if positive real. */
    public boolean isPositive() { return isReal() && a.signum() > 0; }

    // ── Pramana Identity ──

    /** Canonical key: "a,b,c,d". */
    public String pramanaKey() { return a + "," + b + "," + c + "," + d; }

    /** Deterministic UUID v5 identifier. */
    public UUID pramanaId() { return PramanaId.forNumber(pramanaKey()); }

    /** Pramana label: "pra:num:a,b,c,d". */
    public String pramanaString() { return PramanaId.label(pramanaKey()); }

    /** Pramana entity URL using UUID. */
    public String pramanaHashUrl() { return PramanaId.entityUrl(pramanaId()); }

    /** Pramana entity URL using pseudo-class string. */
    public String pramanaUrl() { return "https://pramana-data.ca/entity/" + pramanaString(); }

    // ── Static Factory Methods ──

    /** Returns the imaginary unit i. */
    public static Gauss eye() { return I; }

    /** Returns the four units of Q[i]: [1, -1, i, -i]. */
    public static Gauss[] gaussUnits() {
        return new Gauss[]{ ONE, MINUS_ONE, I, I.negate() };
    }

    /** Wraps a long as a real Gauss. */
    public static Gauss of(long value) { return new Gauss(value); }

    /** Wraps two longs as a Gaussian integer. */
    public static Gauss of(long real, long imag) { return new Gauss(real, imag); }

    /** Creates from a double using continued-fraction approximation. */
    public static Gauss fromDouble(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value))
            throw new IllegalArgumentException("Cannot convert NaN or Infinity to Gauss");
        BigInteger[] frac = doubleToFraction(value);
        return new Gauss(frac[0], frac[1], BigInteger.ZERO, BigInteger.ONE);
    }

    /** Creates from double real and imaginary parts. */
    public static Gauss fromDouble(double real, double imaginary) {
        BigInteger[] rFrac = doubleToFraction(real);
        BigInteger[] iFrac = doubleToFraction(imaginary);
        return new Gauss(rFrac[0], rFrac[1], iFrac[0], iFrac[1]);
    }

    /** Creates from polar coordinates (double approximation). */
    public static Gauss fromPolar(double magnitude, double phase) {
        return fromDouble(magnitude * Math.cos(phase), magnitude * Math.sin(phase));
    }

    /** Parses "a,b,c,d" format. */
    public static Gauss parse(String s) {
        String[] parts = s.split(",");
        if (parts.length != 4)
            throw new IllegalArgumentException("Expected format: a,b,c,d");
        return new Gauss(
                new BigInteger(parts[0].trim()),
                new BigInteger(parts[1].trim()),
                new BigInteger(parts[2].trim()),
                new BigInteger(parts[3].trim())
        );
    }

    /** Parses "pra:num:a,b,c,d" format. */
    public static Gauss fromPramana(String s) {
        if (s.startsWith("pra:num:")) s = s.substring(8);
        return parse(s);
    }

    /** Tries to parse "a,b,c,d" format. Returns null on failure. */
    public static Gauss tryParse(String s) {
        try { return parse(s); }
        catch (Exception e) { return null; }
    }

    // ── Arithmetic ──

    public Gauss add(Gauss other) {
        BigInteger[] realPart = addFractions(a, b, other.a, other.b);
        BigInteger[] imagPart = addFractions(c, d, other.c, other.d);
        return new Gauss(realPart[0], realPart[1], imagPart[0], imagPart[1]);
    }

    public Gauss subtract(Gauss other) {
        BigInteger[] realPart = addFractions(a, b, other.a.negate(), other.b);
        BigInteger[] imagPart = addFractions(c, d, other.c.negate(), other.d);
        return new Gauss(realPart[0], realPart[1], imagPart[0], imagPart[1]);
    }

    public Gauss negate() {
        return new Gauss(a.negate(), b, c.negate(), d);
    }

    /** (a/b + c/d*i)(e/f + g/h*i) using standard complex multiplication. */
    public Gauss multiply(Gauss other) {
        // Real: (a/b)(e/f) - (c/d)(g/h)
        BigInteger[] r1 = multiplyFractions(a, b, other.a, other.b);
        BigInteger[] r2 = multiplyFractions(c, d, other.c, other.d);
        BigInteger[] realPart = addFractions(r1[0], r1[1], r2[0].negate(), r2[1]);

        // Imag: (a/b)(g/h) + (c/d)(e/f)
        BigInteger[] i1 = multiplyFractions(a, b, other.c, other.d);
        BigInteger[] i2 = multiplyFractions(c, d, other.a, other.b);
        BigInteger[] imagPart = addFractions(i1[0], i1[1], i2[0], i2[1]);

        return new Gauss(realPart[0], realPart[1], imagPart[0], imagPart[1]);
    }

    /** Division using conjugate multiplication. */
    public Gauss divide(Gauss other) {
        Gauss conj = other.conjugate();
        Gauss numerator = this.multiply(conj);
        Gauss denominator = other.multiply(conj); // real (c²+d²)

        if (denominator.a.signum() == 0)
            throw new ArithmeticException("Cannot divide by zero");

        BigInteger[] realPart = divideFractions(numerator.a, numerator.b, denominator.a, denominator.b);
        BigInteger[] imagPart = divideFractions(numerator.c, numerator.d, denominator.a, denominator.b);
        return new Gauss(realPart[0], realPart[1], imagPart[0], imagPart[1]);
    }

    /** Modulo for real Gaussians only. */
    public Gauss mod(Gauss other) {
        if (!isReal() || !other.isReal())
            throw new UnsupportedOperationException("Modulo only supported for real numbers");
        if (other.a.signum() == 0)
            throw new ArithmeticException("Cannot compute modulo with zero divisor");
        BigInteger num = a.multiply(other.b).mod(other.a.multiply(b));
        BigInteger den = b.multiply(other.b);
        return new Gauss(num, den, BigInteger.ZERO, BigInteger.ONE);
    }

    /** Integer power using binary exponentiation. Negative exponents use inverse. */
    public static Gauss pow(Gauss base_, int exponent) {
        if (exponent == 0) return ONE;
        if (exponent == 1) return base_;
        if (exponent < 0) {
            base_ = ONE.divide(base_);
            exponent = -exponent;
        }
        Gauss result = ONE;
        Gauss current = base_;
        while (exponent > 0) {
            if ((exponent & 1) == 1) result = result.multiply(current);
            current = current.multiply(current);
            exponent >>= 1;
        }
        return result;
    }

    /** Instance method for power. */
    public Gauss pow(int exponent) { return pow(this, exponent); }

    /** Increment real part by one. */
    public Gauss increment() { return add(ONE); }

    /** Decrement real part by one. */
    public Gauss decrement() { return subtract(ONE); }

    // ── Complex Properties ──

    /** Complex conjugate: a/b - (c/d)i. */
    public Gauss conjugate() { return new Gauss(a, b, c.negate(), d); }

    /** Magnitude squared: (a/b)² + (c/d)². Always real. */
    public Gauss magnitudeSquared() {
        BigInteger[] r = multiplyFractions(a, b, a, b);
        BigInteger[] i = multiplyFractions(c, d, c, d);
        BigInteger[] sum = addFractions(r[0], r[1], i[0], i[1]);
        return new Gauss(sum[0], sum[1], BigInteger.ZERO, BigInteger.ONE);
    }

    /** Norm (alias for magnitudeSquared). */
    public Gauss norm() { return magnitudeSquared(); }

    /** Magnitude as double (approximation). */
    public double magnitude() {
        double re = a.doubleValue() / b.doubleValue();
        double im = c.doubleValue() / d.doubleValue();
        return Math.sqrt(re * re + im * im);
    }

    /** Phase angle in radians (approximation). */
    public double phase() {
        return Math.atan2(c.doubleValue() / d.doubleValue(), a.doubleValue() / b.doubleValue());
    }

    /** Polar form as [magnitude, phase]. */
    public double[] toPolar() { return new double[]{ magnitude(), phase() }; }

    /** Reciprocal (1/z). */
    public Gauss reciprocal() { return ONE.divide(this); }

    /** Inverse (alias for reciprocal). */
    public Gauss inverse() { return reciprocal(); }

    /** Real part as a Gauss. */
    public Gauss realPart() { return new Gauss(a, b, BigInteger.ZERO, BigInteger.ONE); }

    /** Imaginary part coefficient as a Gauss. */
    public Gauss imaginaryPart() { return new Gauss(c, d, BigInteger.ZERO, BigInteger.ONE); }

    // ── Associates ──

    /** Returns the three non-trivial associates. */
    public Gauss[] associates() {
        return new Gauss[]{ negate(), multiply(I), multiply(I.negate()) };
    }

    /** True if other is an associate of this. */
    public boolean isAssociate(Gauss other) {
        if (other.isZero()) return isZero();
        Gauss q = this.divide(other);
        for (Gauss unit : gaussUnits()) {
            if (q.equals(unit)) return true;
        }
        return false;
    }

    // ── Math Functions ──

    /** Absolute value: exact for real, magnitude-squared for complex. */
    public static Gauss abs(Gauss value) {
        if (value.isReal()) return value.a.signum() >= 0 ? value : value.negate();
        return value.magnitudeSquared();
    }

    /** Sign of real part: -1, 0, or 1. */
    public static int sign(Gauss value) { return value.a.signum(); }

    /** Floor of real part (real only). */
    public static Gauss floor(Gauss value) {
        if (!value.isReal()) throw new UnsupportedOperationException("Floor only supported for real numbers");
        if (value.b.equals(BigInteger.ONE)) return value;
        BigInteger fl = value.a.divide(value.b);
        if (value.a.signum() < 0 && !value.a.mod(value.b).equals(BigInteger.ZERO))
            fl = fl.subtract(BigInteger.ONE);
        return new Gauss(fl);
    }

    /** Ceiling of real part (real only). */
    public static Gauss ceiling(Gauss value) {
        if (!value.isReal()) throw new UnsupportedOperationException("Ceiling only supported for real numbers");
        if (value.b.equals(BigInteger.ONE)) return value;
        BigInteger cl = value.a.divide(value.b);
        if (value.a.signum() > 0 && !value.a.mod(value.b).equals(BigInteger.ZERO))
            cl = cl.add(BigInteger.ONE);
        return new Gauss(cl);
    }

    /** Truncate toward zero (real only). */
    public static Gauss truncate(Gauss value) {
        if (!value.isReal()) throw new UnsupportedOperationException("Truncate only supported for real numbers");
        return new Gauss(value.a.divide(value.b));
    }

    /** Minimum by comparison. */
    public static Gauss min(Gauss a, Gauss b) { return a.compareTo(b) <= 0 ? a : b; }

    /** Maximum by comparison. */
    public static Gauss max(Gauss a, Gauss b) { return a.compareTo(b) >= 0 ? a : b; }

    /** Clamp between min and max. */
    public static Gauss clamp(Gauss value, Gauss min, Gauss max) {
        if (min.compareTo(max) > 0) throw new IllegalArgumentException("min must be <= max");
        if (value.compareTo(min) < 0) return min;
        if (value.compareTo(max) > 0) return max;
        return value;
    }

    // ── Fraction Arithmetic ──

    /** a/b + c/d = (ad + bc) / bd */
    private static BigInteger[] addFractions(BigInteger a, BigInteger b, BigInteger c, BigInteger d) {
        return new BigInteger[]{ a.multiply(d).add(b.multiply(c)), b.multiply(d) };
    }

    /** (a/b) * (c/d) = ac / bd */
    private static BigInteger[] multiplyFractions(BigInteger a, BigInteger b, BigInteger c, BigInteger d) {
        return new BigInteger[]{ a.multiply(c), b.multiply(d) };
    }

    /** (a/b) / (c/d) = ad / bc */
    private static BigInteger[] divideFractions(BigInteger a, BigInteger b, BigInteger c, BigInteger d) {
        if (c.signum() == 0) throw new ArithmeticException("Division by zero");
        return new BigInteger[]{ a.multiply(d), b.multiply(c) };
    }

    /** Continued-fraction algorithm to convert double to best rational approximation. */
    private static BigInteger[] doubleToFraction(double value) {
        if (value == 0) return new BigInteger[]{ BigInteger.ZERO, BigInteger.ONE };
        boolean negative = value < 0;
        value = Math.abs(value);

        double tolerance = 1e-15;
        int maxIterations = 64;
        BigInteger num1 = BigInteger.ONE, num2 = BigInteger.ZERO;
        BigInteger den1 = BigInteger.ZERO, den2 = BigInteger.ONE;
        double x = value;

        BigInteger lastNum = BigInteger.ONE, lastDen = BigInteger.ONE;
        for (int i = 0; i < maxIterations; i++) {
            BigInteger intPart = BigInteger.valueOf((long) Math.floor(x));
            BigInteger num = intPart.multiply(num1).add(num2);
            BigInteger den = intPart.multiply(den1).add(den2);
            lastNum = num;
            lastDen = den;

            double approx = num.doubleValue() / den.doubleValue();
            if (Math.abs(approx - value) < tolerance * value) {
                return new BigInteger[]{ negative ? num.negate() : num, den };
            }

            num2 = num1; num1 = num;
            den2 = den1; den1 = den;

            double frac = x - intPart.doubleValue();
            if (frac < tolerance) break;
            x = 1.0 / frac;
        }
        return new BigInteger[]{ negative ? lastNum.negate() : lastNum, lastDen };
    }

    // ── String Representations ──

    /** Raw vector format: "&lt;A,B,C,D&gt;". */
    public String toRawString() { return "<" + a + "," + b + "," + c + "," + d + ">"; }

    /** Human-readable mixed fraction format. */
    @Override
    public String toString() {
        String realStr = formatRational(a, b);
        String imagStr = formatImaginary(c, d);

        if (c.signum() == 0) return realStr;
        if (a.signum() == 0) return imagStr;
        if (c.signum() > 0) return realStr + " + " + imagStr;
        return realStr + " - " + formatImaginary(c.negate(), d);
    }

    /** Decimal approximation string. */
    public String toDecimalString(int precision) {
        double re = a.doubleValue() / b.doubleValue();
        double im = c.doubleValue() / d.doubleValue();
        String fmt = "%." + precision + "f";
        String rePart = String.format(fmt, re);
        if (c.signum() == 0) return rePart;
        String imAbs = String.format(fmt, Math.abs(im));
        if (a.signum() == 0) {
            return (im < 0 ? "-" : "") + imAbs + "i";
        }
        if (im < 0) return rePart + " - " + imAbs + "i";
        return rePart + " + " + imAbs + "i";
    }

    public String toDecimalString() { return toDecimalString(15); }

    /** Improper fraction format. */
    public String toImproperFractionString() {
        String realStr = b.equals(BigInteger.ONE) ? a.toString() : a + "/" + b;
        String imagStr = formatImaginaryImproper(c, d);
        if (c.signum() == 0) return realStr;
        if (a.signum() == 0) return imagStr;
        if (c.signum() > 0) return realStr + " + " + imagStr;
        return realStr + " - " + formatImaginaryImproper(c.negate(), d);
    }

    private static String formatRational(BigInteger num, BigInteger den) {
        if (den.equals(BigInteger.ONE)) return num.toString();
        BigInteger whole = num.divide(den);
        BigInteger remainder = num.mod(den).abs();
        // Handle negative numerator with positive den correctly
        if (num.signum() < 0 && den.signum() > 0) {
            // For negative fractions: -7/2 -> whole = -3 (Java truncates toward zero: -7/2 = -3 remainder -1)
            // We want floor division for proper mixed fractions
            BigInteger[] divRem = num.divideAndRemainder(den);
            whole = divRem[0];
            remainder = divRem[1].abs();
            if (divRem[1].signum() != 0 && num.signum() < 0) {
                whole = whole.subtract(BigInteger.ONE);
                remainder = den.subtract(remainder);
            }
        }
        if (whole.signum() == 0) return num + "/" + den;
        if (remainder.signum() == 0) return whole.toString();
        return whole + " & " + remainder + "/" + den;
    }

    private static String formatImaginary(BigInteger num, BigInteger den) {
        if (num.signum() == 0) return "0";
        if (num.equals(BigInteger.ONE) && den.equals(BigInteger.ONE)) return "i";
        if (num.equals(BigInteger.ONE.negate()) && den.equals(BigInteger.ONE)) return "-i";
        if (den.equals(BigInteger.ONE)) return num + "i";
        BigInteger whole = num.divide(den);
        BigInteger remainder = num.mod(den).abs();
        if (num.signum() < 0 && den.signum() > 0) {
            BigInteger[] divRem = num.divideAndRemainder(den);
            whole = divRem[0];
            remainder = divRem[1].abs();
            if (divRem[1].signum() != 0 && num.signum() < 0) {
                whole = whole.subtract(BigInteger.ONE);
                remainder = den.subtract(remainder);
            }
        }
        if (whole.signum() == 0) return num + "/" + den + " i";
        if (remainder.signum() == 0) return whole + "i";
        return whole + " & " + remainder + "/" + den + " i";
    }

    private static String formatImaginaryImproper(BigInteger num, BigInteger den) {
        if (num.signum() == 0) return "0";
        if (num.equals(BigInteger.ONE) && den.equals(BigInteger.ONE)) return "i";
        if (num.equals(BigInteger.ONE.negate()) && den.equals(BigInteger.ONE)) return "-i";
        if (den.equals(BigInteger.ONE)) return num + "i";
        return num + "/" + den + " i";
    }

    // ── Equality & Comparison ──

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Gauss other)) return false;
        return a.equals(other.a) && b.equals(other.b) && c.equals(other.c) && d.equals(other.d);
    }

    @Override
    public int hashCode() { return Objects.hash(a, b, c, d); }

    /**
     * Compares by real part first (using cross-multiplication), then imaginary.
     */
    @Override
    public int compareTo(Gauss other) {
        // a/b vs e/f → a*f vs e*b
        int realCmp = a.multiply(other.b).compareTo(other.a.multiply(b));
        if (realCmp != 0) return realCmp;
        // c/d vs g/h → c*h vs g*d
        return c.multiply(other.d).compareTo(other.c.multiply(d));
    }

    // ── Conversion to Gint ──

    /** Converts to Gint. Throws if not a Gaussian integer. */
    public Gint toGint() {
        if (!isGaussianInteger())
            throw new ArithmeticException("Cannot convert non-integer Gauss to Gint");
        return new Gint(a, c);
    }

    /** Converts to a double array [real, imaginary]. */
    public double[] toDoubleArray() {
        return new double[]{ a.doubleValue() / b.doubleValue(), c.doubleValue() / d.doubleValue() };
    }
}
