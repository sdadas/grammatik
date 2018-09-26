/*
 * To oprogramowanie jest własnością
 *
 * OPI - Ośrodek Przetwarzania Informacji,
 * Al. Niepodległości 188B, 00-608 Warszawa
 * Numer KRS: 0000127372
 * Sąd Rejonowy dla m. st. Warszawy w Warszawie XII Wydział
 * Gospodarczy KRS
 * REGON: 006746090
 * NIP: 525-000-91-40
 * Wszystkie prawa zastrzeżone. To oprogramowanie może być używane tylko
 * zgodnie z przeznaczeniem. OPI nie odpowiada za ewentualne wadliwe
 * działanie kodu.
 *
 * Id: $Id: TestUtils.java 323 2011-05-25 09:03:41Z sdadas $
 * Data ostatniej modyfikacji: $LastChangedDate: 2011-05-25 11:03:41 +0200 (Śr) $
 * Użytkownik ostatnio modyfikujący: $Author: sdadas $
 * Rewizja: $Revision: 323 $
 */
package pl.org.opi.grammatik.utils;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.*;

/**
 * Klasa utils do generowania losowych wartości.
 *
 * @author Sławomir Dadas <sdadas@opi.org.pl>
 */
public final class RandomUtils {

    /**
     * Domyślna długość tetowego słowa.
     */
    public static final int DEFAULT_TEST_WORD_LENGTH = 16;

    /**
     * Domyślna ilość miejsc po przecinku dla typów zmiennoprzecinkowych.
     */
    public static final int DEFAULT_NUMBER_PRECISION = 2;

    private static final long MINIMUM_DAY = LocalDate.of(1800, Month.JANUARY, 1).toEpochDay();
    private static final long MAXIUMUM_DAY = LocalDate.of(2100, Month.DECEMBER, 31).toEpochDay();

    private static final Random RANDOM = new Random();

    /**
     * Ukryty konstruktor.
     */
    private RandomUtils() {
    }

    /**
     * Zwraca losowy łańcych znakowy ze znaków z podanych zakresów.
     *
     * @param length wymagana długość łańcucha.
     * @param ranges zasięgi znaków do losowania w postaci tablic 2-elementowych.
     * @return losowy łańcuch.
     * @author Dariusz Kaczyński
     */
    public static String randomString(int length, int[]... ranges) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(randomCharacter(ranges));
        }
        return sb.toString();
    }

    /**
     * Zwraca losowy łańcuch ze znaków z podanego zakresu.
     *
     * @param length  wymagana długość łańcucha.
     * @param minChar początek zakresu (włącznie).
     * @param maxChar koniec zakresu (wyłącznie).
     * @return wylosowany łańcuch.
     * @author Dariusz Kaczyński
     */
    public static String randomString(int length, int minChar, int maxChar) {
        return randomString(length, new int[]{minChar, maxChar});
    }

    /**
     * Zwraca lodowy łańcuch znakowy o podanej długiści.
     *
     * @param length wymagana długośc łańcucha.
     * @return losowy łańcuch.
     * @author Dariusz Kaczyński
     */
    public static String randomString(int length) {
        return randomString(length, Character.MIN_CODE_POINT, Character.MAX_CODE_POINT);
    }

    /**
     * Zwraca losowy łąńcuch znakowy.
     *
     * @return losowy łańcuch
     */
    public static String randomString() {
        return randomString(DEFAULT_TEST_WORD_LENGTH,
                Character.MIN_CODE_POINT, Character.MAX_CODE_POINT);
    }

    /**
     * Zwraca losowy łańcych znakowy ze znaków z podanych zakresów.
     *
     * @param minLenght minimalna długość łańcucha.
     * @param maxLenght maksymalna długość łańcucha.
     * @param ranges    zasięgi znaków do losowania w postaci tablic 2-elementowych.
     * @return losowy łańcuch.
     * @author Dariusz Kaczyński
     */
    public static String randomString(int minLenght, int maxLenght, int[]... ranges) {
        int lenght = randomInt(minLenght, maxLenght);
        return randomString(lenght, ranges);
    }

    /**
     * Zwraca losowe słowo złożone z cyfr lub małych lub dużych liter o podanej długości
     * {@link #DEFAULT_TEST_WORD_LENGTH}.
     *
     * @return losowe słowo.
     * @author Dariusz Kaczyński
     */
    public static String randomWord() {
        return randomWord(DEFAULT_TEST_WORD_LENGTH);
    }

    /**
     * Zwraca losowe słowo złożone z cyfr lub małych lub dużych liter o podanej długości.
     *
     * @param lenght minimalna długośc słowa.
     * @return losowe słowo.
     * @author Dariusz Kaczyński
     */
    public static String randomWord(int lenght) {
        return randomWord(lenght, lenght);
    }

    /**
     * Zwraca losowe słowo złożone z cyfr lub małych lub dużych liter o podanej długości.
     *
     * @param minLenght minimalna długośc słowa.
     * @param maxLenght maksmalna długośc słowa.
     * @return losowe słowo.
     * @author Dariusz Kaczyński
     */
    public static String randomWord(int minLenght, int maxLenght) {
        final int digitStart = 48;      // od 0 ..
        final int digitEnd = 58;        // do 9;
        final int capitalStart = 65;    // od A ..
        final int capitalEnd = 91;      // do Z;
        final int letterStart = 97;     // od a ..
        final int letterEnd = 123;      // do z;
        return randomString(minLenght, maxLenght,
                new int[]{digitStart, digitEnd},
                new int[]{capitalStart, capitalEnd},
                new int[]{letterStart, letterEnd});
    }

    /**
     * Zwraca losowy ciąg słów oddzielonych spacjami.
     *
     * @param minWordLenght minimalna długośc słowa.
     * @param maxWordLenght maksmalna długośc słowa.
     * @param numWords      liczba słów.
     * @return losowe ciąg słów.
     * @author Sławomir Dadas
     */
    public static String randomWords(int minWordLenght, int maxWordLenght, int numWords) {

        List<String> words = Lists.newArrayList();
        for (int i = 0; i < numWords; ++i) {
            words.add(randomWord(minWordLenght, maxWordLenght));
        }

        return Joiner.on(" ").join(words);
    }

    /**
     * Zwraca losowe słowo złożone z cyfr o podanej długości.
     *
     * @param length wymagana długośc słowa.
     * @return losowe słowo.
     * @author Sławomir Dadas
     */
    public static String randomDigits(int length) {
        final int digitStart = 48;      // od 0 ..
        final int digitEnd = 58;        // do 9;
        return randomString(length,
                new int[]{digitStart, digitEnd});
    }

    /**
     * Zwraca losowe słowo złożone z cyfr o podanej długości {@link #DEFAULT_TEST_WORD_LENGTH}.
     *
     * @return losowe słowo.
     * @author Sławomir Dadas
     */
    public static String randomDigits() {
        return randomDigits(DEFAULT_TEST_WORD_LENGTH);
    }

    /**
     * Zwraca losowy znak.
     *
     * @return losowy {@link Character}
     */
    public static Character randomCharacter() {
        return randomCharacter(Character.MIN_CODE_POINT, Character.MAX_CODE_POINT);
    }

    /**
     * Zwraca losowy znak z podanego zakresu.
     *
     * @param ranges zakres do losowania.
     * @return losowy znak.
     * @author Dariusz Kaczyński
     */
    public static Character randomCharacter(int[]... ranges) {
        if (ranges == null || ranges.length == 0) {
            throw new IllegalArgumentException("nie podano zasięgu do losowania");
        }
        int[] range = ranges[RANDOM.nextInt(ranges.length)];
        return randomCharacter(range[0], range[1]);
    }

    /**
     * Zwraca losowy znak z podanego zakresu.
     *
     * @param minChar początek zakresu (włącznie).
     * @param maxChar koniec zakresu (wyłącznie).
     * @return losowy znak.
     * @author Dariusz Kaczyński
     */
    public static Character randomCharacter(int minChar, int maxChar) {
        if (minChar < Character.MIN_CODE_POINT) {
            throw new IllegalArgumentException("minChar nie może być mniejszy od " + Character.MIN_CODE_POINT);
        }
        if (maxChar > Character.MAX_CODE_POINT) {
            throw new IllegalArgumentException("maxChar nie może być większy od " + Character.MAX_CODE_POINT);
        }

        int range = maxChar - minChar;

        return Character.valueOf((char) (RANDOM.nextInt(range) + minChar));
    }

    /**
     * Zwraca losową literę.
     *
     * @return losowa litera
     */
    public static Character randomLetter() {

        final int capitalStart = 65;    // od A ..
        final int capitalEnd = 91;      // do Z;
        final int letterStart = 97;     // od a ..
        final int letterEnd = 123;      // do z;
        return randomCharacter(new int[]{capitalStart, capitalEnd}, new int[]{letterStart, letterEnd});
    }

    /**
     * Zwraca losowy email.
     *
     * @return - losowy email.
     * @author Dariusz Kaczyński
     */
    public static String randomEmail() {
        return randomEmail(DEFAULT_TEST_WORD_LENGTH, DEFAULT_TEST_WORD_LENGTH / 2);
    }

    /**
     * Zwraca losowy email.
     *
     * @param loginLength  - dlugosc loginu.
     * @param domainLength - dlugosc domeny.
     * @return - losowy email.
     * @author Andrzej Sadłowski
     */
    public static String randomEmail(int loginLength, int domainLength) {
        return randomWord(loginLength).toLowerCase() + "@" + randomWord(domainLength).toLowerCase() + "." + "pl";
    }

    /**
     * Zwraca losowy email.
     *
     * @param domain explicite nazwa domeny
     * @return losowy email.
     * @author Marcin Kobylarz
     */
    public static String randomEmail(String domain) {
        return randomWord(DEFAULT_TEST_WORD_LENGTH) + "@" + domain;
    }

    /**
     * Generuje losowe wartości dla podanej tablicy.
     *
     * @param bytes tablica {@link Byte}.
     * @author Tomasz Janek
     */
    public static void randomBytes(byte[] bytes) {
        RANDOM.nextBytes(bytes);
    }

    /**
     * Generuje losową tablicę bajtów.
     *
     * @param length length
     * @return byte[]
     */
    public static byte[] randomBytes(int length) {

        byte[] bytes = new byte[length];
        randomBytes(bytes);
        return bytes;
    }

    /**
     * Losuje wartość typu {@link Integer}.
     *
     * @return losowa wartość.
     * @author Dariusz Kaczyński
     */
    public static Integer randomInt() {
        return randomInt(0, Integer.MAX_VALUE);
    }

    /**
     * Losuje wartość typu {@link Integer} z zakresu <code>[0, max)</code>.
     *
     * @param max maksymalna wartość wylosowanej liczby.
     * @return losowa wartość z podanego przedziału.
     * @author Dariusz Kaczyński
     */
    public static Integer randomInt(int max) {
        return randomInt(0, max);
    }

    /**
     * Losuje wartość typu {@link Integer} z zakresu <code>[min, max)</code>.
     *
     * @param min minimalna wartość wylosowanej liczby.
     * @param max maksymalna wartość wylosowanej liczby.
     * @return losowa wartość z podanego przedziału.
     * @author Dariusz Kaczyński
     */
    public static Integer randomInt(int min, int max) {
        return min == max ? min : min + RANDOM.nextInt(max - min);
    }

    /**
     * Losuje wartość typu {@link Short}.
     *
     * @return losowa wartość.
     * @author Dariusz Kaczyński
     */
    public static Short randomShort() {
        return (short) RANDOM.nextInt(Short.MAX_VALUE);
    }

    /**
     * Losuje wartość typu {@link Short} z zakresu <code>[min, max)</code>.
     *
     * @param min minimalna wartość wylosowanej liczby.
     * @param max maksymalna wartość wylosowanej liczby.
     * @return losowa wartość z podanego przedziału.
     * @author Dariusz Kaczyński
     */
    public static Short randomShort(int min, int max) {
        return min == max ? (short) min : (short) (min + RANDOM.nextInt(max - min));
    }

    /**
     * Zwraca losową wartośc typu long.
     *
     * @return losowy {@link Long}.
     * @author Dariusz Kaczyński
     */
    public static Long randomLong() {
        return Long.valueOf(RANDOM.nextLong());
    }

    public static Long randomLong(long min, long max) {
        return min + (long) (Math.random() * (max - min));
    }

    /**
     * Losuje wartość dla pola Id Number(8) która nie ma prawa wystąpić w bazie oracle.
     *
     * @return wartość id dla pola która nie może wystąpić w bazie.
     * @author Adam Faryna
     */
    public static Long randomIdNegative() {
        return -randomId();
    }

    /**
     * Losuje wartość dla pola Id Number(8) z oracle.
     *
     * @return wartość id dla pola.
     */
    public static Long randomId() {
        final long maxId = 99999999L;
        return Long.valueOf(RANDOM.nextInt(Integer.MAX_VALUE) % maxId);
    }

    /**
     * Generuje losowy Integer zawierajacy określoną liczbę cyfr.
     *
     * @param digits digits
     * @return Integer
     */
    public static Integer randomIntDigits(int digits) {
        if (digits < 1) {
            throw new IllegalArgumentException();
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < digits; i++) {
            builder.append('9');
        }
        return RANDOM.nextInt(Integer.MAX_VALUE) % Integer.parseInt(builder.toString());
    }

    /**
     * Zwraca losową wartość logiczną.
     *
     * @return losowy {@link Boolean}.
     */
    public static Boolean randomBoolean() {
        return Boolean.valueOf(RANDOM.nextBoolean());
    }

    /**
     * Zwraca losową wartośc typu float.
     *
     * @return losowy {@link Float}.
     */
    public static Float randomFloat() {
        return randomFloat(DEFAULT_NUMBER_PRECISION);
    }

    /**
     * Zwraca losową wartość typu float z ograniczoną precyzją.
     *
     * @param precision Precyzja.
     * @return losowy {@link Float}.
     */
    public static Float randomFloat(int precision) {
        validate(precision > 0, "Parametr 'precision' musi być większy od 0!");
        BigDecimal decimal = new BigDecimal(Float.toString(RANDOM.nextFloat()));
        return decimal.setScale(precision, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    /**
     * Zwraca losową wartość typu double.
     *
     * @return losowy {@link Double}.
     */
    public static Double randomDouble() {
        return randomDouble(DEFAULT_NUMBER_PRECISION);
    }

    /**
     * Zwraca losową wartość typu double z ograniczoną precyzją.
     *
     * @param precision Precyzja.
     * @return losowy {@link Double}.
     */
    public static Double randomDouble(int precision) {
        validate(precision > 0, "Parametr 'precision' musi być większy od 0!");
        BigDecimal decimal = new BigDecimal(Double.toString(RANDOM.nextDouble()));
        return decimal.setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double randomDouble(double min, double max) {
        validate(min <= max, "max must be greater than min");
        return min + (max - min) * RANDOM.nextDouble();
    }

    /**
     * Zwraca losową wartość typu BigDecimal.
     *
     * @return losowy {@link BigDecimal}.
     */
    public static BigDecimal randomBigDecimal() {
        return new BigDecimal(randomDouble()).multiply(new BigDecimal(randomLong().longValue()));
    }

    private static long randomEpochDay() {
        return randomEpochDay(MINIMUM_DAY, MAXIUMUM_DAY);
    }

    private static long randomEpochDay(long minDay, long maxDay) {
        return randomLong(minDay, maxDay);
    }

    public static LocalDate randomLocalDate() {
        return LocalDate.ofEpochDay(randomEpochDay());
    }

    public static LocalDate randomLocalDate(int yearFrom, int yearTo) {
        if(yearFrom > yearTo) throw new IllegalStateException("Date from is later than date to!");
        LocalDate from = LocalDate.ofYearDay(yearFrom, 1);
        LocalDate to = LocalDate.ofYearDay(yearTo, 1);
        return randomLocalDate(from, to);
    }

    public static LocalDate randomLocalDate(LocalDate from, LocalDate to) {
        if(from.isAfter(to)) throw new IllegalStateException("Date from is later than date to!");
        return LocalDate.ofEpochDay(randomEpochDay(from.toEpochDay(), to.toEpochDay()));
    }

    public static Date randomDate() {
        return Date.from(randomLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static Date randomDate(int yearFrom, int yearTo) {
        return Date.from(randomLocalDate(yearFrom, yearTo).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDate randomFutureDate() {
        long today = LocalDate.now().toEpochDay();
        return LocalDate.ofEpochDay(randomEpochDay(today + 1L, MAXIUMUM_DAY));
    }

    public static LocalDate randomFutureDateAfter(LocalDate after) {
        return LocalDate.ofEpochDay(randomEpochDay(after.toEpochDay(), MAXIUMUM_DAY));
    }

    public static LocalDate randomPastDate() {
        long today = LocalDate.now().toEpochDay();
        return LocalDate.ofEpochDay(randomEpochDay(MINIMUM_DAY, today - 1L));
    }

    public static LocalDate randomPastDate(LocalDate after) {
        long today = LocalDate.now().toEpochDay();
        return LocalDate.ofEpochDay(randomEpochDay(after.toEpochDay(), today - 1L));
    }

    public static XMLGregorianCalendar randomXmlGregorianCalendar() {
        return xmlGregorianCalendarFromDate(randomLocalDate());
    }

    public static XMLGregorianCalendar randomXmlGregorianCalendar(LocalDate from, LocalDate to) {
        return xmlGregorianCalendarFromDate(randomLocalDate(from, to));
    }

    public static XMLGregorianCalendar randomXmlGregorianCalendar(int yearFrom, int yearTo) {
        return xmlGregorianCalendarFromDate(randomLocalDate(yearFrom, yearTo));
    }

    private static XMLGregorianCalendar xmlGregorianCalendarFromDate(LocalDate date) {
        GregorianCalendar calendar = GregorianCalendar.from(date.atStartOfDay(ZoneId.systemDefault()));
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
        } catch (DatatypeConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Zwraca losową wartość typu Timestamp.
     *
     * @return losowy {@link Timestamp}
     */
    public static Timestamp randomTimestamp() {
        return new Timestamp(randomId());
    }

    /**
     * Wybiera losową pozycję spośród podanych możliwości.
     *
     * @param <T>     Typ pozycji.
     * @param options Póla możliwych pozycji.
     * @return Wybrana pozycja.
     * @author Adam Faryna
     */
    @SafeVarargs
    public static <T> T randomChoice(T... options) {
        validate(options != null, "Parametr 'options' nie może być null!");
        return ((options.length == 1) ? options[0] : options[randomInt(options.length)]);
    }

    public static <T> T randomChoice(Iterable<T> options) {
        validate(options != null, "Options cannot be null");
        int size = Iterables.size(options);
        validate(size > 0, "Options cannot be empty");
        return size == 1 ? Iterables.get(options, 0) : Iterables.get(options, randomInt(size));
    }

    /**
     * Wybiera losową wartość wykluczając jedną z nich.
     *
     * @param <T>     typ wartości.
     * @param excude  wartośc wyluczona.
     * @param options zbiór wartości.
     * @return losowa wartość różna od wykluczonej.
     */
    @SafeVarargs
    public static <T> T randomChoiceExcept(T excude, T... options) {
        T result = null;
        while (result == null || result.equals(excude)) {
            result = randomChoice(options);
        }
        return result;
    }

    /**
     * <p>
     * Generuje 4 losowe liczby  z zakresu 1-254 połączone kropkami.
     * </p>
     *
     * @return losowy (być może bezsensowny) adres ip
     */
    public static String randomIp() {
        final int max = 255;

        StringBuffer sb = new StringBuffer();
        sb.append(randomInt(1, max));
        sb.append('.');
        sb.append(randomInt(1, max));
        sb.append('.');
        sb.append(randomInt(1, max));
        sb.append('.');
        sb.append(randomInt(1, max));

        return sb.toString();
    }

    /**
     * Generuje losowy adres URL.
     *
     * @return url
     */
    public static String randomURL() {

        return String.format("http://www.%s.%s",
                randomWord(10),
                randomChoice("pl", "com", "net", "org"));
    }

    public static <T> T randomObject(Collection<T> objects) {

        int max = objects.size() - 1;
        int index = randomInt(max);
        return Iterables.get(objects, index);
    }

    private static void validate(boolean condition, String message) {
        if(!condition) {
            throw new IllegalStateException(message);
        }
    }
}
