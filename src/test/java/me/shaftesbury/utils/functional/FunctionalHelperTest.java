package me.shaftesbury.utils.functional;


import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static me.shaftesbury.utils.functional.FunctionalHelper.allNone;
import static me.shaftesbury.utils.functional.FunctionalHelper.allSome;
import static me.shaftesbury.utils.functional.FunctionalHelper.areNone;
import static me.shaftesbury.utils.functional.FunctionalHelper.areSome;
import static me.shaftesbury.utils.functional.FunctionalHelper.some;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Bob on 16/05/14.
 */
public final class FunctionalHelperTest
{
    @Test void theyAreAllNone()
    {
        final List<Option<Object>> input = Functional.init(integer -> Option.None(), 5);

        assertThat(allNone(input)).isTrue();
    }

    @Test void theyAreNotAllNone()
    {
        final List<Option<Object>> input = Functional.init(integer -> integer==1 ? Option.toOption(Integer.valueOf(1)) : Option.None(), 5);

        assertThat(allNone(input)).isFalse();
    }

    @Test void theyAreAllSome()
    {
        final List<Option<Object>> input = Functional.init(integer -> Option.toOption(Integer.valueOf(1)), 5);

        assertThat(allSome(input)).isTrue();
    }

    @Test void theyAreNotAllSome()
    {
        final List<Option<Object>> input = Functional.init(integer -> integer==1 ? Option.toOption(Integer.valueOf(1)) : Option.None(), 5);

        assertThat(allSome(input)).isFalse();
    }

    @Test void justTheOnesThatAreNone()
    {
        final List<Option<Object>> input = Functional.init(integer -> integer==1 ? Option.toOption(Integer.valueOf(1)) : Option.None(), 5);

        final List<Option<Object>> output = areNone(input).toList();

        assertThat( output.size()).isEqualTo(input.size() - 1);
    }

    @Test void justTheOnesThatAreSome()
    {
        final List<Option<Object>> input = Functional.init(integer -> integer==1 ? Option.toOption(Integer.valueOf(1)) : Option.None(), 5);

        final List<Option<Object>> output = areSome(input).toList();

        assertThat( output.size()).isEqualTo(1);
    }

    @Test void theValueOfJustTheOnesThatAreSome()
    {
        final List<Option<Object>> input = Functional.init(integer -> integer==1 ? Option.toOption(Integer.valueOf(1)) : Option.None(), 5);

        final Iterable2<Option<Object>> output = areSome(input);
        final List<Object> finalOutput = some(output).toList();

        assertThat(finalOutput).isEqualTo(Arrays.asList((Object)1));
    }
}
