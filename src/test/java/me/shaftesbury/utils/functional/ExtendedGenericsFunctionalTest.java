package me.shaftesbury.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Bob on 13/02/14.
 */
public final class ExtendedGenericsFunctionalTest
{
    @Test final void chooseTestParseStringToNumber()
    {
        final Function<String, Option<Number>> toNumber = s -> {
            try {
                if(s.contains("."))// then it's a float
                    return Option.toOption(Double.parseDouble(s));
                else return Option.toOption(Integer.parseInt(s));
            } catch(final NumberFormatException t) {
                return Option.None();
            }
        };

        final List<Number> expected = Arrays.asList(Integer.valueOf(1), Integer.valueOf(2), Double.valueOf(4.5));
        final List<Number> output = Functional.choose(toNumber, Arrays.asList("1","2","at","4.5"));

        assertThat(output.toArray()).isEqualTo(expected.toArray());
    }

    @Test final void chooseTestParseObjectToNumber()
    {
        final Function<Object, Option<Number>> toNumber = o -> {
            final String s = o.toString();
            try {
                if(s.contains("."))// then it's a float
                    return Option.toOption(Double.parseDouble(s));
                else return Option.toOption(Integer.parseInt(s));
            } catch(final NumberFormatException t) {
                return Option.None();
            }
        };

        final List<Number> expected = Arrays.asList(Integer.valueOf(1), Integer.valueOf(2), Double.valueOf(4.5), Double.valueOf(4.5), Integer.valueOf(1), Integer.valueOf(10));
        final List<Number> output = Functional.choose(toNumber, Arrays.<Object>asList("1","2","at","4.5",4.5,1,Integer.valueOf(10)));

        assertThat(output.toArray()).isEqualTo(expected.toArray());
    }
}
