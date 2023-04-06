package uk.co.qualitycode.utils.functional;

import io.vavr.control.Option;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class WithOptionalSetterTest {

    @Nested
    class JavaOptional {
        @Test
        void optionallyDoesNotSetFieldWhenValueIsEmpty() {
            final TestBuilder builder = new TestBuilder().optionally(TestBuilder::s_setField, Optional.empty());

            assertThat(builder.field).isNull();
        }

        @Test
        void optionallySetsFieldWhenValueIsNotEmpty() {
            final TestBuilder builder = new TestBuilder().optionally(TestBuilder::s_setField, Optional.of(new Object()));

            assertThat(builder.field).isNotNull();
        }
    }

    @Nested
    class VavrOption {
        @Test
        void optionallyDoesNotSetFieldWhenValueIsEmpty() {
            final TestBuilder builder = new TestBuilder().optionally(TestBuilder::s_setField, Option.none());

            assertThat(builder.field).isNull();
        }

        @Test
        void optionallySetsFieldWhenValueIsNotEmpty() {
            final TestBuilder builder = new TestBuilder().optionally(TestBuilder::s_setField, Option.some(new Object()));

            assertThat(builder.field).isNotNull();
        }
    }

    @Test
    void self() {
        final TestBuilder builder = new TestBuilder();

        assertThat(builder.self()).isSameAs(builder);
    }

    public static class TestBuilder implements WithOptionalSetter<TestBuilder> {
        public Object field;

        @Override
        public TestBuilder self() {
            return this;
        }

        //        NOTE: This should be setField but there is a bug in java 11 that causes a compilation error
//        Bug ID:9069976
        public static Function<Object, TestBuilder> s_setField(final TestBuilder builder) {
            return builder::setField;
        }

        public TestBuilder setField(final Object t) {
            this.field = t;
            return this;
        }
    }
}
