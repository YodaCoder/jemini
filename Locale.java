/*
 * java.util.Locale: part of the Java Class Libraries project.
 * Copyright (C) 1998 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 */

package java.util;

/**
 * Locales represent a specific country and culture.
 * <br><br>
 * Classes which can be passed a Locale object tailor their information 
 * for a given locale.  For instance, currency number formatting is 
 * handled differently for the USA and France.
 * <br><br>
 * Locales are made up of a language code, a country code, and an optional
 * set of variant strings.
 * <br><br>
 * Language codes are represented by
 * <a href="http://www.indigo.ie/egt/standards/iso639/iso639-1-en.html">ISO 639:1988</a>
 * w/ additions from ISO 639/RA Newsletter No. 1/1989
 * and a decision of the Advisory Committee of ISO/TC39 on
 * August 8, 1997.
 * <br><br>
 * Country codes are represented by 
 * <a href="ftp://ftp.ripe.net/iso3166-countrycodes">ISO 3166</a>.
 * <br><br>
 * Variant strings are vendor and browser specific.  Standard variant
 * strings include "POSIX" for POSIX, "WIN" for MS-Windows, and "MAC" for
 * Macintosh.  When there is more than one variant string, they must
 * be separated by an underscore (U+005F).
 * <br><br>
 * The default locale is determined by the values of the system properties
 * user.language, user.region, and user.variant.
 * <BR>
 * Dummy class, for future support.
 * @see ResourceBundle
 * @see java.text.Format
 * @see java.text.NumberFormat
 * @see java.text.Collator
 */
public class Locale implements java.io.Serializable, Cloneable 
{

    /**
     * Does the same as <code>Object.clone()</code> but does not throw
     * an <code>CloneNotSupportedException</code>.  Why anyone would
     * use this method is a secret to me, since this class is
     * immutable.  
     */
    public Object clone() 
	{
		/*
        try 
		{	return super.clone();
        } 
		catch (CloneNotSupportedException ex) 
		{	return null;
        }
		*/
		return null;
    }

    /**
     * Return the hash code for this locale.  The hashcode is the logical
     * xor of the hash codes of the language, the country and the variant.
     * The hash code is precomputed, since <code>Locale</code>s are often
     * used in hash tables.
     */
    public int hashCode() 
	{
        return 0;
    }

    /**
     * Compares two locales.
     * @param obj the other locale.
     * @return true, if obj is a Locale with the same language, country, and
     * variant code as this locale, otherwise false.
     */
    public boolean equals(Object obj) 
	{
		return false;
    }
    
}
