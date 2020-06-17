package me.shaftesbury.utils.functional;

public interface SupplierWithExceptionDeclaration<T,EX extends Exception> {
    T supply() throws EX;
}
