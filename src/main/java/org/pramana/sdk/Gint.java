package org.pramana.sdk;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

/**
 * Represents a Gaussian integer (Z[i]) — a complex number whose real and imaginary
 * parts are both integers. Supports full arithmetic and number-theoretic operations
 * including GCD, extended GCD, primality testing, and modular arithmetic.
 *
 * <p>This type is immutable and uses {@link BigInteger} for arbitrary-precision
 * integer arithmetic.</p>
 */
public final class Gint implements Comparable<Gint> {

    /** Zero (0 + 0i). */
    public static final Gint ZERO = new Gint(BigInteger.ZERO, BigInteger.ZERO);
    /** One (1 + 0i). */
    public static final Gint ONE = new Gint(BigInteger.ONE, BigInteger.ZERO);
    /** Negative one (-1 + 0i). */
    public static final Gint MINUS_ONE = new Gint(BigInteger.ONE.negate(), BigInteger.ZERO);
    /** The imaginary unit (0 + 1i). */
    public static final Gint I = new Gint(BigInteger.ZERO, BigInteger.ONE);

    private final BigInteger real;
    private final BigInteger imag;

    // ── Constructors ──

    public Gint(BigInteger real, BigInteger imag) {
        this.real = Objects.requireNonNull(real);
        this.imag = Objects.requireNonNull(imag);
    }

    public Gint(BigInteger real) {
        this(real, BigInteger.ZERO);
    }

    public Gint(long real, long imag) {
        this(BigInteger.valueOf(real), BigInteger.valueOf(imag));
    }

    public Gint(long real) {
        this(BigInteger.valueOf(real), BigInteger.ZERO);
    }

    public Gint() {
        this(BigInteger.ZERO, BigInteger.ZERO);
    }

    // ── Accessors ──

    public BigInteger real() { return real; }
    public BigInteger imag() { return imag; }

    // ── Properties ──

    /** Complex conjugate (real - imag*i). */
    public Gint conjugate() { return new Gint(real, imag.negate()); }

    /** Norm (squared absolute value): real² + imag². */
    public BigInteger norm() { return real.multiply(real).add(imag.multiply(imag)); }

    /** True if this is a unit (one of 1, -1, i, -i). */
    public boolean isUnit() { return norm().equals(BigInteger.ONE); }

    /** True if zero. */
    public boolean isZero() { return real.signum() == 0 && imag.signum() == 0; }

    /** True if imaginary part is zero. */
    public boolean isReal() { return imag.signum() == 0; }

    /** True if real part is zero and imaginary is nonzero. */
    public boolean isPurelyImaginary() { return real.signum() == 0 && imag.signum() != 0; }

    // ── Pramana Identity ──

    /** Canonical key: "real,1,imag,1". */
    public String pramanaKey() { return real + ",1," + imag + ",1"; }

    /** Deterministic UUID v5 identifier. */
    public UUID pramanaId() { return PramanaId.forNumber(pramanaKey()); }

    /** Pramana label: "pra:num:real,1,imag,1". */
    public String pramanaString() { return PramanaId.label(pramanaKey()); }

    /** Pramana entity URL using UUID. */
    public String pramanaHashUrl() { return PramanaId.entityUrl(pramanaId()); }

    /** Pramana entity URL using pseudo-class string. */
    public String pramanaUrl() { return "https://pramana.dev/entity/" + pramanaString(); }

    // ── Static Factory Methods ──

    /** Returns the imaginary unit i. */
    public static Gint eye() { return I; }

    /** Returns the four units of Z[i]: [1, -1, i, -i]. */
    public static Gint[] units() {
        return new Gint[]{ ONE, MINUS_ONE, I, I.negate() };
    }

    /** Returns 1+i. */
    public static Gint two() { return new Gint(1, 1); }

    /** Creates from a two-element BigInteger array. */
    public static Gint fromArray(BigInteger[] arr) {
        if (arr.length != 2) throw new IllegalArgumentException("Array must have exactly 2 elements");
        return new Gint(arr[0], arr[1]);
    }

    /** Random Gint with components in the given ranges (inclusive). */
    public static Gint random(int re1, int re2, int im1, int im2) {
        Random rng = new Random();
        int re = rng.nextInt(re2 - re1 + 1) + re1;
        int im = rng.nextInt(im2 - im1 + 1) + im1;
        return new Gint(re, im);
    }

    public static Gint random() { return random(-100, 100, -100, 100); }

    // ── Arithmetic ──

    public Gint add(Gint other) {
        return new Gint(real.add(other.real), imag.add(other.imag));
    }

    public Gint subtract(Gint other) {
        return new Gint(real.subtract(other.real), imag.subtract(other.imag));
    }

    public Gint negate() {
        return new Gint(real.negate(), imag.negate());
    }

    /** (a+bi)(c+di) = (ac-bd) + (ad+bc)i */
    public Gint multiply(Gint other) {
        BigInteger re = real.multiply(other.real).subtract(imag.multiply(other.imag));
        BigInteger im = real.multiply(other.imag).add(imag.multiply(other.real));
        return new Gint(re, im);
    }

    /** Exact division returning a Gauss. */
    public Gauss divide(Gint other) {
        return this.toGauss().divide(other.toGauss());
    }

    /** Floor division: nearest Gaussian integer to exact quotient. */
    public static Gint floorDiv(Gint left, Gint right) {
        Gint numerator = left.multiply(right.conjugate());
        BigInteger denominator = right.norm();
        BigInteger re = roundingDivide(numerator.real, denominator);
        BigInteger im = roundingDivide(numerator.imag, denominator);
        return new Gint(re, im);
    }

    /** Modulo: remainder from modifiedDivmod. */
    public Gint mod(Gint other) {
        return modifiedDivmod(this, other)[1];
    }

    /** Integer power (exponent >= 0). */
    public static Gint pow(Gint base_, int exponent) {
        if (exponent < 0)
            throw new IllegalArgumentException("Negative exponents produce Gaussian rationals. Use Gauss.pow instead.");
        if (exponent == 0) return ONE;
        if (exponent == 1) return base_;

        Gint result = ONE;
        Gint current = base_;
        while (exponent > 0) {
            if ((exponent & 1) == 1) result = result.multiply(current);
            current = current.multiply(current);
            exponent >>= 1;
        }
        return result;
    }

    /** Power as instance method. */
    public Gint pow(int exponent) {
        return pow(this, exponent);
    }

    /** Increment real part by one. */
    public Gint increment() { return new Gint(real.add(BigInteger.ONE), imag); }

    /** Decrement real part by one. */
    public Gint decrement() { return new Gint(real.subtract(BigInteger.ONE), imag); }

    private static BigInteger roundingDivide(BigInteger numerator, BigInteger denominator) {
        if (denominator.signum() < 0) {
            numerator = numerator.negate();
            denominator = denominator.negate();
        }
        BigInteger half = denominator.divide(BigInteger.TWO);
        if (numerator.signum() >= 0)
            return numerator.add(half).divide(denominator);
        else
            return numerator.subtract(half).divide(denominator);
    }

    // ── Conversion ──

    /** Converts to Gauss with denominators of 1. */
    public Gauss toGauss() { return new Gauss(real, BigInteger.ONE, imag, BigInteger.ONE); }

    /** Returns the three non-trivial associates. */
    public Gint[] associates() {
        return new Gint[]{ negate(), multiply(I), multiply(I.negate()) };
    }

    /** True if other is an associate of this. */
    public boolean isAssociate(Gint other) {
        if (other.isZero()) return isZero();
        Gint q = floorDiv(this, other);
        if (q.multiply(other).equals(this)) return q.isUnit();
        return false;
    }

    /** Converts to BigInteger array [real, imag]. */
    public BigInteger[] toArray() { return new BigInteger[]{ real, imag }; }

    // ── Number-Theoretic Methods ──

    /**
     * Modified divmod: returns [q, r] such that a = b*q + r with r.norm < b.norm/2.
     */
    public static Gint[] modifiedDivmod(Gint a, Gint b) {
        if (b.isZero()) throw new ArithmeticException("Cannot divide by zero");
        Gint numerator = a.multiply(b.conjugate());
        BigInteger denominator = b.norm();
        BigInteger qRe = roundingDivide(numerator.real, denominator);
        BigInteger qIm = roundingDivide(numerator.imag, denominator);
        Gint q = new Gint(qRe, qIm);
        Gint r = a.subtract(b.multiply(q));
        return new Gint[]{ q, r };
    }

    /** GCD via Euclidean algorithm. */
    public static Gint gcd(Gint a, Gint b) {
        if (a.isZero() && b.isZero())
            throw new IllegalArgumentException("Both inputs must be non-zero");
        Gint r1 = a, r2 = b;
        while (!r2.isZero()) {
            Gint remainder = modifiedDivmod(r1, r2)[1];
            r1 = r2;
            r2 = remainder;
        }
        return r1;
    }

    /**
     * Extended GCD: returns [gcd, x, y] such that gcd = alpha*x + beta*y.
     */
    public static Gint[] xgcd(Gint alpha, Gint beta) {
        Gint a = alpha, b = beta;
        Gint x = ONE, nextX = ZERO;
        Gint y = ZERO, nextY = ONE;

        while (!b.isZero()) {
            Gint q = floorDiv(a, b);
            Gint tmpX = x.subtract(q.multiply(nextX));
            x = nextX;
            nextX = tmpX;
            Gint tmpY = y.subtract(q.multiply(nextY));
            y = nextY;
            nextY = tmpY;
            Gint tmpA = a.mod(b);
            a = b;
            b = tmpA;
        }
        return new Gint[]{ a, x, y };
    }

    /** Tests a ≡ b (mod c). Returns true if (a-b)/c is a Gaussian integer. */
    public static boolean congruentModulo(Gint a, Gint b, Gint c) {
        Gint diff = a.subtract(b);
        Gauss result = diff.divide(c);
        return result.isGaussianInteger();
    }

    /** True if gcd(a,b) is a unit. */
    public static boolean isRelativelyPrime(Gint a, Gint b) {
        return gcd(a, b).isUnit();
    }

    /**
     * Tests Gaussian primality.
     * If both parts nonzero: prime iff norm is prime.
     * If one part zero: the nonzero part must be prime AND ≡ 3 (mod 4).
     */
    public static boolean isGaussianPrime(Gint x) {
        BigInteger re = x.real.abs();
        BigInteger im = x.imag.abs();
        BigInteger n = x.norm();

        if (re.signum() != 0 && im.signum() != 0)
            return NumberTheory.isPrime(n);
        if (re.signum() == 0)
            return NumberTheory.isPrime(im) && im.mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(3));
        return NumberTheory.isPrime(re) && re.mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(3));
    }

    /**
     * If the larger norm divides the smaller norm evenly, returns the quotient;
     * otherwise returns null.
     */
    public static BigInteger normsDivide(Gint a, Gint b) {
        BigInteger x = a.norm();
        BigInteger y = b.norm();
        BigInteger sm = x.min(y);
        BigInteger lg = x.max(y);
        if (sm.signum() == 0) return null;
        if (lg.mod(sm).signum() == 0) return lg.divide(sm);
        return null;
    }

    // ── Equality & Comparison ──

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Gint other)) return false;
        return real.equals(other.real) && imag.equals(other.imag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(real, imag);
    }

    @Override
    public int compareTo(Gint other) {
        int cmp = real.compareTo(other.real);
        return cmp != 0 ? cmp : imag.compareTo(other.imag);
    }

    // ── String Representations ──

    @Override
    public String toString() {
        if (imag.signum() == 0) return real.toString();
        if (real.signum() == 0) {
            if (imag.equals(BigInteger.ONE)) return "i";
            if (imag.equals(BigInteger.ONE.negate())) return "-i";
            return imag + "i";
        }
        if (imag.equals(BigInteger.ONE)) return real + " + i";
        if (imag.equals(BigInteger.ONE.negate())) return real + " - i";
        if (imag.signum() > 0) return real + " + " + imag + "i";
        return real + " - " + imag.negate() + "i";
    }

    /** Raw representation: "(real, imag)". */
    public String toRawString() { return "(" + real + ", " + imag + ")"; }

    // ── Static convenience ──

    /** Wraps a long as a real Gint. */
    public static Gint of(long value) { return new Gint(value); }

    /** Wraps two longs. */
    public static Gint of(long real, long imag) { return new Gint(real, imag); }
}
