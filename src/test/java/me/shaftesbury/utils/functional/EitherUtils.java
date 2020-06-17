package me.shaftesbury.utils.functional;

import io.vavr.collection.List;
import io.vavr.control.Either;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class EitherUtils {

    @Test void filterLeft() {
        final Exception input = new Exception();
        final List<Either<Exception, BigDecimal>> eithers = List.ofAll(Arrays.asList(Either.left(input), Either.right(BigDecimal.ONE)));

        final List<Exception> lefts = eithers.filter(Either::isLeft).map(Either::getLeft);

        assertThat( lefts).isEqualTo(List.of(input));
    }

    @Test void filterRight() {
        final BigDecimal input = BigDecimal.ONE;
        final List<Either<Exception, BigDecimal>> eithers = List.ofAll(Arrays.asList(Either.left(new Exception()), Either.right(input)));

        final List<BigDecimal> rights = eithers.filter(Either::isRight).map(Either::get);

        assertThat( rights).isEqualTo(List.of(input));
    }
}
