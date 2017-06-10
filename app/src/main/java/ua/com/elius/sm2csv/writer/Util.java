package ua.com.elius.sm2csv.writer;

import java.util.List;
import java.util.function.UnaryOperator;

public class Util {

    /**
     * Replaces all elements which are equals to <code>find</code>
     * with <code>replaceWith</code>
     *
     * @param list        list to work with
     * @param find        value to find
     * @param replaceWith value to replace with
     */
    public static <T> void findAndReplace(List<T> list, T find, T replaceWith) {
        list.replaceAll(swapIfEqualsTo(find, replaceWith));
    }

    /**
     * Creates operator which replaces <code>compareWith</code> values
     * with <code>swapTo</code>
     * <p>
     * If operators argument not equals to <code>compareWith</code>
     * then it returns the same argument
     *
     * @param compareWith value to compare operators argument with
     * @param swapTo      value operator returns if its argument equals to
     *                    <code>compareWith</code>
     * @return Swapping operator
     */
    public static <T> UnaryOperator<T> swapIfEqualsTo(T compareWith, T swapTo) {
        return s -> s.equals(compareWith) ? swapTo : s;
    }
}
