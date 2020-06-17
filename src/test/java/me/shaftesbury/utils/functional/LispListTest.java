package me.shaftesbury.utils.functional;

import me.shaftesbury.utils.functional.LispList.EmptyListHasNoHead;
import me.shaftesbury.utils.functional.LispList.EmptyListHasNoTail;
import me.shaftesbury.utils.functional.LispList.List;
import org.junit.jupiter.api.Test;

import static me.shaftesbury.utils.functional.LispList.cadr;
import static me.shaftesbury.utils.functional.LispList.car;
import static me.shaftesbury.utils.functional.LispList.cdr;
import static me.shaftesbury.utils.functional.LispList.compose;
import static me.shaftesbury.utils.functional.LispList.cons;
import static me.shaftesbury.utils.functional.LispList.filter;
import static me.shaftesbury.utils.functional.LispList.fold;
import static me.shaftesbury.utils.functional.LispList.foldRight;
import static me.shaftesbury.utils.functional.LispList.list;
import static me.shaftesbury.utils.functional.LispList.map;
import static me.shaftesbury.utils.functional.LispList.reverse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class LispListTest {
    @Test void headTest1() {
        final List<Integer> l = list(1, LispList.nil());
        assertThat(l.head()).isEqualTo(Integer.valueOf(1));
    }

    @Test void tailTest1() {
        final List<Integer> l = list(1, list(2, LispList.nil()));
        assertThat(l.tail().head()).isEqualTo(Integer.valueOf(2));
    }

    @Test void equalsTest1() {
        final List<Integer> l = list(1, list(2, LispList.nil()));
        assertThat(l.tail()).isEqualTo(list(2, LispList.nil()));
    }

    @Test void mapTest1() {
        final List<Integer> input = list(1, list(2, list(3, list(4, list(5, LispList.nil())))));
        final List<String> output = map(Functional.dStringify(), input);
        assertThat(output.head()).isEqualTo("1");
        assertThat(output.tail().head()).isEqualTo("2");
        assertThat(output.tail().tail().head()).isEqualTo("3");
        assertThat(output.tail().tail().tail().head()).isEqualTo("4");
        assertThat(output.tail().tail().tail().tail().head()).isEqualTo("5");
    }

    @Test void equalsTest2() {
        final List<Integer> input = list(1, list(2, list(3, list(4, list(5, LispList.nil())))));
        final List<String> output = map(Functional.dStringify(), input);
        assertThat(output).isEqualTo(list("1", list("2", list("3", list("4", list("5", LispList.nil()))))));
    }

    @Test void filterTest1() {
        final List<Integer> input = list(2, list(4, list(6, list(8, list(10, LispList.nil())))));
        final List<Integer> oddElems = filter(Functional.isOdd, input);
        assertThat(oddElems).isEqualTo(LispList.<Integer>nil());
    }

    @Test void filterTest2() {
        final List<Integer> input = list(2, list(5, list(7, list(8, list(10, LispList.nil())))));
        final List<Integer> evenElems = filter(Functional.isEven, input);

        assertThat(evenElems.head()).isEqualTo(Integer.valueOf(2));
        assertThat(evenElems.tail().head()).isEqualTo(Integer.valueOf(8));
        assertThat(evenElems.tail().tail().head()).isEqualTo(Integer.valueOf(10));
    }

    @Test void equalsTest3() {
        final List<Integer> input = list(2, list(5, list(7, list(8, list(10, LispList.nil())))));
        final List<Integer> evenElems = filter(Functional.isEven, input);

        final List<Integer> expected = list(2, list(8, list(10, LispList.nil())));
        assertThat(evenElems).isEqualTo(expected);
    }

    @Test void filterTest3() {
        final List<Integer> input = list(2, list(4, list(6, list(8, list(10, LispList.nil())))));
        final Integer limit = 5;
        final List<Integer> highElems = filter(
                a -> a > limit, input);

        final List<Integer> expected = list(6, list(8, list(10, LispList.nil())));
        assertThat(highElems).isEqualTo(expected);
    }

    @Test void filterTest4() {
        final List<Integer> input = list(2, list(4, list(6, list(8, list(10, LispList.nil())))));
        final Integer limit = 10;
        final List<Integer> output = filter(
                a -> a > limit, input);

        assertThat(output.isEmpty()).isTrue();
    }

    @Test void filterTest5() {
        final List<Integer> input = list(2, list(4, list(6, list(8, list(10, list(12, list(14, list(16, list(18, list(20, LispList.nil()))))))))));
        final List<Integer> expected = list(4, list(8, list(12, list(16, list(20, LispList.nil())))));
        final List<Integer> output = filter(
                a -> a % 4 == 0, input);

        assertThat(output).isEqualTo(expected);
    }

    @Test void consTest1() {
        final List<Integer> input = cons(2, cons(4, cons(6, compose(8, 10))));
        assertThat(input.head()).isEqualTo(Integer.valueOf(2));
        assertThat(input.tail().head()).isEqualTo(Integer.valueOf(4));
        assertThat(input.tail().tail().head()).isEqualTo(Integer.valueOf(6));
        assertThat(input.tail().tail().tail().head()).isEqualTo(Integer.valueOf(8));
        assertThat(input.tail().tail().tail().tail().head()).isEqualTo(Integer.valueOf(10));
    }

    @Test void carTest1() {
        final List<Integer> input = list(2, list(4, list(6, list(8, list(10, LispList.nil())))));
        assertThat(car(input)).isEqualTo(Integer.valueOf(2));
    }

    @Test void cdrTest1() {
        final List<Integer> input = list(2, list(4, list(6, list(8, list(10, LispList.nil())))));
        assertThat(cdr(input)).isEqualTo(list(4, list(6, list(8, list(10, LispList.nil())))));
    }

    @Test void cadrTest1() {
        final List<Integer> input = list(2, list(4, list(6, list(8, list(10, LispList.nil())))));
        assertThat(cadr(input)).isEqualTo(Integer.valueOf(4));
    }

    @Test void reverseTest1() {
        final List<Integer> input = list(2, list(4, list(6, list(8, list(10, LispList.nil())))));
        final List<Integer> expected = list(10, list(8, list(6, compose(4, 2))));
        final List<Integer> output = reverse(input);
        assertThat(output).isEqualTo(expected);
    }

    @Test void foldTest1() {
        final List<Integer> input = list(2, list(4, list(6, list(8, list(10, LispList.nil())))));
        final Integer expected = 30;
        final Integer output = fold(Integer::sum, 0, input);
        assertThat(output).isEqualTo(expected);
    }

    @Test void foldTest2() {
        final List<String> input = list("2", list("4", list("6", list("8", LispList.nil()))));
        final String expected = "2468";
        final String output = fold((state, o) -> state + o, "", input);
        assertThat(output).isEqualTo(expected);
    }

    @Test void foldRightTest1() {
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
}
