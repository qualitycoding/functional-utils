package uk.co.qualitycode.utils.functional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Predicate;

public final class GenericCollections {
    public interface Generator<A> {
        Collection<A> initialiseEmptyContainer();

        Collection<A> createUnmodifiableContainer(Collection<A> c);
    }

    public static <A> Generator<A> createArrayListGenerator() {
        return new GenericCollections().new ArrayListGenerator<>();
    }

    public class ArrayListGenerator<A> implements Generator<A> {

        public Collection<A> initialiseEmptyContainer() {
            return new ArrayList<>();
        }

        public Collection<A> createUnmodifiableContainer(final Collection<A> c) {
            return java.util.Collections.unmodifiableList(new ArrayList<>(c));
        }
    }

    public static <A> Generator<A> createHashSetGenerator() {
        return new GenericCollections().new HashSetGenerator<>();
    }

    public class HashSetGenerator<A> implements Generator<A> {

        public Collection<A> initialiseEmptyContainer() {
            return new HashSet<>();
        }


        public Collection<A> createUnmodifiableContainer(final Collection<A> c) {
            return java.util.Collections.unmodifiableSet(io.vavr.collection.HashSet.ofAll(c).toJavaSet());
        }
    }

    public static <A> Iterable<A> filter(final Predicate<A> pred, final Generator<A> generator, final Iterable<A> input) {
        final Collection<A> output = generator.initialiseEmptyContainer();
        for (final A element : input)
            if (pred.test(element))
                output.add(element);

        return generator.createUnmodifiableContainer(output);
    }
}
