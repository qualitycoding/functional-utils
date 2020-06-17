package me.shaftesbury.utils.functional;

@FunctionalInterface
public interface FunctionWithExceptionDeclaration<A,R,E extends Exception> {
    R apply(A a) throws E;
}
