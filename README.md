# pramana-java-sdk

Java SDK for the [Pramana](https://pramana.dev) knowledge graph. Provides exact-arithmetic value types, item model mapping, and data source connectors for working with Pramana data in Java.

## Status

**Pre-implementation** - Project structure and implementation plan documented. See [IMPLEMENTATION.md](IMPLEMENTATION.md) for the full design.

## Key Features (Planned)

- **GaussianRational** (standard short name: **Gauss**; Gaussian integers: **Gint**) - Exact complex rational arithmetic (`a/b + (c/d)i`) with `java.math.BigInteger`
- **Deterministic Pramana IDs** - UUID v5 generation matching the canonical Pramana web app
- **Modern Java** - Sealed interfaces, records, pattern matching (Java 17+)
- **Annotation-based ORM** - `@PramanaEntity` and `@PramanaProperty` with reflection-based mapping
- **Multiple data sources** - `.pra` files, SPARQL, REST API, SQLite

## Installation (Future)

### Maven
```xml
<dependency>
    <groupId>org.pramana</groupId>
    <artifactId>pramana-sdk</artifactId>
    <version>0.1.0</version>
</dependency>
```

### Gradle
```kotlin
implementation("org.pramana:pramana-sdk:0.1.0")
```

## Quick Example (Planned API)

```java
import org.pramana.sdk.GaussianRational;

GaussianRational half = new GaussianRational(1, 2, 0, 1);   // 1/2
GaussianRational third = new GaussianRational(1, 3, 0, 1);  // 1/3
GaussianRational result = half.add(third);                    // 5/6

System.out.println(result.pramanaId());  // deterministic UUID v5
```

## Documentation

- [General SDK Specification](08_SDK_LIBRARY_SPECIFICATION.md) - Cross-language design spec
- [Java Implementation Guide](IMPLEMENTATION.md) - Java-specific implementation details

## Acknowledgments

The Gauss and Gint implementations across all Pramana SDKs were heavily inspired by [gaussian_integers](https://github.com/alreich/gaussian_integers) by **Alfred J. Reich, Ph.D.**, which provides exact arithmetic for Gaussian integers and Gaussian rationals in Python.

## Pramana SDK Family

| Language | Repository | Package |
|----------|-----------|---------|
| C# / .NET | [pramana-dotnet-sdk](https://github.com/Emma-Leonhart/pramana-dotnet-sdk) | `Pramana.SDK` (NuGet) |
| Python | [pramana-python-sdk](https://github.com/Emma-Leonhart/pramana-python-sdk) | `pramana-sdk` (PyPI) |
| npm (TypeScript or JavaScript) | [pramana-npm-sdk](https://github.com/Emma-Leonhart/pramana-ts-sdk) | `@pramana/sdk` (npm) |
| Java | **pramana-java-sdk** (this repo) | `org.pramana:pramana-sdk` (Maven) |
| Rust | [pramana-rust-sdk](https://github.com/Emma-Leonhart/pramana-rust-sdk) | `pramana-sdk` (crates.io) |
| Go | [pramana-go-sdk](https://github.com/Emma-Leonhart/pramana-go-sdk) | `github.com/Emma-Leonhart/pramana-go-sdk` |

All SDKs implement the same core specification and must produce identical results for UUID v5 generation, canonical string normalization, and arithmetic operations.
