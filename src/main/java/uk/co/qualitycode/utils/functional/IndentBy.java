package uk.co.qualitycode.utils.functional;

public class IndentBy {

    private final String indentThis;

    private IndentBy(final String indentThis) {
        this.indentThis = indentThis;
    }

    public static IndentBy indent(final String indentThis) {
        return new IndentBy(indentThis);
    }

    public IndentThis with(final int howMany) {
        return unitOfIndentation -> Functional.indentBy(howMany, unitOfIndentation, indentThis);
    }

    public interface IndentThis {
        String of(String unitOfIndentation);
    }
}
