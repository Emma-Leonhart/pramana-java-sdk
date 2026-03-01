# Pramana Java SDK - Implementation Guide

**Package name:** `org.pramana:pramana-sdk` (Maven Central)
**Minimum Java:** 17+
**Reference implementation:** [PramanaLib (C#)](https://github.com/Emma-Leonhart/PramanaLib)

---

## 1. Project Structure

```
pramana-java-sdk/
├── pom.xml                             # Maven build configuration
├── src/
│   ├── main/
│   │   └── java/
│   │       └── org/
│   │           └── pramana/
│   │               └── sdk/
│   │                   ├── GaussianRational.java
│   │                   ├── PramanaId.java
│   │                   ├── NumberType.java
│   │                   ├── ItemType.java
│   │                   ├── model/
│   │                   │   ├── PramanaItem.java
│   │                   │   ├── PramanaEntity.java
│   │                   │   ├── PramanaProperty.java
│   │                   │   ├── PramanaProposition.java
│   │                   │   └── PramanaSense.java
│   │                   ├── orm/
│   │                   │   ├── PramanaEntityAnnotation.java
│   │                   │   ├── PramanaPropertyAnnotation.java
│   │                   │   ├── ComputedPropertyAnnotation.java
│   │                   │   ├── PramanaConfig.java
│   │                   │   ├── EntityMapper.java
│   │                   │   └── QueryBuilder.java
│   │                   ├── datasource/
│   │                   │   ├── DataSource.java
│   │                   │   ├── PraFileDataSource.java
│   │                   │   ├── SparqlDataSource.java
│   │                   │   ├── RestApiDataSource.java
│   │                   │   └── SqliteDataSource.java
│   │                   ├── structs/
│   │                   │   ├── PramanaDate.java
│   │                   │   ├── PramanaTime.java
│   │                   │   ├── PramanaInterval.java
│   │                   │   ├── Coordinate.java
│   │                   │   └── ChemicalIdentifier.java
│   │                   └── serialization/
│   │                       ├── PramanaGraph.java
│   │                       ├── PramanaSerializer.java
│   │                       └── PramanaDeserializer.java
│   └── test/
│       └── java/
│           └── org/
│               └── pramana/
│                   └── sdk/
│                       ├── GaussianRationalTest.java
│                       ├── PramanaIdTest.java
│                       ├── ItemModelTest.java
│                       ├── OrmTest.java
│                       └── SerializationTest.java
├── src/test/resources/
│   └── test-vectors.json               # Cross-language test vectors
└── docs/
    └── api.md
```

## 2. Build & Packaging

### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.pramana</groupId>
    <artifactId>pramana-sdk</artifactId>
    <version>0.1.0</version>
    <packaging>jar</packaging>

    <name>Pramana SDK</name>
    <description>Java SDK for the Pramana knowledge graph</description>

    <properties>
        <java.version>17</java.version>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <!-- Core has ZERO runtime dependencies -->

        <!-- Test dependencies -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>5.10.0</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
            <version>2.10.1</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
</project>
```

### Gradle Alternative (build.gradle.kts)

```kotlin
plugins {
    java
    `maven-publish`
}

group = "org.pramana"
version = "0.1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    compileOnly("com.google.code.gson:gson:2.10.1")
}
```

### Key decisions:
- **Java 17+** — enables records, sealed classes, text blocks, pattern matching
- **Zero runtime dependencies** for core
- **Gson as optional** (compile-only) for JSON serialization; can use Jackson or any JSON library
- **Maven primary** build, Gradle as alternative
- Both Maven Central and GitHub Packages as distribution targets

## 3. GaussianRational (Gauss) Implementation

> **Naming convention:** The standard short name for a Gaussian rational is **`Gauss`**. When referring specifically to a Gaussian integer (both denominators are 1), the standard short name is **`Gint`**.

### 3.1 Class Design

Java uses `java.math.BigInteger` for arbitrary precision. No operator overloading; named methods throughout.

```java
package org.pramana.sdk;

import java.math.BigInteger;
import java.util.Objects;
import java.util.UUID;

/**
 * Exact complex rational number: a/b + (c/d)i.
 * Immutable — all fields are final and normalized at construction.
 */
public final class GaussianRational implements Comparable<GaussianRational> {

    private final BigInteger a; // real numerator
    private final BigInteger b; // real denominator (positive, nonzero)
    private final BigInteger c; // imaginary numerator
    private final BigInteger d; // imaginary denominator (positive, nonzero)

    public GaussianRational(BigInteger a, BigInteger b, BigInteger c, BigInteger d) {
        if (b.signum() <= 0 || d.signum() <= 0) {
            throw new IllegalArgumentException("Denominators must be positive integers");
        }
        // Normalize to canonical form
        BigInteger gReal = a.abs().gcd(b);
        BigInteger gImag = c.abs().gcd(d);
        this.a = a.divide(gReal);
        this.b = b.divide(gReal);
        this.c = c.divide(gImag);
        this.d = d.divide(gImag);
    }

    // Convenience constructor from longs
    public GaussianRational(long a, long b, long c, long d) {
        this(BigInteger.valueOf(a), BigInteger.valueOf(b),
             BigInteger.valueOf(c), BigInteger.valueOf(d));
    }
}
```

### 3.2 Static Factory Methods

```java
    public static GaussianRational fromInt(long value) {
        return new GaussianRational(value, 1, 0, 1);
    }

    public static GaussianRational fromInt(BigInteger value) {
        return new GaussianRational(value, BigInteger.ONE, BigInteger.ZERO, BigInteger.ONE);
    }

    public static GaussianRational fromComplex(long real, long imag) {
        return new GaussianRational(real, 1, imag, 1);
    }

    public static GaussianRational parse(String s) {
        String normalized = s.startsWith("num:") ? s.substring(4) : s;
        String[] parts = normalized.split(",");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Expected 4 comma-separated integers: " + s);
        }
        return new GaussianRational(
            new BigInteger(parts[0].trim()), new BigInteger(parts[1].trim()),
            new BigInteger(parts[2].trim()), new BigInteger(parts[3].trim())
        );
    }
```

### 3.3 Arithmetic Methods (No Operator Overloading)

Java does not support operator overloading. All arithmetic uses named methods following Java conventions:

```java
    public GaussianRational add(GaussianRational other) {
        // a1/b1 + a2/b2 = (a1*b2 + a2*b1) / (b1*b2)
        BigInteger realNum = this.a.multiply(other.b).add(other.a.multiply(this.b));
        BigInteger realDen = this.b.multiply(other.b);
        BigInteger imagNum = this.c.multiply(other.d).add(other.c.multiply(this.d));
        BigInteger imagDen = this.d.multiply(other.d);
        return new GaussianRational(realNum, realDen, imagNum, imagDen);
    }

    public GaussianRational subtract(GaussianRational other) { ... }
    public GaussianRational negate() { ... }

    public GaussianRational multiply(GaussianRational other) {
        // (a+bi)(c+di) = (ac-bd) + (ad+bc)i
        // real = (a1/b1)*(a2/b2) - (c1/d1)*(c2/d2)
        // imag = (a1/b1)*(c2/d2) + (c1/d1)*(a2/b2)
        ...
    }

    public GaussianRational divide(GaussianRational other) {
        // Multiply by conjugate, divide
        ...
    }

    public GaussianRational mod(GaussianRational other) {
        if (!this.isReal() || !other.isReal()) {
            throw new ArithmeticException("Modulo only defined for real values");
        }
        ...
    }

    public GaussianRational pow(int exponent) {
        // Integer exponents only
        ...
    }
```

### 3.4 Comparable and Equals

```java
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof GaussianRational other)) return false;
        return this.a.equals(other.a) && this.b.equals(other.b)
            && this.c.equals(other.c) && this.d.equals(other.d);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b, c, d);
    }

    @Override
    public int compareTo(GaussianRational other) {
        if (!this.isReal() || !other.isReal()) {
            throw new UnsupportedOperationException("Ordering only defined for real values");
        }
        // Compare a1/b1 vs a2/b2 via cross-multiplication
        return this.a.multiply(other.b).compareTo(other.a.multiply(this.b));
    }
```

### 3.5 Properties (Accessor Methods)

```java
    public BigInteger realNumerator()      { return a; }
    public BigInteger realDenominator()    { return b; }
    public BigInteger imagNumerator()      { return c; }
    public BigInteger imagDenominator()    { return d; }

    public boolean isReal()             { return c.signum() == 0; }
    public boolean isInteger()          { return isReal() && b.equals(BigInteger.ONE); }
    public boolean isGaussianInteger()  { return b.equals(BigInteger.ONE) && d.equals(BigInteger.ONE); }
    public boolean isZero()             { return a.signum() == 0 && c.signum() == 0; }
    public boolean isPositive()         { return isReal() && a.signum() > 0; }
    public boolean isNegative()         { return isReal() && a.signum() < 0; }

    public GaussianRational conjugate() {
        return new GaussianRational(a, b, c.negate(), d);
    }

    public GaussianRational magnitudeSquared() { ... }
    public GaussianRational realPart()         { return new GaussianRational(a, b, BigInteger.ZERO, BigInteger.ONE); }
    public GaussianRational imaginaryPart()    { return new GaussianRational(c, d, BigInteger.ZERO, BigInteger.ONE); }
    public GaussianRational reciprocal()       { ... }

    public NumberType classify() {
        if (!isReal()) return NumberType.GAUSSIAN_RATIONAL;
        if (!isInteger()) return NumberType.RATIONAL_NUMBER;
        if (a.signum() > 0) return NumberType.NATURAL_NUMBER;
        if (a.signum() >= 0) return NumberType.WHOLE_NUMBER;
        return NumberType.INTEGER;
    }
```

### 3.6 NumberType Enum

```java
public enum NumberType {
    NATURAL_NUMBER("Natural Number"),
    WHOLE_NUMBER("Whole Number"),
    INTEGER("Integer"),
    RATIONAL_NUMBER("Rational Number"),
    GAUSSIAN_RATIONAL("Gaussian Rational");

    private final String label;

    NumberType(String label) { this.label = label; }
    public String getLabel() { return label; }
}
```

### 3.7 Pramana ID (UUID v5)

```java
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class PramanaId {

    public static final UUID NUM_NAMESPACE =
        UUID.fromString("a6613321-e9f6-4348-8f8b-29d2a3c86349");

    public static UUID uuidV5(UUID namespace, String name) {
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            // 1. Namespace UUID to bytes (big-endian)
            ByteBuffer nsBuffer = ByteBuffer.allocate(16);
            nsBuffer.putLong(namespace.getMostSignificantBits());
            nsBuffer.putLong(namespace.getLeastSignificantBits());
            sha1.update(nsBuffer.array());
            // 2. Name bytes (UTF-8)
            sha1.update(name.getBytes(StandardCharsets.UTF_8));
            // 3. Hash
            byte[] hash = sha1.digest();
            // 4. Set version 5
            hash[6] = (byte) ((hash[6] & 0x0F) | 0x50);
            // 5. Set variant RFC 4122
            hash[8] = (byte) ((hash[8] & 0x3F) | 0x80);
            // 6. Convert to UUID
            ByteBuffer buf = ByteBuffer.wrap(hash, 0, 16);
            return new UUID(buf.getLong(), buf.getLong());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-1 not available", e);
        }
    }
}

// On GaussianRational:
public String canonical() {
    return String.format("num:%s,%s,%s,%s", a, b, c, d);
}

public UUID pramanaId() {
    return PramanaId.uuidV5(PramanaId.NUM_NAMESPACE, canonical());
}

public String pramanaUri() {
    return "pra:" + pramanaId();
}
```

### 3.8 Formatting

```java
    public String toMixed()    { ... }  // "3 & 1/2 + 3/4 i"
    public String toImproper() { ... }  // "7/2 + 3/4 i"
    public String toRaw()      { return String.format("<%s,%s,%s,%s>", a, b, c, d); }

    @Override
    public String toString() { return canonical(); }
```

### 3.9 Intentionally Unsupported

```java
    public double magnitude() {
        throw new UnsupportedOperationException(
            "Complex magnitude produces irrationals. Use magnitudeSquared() for exact result.");
    }
    // phase(), toPolar(), sqrt() — same treatment
```

## 4. Item Model

### 4.1 Sealed Interface Hierarchy (Java 17+)

```java
public sealed interface PramanaItem permits PramanaEntity, PramanaProperty,
                                            PramanaProposition, PramanaSense {
    UUID uuid();
    ItemType type();
    Map<String, Object> properties();
    Map<String, UUID> edges();
}

public enum ItemType {
    ENTITY, PROPERTY, PROPOSITION, SENSE, EVIDENCE, STANCE_LINK
}
```

### 4.2 Record-Based Value Types

```java
public record PramanaEntity(
    UUID uuid,
    String label,
    UUID instanceOfId,
    UUID subclassOfId,
    Map<String, Object> properties,
    Map<String, UUID> edges
) implements PramanaItem {

    @Override
    public ItemType type() { return ItemType.ENTITY; }

    // Lazy resolution via graph context
    public PramanaEntity instanceOf(PramanaGraph graph) {
        return instanceOfId != null ? graph.getEntity(instanceOfId) : null;
    }
}
```

### 4.3 JSON Serialization

```java
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PramanaGraph {
    private final Map<UUID, PramanaItem> items = new LinkedHashMap<>();

    public static PramanaGraph fromFile(Path path) throws IOException {
        String json = Files.readString(path);
        // Parse JSON and hydrate items...
    }

    public static PramanaGraph fromFile(String path) throws IOException {
        return fromFile(Path.of(path));
    }

    public void toFile(Path path) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.writeString(path, gson.toJson(serialize()));
    }
}
```

## 5. ORM-Style Mapping

### 5.1 Annotation-Based Mapping

```java
import org.pramana.sdk.orm.*;

@PramanaEntity(instanceOf = "uuid-of-shinto-shrine-class")
public class ShintoShrine {

    @PramanaProperty("coordinates")
    private Coordinate coordinates;

    @PramanaProperty("Wikidata ID")
    private String wikidataId;

    @PramanaProperty(value = "part of", type = ShintoShrine.class)
    private ShintoShrine partOf;

    // Getters/setters...
}
```

### 5.2 Annotation Definitions

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PramanaEntity {
    String instanceOf();
    int flattenDepth() default 3;
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PramanaProperty {
    String value();                    // Property name or UUID
    boolean required() default false;
    boolean multiple() default false;
    Class<?> type() default Void.class;
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ComputedProperty {
    String value();  // Property name
}
```

### 5.3 Entity Mapper (Reflection-Based)

```java
public class EntityMapper {
    public <T> T map(PramanaItem item, Class<T> targetClass) {
        PramanaEntity annotation = targetClass.getAnnotation(PramanaEntity.class);
        if (annotation == null) {
            throw new IllegalArgumentException(targetClass + " is not a @PramanaEntity");
        }

        T instance = targetClass.getDeclaredConstructor().newInstance();

        for (Field field : targetClass.getDeclaredFields()) {
            PramanaProperty prop = field.getAnnotation(PramanaProperty.class);
            if (prop != null) {
                Object value = resolvePropertyValue(item, prop, field.getType());
                field.setAccessible(true);
                field.set(instance, value);
            }
        }

        return instance;
    }
}
```

### 5.4 Query Interface

```java
List<ShintoShrine> shrines = pramana.query(ShintoShrine.class)
    .where("coordinates", Operator.IS_NOT_NULL)
    .limit(100)
    .list();

ChemicalCompound water = pramana.getById(
    UUID.fromString("00000007-0000-4000-8000-000000000007"),
    ChemicalCompound.class
);
```

### 5.5 Configuration

```java
PramanaConfig config = PramanaConfig.builder()
    .flattenDepth(3)
    .lazyResolve(true)
    .includeProvenance(false)
    .build();
```

## 6. Data Sources

| Source | Class | Dependency |
|--------|-------|------------|
| `.pra` JSON file | `PraFileDataSource` | Gson (optional) or Jackson |
| GraphDB SPARQL | `SparqlDataSource` | `java.net.http.HttpClient` (JDK 11+) |
| Pramana REST API | `RestApiDataSource` | `java.net.http.HttpClient` |
| SQLite export | `SqliteDataSource` | `org.xerial:sqlite-jdbc` |

Java 11+ includes `java.net.http.HttpClient`, so SPARQL and REST API connectors need no external HTTP library.

## 7. Struct Pseudo-Classes

| Pseudo-class | Java type | Mapping strategy |
|-------------|-----------|-----------------|
| `num:` | `GaussianRational` | Custom class (see above) |
| `date:` | `PramanaDate` wrapping `java.time.LocalDate` | With `pramanaId()` method |
| `time:` | `PramanaTime` wrapping `java.time.LocalTime` | With `pramanaId()` method |
| `interval:` | `PramanaInterval` wrapping two `LocalDate` | With `pramanaId()` method |
| `coord:` | `Coordinate` record | `record Coordinate(double lat, double lon)` with `pramanaId()` |
| `chem:` | `ChemicalIdentifier` | InChI string wrapper with `pramanaId()` |
| `element:` | `ChemicalElement` | Atomic number wrapper with `pramanaId()` |

All pseudo-class wrappers use `PramanaId.uuidV5()` with their respective namespace UUIDs.

## 8. Java-Specific Considerations

### 8.1 Records (Java 16+)
Use records for immutable value types where appropriate:

```java
public record Coordinate(double lat, double lon) {
    public UUID pramanaId() {
        String canonical = String.format("coord:%s,%s", lat, lon);
        return PramanaId.uuidV5(COORD_NAMESPACE, canonical);
    }
}
```

### 8.2 Sealed Classes (Java 17+)
Use sealed interfaces to constrain the item type hierarchy:

```java
public sealed interface PramanaItem permits PramanaEntity, PramanaProperty,
                                            PramanaProposition, PramanaSense { ... }
```

### 8.3 Pattern Matching (Java 17+)
Pattern matching with `instanceof` for clean item type dispatch:

```java
if (item instanceof PramanaEntity entity) {
    System.out.println(entity.label());
} else if (item instanceof PramanaProperty property) {
    System.out.println(property.datatype());
}
```

### 8.4 Interfaces for Diamond Patterns
Java interfaces handle multiple classification naturally:

```java
public interface ChemicalCompound {
    String molecularFormula();
}

public interface QuantumSubstance {
    String quantumState();
}

public class Water extends PramanaEntity implements ChemicalCompound, QuantumSubstance {
    // Implements both interfaces
}
```

### 8.5 No Operator Overloading
The biggest ergonomic difference from C#/Python/Rust. Method chaining helps:

```java
// Instead of: result = a + b * c
GaussianRational result = a.add(b.multiply(c));
```

### 8.6 BigInteger Verbose API
`BigInteger` methods are more verbose than other languages' BigInt:
- `a.add(b)` not `a + b`
- `a.multiply(b)` not `a * b`
- `a.divide(b)` not `a / b`
- `a.signum()` not `a > 0`
- `a.gcd(b)` — at least this is a method

## 9. Testing Strategy

- **JUnit 5** as test framework
- **Cross-language test vectors** loaded from `test-vectors.json` in resources
- Test categories match the spec phases

```bash
# Maven
mvn test

# Gradle
./gradlew test
```

## 10. Implementation Priority

### Phase 1 - GaussianRational (core)
1. Implement `GaussianRational` final class with BigInteger components
2. Implement all arithmetic methods (`add`, `subtract`, `multiply`, `divide`, `mod`, `pow`)
3. Implement `Comparable<GaussianRational>`, `equals()`, `hashCode()`
4. Implement UUID v5 generation via `MessageDigest`
5. Implement `parse()`, formatting methods, and `classify()`
6. Write JUnit 5 test suite against cross-language test vectors

### Phase 2 - Base Item Model
1. Define sealed interface hierarchy with records
2. Implement `PramanaGraph` with JSON serialization (Gson)
3. Implement `.pra` file reader

### Phase 3 - ORM Mapping
1. Define `@PramanaEntity`, `@PramanaProperty`, `@ComputedProperty` annotations
2. Implement reflection-based `EntityMapper`
3. Implement query builder interface

### Phase 4 - Data Sources & Provenance
1. SPARQL connector (`java.net.http.HttpClient`)
2. REST API connector (`java.net.http.HttpClient`)
3. Provenance metadata on fields

### Phase 5 - Pseudo-Classes
1. `PramanaDate`, `PramanaTime`, `PramanaInterval` wrappers (java.time)
2. `Coordinate` record
3. `ChemicalIdentifier` / `ChemicalElement` wrappers
