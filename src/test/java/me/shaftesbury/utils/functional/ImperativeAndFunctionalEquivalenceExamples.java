package me.shaftesbury.utils.functional;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import static me.shaftesbury.utils.functional.Functional.choose;
import static me.shaftesbury.utils.functional.Functional.filter;
import static me.shaftesbury.utils.functional.Functional.fold;
import static me.shaftesbury.utils.functional.Functional.init;
import static me.shaftesbury.utils.functional.Functional.sum;
import static me.shaftesbury.utils.functional.Functional.toList;
import static me.shaftesbury.utils.functional.MException.toMException;
import static me.shaftesbury.utils.functional.Option.toOption;
import static me.shaftesbury.utils.functional.UnaryFunction.delay;
import static org.assertj.core.api.Assertions.assertThat;

public final class ImperativeAndFunctionalEquivalenceExamples
{
    public static class CalculateSumOfFirstNPositiveIntegers
    {
        private static int imperative(final int n)
        {
            int sum = 0;
            for(int i=1;i<=n;++i)
                sum+=i;
            return sum;
        }

        private static int functional(final int n)
        {
            return fold(sum, 0, init(Functional.identity(), n));
        }

        @Test void test()
        {
            assertThat(functional(10)).isEqualTo(imperative(10));
        }
    }

    public static class CalculateSumOfFirstNOddPositiveIntegers
    {
        private static int imperative(final int n)
        {
            int sum = 0;
            for(int i=0;i<n;++i)
            {
                final int oddElem = 2*i + 1;
                sum += oddElem;
            }
            return sum;
        }

        private static int functional(final int n)
        {
            final Function<Integer,Integer> oddGenerator = integer -> {
                return 2*(integer-1) + 1;            // because init starts counting from 1
            };
            return fold(sum, 0, init(oddGenerator, n));
        }

        @Test void test()
        {
            assertThat(functional(10)).isEqualTo(imperative(10));
        }
    }

    public static class GivenASeqOfIntsFindThoseDivisibleBy3
    {
        private static Iterable<Integer> imperative(final Iterable<Integer> ys)
        {
            final List<Integer> result = new ArrayList<>();
            for(final Integer y : ys)
            {
                if(y%3==0)
                  result.add(y);
            }
            return result;
        }

        public static Function<Integer,Boolean> isDivisibleBy(final int denominator)
        {
            return numerator -> numerator % denominator == 0;
        }

        private static Iterable<Integer> functional(final Iterable<Integer> ys)
        {
            return filter(isDivisibleBy(3),ys);
        }

        @Test void test()
        {
            final List<Integer> ys = Arrays.asList(1,4,5,6,8,9,12,14,15,18,21,22);
            assertThat(functional(ys)).isEqualTo(imperative(ys));
        }
    }

    public static class FindAllBooksWhoseAuthorsNamesStartWith
    {
        public class Person
        {
            public Person(final String name) { this.name = name; }
            public String name;
        }

        public class Book
        {
            public Book(final Date publicationDate, final String title, final List<Person> authors)
            {
                this.publicationDate=publicationDate;
                this.title=title;
                this.authors=authors;
            }

            public final Date publicationDate;
            public final String title;
            public final List<Person> authors;
        }

        private static List<Book> imperative(final String s, final List<Book> books)
        {
            final List<Book> results = new ArrayList<>();
            for(final Book b : books)
            {
                final List<Person> authors = b.authors;
                boolean authorsFound = false;
                for(final Person p : authors)
                {
                    if(p.name.startsWith(s)) { authorsFound=true; break; }
                }
                if(authorsFound) results.add(b);
            }
            return results;
        }

        private static List<Book> functional(final String s, final List<Book> books)
        {
            return filter(book -> IterableHelper.create(book.authors).exists(person -> person.name.startsWith(s)), books);
        }

        @Test void test()
        {
            final List<Book> ys = Arrays.asList(
                    new Book(new Date(),"title",Arrays.asList(new Person("Bob"))),
                    new Book(new Date(),"title",Arrays.asList(new Person("John"),new Person("Julie"))),
                    new Book(new Date(),"title",Arrays.asList(new Person("Sid"),new Person("Nancy"))),
                    new Book(new Date(),"title",Arrays.asList(new Person("Nancy"))),
                    new Book(new Date(),"title",Arrays.asList(new Person("Marge"))),
                    new Book(new Date(),"title",Arrays.asList(new Person("John")))
            );
            assertThat(functional("N",ys)).isEqualTo(imperative("N",ys));
        }
    }

    public static class DivideFirstIntByEachSubsequentUsingMException
    {
        private final List<Integer> imperative(final List<Integer> l)
        {
            final List<Integer> results = new ArrayList<>();
            try {
                final Integer first = l.get(0);
                try {
                    for(int i=1;i<l.size();++i)
                        results.add(first/l.get(i));
                }
                catch(final IndexOutOfBoundsException i2) {    // in the event that there is only one element in the input list

                }
                catch(final ArithmeticException d) {      // if we divide by zero

                }
            } catch(final IndexOutOfBoundsException i)    // if the input list is empty
            {

            }

            // Now calculate the square of each number in the resulting list

            final List<Integer> squares = new ArrayList<>();
            for(final Integer i : results)
                squares.add(i*i);

            return squares;
        }

        private final List<Integer> functional(final List<Integer> l)
        {
            final MException<Integer> firstElement = toMException(delay(o -> o.get(0), l));
            final Iterable<MException<Integer>> results =
                    IterableHelper.create(Functional.skip(1,l)).map(integer -> firstElement.bind(underlyingInteger -> toMException(delay(o -> underlyingInteger / o, integer))));
            final Iterable<MException<Integer>> squares = Functional.map(o -> o.bind(i -> toMException(() -> i * i)), results);

            return toList(choose(i -> i.hasException() ? Option.None() : toOption(i.read()), squares));
        }

        @Test void test()
        {
            final List<Integer> testVals = Arrays.asList(0,1,2,3,4,5,6);
            assertThat(functional(testVals)).isEqualTo(imperative(testVals));
        }
    }

    public final static class PerformATransformationAndACollection
    {
        private static Pair<List<String>,List<String>> imperative(final List<Integer> testVals)
        {
            final List<String> retval1 = new ArrayList<>(testVals.size());
            final List<String> retval2 = new ArrayList<>(testVals.size());

            for(final Integer val : testVals)
            {
                retval1.add(val.toString());
                final List<String> tmp = new ArrayList<>();
                for(int i=1;i<(1+val)*2;++i) {
                    tmp.add(Integer.toString(10 * val + 1));
                }
                retval2.addAll(tmp);
            }

            return Pair.of(retval1,retval2);
        }

//        private static Pair<List<String>,List<String>> functional(final List<Integer> testVals)
//        {
//            final Function<Integer, String> func1 = new Function<Integer, String>() {
//
//                public String apply(final Integer integer) {
//                    return integer.toString();
//                }
//            };
//
//            final Function<Integer, Iterable<String>> func2 = new Function<Integer, Iterable<String>>() {
//
//                public Iterable<String> apply(final Integer integer) {
//                    return Functional.map(new Function<Integer, String>() {
//
//                        public String apply(Integer integer) {
//                            return Integer.toString(10 * integer + 1);
//                        }
//                    }, seq.init(Functional.constant(integer), 2 * (1 + integer)));
//                }
//            };
//
//            final Iterable<Pair<String, Iterable<String>>> zip = seq.zip(func1, func2, testVals);
//
//        }

        @Test void test()
        {
            final List<Integer> testVals = Arrays.asList(0,1,2,3,4,5,6);
//            assertThat(functional(testVals)).isEqualTo(imperative(testVals));
        }
    }
}
