package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;
import static uk.co.qualitycode.utils.functional.Functional.flatMap;
import static uk.co.qualitycode.utils.functional.FunctionalTest.doublingGenerator;
import static uk.co.qualitycode.utils.functional.FunctionalTest.repeat;

class Functional_FlatMap_Test {
    @Test
    void preconditions() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> flatMap(null, new ArrayList<>()))
                .withMessage("flatMap(Function<A,B>,Collection<A>): f must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> flatMap(i -> Collections.emptyList(), (List) null))
                .withMessage("flatMap(Function<A,B>,Collection<A>): input must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> flatMap(null, mock(Iterable.class)))
                .withMessage("flatMap(Function<A,B>,Iterable<A>): f must not be null");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> flatMap(i -> Collections.emptyList(), (Iterable) null))
                .withMessage("flatMap(Function<A,B>,Iterable<A>): input must not be null");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> flatMap(null))
                .withMessage("flatMap(Function<A,Iterable<B>>): f must not be null");
    }

    @Test
    void flatMapUsingIterable() {
        final Iterable<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
        final List<Integer> output = flatMap(Collections::singleton, expected);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void flatMapUsingList() {
        final List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
        final List<Integer> output = flatMap(Collections::singleton, expected);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void flatMapUsingIterableReturnsImmutableList() {
        final List<Integer> output = flatMap(Collections::singleton, (Iterable) Arrays.asList(1, 2, 3, 4, 5));
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> output.add(10));
    }

    @Test
    void flatMapUsingListReturnsImmutableList() {
        final List<Integer> output = flatMap(Collections::singleton, Arrays.asList(1, 2, 3, 4, 5));
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> output.add(10));
    }

    @Test
    void curriedFlatMap() {
        final Function<Iterable<Object>, List<Object>> flatMapFunc = flatMap(Collections::singleton);
        final List<Object> output = flatMapFunc.apply(Arrays.asList(1, 2, 3, 4, 5));
        assertThat(output).containsExactly(1, 2, 3, 4, 5);
    }

    @Nested
    class Lazy extends FiniteIterableTest<Integer, Integer, Integer> {
        @Test
        void preconditions() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Lazy.flatMap(null, mock(Iterable.class)))
                    .withMessage("Lazy.flatMap(Function<A,Iterable<B>>,Iterable<A>): f must not be null");
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Lazy.flatMap(i -> Collections.emptyList(), (Iterable) null))
                    .withMessage("Lazy.flatMap(Function<A,Iterable<B>>,Iterable<A>): input must not be null");

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> Functional.Lazy.flatMap(null))
                    .withMessage("Lazy.flatMap(Function<A,Iterable<B>>): f must not be null");
        }

        @Test
        void flatMapUsingIterable() {
            final Iterable<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final Iterable<Integer> output = Functional.Lazy.flatMap(Collections::singleton, expected);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Test
        void curriedFlatMapUsingIterable() {
            final Iterable<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
            final Iterable<Integer> output = Functional.Lazy.<Integer, Integer>flatMap(Collections::singleton).apply(expected);
            assertThat(output).containsExactlyElementsOf(expected);
        }

        @Override
        protected Collection<Integer> initialValues() {
            return Arrays.asList(1, 2, 3, 4, 5);
        }

        @Override
        protected Iterable<Integer> testFunction(final Iterable<Integer> l) {
            return Functional.Lazy.flatMap(Collections::singleton, l);
        }

        @Override
        protected String methodNameInExceptionMessage() {
            return "Lazy.flatMap(Function<T,Iterable<U>>,Iterable<T>)";
        }

        @Override
        protected int noOfElementsInOutput() {
            return 5;
        }
    }


//    @Nested
//    class RecMap {
//        @Test
//        void recMapTest1() {
//            final Collection<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
//            final Iterable<String> output = Functional.Rec.flatMap(Collections::singleton, input);
//            assertThat(output).isEqualTo(Arrays.asList("1", "2", "3", "4", "5"));
//        }
//    }

    //    @Nested
//    class SetMap {
//        @Test
//        void setMapTest1() {
//            final Set<Integer> input = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
//            final Set<String> output = Functional.set.flatMap(Collections::singleton, input);
//            final Set<String> expected = new HashSet<>(Arrays.asList("1", "2", "3", "4", "5"));
//            assertThat(expected.containsAll(output)).isTrue();
//            assertThat(output.containsAll(expected)).isTrue();
//        }
//    }

    @Nested
    class SetFlatMap {
        @Test
        void setCollectTest1() {
            final Iterable<Integer> input = Functional.init(doublingGenerator, 5);
            final Set<Integer> output = Functional.set.flatMap(repeat(3), input);
            final Set<Integer> expected = new HashSet<>(Arrays.asList(2, 4, 6, 8, 10));

            assertThat(expected.containsAll(output)).isTrue();
            assertThat(output.containsAll(expected)).isTrue();
        }

        @Test
        void setCollectTest2() {
            final Iterable<Integer> input = Functional.init(doublingGenerator, 5);
            final Set<Integer> output1 = Functional.set.flatMap(repeat(3), input);
            final Set<Integer> output2 = output1;
            final Set<Integer> expected = new HashSet<>(Arrays.asList(2, 4, 6, 8, 10));

            assertThat(expected.containsAll(output1)).isTrue();
            assertThat(output1.containsAll(expected)).isTrue();
            assertThat(expected.containsAll(output2)).isTrue();
            assertThat(output2.containsAll(expected)).isTrue();
        }
    }
}