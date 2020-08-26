package uk.co.qualitycode.utils.functional.monad;

import uk.co.qualitycode.utils.functional.IsBetween;

public class Requirement {
    public static <T extends Comparable<T>> Require<T> require(final T t) {
        return new Require<>(t);
    }

    public static class Require<T extends Comparable<T>> {
        private final T t;

        public Require(final T t) {
            this.t = t;
        }

        public Require<T> toBe() {
            return this;
        }

        public IsBetween.IsIt<T,T> between(final T lower) {
            return upper -> {
                if (IsBetween.is(t).between(lower).and(upper)) return t;
                throw new RequirementException(java.lang.String.format("'%s' is not between '%s' and '%s'", t, lower, upper));
            };
        }

        public T nonNull() {
            return Option.of(t).toVavrOption().getOrElseThrow(()->new RequirementException("value must not be null"));
        }
    }
}