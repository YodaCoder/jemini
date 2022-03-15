/*************************************************************************
 * Copyright (c) 1998 by Free Software Foundation, Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Library General Public License as published
 * by the Free Software Foundation, version 2. (see COPYING.LIB)
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation
 * Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307 USA
 ************************************************************************
 * Copyright (c) 2000 WebSprocket LLC. All Rights Reserved.
 *
 * WEBSPROCKET MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. WEBSPROCKET SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */

package java.util;
public class Date implements java.io.Serializable, Cloneable, Comparable
{
    /**
     * Allocates a <code>Date</code> object and initializes it so that
     * it represents the time at which it was allocated, measured to the
     * nearest millisecond.
     *
     * @see     java.lang.System#currentTimeMillis()
     */
    private static int defaultCenturyStart = 0;
	private int year, month, date, hours, minits, seconds;
    private long fastTime;
	private GregorianCalendar staticCal = null;
	private Calendar cal = null;
	private String pattern = "EEE MMM dd HH:mm:ss zzz yyyy";
	private final static String[] WEEKS = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri","Sat"};

	private final static String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun","Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

	public Date()
    {
        this(System.currentTimeMillis());
    }
    /**
     * Allocates a <code>Date</code> object and initializes it to
     * represent the specified number of milliseconds since the
     * standard base time known as "the epoch", namely January 1,
     * 1970, 00:00:00 GMT.
     *
     * @param   date   the milliseconds since January 1, 1970, 00:00:00 GMT.
     * @see     java.lang.System#currentTimeMillis()
     */
    public Date(long date)
    {
        fastTime = date;
    }
    /**
     * Allocates a <code>Date</code> object and initializes it so that
     * it represents midnight, local time, at the beginning of the day
     * specified by the <code>year</code>, <code>month</code>, and
     * <code>date</code> arguments.
     *
     * @param   year    the year minus 1900.
     * @param   month   the month between 0-11.
     * @param   date    the day of the month between 1-31.
     */
    public Date(int year, int month, int date)
    {
        this(year, month, date, 0, 0, 0);
    }
    /**
     * Allocates a <code>Date</code> object and initializes it so that
     * it represents the instant at the start of the minute specified by
     * the <code>year</code>, <code>month</code>, <code>date</code>,
     * <code>hrs</code>, and <code>min</code> arguments, in the local
     * time zone.
     *
     * @param   year    the year minus 1900.
     * @param   month   the month between 0-11.
     * @param   date    the day of the month between 1-31.
     * @param   hrs     the hours between 0-23.
     * @param   min     the minutes between 0-59.
     */
    public Date(int year, int month, int date, int hrs, int min)
    {
        this(year, month, date, hrs, min, 0);
    }

    /**
     * Allocates a <code>Date</code> object and initializes it so that
     * it represents the instant at the start of the second specified
     * by the <code>year</code>, <code>month</code>, <code>date</code>,
     * <code>hrs</code>, <code>min</code>, and <code>sec</code> arguments,
     * in the local time zone.
     *
     * @param   year    the year minus 1900.
     * @param   month   the month between 0-11.
     * @param   date    the day of the month between 1-31.
     * @param   hrs     the hours between 0-23.
     * @param   min     the minutes between 0-59.
     * @param   sec     the seconds between 0-59.
     */
    public Date(int year, int month, int date, int hrs, int min, int sec)
    {
        cal = null;
        if (staticCal == null)
            makeStaticCalendars();
     	staticCal.clear();
     	staticCal.set(year + 1900, month, date, hrs, min, sec);
     	fastTime = staticCal.getTimeInMillis();
    }

    /**
     * Allocates a <code>Date</code> object and initializes it so that
     * it represents the date and time indicated by the string
     * <code>s</code>, which is interpreted as if by the
     * {@link Date#parse} method.
     *
     * @param   s   a string representation of the date.
     * @see     java.text.DateFormat
     * @see     java.util.Date#parse(java.lang.String)
     */
    /*public Date(String s)
    {
        this(parse(s));
    }*/

	/**
     * Determines the date and time based on the arguments. The
     * arguments are interpreted as a year, month, day of the month,
     * hour of the day, minute within the hour, and second within the
     * minute, exactly as for the <tt>Date</tt> constructor with six
     * arguments, except that the arguments are interpreted relative
     * to UTC rather than to the local time zone. The time indecated is
     * returned represented as the distance, measured in milliseconds,
     * of that time from the epoch (00:00:00 GMT on January 1, 1970).
     *
     * @param   year    the year minus 1900.
     * @param   month   the month between 0-11.
     * @param   date    the day of the month between 1-31.
     * @param   hrs     the hours between 0-23.
     * @param   min     the minutes between 0-59.
     * @param   sec     the seconds between 0-59.
     * @return  the number of milliseconds since January 1, 1970, 00:00:00 GMT for
     *          the date and time specified by the arguments.
     */

    /*public static long UTC(int year, int month, int date, int hrs, int min, int sec)
    {
        if (utcCal == null)
            makeStaticCalendars();
        synchronized (utcCal) {
            utcCal.clear();
            utcCal.set(year + 1900, month, date, hrs, min, sec);
            return utcCal.getTimeInMillis();
        }
    }*/

    /**
     * Attempts to interpret the string <tt>s</tt> as a representation
     * of a date and time. If the attempt is successful, the time
     * indicated is returned represented as the distance, measured in
     * milliseconds, of that time from the epoch (00:00:00 GMT on
     * January 1, 1970). If the attempt fails, an
     * <tt>IllegalArgumentException</tt> is thrown.
     * <p>
     * It accepts many syntaxes; in particular, it recognizes the IETF
     * standard date syntax: "Sat, 12 Aug 1995 13:30:00 GMT". It also
     * understands the continental U.S. time-zone abbreviations, but for
     * general use, a time-zone offset should be used: "Sat, 12 Aug 1995
     * 13:30:00 GMT+0430" (4 hours, 30 minutes west of the Greenwich
     * meridian). If no time zone is specified, the local time zone is
     * assumed. GMT and UTC are considered equivalent.
     * <p>
     * The string <tt>s</tt> is processed from left to right, looking for
     * data of interest. Any material in <tt>s</tt> that is within the
     * ASCII parenthesis characters <tt>(</tt> and <tt>)</tt> is ignored.
     * Parentheses may be nested. Otherwise, the only characters permitted
     * within <tt>s</tt> are these ASCII characters:
     * <blockquote><pre>
     * abcdefghijklmnopqrstuvwxyz
     * ABCDEFGHIJKLMNOPQRSTUVWXYZ
     * 0123456789,+-:/</pre></blockquote>
     * and whitespace characters.<p>
     * A consecutive sequence of decimal digits is treated as a decimal
     * number:<ul>
     * <li>If a number is preceded by <tt>+</tt> or <tt>-</tt> and a year
     *     has already been recognized, then the number is a time-zone
     *     offset. If the number is less than 24, it is an offset measured
     *     in hours. Otherwise, it is regarded as an offset in minutes,
     *     expressed in 24-hour time format without punctuation. A
     *     preceding <tt>-</tt> means a westward offset. Time zone offsets
     *     are always relative to UTC (Greenwich). Thus, for example,
     *     <tt>-5</tt> occurring in the string would mean "five hours west
     *     of Greenwich" and <tt>+0430</tt> would mean "four hours and
     *     thirty minutes east of Greenwich." It is permitted for the
     *     string to specify <tt>GMT</tt>, <tt>UT</tt>, or <tt>UTC</tt>
     *     redundantly-for example, <tt>GMT-5</tt> or <tt>utc+0430</tt>.
     * <li>The number is regarded as a year number if one of the
     *     following conditions is true:
     * <ul>
     *     <li>The number is equal to or greater than 70 and followed by a
     *         space, comma, slash, or end of string
     *     <li>The number is less than 70, and both a month and a day of
     *         the month have already been recognized</li>
     * </ul>
     *     If the recognized year number is less than 100, it is
     *     interpreted as an abbreviated year relative to a century of
     *     which dates are within 80 years before and 19 years after
     *     the time when the Date class is initialized.
     *     After adjusting the year number, 1900 is subtracted from
     *     it. For example, if the current year is 1999 then years in
     *     the range 19 to 99 are assumed to mean 1919 to 1999, while
     *     years from 0 to 18 are assumed to mean 2000 to 2018.  Note
     *     that this is slightly different from the interpretation of
     *     years less than 100 that is used in {@link java.text.SimpleDateFormat}.
     * <li>If the number is followed by a colon, it is regarded as an hour,
     *     unless an hour has already been recognized, in which case it is
     *     regarded as a minute.
     * <li>If the number is followed by a slash, it is regarded as a month
     *     (it is decreased by 1 to produce a number in the range <tt>0</tt>
     *     to <tt>11</tt>), unless a month has already been recognized, in
     *     which case it is regarded as a day of the month.
     * <li>If the number is followed by whitespace, a comma, a hyphen, or
     *     end of string, then if an hour has been recognized but not a
     *     minute, it is regarded as a minute; otherwise, if a minute has
     *     been recognized but not a second, it is regarded as a second;
     *     otherwise, it is regarded as a day of the month. </ul><p>
     * A consecutive sequence of letters is regarded as a word and treated
     * as follows:<ul>
     * <li>A word that matches <tt>AM</tt>, ignoring case, is ignored (but
     *     the parse fails if an hour has not been recognized or is less
     *     than <tt>1</tt> or greater than <tt>12</tt>).
     * <li>A word that matches <tt>PM</tt>, ignoring case, adds <tt>12</tt>
     *     to the hour (but the parse fails if an hour has not been
     *     recognized or is less than <tt>1</tt> or greater than <tt>12</tt>).
     * <li>Any word that matches any prefix of <tt>SUNDAY, MONDAY, TUESDAY,
     *     WEDNESDAY, THURSDAY, FRIDAY</tt>, or <tt>SATURDAY</tt>, ignoring
     *     case, is ignored. For example, <tt>sat, Friday, TUE</tt>, and
     *     <tt>Thurs</tt> are ignored.
     * <li>Otherwise, any word that matches any prefix of <tt>JANUARY,
     *     FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER,
     *     OCTOBER, NOVEMBER</tt>, or <tt>DECEMBER</tt>, ignoring case, and
     *     considering them in the order given here, is recognized as
     *     specifying a month and is converted to a number (<tt>0</tt> to
     *     <tt>11</tt>). For example, <tt>aug, Sept, april</tt>, and
     *     <tt>NOV</tt> are recognized as months. So is <tt>Ma</tt>, which
     *     is recognized as <tt>MARCH</tt>, not <tt>MAY</tt>.
     * <li>Any word that matches <tt>GMT, UT</tt>, or <tt>UTC</tt>, ignoring
     *     case, is treated as referring to UTC.
     * <li>Any word that matches <tt>EST, CST, MST</tt>, or <tt>PST</tt>,
     *     ignoring case, is recognized as referring to the time zone in
     *     North America that is five, six, seven, or eight hours west of
     *     Greenwich, respectively. Any word that matches <tt>EDT, CDT,
     *     MDT</tt>, or <tt>PDT</tt>, ignoring case, is recognized as
     *     referring to the same time zone, respectively, during daylight
     *     saving time.</ul><p>
     * Once the entire string s has been scanned, it is converted to a time
     * result in one of two ways. If a time zone or time-zone offset has been
     * recognized, then the year, month, day of month, hour, minute, and
     * second are interpreted in UTC and then the time-zone offset is
     * applied. Otherwise, the year, month, day of month, hour, minute, and
     * second are interpreted in the local time zone.
     *
     * @param   s   a string to be parsed as a date.
     * @return  the number of milliseconds since January 1, 1970, 00:00:00 GMT
     *          represented by the string argument.
     */
    /*public static long parse(String s)
    {
        if (staticCal == null)
            makeStaticCalendars(); // Called only for side-effect of setting defaultCenturyStart

        int year = Integer.MIN_VALUE;
        int mon = -1;
        int mday = -1;
        int hour = -1;
        int min = -1;
        int sec = -1;
        int millis = -1;
        int c = -1;
        int i = 0;
        int n = -1;
        int wst = -1;
        int tzoffset = -1;
        int prevc = 0;

    syntax:
        {
            if (s == null)
                break syntax;
            int limit = s.length();
            while (i < limit) {
                c = s.charAt(i);
                i++;
                if (c <= ' ' || c == ',')
                    continue;
                if (c == '(') { // skip comments
                    int depth = 1;
                    while (i < limit) {
                        c = s.charAt(i);
                        i++;
                        if (c == '(') depth++;
                        else if (c == ')')
                            if (--depth <= 0)
                                break;
                    }
                    continue;
                }
                if ('0' <= c && c <= '9') {
                    n = c - '0';
                    while (i < limit && '0' <= (c = s.charAt(i)) && c <= '9') {
                        n = n * 10 + c - '0';
                        i++;
                    }
                    if (prevc == '+' || prevc == '-' && year != Integer.MIN_VALUE) {
                        // timezone offset
                        if (n < 24)
                            n = n * 60; // EG. "GMT-3"
                        else
                            n = n % 100 + n / 100 * 60; // eg "GMT-0430"
                        if (prevc == '+')   // plus means east of GMT
                            n = -n;
                        if (tzoffset != 0 && tzoffset != -1)
                            break syntax;
                        tzoffset = n;
                    } else if (n >= 70)
                        if (year != Integer.MIN_VALUE)
                            break syntax;
                        else if (c <= ' ' || c == ',' || c == '/' || i >= limit)
                            // year = n < 1900 ? n : n - 1900;
                            year = n;
                        else
                            break syntax;
                    else if (c == ':')
                        if (hour < 0)
                            hour = (byte) n;
                        else if (min < 0)
                            min = (byte) n;
                        else
                            break syntax;
                    else if (c == '/')
                        if (mon < 0)
                            mon = (byte) (n - 1);
                        else if (mday < 0)
                            mday = (byte) n;
                        else
                            break syntax;
                    else if (i < limit && c != ',' && c > ' ' && c != '-')
                        break syntax;
                    else if (hour >= 0 && min < 0)
                        min = (byte) n;
                    else if (min >= 0 && sec < 0)
                        sec = (byte) n;
                    else if (mday < 0)
                        mday = (byte) n;
                    // Handle two-digit years < 70 (70-99 handled above).
                    else if (year == Integer.MIN_VALUE && mon >= 0 && mday >= 0)
                        year = n;
                    else
                        break syntax;
                    prevc = 0;
                } else if (c == '/' || c == ':' || c == '+' || c == '-')
                    prevc = c;
                else {
                    int st = i - 1;
                    while (i < limit) {
                        c = s.charAt(i);
                        if (!('A' <= c && c <= 'Z' || 'a' <= c && c <= 'z'))
                            break;
                        i++;
                    }
                    if (i <= st + 1)
                        break syntax;
                    int k;
                    for (k = wtb.length; --k >= 0;)
                        if (wtb[k].regionMatches(true, 0, s, st, i - st)) {
                            int action = ttb[k];
                            if (action != 0) {
                                if (action == 1) {  // pm
                                    if (hour > 12 || hour < 1)
                                        break syntax;
                                    else if (hour < 12)
                                        hour += 12;
                                } else if (action == 14) {  // am
                                    if (hour > 12 || hour < 1)
                                        break syntax;
                                    else if (hour == 12)
                                        hour = 0;
                                } else if (action <= 13) {  // month!
                                    if (mon < 0)
                                        mon = (byte) (action - 2);
                                    else
                                        break syntax;
                                } else {
                                    tzoffset = action - 10000;
                                }
                            }
                            break;
                        }
                    if (k < 0)
                        break syntax;
                    prevc = 0;
                }
            }
            if (year == Integer.MIN_VALUE || mon < 0 || mday < 0)
                break syntax;
            // Parse 2-digit years within the correct default century.
            if (year < 100) {
                year += (defaultCenturyStart / 100) * 100;
                if (year < defaultCenturyStart) year += 100;
            }
            year -= 1900;
            if (sec < 0)
                sec = 0;
            if (min < 0)
                min = 0;
            if (hour < 0)
                hour = 0;
            if (tzoffset == -1) // no time zone specified, have to use local
                return new Date (year, mon, mday, hour, min, sec).getTime();
            return UTC(year, mon, mday, hour, min, sec) + tzoffset * (60 * 1000);
        }
        // syntax error
        throw new IllegalArgumentException();
    }*/
    private final static String wtb[] =
    {
        "am", "pm",
        "monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday",
        "january", "february", "march", "april", "may", "june", "july", "august", "september", "october", "november", "december",
        "gmt", "ut", "utc", "est", "edt", "cst", "cdt", "mst", "mdt", "pst", "pdt"
    };
    private final static int ttb[] =
    {
        14, 1, 0, 0, 0, 0, 0, 0, 0,
        2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
        10000 + 0, 10000 + 0, 10000 + 0,    // GMT/UT/UTC
        10000 + 5 * 60, 10000 + 4 * 60, // EST/EDT
        10000 + 6 * 60, 10000 + 5 * 60,
        10000 + 7 * 60, 10000 + 6 * 60,
        10000 + 8 * 60, 10000 + 7 * 60
    };


    public Object clone() {
	return null;
    }

    /**
     * Returns a value that is the result of subtracting 1900 from the
     * year that contains or begins with the instant in time represented
     * by this <code>Date</code> object, as interpreted in the local
     * time zone.
     *
     * @return  the year represented by this date, minus 1900.
     */
    public int getYear()
    {
        return getField(Calendar.YEAR) - 1900;
    }
    /**
     * Sets the year of this <tt>Date</tt> object to be the specified
     * value plus 1900. This <code>Date</code> object is modified so
     * that it represents a point in time within the specified year,
     * with the month, date, hour, minute, and second the same as
     * before, as interpreted in the local time zone. (Of course, if
     * the date was February 29, for example, and the year is set to a
     * non-leap year, then the new date will be treated as if it were
     * on March 1.)
     *
     * @param   year    the year value.
     */
    public void setYear(int year)
    {
        setField(Calendar.YEAR, year + 1900);
    }

    /**
     * Returns a number representing the month that contains or begins
     * with the instant in time represented by this <tt>Date</tt> object.
     * The value returned is between <code>0</code> and <code>11</code>,
     * with the value <code>0</code> representing January.
     *
     * @return  the month represented by this date.
     */
    public int getMonth()
    {
        return getField(Calendar.MONTH);
    }
    /**
     * Sets the month of this date to the specified value. This
     * <tt>Date</tt> object is modified so that it represents a point
     * in time within the specified month, with the year, date, hour,
     * minute, and second the same as before, as interpreted in the
     * local time zone. If the date was October 31, for example, and
     * the month is set to June, then the new date will be treated as
     * if it were on July 1, because June has only 30 days.
     *
     * @param   month   the month value between 0-11.
     */
    public void setMonth(int month)
    {
        setField(Calendar.MONTH, month);
    }

    /**
     * Returns the day of the month represented by this <tt>Date</tt> object.
     * The value returned is between <code>1</code> and <code>31</code>
     * representing the day of the month that contains or begins with the
     * instant in time represented by this <tt>Date</tt> object, as
     * interpreted in the local time zone.
     *
     * @return  the day of the month represented by this date.
     */
    public int getDate()
    {
        return getField(Calendar.DATE);
    }

    /**
     * Sets the day of the month of this <tt>Date</tt> object to the
     * specified value. This <tt>Date</tt> object is modified so that
     * it represents a point in time within the specified day of the
     * month, with the year, month, hour, minute, and second the same
     * as before, as interpreted in the local time zone. If the date
     * was April 30, for example, and the date is set to 31, then it
     * will be treated as if it were on May 1, because April has only
     * 30 days.
     *
     * @param   date   the day of the month value between 1-31.
     */
    public void setDate(int date)
    {
        setField(Calendar.DATE, date);
    }

    /**
     * Returns the day of the week represented by this date. The
     * returned value (<tt>0</tt> = Sunday, <tt>1</tt> = Monday,
     * <tt>2</tt> = Tuesday, <tt>3</tt> = Wednesday, <tt>4</tt> =
     * Thursday, <tt>5</tt> = Friday, <tt>6</tt> = Saturday)
     * represents the day of the week that contains or begins with
     * the instant in time represented by this <tt>Date</tt> object,
     * as interpreted in the local time zone.
     *
     * @return  the day of the week represented by this date.
     */
    public int getDay()
    {
        return getField(Calendar.DAY_OF_WEEK);// - Calendar.SUNDAY;
    }

    /**
     * Returns the hour represented by this <tt>Date</tt> object. The
     * returned value is a number (<tt>0</tt> through <tt>23</tt>)
     * representing the hour within the day that contains or begins
     * with the instant in time represented by this <tt>Date</tt>
     * object, as interpreted in the local time zone.
     *
     * @return  the hour represented by this date.
     */
    public int getHours()
    {
        return getField(Calendar.HOUR_OF_DAY);
    }

    /**
     * Sets the hour of this <tt>Date</tt> object to the specified value.
     * This <tt>Date</tt> object is modified so that it represents a point
     * in time within the specified hour of the day, with the year, month,
     * date, minute, and second the same as before, as interpreted in the
     * local time zone.
     *
     * @param   hours   the hour value.
     */
    public void setHours(int hours)
    {
        setField(Calendar.HOUR_OF_DAY, hours);
    }

    /**
     * Returns the number of minutes past the hour represented by this date,
     * as interpreted in the local time zone.
     * The value returned is between <code>0</code> and <code>59</code>.
     *
     * @return  the number of minutes past the hour represented by this date.
     */
    public int getMinutes()
    {
        return getField(Calendar.MINUTE);
    }

    /**
     * Sets the minutes of this <tt>Date</tt> object to the specified value.
     * This <tt>Date</tt> object is modified so that it represents a point
     * in time within the specified minute of the hour, with the year, month,
     * date, hour, and second the same as before, as interpreted in the
     * local time zone.
     *
     * @param   minutes   the value of the minutes.
     */
    public void setMinutes(int minutes)
    {
        setField(Calendar.MINUTE, minutes);
    }

    /**
     * Returns the number of seconds past the minute represented by this date.
     * The value returned is between <code>0</code> and <code>61</code>. The
     * values <code>60</code> and <code>61</code> can only occur on those
     * Java Virtual Machines that take leap seconds into account.
     *
     * @return  the number of seconds past the minute represented by this date.
     */
    public int getSeconds()
    {
        return getField(Calendar.SECOND);
    }

    /**
     * Sets the seconds of this <tt>Date</tt> to the specified value.
     * This <tt>Date</tt> object is modified so that it represents a
     * point in time within the specified second of the minute, with
     * the year, month, date, hour, and minute the same as before, as
     * interpreted in the local time zone.
     *
     * @param   seconds   the seconds value.
     */
    public void setSeconds(int seconds)
    {
        setField(Calendar.SECOND, seconds);
    }

    /**
     * Returns the number of milliseconds since January 1, 1970, 00:00:00 GMT
     * represented by this <tt>Date</tt> object.
     *
     * @return  the number of milliseconds since January 1, 1970, 00:00:00 GMT
     *          represented by this date.
     */
    public long getTime()
    {
        return (cal == null) ? fastTime : cal.getTimeInMillis();
    }

    /**
     * Sets this <tt>Date</tt> object to represent a point in time that is
     * <tt>time</tt> milliseconds after January 1, 1970 00:00:00 GMT.
     *
     * @param   time   the number of milliseconds.
     */
    public void setTime(long time)
    {
        if (cal == null)
        	fastTime = time;
        else
            cal.setTimeInMillis(time);
    }

    /**
     * Tests if this date is before the specified date.
     *
     * @param   when   a date.
     * @return  <code>true</code> if and only if the instant of time
     *            represented by this <tt>Date</tt> object is strictly
     *            earlier than the instant represented by <tt>when</tt>;
     *          <code>false</code> otherwise.
     */
    public boolean before(Date when)
    {
        return getTime() < when.getTime();
    }

    /**
     * Tests if this date is after the specified date.
     *
     * @param   when   a date.
     * @return  <code>true</code> if and only if the instant represented
     *          by this <tt>Date</tt> object is strictly later than the
     *          instant represented by <tt>when</tt>;
     *          <code>false</code> otherwise.
     */
    public boolean after(Date when)
    {
        return getTime() > when.getTime();
    }

    /**
     * Compares two dates for equality.
     * The result is <code>true</code> if and only if the argument is
     * not <code>null</code> and is a <code>Date</code> object that
     * represents the same point in time, to the millisecond, as this object.
     * <p>
     * Thus, two <code>Date</code> objects are equal if and only if the
     * <code>getTime</code> method returns the same <code>long</code>
     * value for both.
     *
     * @param   obj   the object to compare with.
     * @return  <code>true</code> if the objects are the same;
     *          <code>false</code> otherwise.
     * @see     java.util.Date#getTime()
     */
    public boolean equals(Object obj)
    {
        return obj instanceof Date && getTime() == ((Date) obj).getTime();
    }

    /**
     * Compares two Dates for ordering.
     *
     * @param   anotherDate   the <code>Date</code> to be compared.
     * @return  the value <code>0</code> if the argument Date is equal to
     *          this Date; a value less than <code>0</code> if this Date
     *          is before the Date argument; and a value greater than
     *      <code>0</code> if this Date is after the Date argument.
     */
    public int compareTo(Date anotherDate)
    {
    	long thisTime = this.getTime();
    	long anotherTime = anotherDate.getTime();
    	return (thisTime<anotherTime ? -1 : (thisTime==anotherTime ? 0 : 1));
    }

    /**
     * Compares this Date to another Object.  If the Object is a Date,
     * this function behaves like <code>compareTo(Date)</code>.  Otherwise,
     * it throws a <code>ClassCastException</code> (as Dates are comparable
     * only to other Dates).
     *
     * @param   o the <code>Object</code> to be compared.
     * @return  the value <code>0</code> if the argument is a Date
     *      equal to this Date; a value less than <code>0</code> if the
     *      argument is a Date after this Date; and a value greater than
     *      <code>0</code> if the argument is a Date before this Date.
     * @exception ClassCastException if the argument is not a
     *        <code>Date</code>.
     * @see     java.lang.Comparable
     */
    public int compareTo(Object o)
    {
    	return compareTo((Date)o);
    }

    /**
     * Returns a hash code value for this object. The result is the
     * exclusive OR of the two halves of the primitive <tt>long</tt>
     * value returned by the {@link Date#getTime}
     * method. That is, the hash code is the value of the expression:
     * <blockquote><pre>
     * (int)(this.getTime()^(this.getTime() >>> 32))</pre></blockquote>
     *
     * @return  a hash code value for this object.
     */
    public int hashCode()
    {
        long ht = getTime();
        return (int) ht ^ (int) (ht >> 32);
    }

    /**
     * Converts this <code>Date</code> object to a <code>String</code>
     * of the form:
     * <blockquote><pre>
     * dow mon dd hh:mm:ss zzz yyyy</pre></blockquote>
     * where:<ul>
     * <li><tt>dow</tt> is the day of the week (<tt>Sun, Mon, Tue, Wed,
     *     Thu, Fri, Sat</tt>).
     * <li><tt>mon</tt> is the month (<tt>Jan, Feb, Mar, Apr, May, Jun,
     *     Jul, Aug, Sep, Oct, Nov, Dec</tt>).
     * <li><tt>dd</tt> is the day of the month (<tt>01</tt> through
     *     <tt>31</tt>), as two decimal digits.
     * <li><tt>hh</tt> is the hour of the day (<tt>00</tt> through
     *     <tt>23</tt>), as two decimal digits.
     * <li><tt>mm</tt> is the minute within the hour (<tt>00</tt> through
     *     <tt>59</tt>), as two decimal digits.
     * <li><tt>ss</tt> is the second within the minute (<tt>00</tt> through
     *     <tt>61</tt>, as two decimal digits.
     * <li><tt>zzz</tt> is the time zone (and may reflect daylight savings
     *     time). Standard time zone abbreviations include those
     *     recognized by the method <tt>parse</tt>. If time zone
     *     informationi is not available, then <tt>zzz</tt> is empty -
     *     that is, it consists of no characters at all.
     * <li><tt>yyyy</tt> is the year, as four decimal digits.
     * </ul>
     *
     * @return  a string representation of this date.
     * @see     java.util.Date#toLocaleString()
     * @see     java.util.Date#toGMTString()
     */

    public String toString()
    {
		String df=WEEKS[getDay()%7]+" "+MONTHS[getMonth()]+" "+getDate()+" "+getHours()+":"+getMinutes()+":"+getSeconds()+"   "+getYear();
		return df;
    }

    /**
     * Returns the offset, measured in minutes, for the local time zone
     * relative to UTC that is appropriate for the time represented by
     * this <tt>Date</tt> object.
     * <p>
     * For example, in Massachusetts, five time zones west of Greenwich:
     * <blockquote><pre>
     * new Date(96, 1, 14).getTimezoneOffset() returns 300</pre></blockquote>
     * because on February 14, 1996, standard time (Eastern Standard Time)
     * is in use, which is offset five hours from UTC; but:
     * <blockquote><pre>
     * new Date(96, 5, 1).getTimezoneOffset() returns 240</pre></blockquote>
     * because on May 1, 1996, daylight savings time (Eastern Daylight Time)
     * is in use, which is offset only four hours from UTC.<p>
     * This method produces teh same result as if it computed:
     * <blockquote><pre>
     * (this.getTime() - UTC(this.getYear(),
     *                       this.getMonth(),
     *                       this.getDate(),
     *                       this.getHours(),
     *                       this.getMinutes(),
     *                       this.getSeconds())) / (60 * 1000)
     * </pre></blockquote>
     *
     * @return  the time-zone offset, in minutes, for the current locale.
     * @see     java.util.Calendar
     * @see     java.util.TimeZone
     */
    public int getTimezoneOffset()
    {
        int offset=0;
        if (cal == null)
        {
            if (staticCal == null)
                makeStaticCalendars();
			offset = staticCal.get(Calendar.ZONE_OFFSET) + staticCal.get(Calendar.DST_OFFSET);
        }
        else
			offset = cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET);
        return -(offset / 1000 / 60);  // convert to minutes
    }
    private void makeStaticCalendars()
    {
		staticCal = new GregorianCalendar();
        defaultCenturyStart = staticCal.get(Calendar.YEAR) - 80;
    }
    /**
     * Return a field for this date by looking it up in a Calendar object.
     *
     * @return the field value
     * @see    java.util.Calendar
     * @param  field the field to return
     */
    private final int getField(int field)
    {
        if (cal == null)
        {
            if (staticCal == null)
                makeStaticCalendars();
			return staticCal.get(field);
        }
        else
			return cal.get(field);
    }
    /**
     * Set a field for this day.
     *
     * @param field the field to set
     * @param value the value to set it to
     * @see java.util.Calendar
     */
    private final void setField(int field, int value)
    {
        if (cal == null)
        {
            cal = new GregorianCalendar();
            cal.setTimeInMillis(fastTime);
        }
        cal.set(field, value);
    }
}
