package me.shaftesbury.utils.functional;

@FunctionalInterface
public interface ConsumerWithExceptionDeclaration<T,EX extends Exception> {
    void accept(T t) throws EX;
}
