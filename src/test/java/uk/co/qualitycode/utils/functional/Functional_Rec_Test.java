package uk.co.qualitycode.utils.functional;

import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.co.qualitycode.utils.functional.FunctionalTest.doublingGenerator;
import static uk.co.qualitycode.utils.functional.Functional_Fold_Test.csv;

class Functional_Rec_Test {
    @Test
    void recFold() {
        final Collection<Integer> li = Functional.init(doublingGenerator, 5);
        final String s1 = "0,2,4,6,8,10";
        final String s2 = Functional.rec.fold(csv, "0", li);
        assertThat(s2).isEqualTo(s1);
    }

    @Test
    void recMap() {
        final Collection<Integer> li = Functional.init(doublingGenerator, 5);
        final Iterable<String> actual = Functional.rec.map(Functional.stringify(), li);
        assertThat(actual).containsExactly("2", "4", "6", "8", "10");
    }
}
