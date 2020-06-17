package me.shaftesbury.utils.functional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.fail;

public class IteratorsTest
{
    private static Function<Integer, Integer> DoublingGenerator = a -> 2*a;

    @Test void reverseTest1()
    {
        final List<Integer> li = Functional.init(DoublingGenerator, 10);
        final Collection<Integer> expected = Arrays.asList(20,18,16,14,12,10,8,6,4,2);
        final Iterable<Integer> output = Iterators.reverse(li);
        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test void cantReverseAnEmptyList()
    {
        final List<Integer> li = new ArrayList<>();
        assertThatIllegalArgumentException().isThrownBy(()->Iterators.reverse(li));
    }

    @Test void cantRemoveFromReversedIterator()
    {
        final Iterable<Integer> rv = Iterators.reverse(Arrays.asList(1, 2, 3, 4));
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(()->rv.iterator().remove());
    }

    private final Map<Integer, String> map = new LinkedHashMap<>();
    private final List<String> list = new ArrayList<>();

    @BeforeEach
    public void initialise()
    {
        map.put(-1, "minus one");
        map.put(0, "zero zero UFO");
        map.put(1, "one");
        map.put(2, "two");
        map.put(10, "ten");
        map.put(97, "ninety seven");
        map.put(100, "one hundred");
        list.addAll(map.values());
        Collections.sort(list);
    }

    @AfterEach
    public void clear()
    {
        map.clear();
        list.clear();
    }

    @Test void steppedEnumTest1()
    {
        final StringBuilder sb = new StringBuilder();
        for (final String s : Iterators.everyNth(3, list))
            sb.append(s.toString());

        final String expected =
                new StringBuilder("minus one").append("one hundred").append("zero zero UFO").toString();
        assertThat( sb.toString()).isEqualTo(expected);
    }

    @Test void steppedEnumTest2()
    {
        final StringBuilder sb = new StringBuilder();
        for (final String s : Iterators.everyNth(2, list))
            sb.append(s.toString());

        final String expected =
                new StringBuilder("minus one").append("one").append("ten").append("zero zero UFO").toString();
        assertThat( sb.toString()).isEqualTo(expected);
    }

    @Test void steppedEnumTest3()
    {
        final StringBuilder sb = new StringBuilder();
        for (final String s : Iterators.everyNth(1, list))
            sb.append(s.toString());

        final String expected =
                new StringBuilder("minus one").append("ninety seven").append("one").append("one hundred").append("ten").append("two").
                        append("zero zero UFO").toString();
        assertThat( sb.toString()).isEqualTo(expected);
    }

    @Test void steppedEnumTest4()
    {
        final StringBuilder sb = new StringBuilder();
        for (final String s : Iterators.everyNth(4, Iterators.reverse(list)))
            sb.append(s.toString());

        final String expected =
                new StringBuilder("zero zero UFO").append("one").toString();
        assertThat( sb.toString()).isEqualTo(expected);
    }

    @Test void cantStepLessThanOne() {
        assertThatIllegalArgumentException().isThrownBy(()->Iterators.everyNth(0,Arrays.asList(1,2,34)));
    }

    @Test
    void cantRemoveFromEveryNth() {
        final Iterable<Integer> integers = Iterators.everyNth(2, Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8));
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(()->integers.iterator().remove());
    }

    @Test
    void cantRestartIteratorTest1() {
        final Iterable<Integer> rv = Iterators.reverse(Arrays.asList(1, 2, 3));
        try {
            rv.iterator();
        } catch(final UnsupportedOperationException e) { fail("Shouldn't reach here"); }
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(rv::iterator);
    }

    @Test
    void cantRestartIteratorTest2() {
        final Iterable<Integer> rv = Iterators.everyNth(2, Arrays.asList(1, 2, 3));
        try {
            rv.iterator();
        } catch(final UnsupportedOperationException e) { fail("Shouldn't reach here"); }
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(rv::iterator);
    }

    @Test void everyNthRepeatedHasNextTest()
    {
        final Iterable<Integer> integers = Iterators.everyNth(2, Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        final Iterator<Integer> iterator = integers.iterator();
        assertThat(iterator.hasNext()).isTrue();
        assertThat( iterator.next()).isEqualTo(Integer.valueOf(1));
        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.hasNext()).isTrue();
        assertThat( iterator.next()).isEqualTo(Integer.valueOf(3));
        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.hasNext()).isTrue();
        assertThat( iterator.next()).isEqualTo(Integer.valueOf(5));
    }

    @Test void everyNthRepeatedNextTest()
    {
        final Iterable<Integer> integers = Iterators.everyNth(2, Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        final Iterator<Integer> iterator = integers.iterator();
        assertThat( iterator.next()).isEqualTo(Integer.valueOf(1));
        assertThat( iterator.next()).isEqualTo(Integer.valueOf(3));
        assertThat( iterator.next()).isEqualTo(Integer.valueOf(5));
    }
}
