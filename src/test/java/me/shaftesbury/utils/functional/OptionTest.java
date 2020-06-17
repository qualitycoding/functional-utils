package me.shaftesbury.utils.functional;

import org.assertj.core.api.IterableAssert;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class OptionTest {
    @Test void equalityTest1() {
        final Option<String> a = Option.toOption("TEST");
        //noinspection EqualsWithItself
        assertThat(a.equals(a)).isTrue();
    }

    @Test void equalityTest2() {
        final Option<String> a = Option.toOption("TEST");
        final Option<String> b = Option.toOption("TEST");
        assertThat(a.equals(b)).isTrue();
    }

    @Test void equalityTest3() {
        final Option<Integer> a = Option.toOption(1);
        final Option<Integer> b = Option.toOption(2);
        //noinspection EqualsWithItself
        assertThat(a.equals(a)).isTrue();
        assertThat(a.equals(b)).isFalse();
    }

    @Test void optionTestValueType1() {
        final int expected = 10;
        final Option<Integer> a = Option.toOption(expected);
        assertThat(a.isSome()).isTrue();
        assertThat(a.isNone()).isFalse();
        assertThat(a.Some()).isEqualTo((Integer) expected);
    }

    @Test void optionTestStringType1() {
        final String expected = "ll";
        final Option<String> a = Option.toOption(expected);
        assertThat(a.isSome()).isTrue();
        assertThat(a.isNone()).isFalse();
        assertThat(a.Some()).isEqualTo(expected);
    }

    @Test void optionTestValueType2() {
        final Option<Integer> a = Option.None();
        assertThat(a.isNone()).isTrue();
        assertThat(a.isSome()).isFalse();
    }

    static class tmp {
    }

    @Test void optionTestReferenceType1() {
        final Option<tmp> a = Option.toOption(new tmp());
        assertThat(a.isSome()).isTrue();
        assertThat(a.isNone()).isFalse();
        a.Some();
    }

    @Test void optionTestReferenceType2() {
        final Option<tmp> a = Option.toOption(null);
        assertThat(a.isNone()).isTrue();
        assertThat(a.isSome()).isFalse();
    }

    @Test
    void optionTestReferenceType3() {
        final Option<tmp> a = Option.toOption(null);
        assertThatExceptionOfType(OptionNoValueAccessException.class).isThrownBy(a::Some);
    }

    @Test void compareOptionsForEqualityTest1() {
        final Option<Integer> some1 = Option.toOption(1);
        final Option<Integer> some2 = Option.toOption(1);

        assertThat(some2).isEqualTo(some1);
    }

    @Test void compareOptionsForEqualityTest2() {
        final Option<Integer> some1 = Option.toOption(1);
        final Option<Integer> some2 = Option.toOption(2);

        assertThat( some2).isNotEqualTo(some1);
    }

    @Test void compareOptionsForEqualityTest3() {
        final Option<Integer> some1 = Option.toOption(1);
        final Option<Double> some2 = Option.toOption(2.1);

        assertThat( some2).isNotEqualTo(some1);
    }

    @Test void compareOptionsForEqualityTest4() {
        final Option<Integer> none1 = Option.None();
        final Option<Integer> none2 = Option.None();

        assertThat(none2).isEqualTo(none1);
    }

    @Test void compareOptionsForEqualityTest5() {
        final Option<Integer> some = Option.toOption(2);
        final Integer theInt = 2;

        assertThat( theInt).isNotEqualTo(some);
    }

//    @Test
//    public void optionMonadTest1()
//    {
//        var e = from a in 10.toOption()
//        from b in 2.toOption()
//        select a + b;
//        assertThat(e.isSome()).isTrue();
//        assertThat(e.isNone()).isFalse();
//        assertThat( e.Some()).isEqualTo(12);
//    }

//    @Test(expected = OptionNoValueAccessException.class)
//    public void optionMonadTest2()
//    {
//        var e = from a in Option<Integer>.None()
//        from b in 2.toOption()
//        select a + b;
//        assertThat(e.isNone()).isTrue();
//        assertThat(e.isSome()).isFalse();
//    }

//    @Test(expected = OptionNoValueAccessException.class)
//    public void optionMonadTest3()
//    {
//        var e = from a in 10.toOption()
//        from b in Option<Integer>.None()
//        select a + b;
//        assertThat(e.isNone()).isTrue();
//        assertThat(e.isSome()).isFalse();
//    }

//    @Test(expected = OptionNoValueAccessException.class)
//    public void optionMonadTest4()
//    {
//        var e = from a in Option<Integer>.None()
//        from b in Option<Integer>.None()
//        select a + b;
//        assertThat(e.isNone()).isTrue();
//        assertThat(e.isSome()).isFalse();
//    }

    @Test void optionNoneisNoneTest1() {
        final Option<Integer> none = Option.None();
        assertThat(none.isNone()).isTrue();
        assertThat(none.isSome()).isFalse();
    }

    @Test void optionNoneisNoneTest2() {
        final Option<String> none = Option.None();
        assertThat(none.isNone()).isTrue();
        assertThat(none.isSome()).isFalse();
    }

    @Test void optionNoneisNoneTest3() {
        final Option<tmp> none = Option.None();
        assertThat(none.isNone()).isTrue();
        assertThat(none.isSome()).isFalse();
    }

    // Java doesn't support custom value types at the moment ...
//    struct tmp2{}
//
//    @Test
//    public void optionNoneisNone()Test4()
//    {
//        var none = Option<tmp2>.None();
//        assertThat(none.isNone()).isTrue();
//        assertThat(none.isSome()).isFalse();
//    }
//
    @Test void optionBindTest1() {
        final Option<Integer> a = Option.toOption(1);
        final Option<Integer> b = a.bind(
                integer -> Option.toOption(integer * 2));
        assertThat(b.isSome()).isTrue();
        assertThat(b.Some()).isEqualTo(Integer.valueOf(2));
    }

    @Test void optionBindTest2() {
        final Option<Integer> a = Option.None();
        final Option<Integer> b = a.bind(
                integer -> Option.toOption(integer * 2));
        assertThat(b.isNone()).isTrue();
    }

    @Test void optionBindTest3() {
        final Iterable2<Integer> input = IterableHelper.asList(1, 2, 3, 4, 5, 6);
        final java.util.List<Integer> expected = Arrays.asList(2, 4, 6);

        // Note that this really ought to be an example of 'choose' but we use bind here to exercise the code ;-)

        final Iterable2<Option<Integer>> output = input.map(
                integer -> Option.toOption(integer).bind(
                        i -> Functional.isEven.apply(i) ? Option.toOption(i) : Option.None()));


        final Iterable2<Integer> choose = output.choose(Function.identity());
        final IterableAssert<Integer> integerIterableAssert = assertThat(choose);
        integerIterableAssert.containsExactlyElementsOf(expected);
    }

    private static final BiFunction<Integer, Integer, Integer> plus = Integer::sum;

    @Test void optionLiftTest1() {
        final Option<Integer> a = Option.toOption(10);
        final Option<Integer> b = Option.toOption(100);
        final Option<Integer> c = Option.lift(plus, a, b);

        assertThat(c.isSome()).isTrue();
        assertThat(c.Some()).isEqualTo(Integer.valueOf(110));
    }

    @Test void optionLiftTest2() {
        final Option<Integer> a = Option.toOption(10);
        final Option<Integer> b = Option.None();
        final Option<Integer> c = Option.lift(plus, a, b);

        assertThat(c.isNone()).isTrue();
    }

    @Test void optionLiftTest3() {
        final Option<Integer> a = Option.None();
        final Option<Integer> b = Option.toOption(10);
        final Option<Integer> c = Option.lift(plus, a, b);

        assertThat(c.isNone()).isTrue();
    }

    @Test void optionLiftTest4() {
        final Option<Integer> a = Option.None();
        final Option<Integer> b = Option.None();
        final Option<Integer> c = Option.lift(plus, a, b);

        assertThat(c.isNone()).isTrue();
    }

    @Test void optionToStringTest() {
        final Option<String> a = Option.toOption("string");
        final Option<Integer> b = Option.toOption(10);
        final Option<Boolean> c = Option.toOption(false);
        final Option<String> d = Option.None();

        assertThat(a.toString()).isEqualTo("Option( string )");
        assertThat(b.toString()).isEqualTo("Option( 10 )");
        assertThat(c.toString()).isEqualTo("Option( false )");
        assertThat(d.toString()).isEqualTo("None");
    }

    @Test void optionSomeCreatorTestValueType1() {
        final int expected = 10;
        final Option<Integer> a = Option.Some(expected);
        assertThat(a.isSome()).isTrue();
        assertThat(a.isNone()).isFalse();
        assertThat(a.Some()).isEqualTo((Integer) expected);
    }

    @Test void optionSomeCreatorTestStringType1() {
        final String expected = "ll";
        final Option<String> a = Option.Some(expected);
        assertThat(a.isSome()).isTrue();
        assertThat(a.isNone()).isFalse();
        assertThat(a.Some()).isEqualTo(expected);
    }

    @Test
    void optionSomeCreatorTestValueType2() {
        assertThatExceptionOfType(OptionNoValueAccessException.class).isThrownBy(()->Option.Some(null));
    }

    @Test void collectionOfOption() {
        final Map<Option<Integer>, Integer> map = new HashMap<>();
        map.put(Option.toOption(1), 1);
        assertThat(map.get(Option.toOption(1))).isEqualTo(Integer.valueOf(1));
    }
}
