package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Test;
import uk.co.qualitycode.utils.functional.function.BinaryFunction;
import uk.co.qualitycode.utils.functional.function.UnaryFunction;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class FunctionTest {
    @Test
    void unaryFunctionCompositionTest1() {
        final UnaryFunction<Integer, String> stringify = new UnaryFunction<Integer, String>() {

            public String apply(final Integer integer) {
                return Functional.Stringify(integer);
            }
        };

        final UnaryFunction<String, Iterable<Double>> expander =
                new UnaryFunction<String, Iterable<Double>>() {

                    public List<Double> apply(final String s) {
                        final Double m = Double.parseDouble(s);
                        return Arrays.asList(m - 1.0, m, m + 1.0);
                    }
                };

        final UnaryFunction<Integer, Iterable<Double>> composition = stringify.then(expander);

        final Iterable2<Integer> input = Iterable2.asList(-1, 0, 1);
        final Iterable2<Double> expected = Iterable2.asList(-2.0, -1.0, 0.0, -1.0, 0.0, 1.0, 0.0, 1.0, 2.0);

        final Iterable2<Double> output = input.collect(composition);

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void binaryFunctionDecompositionTest1() {
        final BinaryFunction<Double, Integer, String> f =
                new BinaryFunction<Double, Integer, String>() {

                    public String apply(final Double d, final Integer i) {
                        return Functional.Stringify((int) (i * d));
                    }
                };

        final Function<Integer, Function<Double, String>> decomposed = f.toFunc();
        final Function<Double, String> decomposed1 = decomposed.apply(2);
        final Iterable2<String> output = Iterable2.asList(1.0, 2.0, 3.0, 4.0, 5.0).map(decomposed1);
        final Iterable<String> expected = Arrays.asList("2", "4", "6", "8", "10");

        assertThat(output).containsExactlyElementsOf(expected);
    }

    @Test
    void binaryFunctionDecompositionTest2() {
        final BinaryFunction<Double, Integer, String> f =
                new BinaryFunction<Double, Integer, String>() {

                    public String apply(final Double d, final Integer i) {
                        return Functional.Stringify((int) (i * d));
                    }
                };

        final Function<Double, String> decomposed = f.toFunc(2);
        final Iterable2<String> output = Iterable2.asList(1.0, 2.0, 3.0, 4.0, 5.0).map(decomposed);
        final Iterable<String> expected = Arrays.asList("2", "4", "6", "8", "10");

        assertThat(output).containsExactlyElementsOf(expected);
    }
}
