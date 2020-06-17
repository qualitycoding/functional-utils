package me.shaftesbury.utils.functional;

import io.vavr.Function2;
import io.vavr.Function3;
import io.vavr.Function4;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.Tuple4;
import org.junit.jupiter.api.Test;

import static me.shaftesbury.utils.functional.UsingWrapper.using;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class UsingWrapperTest {
    @Test
    void inShouldRejectNullFunction() {
        assertThatNullPointerException().isThrownBy(() -> using(new Object()).in(null));
    }

    @Test
    void mapShouldRejectNullFunction() {
        assertThatNullPointerException().isThrownBy(() -> using(new Object()).map(null));
    }

    @Test
    void flatMapShouldRejectNullFunction() {
        assertThatNullPointerException().isThrownBy(() -> using(new Object()).flatMap(null));
    }

    @Test
    void usingShouldRejectNullSupplier() {
        assertThatNullPointerException().isThrownBy(() -> using(null));
    }

    @Test
    void inShouldRejectNullFunction2() {
        assertThatNullPointerException().isThrownBy(() -> using(new Object(), new Object()).in((Function2) null));
    }

    @Test
    void inShouldRejectNullFunction3() {
        assertThatNullPointerException().isThrownBy(() -> using(new Object(), new Object(), new Object()).in((Function3) null));
    }

    @Test
    void inShouldRejectNullFunction4() {
        assertThatNullPointerException().isThrownBy(() -> using(new Object(), new Object(), new Object(), new Object()).in((Function4) null));
    }

    @Test
    void inShouldExpectTheSameNumberofParametersAsUsing3() {
        assertThatIllegalArgumentException().isThrownBy(() -> using(new Object(), new Object()).in((x, y, z) -> x));
    }

    @Test
    void inShouldExpectTheSameNumberofParametersAsUsing4() {
        assertThatIllegalArgumentException().isThrownBy(() -> using(new Object(), new Object(), new Object()).in((x, y, z, a) -> x));
    }

    @Test
    void using1Test() {
        final Object value = new Object();
        final Using<Object> actual = using(value);
        assertThat(actual.getValue().get()).isSameAs(value);
    }

    @Test
    void usingSupplierTest() {
        final Object value = new Object();
        final Using<Object> actual = using(() -> value);
        assertThat(actual.getValue().get()).isSameAs(value);
    }

    @Test
    void inTest() {
        final boolean actual = using("fish").in("fish"::equals);
        assertThat(actual).isTrue();
    }

    @Test
    void mapTest() {
        final boolean actual = using("fish").map("fish"::equals).getValue().get();
        assertThat(actual).isTrue();
    }

    @Test
    void flatMapTest() {
        final boolean actual = using("fish").flatMap(v -> using("fish".equals(v))).getValue().get();
        assertThat(actual).isTrue();
    }

    @Test
    void using2Test() {
        final Object value1 = new Object();
        final java.lang.String value2 = "glkjdf";

        final Tuple2<Object, java.lang.String> actual = using(value1, value2).in(Tuple2::new);

        assertThat(actual).isEqualTo(new Tuple2<>(value1, value2));
    }

    @Test
    void using3Test() {
        final Object value1 = new Object();
        final java.lang.String value2 = "glkjdf";
        final Integer value3 = 678;

        final Tuple3<Object, java.lang.String, Integer> actual = using(value1, value2, value3).in(Tuple3<Object,java.lang.String,Integer>::new);

        assertThat(actual).isEqualTo(new Tuple3<>(value1, value2, value3));
    }

    @Test
    void using4Test() {
        final Object value1 = new Object();
        final java.lang.String value2 = "glkjdf";
        final Integer value3 = 678;
        final java.lang.String value4 = "glygv;jbn";

        final Tuple4<Object, java.lang.String, Integer, java.lang.String> actual = using(value1, value2, value3, value4).in(Tuple4<Object,java.lang.String,Integer,java.lang.String>::new);

        assertThat(actual).isEqualTo(new Tuple4<>(value1, value2, value3, value4));
    }
}
