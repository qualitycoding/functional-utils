package uk.co.qualitycoding.utils.functional.function;

@FunctionalInterface
public interface FunctionWithExceptionDeclaration<A, R, E extends Exception> {
    R apply(A a) throws E;
}
