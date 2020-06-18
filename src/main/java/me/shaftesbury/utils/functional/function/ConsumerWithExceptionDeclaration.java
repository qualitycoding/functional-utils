package me.shaftesbury.utils.functional.function;

@FunctionalInterface
public interface ConsumerWithExceptionDeclaration<T, EX extends Exception> {
    void accept(T t) throws EX;
}
