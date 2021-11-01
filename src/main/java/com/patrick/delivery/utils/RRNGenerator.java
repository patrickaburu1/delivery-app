/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.patrick.delivery.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * To God be the Glory
 *
 * @author David
 */
public class RRNGenerator {

    private long prevTime;
    private int char1 = 0;
    private int char2 = 0;
    private static String strPrepender;
    private Random random;
    private static List<Character> firstCharList;
    private static List<Character> secondCharList;

    final static char[] DigitTens = {
        '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
        '1', '1', '1', '1', '1', '1', '1', '1', '1', '1',
        '2', '2', '2', '2', '2', '2', '2', '2', '2', '2',
        '3', '3', '3', '3', '3', '3', '3', '3', '3', '3',
        '4', '4', '4', '4', '4', '4', '4', '4', '4', '4',
        '5', '5', '5', '5', '5', '5', '5', '5', '5', '5',
        '6', '6', '6', '6', '6', '6', '6', '6', '6', '6',
        '7', '7', '7', '7', '7', '7', '7', '7', '7', '7',
        '8', '8', '8', '8', '8', '8', '8', '8', '8', '8',
        '9', '9', '9', '9', '9', '9', '9', '9', '9', '9',};

    final static char[] DigitOnes = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',};

    private final static char[] digits = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};

    public RRNGenerator(String prepender) {
        prevTime = 0;
        firstCharList = Collections.synchronizedList(new ArrayList<Character>());
        secondCharList = Collections.synchronizedList(new ArrayList<Character>());
        random = new Random();

        prependerCheck(prepender);

        strPrepender = prepender.toUpperCase();

        refreshCharacterList();
    }

    private void prependerCheck(String prepender) throws IllegalArgumentException {
        if (prepender == null || prepender.trim().equals("")) {

            throw new IllegalArgumentException("The passed 'prepender' should not be null or empty");
        }

        if (prepender.length() != 2) {
            throw new IllegalArgumentException("Prepender should be 2 characters. Supplied prepender: " + prepender);
        }

        char charAt0 = prepender.charAt(0);
        char charAt1 = prepender.charAt(1);

        if (!Character.isLetterOrDigit(charAt0) || !Character.isLetterOrDigit(charAt1)) {
            throw new IllegalArgumentException("All characters should be alpha-numeric. Supplied prepender: " + prepender);
        }
    }

    private synchronized String getRandomCharacterRRN(long time) {
        return getRandomCharacterRRN(time, "");
    }

    private synchronized String getRandomCharacterRRN(long time, String prepender) {
        StringBuilder rrn = null;
        if (prepender == null || prepender.trim().isEmpty()) {
            rrn = new StringBuilder(strPrepender);
        } else {
            rrn = new StringBuilder(prepender);
        }

        char firstChar = 0;
        char secondChar = 0;
        if (time == prevTime) {
            if (firstCharList.isEmpty()) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ex) {
                    Logger.getLogger(RRNGenerator.class.getName()).log(Level.SEVERE, null, ex);
                }

                refreshCharacterList();
                time = System.currentTimeMillis();
            }
        } else {
            refreshCharacterList();
        }
        firstChar = firstCharList.remove(random.nextInt(firstCharList.size()));
        secondChar = secondCharList.remove(random.nextInt(secondCharList.size()));

        prevTime = time;

        rrn.append(firstChar).append(toString(time, 34).toUpperCase()).append(secondChar);

        return rrn.toString();
    }

    public synchronized String getRandomCharacterRRN() {
        long time = System.currentTimeMillis();

        return getRandomCharacterRRN(time);

    }

    public synchronized String getRandomCharacterRRN(String prepender) {

        prependerCheck(prepender);

        long time = System.currentTimeMillis();

        return getRandomCharacterRRN(time, prepender);

    }

    private synchronized String getRRN(long time) {
        return getRRN(time, "");
    }

    private synchronized String getRRN(long time, String prepender) {
        StringBuilder rrn = null;
        if (prepender == null || prepender.trim().isEmpty()) {
            rrn = new StringBuilder(strPrepender);
        } else {
            rrn = new StringBuilder(prepender);
        }

        if (time == prevTime) {
            char1++;
            if (char1 >= digits.length) {
                char1 = 0;
                char2++;
                if (char2 >= digits.length) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(RRNGenerator.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    char1 = 0;
                    char2 = 0;
                    return getRRN();
                }
            }
        } else {
            char1 = 0;
            char2 = 0;
        }

        prevTime = time;

        rrn.append(digits[char2]).append(toString(time, 34).toUpperCase()).append(digits[char1]);
        try {
            Thread.sleep(1);
        } catch (InterruptedException ex) {
            Logger.getLogger(RRNGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rrn.toString().substring(0, 12);
    }

    public synchronized String getRRN() {

        long time = System.currentTimeMillis();

        return getRRN(time);

    }

    public synchronized String getRRN(String prepender) {

        prependerCheck(prepender);

        long time = System.currentTimeMillis();

        return getRRN(time, prepender);

    }

    private void refreshCharacterList() {
        firstCharList.clear();
        secondCharList.clear();
        for (char c : digits) {
            firstCharList.add(c);
            secondCharList.add(c);
        }
    }

    /**
     * Returns a string representation of the first argument in the radix
     * specified by the second argument.
     *
     * <p>
     * If the radix is smaller than {@code Character.MIN_RADIX} or larger than
     * {@code Character.MAX_RADIX}, then the radix {@code 10} is used instead.
     *
     * <p>
     * If the first argument is negative, the first element of the result is the
     * ASCII minus sign {@code '-'} (<code>'&#92;u002d'</code>). If the first
     * argument is not negative, no sign character appears in the result.
     *
     * <p>
     * The remaining characters of the result represent the magnitude of the
     * first argument. If the magnitude is zero, it is represented by a single
     * zero character {@code '0'} (<code>'&#92;u0030'</code>); otherwise, the
     * first character of the representation of the magnitude will not be the
     * zero character. The following ASCII characters are used as digits:
     *
     * <blockquote> {@code 0123456789abcdefghijklmnopqrstuvwxyz}
     * </blockquote>
     *
     * These are <code>'&#92;u0030'</code> through <code>'&#92;u0039'</code> and
     * <code>'&#92;u0061'</code> through <code>'&#92;u007a'</code>. If
     * {@code radix} is
     * <var>N</var>, then the first <var>N</var> of these characters are used as
     * radix-<var>N</var> digits in the order shown. Thus, the digits for
     * hexadecimal (radix 16) are {@code 0123456789abcdef}. If uppercase letters
     * are desired, the {@link String#toUpperCase()} method may be
     * called on the result:
     *
     * <blockquote> {@code Long.toString(n, 16).toUpperCase()}
     * </blockquote>
     *
     * @param i a {@code long} to be converted to a string.
     * @param radix the radix to use in the string representation.
     * @return a string representation of the argument in the specified radix.
     * @see Character#MAX_RADIX
     * @see Character#MIN_RADIX
     */
    public static String toString(long i, int radix) {
        if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX) {
            radix = 10;
        }
        if (radix == 10) {
            return toString(i);
        }
        char[] buf = new char[65];
        int charPos = 64;
        boolean negative = (i < 0);

        if (!negative) {
            i = -i;
        }

        while (i <= -radix) {
            buf[charPos--] = digits[(int) (-(i % radix))];
            i = i / radix;
        }
        buf[charPos] = digits[(int) (-i)];

        if (negative) {
            buf[--charPos] = '-';
        }

        return new String(buf, charPos, (65 - charPos));
    }

    /**
     * Returns a {@code String} object representing the specified {@code long}.
     * The argument is converted to signed decimal representation and returned
     * as a string, exactly as if the argument and the radix 10 were given as
     * arguments to the {@link
     * #toString(long, int)} method.
     *
     * @param i a {@code long} to be converted.
     * @return a string representation of the argument in base&nbsp;10.
     */
    public static String toString(long i) {
        if (i == Long.MIN_VALUE) {
            return "-9223372036854775808";
        }
        int size = (i < 0) ? stringSize(-i) + 1 : stringSize(i);
        char[] buf = new char[size];
        getChars(i, size, buf);
        return new String(buf);
    }

    // Requires positive x
    static int stringSize(long x) {
        long p = 10;
        for (int i = 1; i < 19; i++) {
            if (x < p) {
                return i;
            }
            p = 10 * p;
        }
        return 19;
    }

    /**
     * Places characters representing the integer i into the character array
     * buf. The characters are placed into the buffer backwards starting with
     * the least significant digit at the specified index (exclusive), and
     * working backwards from there.
     *
     * Will fail if i == Long.MIN_VALUE
     */
    static void getChars(long i, int index, char[] buf) {
        long q;
        int r;
        int charPos = index;
        char sign = 0;

        if (i < 0) {
            sign = '-';
            i = -i;
        }

        // Get 2 digits/iteration using longs until quotient fits into an int
        while (i > Integer.MAX_VALUE) {
            q = i / 100;
            // really: r = i - (q * 100);
            r = (int) (i - ((q << 6) + (q << 5) + (q << 2)));
            i = q;
            buf[--charPos] = DigitOnes[r];
            buf[--charPos] = DigitTens[r];
        }

        // Get 2 digits/iteration using ints
        int q2;
        int i2 = (int) i;
        while (i2 >= 65536) {
            q2 = i2 / 100;
            // really: r = i2 - (q * 100);
            r = i2 - ((q2 << 6) + (q2 << 5) + (q2 << 2));
            i2 = q2;
            buf[--charPos] = DigitOnes[r];
            buf[--charPos] = DigitTens[r];
        }

        // Fall thru to fast mode for smaller numbers
        // assert(i2 <= 65536, i2);
        for (;;) {
            q2 = (i2 * 52429) >>> (16 + 3);
            r = i2 - ((q2 << 3) + (q2 << 1));  // r = i2-(q2*10) ...
            buf[--charPos] = digits[r];
            i2 = q2;
            if (i2 == 0) {
                break;
            }
        }
        if (sign != 0) {
            buf[--charPos] = sign;
        }
    }

    /**
     * Reverses the rrn to the time that was used in constructing it
     *
     * @param rrn
     * @return
     */
    public static long reverse(String rrn) {
        return rebase(rrn.substring(4, 12));
    }

    /**
     * Reverses the 'time-part' in the rrn to get the long value representing
     * the date
     *
     * @param timePartString
     * @return
     */
    public static long rebase(String timePartString) {
        long result = 0;
        int position = timePartString.length(); //we start from the last digit in a String (lowest value)
        for (char ch : timePartString.toCharArray()) {
            int value = String.valueOf(digits).indexOf(ch);
            result += value * Math.pow(digits.length, --position); //this is your 1x2(pow 0)+0x2(pow 1)+0x2(pow 2)+1x2(pow 3)

        }
        return result;
    }

}
