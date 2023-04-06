package uk.co.qualitycode.utils.functional.countedstream;

import uk.co.qualitycode.utils.functional.comparing.Comparator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static uk.co.qualitycode.utils.functional.comparing.Or.or;
import static uk.co.qualitycode.utils.functional.comparing.integer.HasSize.hasSize;
import static uk.co.qualitycode.utils.functional.comparing.integer.HasSize.isEmpty;
import static uk.co.qualitycode.utils.functional.comparing.integer.LessThan.lessThan;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

@ExtendWith(MockitoExtension.class)
class SeqCounterTest {
    @Nested
    class Creation {
        @Nested
        class Preconditions {
            @Test
            void factory1() {
                assertThatIllegalArgumentException()
                        .isThrownBy(() -> SeqCounter.$(null))
                        .withMessage("stream must not be null");
            }

            @Test
            void factory2() {
                assertThatIllegalArgumentException()
                        .isThrownBy(() -> SeqCounter.of(null))
                        .withMessage("stream must not be null");
            }
        }

        @Test
        void factory1() {
            final Collection<Object> c = Collections.singleton(new Object());
            final Stream<Object> str = c.stream();
            final SeqCounter<Object> counter = SeqCounter.$(str);
            assertThat(counter).isNotNull();
        }

        @Test
        void factory2() {
            final Collection<Object> c = Collections.singleton(new Object());
            final Stream<Object> str = c.stream();
            final SeqCounter<Object> counter = SeqCounter.of(str);
            assertThat(counter).isNotNull();
        }

        @Nested
        class Collector {
            @Test
            void toSeqCounter() {
                final Collection<Object> c = io.vavr.collection.List.fill(20, Object::new).asJava();
                final Counted<Object> counter = c.stream().collect(SeqCounter.toCountedSeq());
                assertThat(counter).isNotNull();
            }
        }
    }

    @Nested
    class Branch {
        @Nested
        class Preconditions {
            @Test
            void comparatorMustNotBeNull(@Mock final Function<Object, Object> then) {
                final Counted<Object> counter = Stream.empty().collect(SeqCounter.toCountedSeq());
                assertThatIllegalArgumentException().isThrownBy(() -> counter.sizeSatisfies(null, then)).withMessage("comparator must not be null");
            }

            @Test
            void thenClauseMustNotBeNull(@Mock final Comparator<Integer> comparator) {
                final Counted<Object> counter = Stream.empty().collect(SeqCounter.toCountedSeq());
                assertThatIllegalArgumentException().isThrownBy(() -> counter.sizeSatisfies(comparator, null)).withMessage("thenClause must not be null");
            }
        }

        @Test
        void sizeSatisfies(@Mock final Comparator<Integer> comparator, @Mock final Function<Object, Object> then) {
            final Collection<Object> c = io.vavr.collection.List.fill(20, Object::new).asJava();
            final Counted<Object> counter = c.stream().collect(SeqCounter.toCountedSeq());
            final BranchedCounter<Object, Object> branchedCounter = counter.sizeSatisfies(comparator, then);
            assertThat(branchedCounter).isNotNull();
        }
    }

    @Nested
    class Count {
        @Test
        void cannotAddElementToTheCountedStream() {
            final Collection<Object> c = io.vavr.collection.List.fill(20, Object::new).asJava();
            final Stream<Object> str = c.stream();
            final SeqCounter<Object> counter = SeqCounter.of(str);
            final Counted<Object> counted = counter.count();

            assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> counted.toList().add(new Object()));
        }

        @Test
        void applyPrecondition() {
            final Collection<Object> c = io.vavr.collection.List.fill(20, Object::new).asJava();

            assertThatIllegalArgumentException().isThrownBy(() -> c.stream().collect(SeqCounter.toCountedSeq()).applyToCounter(null)).withMessage("consumer must not be null");
        }

        @Test
        void applyToCounter() {
            final int howMany = 20;
            final Collection<Object> c = io.vavr.collection.List.fill(howMany, Object::new).asJava();
            final List<Integer> mutableState = new ArrayList<>();
            final Counted<Object> objectCounted = c.stream().collect(SeqCounter.toCountedSeq()).applyToCounter(mutableState::add);

            assertThat(objectCounted).isNotNull();
            assertThat(mutableState).containsExactly(20);
        }
    }

    @Nested
    class Convert {
        @Test
        void toStream() {
            final Collection<Object> c = io.vavr.collection.List.fill(20, Object::new).asJava();
            final Stream<Object> str = c.stream();
            final SeqCounter<Object> counter = SeqCounter.of(str);
            final Counted<Object> counted = counter.count();

            assertThat(counted.toStream()).containsExactlyElementsOf(c);
        }

        @Test
        void toList() {
            final Collection<Object> c = io.vavr.collection.List.fill(20, Object::new).asJava();
            final Stream<Object> str = c.stream();
            final SeqCounter<Object> counter = SeqCounter.of(str);
            final Counted<Object> counted = counter.count();

            assertThat(counted.toList()).containsExactlyElementsOf(c);
        }
    }

    @Nested
    class OrElse {
        @Test
        void functionMustNotBeNull() {
            final BranchedCounter<Object, Boolean> counter = Stream.empty()
                    .collect(SeqCounter.toCountedSeq())
                    .sizeSatisfies(or(isEmpty(), hasSize(lessThan(5))), __ -> true);

            assertThatIllegalArgumentException().isThrownBy(() -> counter.orElse(null)).withMessage("elseClause must not be null");
        }

        @Test
        void orElse() {
            final BranchedCounter<Object, Boolean> counter = Stream.empty()
                    .collect(SeqCounter.toCountedSeq())
                    .sizeSatisfies(or(isEmpty(), hasSize(lessThan(5))), __ -> true);

            assertThat(counter.orElse(__ -> false)).isNotNull();
        }
    }

    @Nested
    class CounterResultsIsEmpty {
        @Test
        void resultsIsEmptyWhenInputIsEmpty() {
            final CounterResults<Object, Boolean> counter = Stream.empty()
                    .collect(SeqCounter.toCountedSeq())
                    .sizeSatisfies(or(isEmpty(), hasSize(lessThan(5))), __ -> true)
                    .orElse(__ -> false);
            assertThat(counter).isNotNull();
            assertThat(counter.getResults()).isEmpty();
        }
    }

    @Nested
    class Usage {
        @ParameterizedTest
        @CsvSource({"3,true", "7,false"})
        void thisIsHowWeDoIt(final int upperBound, final boolean expected) {
            final List<Integer> counter = new ArrayList<>();
            final List<Integer> c = io.vavr.collection.List.range(0, upperBound).asJava();
            assertThat(c.stream()
                    .collect(SeqCounter.toCountedSeq())
                    .applyToCounter(counter::add)
                    .sizeSatisfies(or(isEmpty(), hasSize(lessThan(5))), __ -> true)
                    .orElse(__ -> false)
                    .get())
                    .satisfies(t -> {
                        assertThat(t._1).isEqualTo(c);
                        assertThat(t._2).containsOnly(expected);
                    });
            assertThat(counter).containsExactly(upperBound);

            assertThat(c.stream()
                    .collect(SeqCounter.toCountedSeq())
                    .sizeSatisfies(or(isEmpty(), hasSize(lessThan(5))), __ -> true)
                    .orElse(__ -> false)
                    .getResults())
                    .containsOnly(expected);

            assertThat(c.stream()
                    .collect(SeqCounter.toCountedSeq())
                    .sizeSatisfies(or(isEmpty(), hasSize(lessThan(5))), __ -> true)
                    .orElse(__ -> false)
                    .getCollection())
                    .containsExactlyElementsOf(c);
        }

        @Test
        void thisIsHowWeDoItWithEmptyCollection() {
            assertThat(Stream.of()
                    .collect(SeqCounter.toCountedSeq())
                    .sizeSatisfies(hasSize(1), __ -> true)
                    .orElse(__ -> false)
                    .get())
                    .satisfies(t -> {
                        assertThat(t._1).isEmpty();
                        assertThat(t._2).isEmpty();
                    });
        }
    }
}
