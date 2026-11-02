# functional-utils

A small Java library of utilities for functional-style programming on Java 8 and later.
Maven coordinates: `uk.co.qualitycode:functional-utils` (the `pom.xml` version is `2.0-SNAPSHOT`).
Package: `uk.co.qualitycode.utils.functional`.

## What is in it

| Area | Classes |
|---|---|
| Collection algorithms | `Functional`: static `map`, `zip` and many other higher-order operations over `Iterable` and `Collection`, with curried variants. `GenericCollections`, `Iterators`, `Iterable2`, `ArrayIterable` (wraps an array as an `Iterable`) |
| Lists | `LispList`: a Lisp-style cons list |
| Optional values | `monad.Option`, `OptionStream`, and transformers in `monad.transformer` between `Option`, Vavr and Java `Optional`, `Either`, lists and sets |
| Resource handling | `Using`, `Using2`, `UsingWrapper`: try-with-resources style helpers |
| Argument checks | `Requirement`, `RequirementException` |
| Predicates and formatting | `IsBetween`, `BiPredicate`, `IndentBy` |
| Function types | `function.*` (unary and binary functions, and variants that declare checked exceptions); `primitive.integer.*` (int-specialised function interfaces and `Functional`) |
| Builders | `WithOptionalSetter`, `monad.ConformingObjectBuilder` |

It depends on [Vavr](https://vavr.io) (currently `1.0.0-alpha-3`) and Apache Commons Lang 3.

## Build and test

You need a JDK (the code targets Java 8) and Maven.

```bash
mvn test       # JUnit 5, AssertJ, AssertJ-Vavr, Mockito and Vavr-Test
mvn package
```

## Publishing

Two GitHub Actions workflows publish to Maven Central and to GitHub Packages when a GitHub release is
created. Signed releases use the `release-sign-artifacts` profile (`-DperformRelease=true`).

## Status

The `ToDo` file lists known gaps, for example more tests for the wildcard bounds and a review of the
lazy-iteration code. Older feature branches (`feature/*`, `publishing`) are historical.

## Licence

Apache License 2.0. See [LICENSE](LICENSE).
