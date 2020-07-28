package uk.co.qualitycoding.utils.functional.function;

public interface SupplierWithExceptionDeclaration<T, EX extends Exception> {
    T supply() throws EX;
}
