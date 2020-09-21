package uk.co.qualitycode.utils.functional.assertions;

import org.assertj.core.api.Assertions;
import uk.co.qualitycode.utils.functional.OptionStream;

import java.util.List;
import java.util.stream.Collectors;

public class OptionStreamAssert<T> {
    private final List<T> os;

    private OptionStreamAssert(final OptionStream<T> os) {
        this.os = os.collect(Collectors.toList());
    }

    public static <T> OptionStreamAssert<T> assertThat(final OptionStream<T> os) {
        return new OptionStreamAssert<>(os);
    }

    @SafeVarargs
    public final OptionStreamAssert<T> containsExactly(final T... values) {
        Assertions.assertThat(os).containsExactly(values);
        return this;
    }
}
