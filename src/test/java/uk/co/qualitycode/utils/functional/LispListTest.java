package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Test;
import uk.co.qualitycode.utils.functional.LispList.EmptyListHasNoHead;
import uk.co.qualitycode.utils.functional.LispList.EmptyListHasNoTail;
import uk.co.qualitycode.utils.functional.LispList.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static uk.co.qualitycode.utils.functional.LispList.cadr;
import static uk.co.qualitycode.utils.functional.LispList.car;
import static uk.co.qualitycode.utils.functional.LispList.cdr;
import static uk.co.qualitycode.utils.functional.LispList.compose;
import static uk.co.qualitycode.utils.functional.LispList.cons;
import static uk.co.qualitycode.utils.functional.LispList.filter;
import static uk.co.qualitycode.utils.functional.LispList.fold;
import static uk.co.qualitycode.utils.functional.LispList.foldRight;
import static uk.co.qualitycode.utils.functional.LispList.list;
import static uk.co.qualitycode.utils.functional.LispList.map;
import static uk.co.qualitycode.utils.functional.LispList.reverse;

class LispListTest {
    @Test
    void headTest1() {
        final List<Integer> l = list(1, LispList.nil());
        assertThat(l.head()).isEqualTo(Integer.valueOf(1));
    }

    @Test
    void tailTest1() {
        final List<Integer> l = list(1, list(2, LispList.nil()));
        assertThat(l.tail().head()).isEqualTo(Integer.valueOf(2));
    }

    @Test
    void equalsTest1() {
        final List<Integer> l = list(1, list(2, LispList.nil()));
        assertThat(l.tail()).isEqualTo(list(2, LispList.nil()));
    }

    @Test
    void mapTest1() {
        final List<Integer> input = list(1, list(2, list(3, list(4, list(5, LispList.nil())))));
        final List<String> output = map(Functional.stringify(), input);
        assertThat(output.head()).isEqualTo("1");
        assertThat(output.tail().head()).isEqualTo("2");
        assertThat(output.tail().tail().head()).isEqualTo("3");
        assertThat(output.tail().tail().tail().head()).isEqualTo("4");
        assertThat(output.tail().tail().tail().tail().head()).isEqualTo("5");
    }

    @Test
    void equalsTest2() {
        final List<Integer> input = list(1, list(2, list(3, list(4, list(5, LispList.nil())))));
        final List<String> output = map(Functional.stringify(), input);
        assertThat(output).isEqualTo(list("1", list("2", list("3", list("4", list("5", LispList.nil()))))));
    }

    @Test
    void filterTest1() {
        final List<Integer> input = list(2, list(4, list(6, list(8, list(10, LispList.nil())))));
        final List<Integer> oddElems = filter(Functional::isOdd, input);
        assertThat(oddElems).isEqualTo(LispList.<Integer>nil());
    }

    @Test
    void filterTest2() {
        final List<Integer> input = list(2, list(5, list(7, list(8, list(10, LispList.nil())))));
        final List<Integer> evenElems = filter(Functional::isEven, input);

        assertThat(evenElems.head()).isEqualTo(Integer.valueOf(2));
        assertThat(evenElems.tail().head()).isEqualTo(Integer.valueOf(8));
        assertThat(evenElems.tail().tail().head()).isEqualTo(Integer.valueOf(10));
    }

    @Test
    void equalsTest3() {
        final List<Integer> input = list(2, list(5, list(7, list(8, list(10, LispList.nil())))));
        final List<Integer> evenElems = filter(Functional::isEven, input);

        final List<Integer> expected = list(2, list(8, list(10, LispList.nil())));
        assertThat(evenElems).isEqualTo(expected);
    }

    @Test
    void filterTest3() {
        final List<Integer> input = list(2, list(4, list(6, list(8, list(10, LispList.nil())))));
        final Integer limit = 5;
        final List<Integer> highElems = filter(
                a -> a > limit, input);

        final List<Integer> expected = list(6, list(8, list(10, LispList.nil())));
        assertThat(highElems).isEqualTo(expected);
    }

    @Test
    void filterTest4() {
        final List<Integer> input = list(2, list(4, list(6, list(8, list(10, LispList.nil())))));
        final Integer limit = 10;
        final List<Integer> output = filter(
                a -> a > limit, input);

        assertThat(output).isEqualTo(LispList.nil());
    }

    @Test
    void filterTest5() {
        final List<Integer> input = list(2, list(4, list(6, list(8, list(10, list(12, list(14, list(16, list(18, list(20, LispList.nil()))))))))));
        final List<Integer> expected = list(4, list(8, list(12, list(16, list(20, LispList.nil())))));
        final List<Integer> output = filter(
                a -> a % 4 == 0, input);

        assertThat(output).isEqualTo(expected);
    }

    @Test
    void consTest1() {
        final List<Integer> input = cons(2, cons(4, cons(6, compose(8, 10))));
        assertThat(input.head()).isEqualTo(Integer.valueOf(2));
        assertThat(input.tail().head()).isEqualTo(Integer.valueOf(4));
        assertThat(input.tail().tail().head()).isEqualTo(Integer.valueOf(6));
        assertThat(input.tail().tail().tail().head()).isEqualTo(Integer.valueOf(8));
        assertThat(input.tail().tail().tail().tail().head()).isEqualTo(Integer.valueOf(10));
    }

    @Test
    void carTest1() {
        final List<Integer> input = list(2, list(4, list(6, list(8, list(10, LispList.nil())))));
        assertThat(car(input)).isEqualTo(Integer.valueOf(2));
    }

    @Test
    void cdrTest1() {
        final List<Integer> input = list(2, list(4, list(6, list(8, list(10, LispList.nil())))));
        assertThat(cdr(input)).isEqualTo(list(4, list(6, list(8, list(10, LispList.nil())))));
    }

    @Test
    void cadrTest1() {
        final List<Integer> input = list(2, list(4, list(6, list(8, list(10, LispList.nil())))));
        assertThat(cadr(input)).isEqualTo(Integer.valueOf(4));
    }

    @Test
    void reverseTest1() {
        final List<Integer> input = list(2, list(4, list(6, list(8, list(10, LispList.nil())))));
        final List<Integer> expected = list(10, list(8, list(6, compose(4, 2))));
        final List<Integer> output = reverse(input);
        assertThat(output).isEqualTo(expected);
    }

    @Test
    void foldTest1() {
        final List<Integer> input = list(2, list(4, list(6, list(8, list(10, LispList.nil())))));
        final Integer expected = 30;
        final Integer output = fold(Integer::sum, 0, input);
        assertThat(output).isEqualTo(expected);
    }

    @Test
    void foldTest2() {
        final List<String> input = list("2", list("4", list("6", list("8", LispList.nil()))));
        final String expected = "2468";
        final String output = fold((state, o) -> state + o, "", input);
        assertThat(output).isEqualTo(expected);
    }

    @Test
    void foldRightTest1() {
        final List<Integer> input = list(2, list(4, list(6, list(8, LispList.nil()))));
        final String expected = "8642";
        final String output = foldRight((o, state) -> state + o, "", input);
        assertThat(output).isEqualTo(expected);
    }

    @Test
    void emptyListHasNoHeadTest1() {
        assertThatExceptionOfType(EmptyListHasNoHead.class).isThrownBy(() -> LispList.<Integer>nil().head());
    }

    @Test
    void emptyListHasNoTailTest1() {
        assertThatExceptionOfType(EmptyListHasNoTail.class).isThrownBy(() -> LispList.<Integer>nil().tail());
    }

    private static final int LONGER_THAN_THE_STACK_ALLOWS = 200_000;

    private static List<Integer> longList() {
        return java.util.stream.IntStream.range(0, LONGER_THAN_THE_STACK_ALLOWS)
                .boxed()
                .reduce(LispList.<Integer>nil(), (l, i) -> cons(i, l), (a, b) -> b);
    }

    @Test
    void foldDoesNotOverflowTheStackOnALongList() {
        assertThat(fold((count, i) -> count + 1, 0, longList())).isEqualTo(LONGER_THAN_THE_STACK_ALLOWS);
    }

    @Test
    void foldRightDoesNotOverflowTheStackOnALongList() {
        assertThat(foldRight((i, count) -> count + 1, 0, longList())).isEqualTo(LONGER_THAN_THE_STACK_ALLOWS);
    }

    @Test
    void mapDoesNotOverflowTheStackOnALongList() {
        final List<Integer> doubled = map(i -> i * 2, longList());
        assertThat(doubled.head()).isEqualTo(2 * (LONGER_THAN_THE_STACK_ALLOWS - 1));
    }

    @Test
    void filterDoesNotOverflowTheStackOnALongList() {
        final List<Integer> evens = filter(i -> i % 2 == 0, longList());
        assertThat(fold((count, i) -> count + 1, 0, evens)).isEqualTo(LONGER_THAN_THE_STACK_ALLOWS / 2);
    }

    @Test
    void reverseDoesNotOverflowTheStackOnALongList() {
        assertThat(reverse(longList()).head()).isZero();
    }

    @Test
    void equalsHashCodeAndToStringDoNotOverflowTheStackOnALongList() {
        assertThat(longList()).isEqualTo(longList()).hasSameHashCodeAs(longList());
        assertThat(longList().toString()).startsWith("( " + (LONGER_THAN_THE_STACK_ALLOWS - 1) + ", ");
    }

    @Test
    void equalListsHaveEqualHashCodes() {
        assertThat(compose(1, 2)).isEqualTo(compose(1, 2)).hasSameHashCodeAs(compose(1, 2));
        assertThat(LispList.nil()).hasSameHashCodeAs(LispList.nil());
    }

    @Test
    void listsDifferingInLengthAreNotEqual() {
        assertThat(compose(1, 2)).isNotEqualTo(list(1, LispList.nil()));
    }

    @Test
    void describesItselfAsNestedPairs() {
        assertThat(compose(1, 2)).hasToString("( 1, ( 2, ( ) ) )");
        assertThat(LispList.nil()).hasToString("( )");
    }

    @Test
    void mapAppliesTheFunctionToElementsInOrder() {
        final java.util.List<Integer> seen = new java.util.ArrayList<>();
        map(i -> seen.add(i), compose(1, 2));
        assertThat(seen).containsExactly(1, 2);
    }
}
