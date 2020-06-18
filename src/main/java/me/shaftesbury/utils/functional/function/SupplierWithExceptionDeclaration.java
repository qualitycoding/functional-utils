package me.shaftesbury.utils.functional.function;

public interface SupplierWithExceptionDeclaration<T, EX extends Exception> {
    T supply() throws EX;
}
