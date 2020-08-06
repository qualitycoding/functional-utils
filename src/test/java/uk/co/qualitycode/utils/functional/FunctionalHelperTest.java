package uk.co.qualitycode.utils.functional;


import org.junit.jupiter.api.Test;
import uk.co.qualitycode.utils.functional.monad.Option;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.co.qualitycode.utils.functional.FunctionalHelper.allNone;
import static uk.co.qualitycode.utils.functional.FunctionalHelper.allSome;
import static uk.co.qualitycode.utils.functional.FunctionalHelper.areNone;
import static uk.co.qualitycode.utils.functional.FunctionalHelper.areSome;
import static uk.co.qualitycode.utils.functional.FunctionalHelper.some;

/**
 * Created by Bob on 16/05/14.
 */
public final class FunctionalHelperTest {
    @Test
    void theyAreAllNone() {
        final List<Option<Object>> input = Functional.init(integer -> Option.none(), 5);

        assertThat(allNone(input)).isTrue();
    }

    @Test
    void theyAreNotAllNone() {
        final List<Option<Object>> input = Functional.init(integer -> integer == 1 ? Option.of(Integer.valueOf(1)) : Option.none(), 5);

        assertThat(allNone(input)).isFalse();
    }

    @Test
    void theyAreAllSome() {
        final List<Option<Object>> input = Functional.init(integer -> Option.of(Integer.valueOf(1)), 5);

        assertThat(allSome(input)).isTrue();
    }

    @Test
    void theyAreNotAllSome() {
        final List<Option<Object>> input = Functional.init(integer -> integer == 1 ? Option.of(Integer.valueOf(1)) : Option.none(), 5);

        assertThat(allSome(input)).isFalse();
    }

    @Test
    void justTheOnesThatAreNone() {
        final List<Option<Object>> input = Functional.init(integer -> integer == 1 ? Option.of(Integer.valueOf(1)) : Option.none(), 5);

        final List<Option<Object>> output = areNone(input).toList();

        assertThat(output).hasSize(input.size() - 1);
    }

    @Test
    void justTheOnesThatAreSome() {
        final List<Option<Object>> input = Functional.init(integer -> integer == 1 ? Option.of(Integer.valueOf(1)) : Option.none(), 5);

        final List<Option<Object>> output = areSome(input).toList();

        assertThat(output).hasSize(1);
    }

    @Test
    void theValueOfJustTheOnesThatAreSome() {
        final List<Option<Object>> input = Functional.init(integer -> integer == 1 ? Option.of(Integer.valueOf(1)) : Option.none(), 5);

        final Iterable2<Option<Object>> output = areSome(input);
        final List<Object> finalOutput = some(output).toList();

        assertThat(finalOutput).isEqualTo(Arrays.asList((Object) 1));
    }
}
