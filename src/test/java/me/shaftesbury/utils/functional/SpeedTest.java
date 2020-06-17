package me.shaftesbury.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class SpeedTest {
    public static <A, B> List<B> mapWithIterable(final Function<A, ? extends B> f, final Iterable<? extends A> input) {
        final List<B> output = new ArrayList<>();
        for (final A a : input)
            output.add(f.apply(a));
        return Collections.unmodifiableList(output);
    }

    public static <A, B> List<B> mapWithCollection(final Function<A, ? extends B> f, final Collection<? extends A> input) {
        final List<B> output = new ArrayList<>(input.size());
        for (final A a : input)
            output.add(f.apply(a));
        return Collections.unmodifiableList(output);
    }

    public static <A, B> List<B> mapWithInstanceOf(final Function<A, ? extends B> f, final Iterable<? extends A> input) {
        final List<B> output = input instanceof Collection<?> ? new ArrayList<>(((Collection<? extends A>) input).size()) : new ArrayList<>();
        for (final A a : input)
            output.add(f.apply(a));
        return Collections.unmodifiableList(output);
    }


    @Test
    void MapTest1() {
        final int howMany = 10;
        final Collection<Integer> input = Functional.init(Functional.constant(10), 1000000);
        {
            final long start = System.currentTimeMillis();
            for (int i = 0; i < howMany; ++i) {
                mapWithIterable(Functional.dStringify(), input);
            }
            final long howLong = System.currentTimeMillis() - start;
            System.out.println(howLong + "ms to map using Iterable and whatever array resizing the JVM implements");
        }
        {
            final long start = System.currentTimeMillis();
            for (int i = 0; i < howMany; ++i) {
                mapWithCollection(Functional.dStringify(), input);
            }
            final long howLong = System.currentTimeMillis() - start;
            System.out.println(howLong + "ms to map using Collection");
        }
        {
            final long start = System.currentTimeMillis();
            for (int i = 0; i < howMany; ++i) {
                mapWithInstanceOf(Functional.dStringify(), input);
            }
            final long howLong = System.currentTimeMillis() - start;
            System.out.println(howLong + "ms to map using a Collection and instanceof to determine the initial array size");
        }
    }

    private static Function<Integer, Integer> DoublingGenerator = a -> 2 * a;

    public static <A> List<A> filterWithIterable(final Function<? super A, Boolean> pred, final Iterable<A> input) {
        final List<A> output = new ArrayList<>();
        for (final A element : input)
            if (pred.apply(element))
                output.add(element);

        return Collections.unmodifiableList(output);
    }

    public static <A> List<A> filterWithCollection(final Function<? super A, Boolean> pred, final Collection<A> input) {
        final List<A> output = new ArrayList<>(input.size());
        for (final A element : input)
            if (pred.apply(element))
                output.add(element);

        return Collections.unmodifiableList(output);
    }

    public static <A> List<A> filterWithHalfCollection(final Function<? super A, Boolean> pred, final Collection<A> input) {
        final List<A> output = new ArrayList<>((input.size() / 2) + 1);
        for (final A element : input)
            if (pred.apply(element))
                output.add(element);

        return Collections.unmodifiableList(output);
    }

    public static <A> List<A> filterWithInstanceOf(final Function<? super A, Boolean> pred, final Iterable<A> input) {
        final List<A> output = input instanceof Collection<?> ? new ArrayList<>(((Collection<A>) input).size()) : new ArrayList<>();
        for (final A element : input)
            if (pred.apply(element))
                output.add(element);

        return Collections.unmodifiableList(output);
    }

    @Test
    void filterTestWithEmptyResultsAndIterable() {
        final int howMany = 10;
        final Collection<Integer> input = Functional.init(DoublingGenerator, 10000000);

        {
            final long start = System.currentTimeMillis();
            for (int i = 0; i < howMany; ++i) {
                filterWithIterable(Functional.isOdd, input);
            }
            final long howLong = System.currentTimeMillis() - start;
            System.out.println(howLong + "ms to filter using Iterable and whatever array resizing the JVM implements");
        }

        final Collection<Integer> output1 = filterWithIterable(Functional.isOdd, input);

        System.out.println("Size of output1 (Iterable) is " + output1.size());
    }

    @Test
    void filterTestWithEmptyResultsAndCollection() {
        final int howMany = 10;
        final Collection<Integer> input = Functional.init(DoublingGenerator, 10000000);

        {
            final long start = System.currentTimeMillis();
            for (int i = 0; i < howMany; ++i) {
                filterWithCollection(Functional.isOdd, input);
            }
            final long howLong = System.currentTimeMillis() - start;
            System.out.println(howLong + "ms to filter using Collection");
        }

        final Collection<Integer> output2 = filterWithCollection(Functional.isOdd, input);

        System.out.println("Size of output2 (Collection) is " + output2.size());
    }

    @Test
    void filterTestWithEmptyResultsAndInstanceOf() {
        final int howMany = 10;
        final Collection<Integer> input = Functional.init(DoublingGenerator, 10000000);

        {
            final long start = System.currentTimeMillis();
            for (int i = 0; i < howMany; ++i) {
                filterWithInstanceOf(Functional.isOdd, input);
            }
            final long howLong = System.currentTimeMillis() - start;
            System.out.println(howLong + "ms to filter using a Collection and instanceof to determine the initial array size");
        }

        final Collection<Integer> output3 = filterWithInstanceOf(Functional.isOdd, input);

        System.out.println("Size of output3 (instanceof) is " + output3.size());
    }

    @Test
    void filterTestWithEmptyResultsAndHalfSizeCollection() {
        final int howMany = 10;
        final Collection<Integer> input = Functional.init(DoublingGenerator, 10000000);

        {
            final long start = System.currentTimeMillis();
            for (int i = 0; i < howMany; ++i) {
                filterWithHalfCollection(Functional.isOdd, input);
            }
            final long howLong = System.currentTimeMillis() - start;
            System.out.println(howLong + "ms to filter using a Collection initialised to half-size to determine the initial array size");
        }

        final Collection<Integer> output4 = filterWithHalfCollection(Functional.isOdd, input);

        System.out.println("Size of output4 (Collection half-size) is " + output4.size());
    }

    @Test
    void filterTestWithHalfSizeResultsAndIterable() {
        final int howMany = 10;
        final Collection<Integer> input = Functional.init(Functional.identity(), 10000000);

        {
            final long start = System.currentTimeMillis();
            for (int i = 0; i < howMany; ++i) {
                filterWithIterable(Functional.isOdd, input);
            }
            final long howLong = System.currentTimeMillis() - start;
            System.out.println(howLong + "ms to filter using Iterable and whatever array resizing the JVM implements");
        }

        final Collection<Integer> output1 = filterWithIterable(Functional.isOdd, input);

        System.out.println("Size of output1 (Iterable) is " + output1.size());
    }

    @Test
    void filterTestWithHalfSizeResultsAndCollection() {
        final int howMany = 10;
        final Collection<Integer> input = Functional.init(Functional.identity(), 10000000);

        {
            final long start = System.currentTimeMillis();
            for (int i = 0; i < howMany; ++i) {
                filterWithCollection(Functional.isOdd, input);
            }
            final long howLong = System.currentTimeMillis() - start;
            System.out.println(howLong + "ms to filter using Collection");
        }

        final Collection<Integer> output2 = filterWithCollection(Functional.isOdd, input);

        System.out.println("Size of output2 (Collection) is " + output2.size());
    }

    @Test
    void filterTestWithHalfSizeResultsAndInstanceOf() {
        final int howMany = 10;
        final Collection<Integer> input = Functional.init(Functional.identity(), 10000000);

        {
            final long start = System.currentTimeMillis();
            for (int i = 0; i < howMany; ++i) {
                filterWithInstanceOf(Functional.isOdd, input);
            }
            final long howLong = System.currentTimeMillis() - start;
            System.out.println(howLong + "ms to filter using a Collection and instanceof to determine the initial array size");
        }

        final Collection<Integer> output3 = filterWithInstanceOf(Functional.isOdd, input);

        System.out.println("Size of output3 (instanceof) is " + output3.size());
    }

    @Test
    void filterTestWithHalfSizeResultsAndHalfSizeCollection() {
        final int howMany = 10;
        final Collection<Integer> input = Functional.init(Functional.identity(), 10000000);

        {
            final long start = System.currentTimeMillis();
            for (int i = 0; i < howMany; ++i) {
                filterWithHalfCollection(Functional.isOdd, input);
            }
            final long howLong = System.currentTimeMillis() - start;
            System.out.println(howLong + "ms to filter using a Collection initialised to half-size to determine the initial array size");
        }

        final Collection<Integer> output4 = filterWithHalfCollection(Functional.isOdd, input);

        System.out.println("Size of output4 (Collection half-size) is " + output4.size());
    }
}
