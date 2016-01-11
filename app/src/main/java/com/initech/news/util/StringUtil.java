package com.initech.news.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

	// private static final Pattern emojiRegexp =
	// Pattern.compile("[🀄🅰🅱🅾🅿🆎🆒🆔🆕🆗🆙🆚🇧🇨🇩🇪🇪🇫🇬🇮🇯🇰🇳🇵🇷🇷🇷🇸🇸🇹🇺🇺�?🈂🈚🈯🈳🈵🈶🈷🈸🈹🈺�?🌀🌂🌃🌄🌅🌆🌇🌈🌊🌙🌟🌴🌵🌷🌸🌹🌺🌻🌾�?��??�?��?��?��?��?��?��?��?��?��?��?��?��?��?��??�?��?��?��?��?��?��?��?��?��?��?��?��?��?��?��?��?�🎀�?🎂🎃🎄🎅🎆🎇🎈🎉🎌�?🎎�?�?🎑🎒🎓🎡🎢🎤🎥🎦🎧🎨🎩🎫🎬🎯🎰🎱🎵🎶🎷🎸🎺🎾🎿�?��??�?��?��?��?��?��?��?��?��?��?��?��?��?��?��?��?��?��?��?��??�?��?��?��?��?��?��?��?��?��?��?��?��?��?��?��?��?��?��?��?��?��?��?��?��?��?��?��?��?��?��?�👀👂👃👄👆👇👈👉👊👋👌�?👎�?�?👑👒👔👕👗👘👙👜👟👠👡👢👣👦👧👨👩👫👮👯👱👲👳👴👵👶👷👸👻👼👽👾👿💀�?💂💃💄💅💆💇💈💉💊💋�?💎�?�?💑💒💓💔💗💘💙💚💛💜�?💟💡💢💣💤💦💨💩💪💰💱💱💹💹💺💻💼💽💿📀📖�?📠📡📢📣📩📫📮📱📲📳📴📶📷📺📻📼🔊�?🔑🔒🔓🔔�?🔞🔥🔨🔫🔯🔰🔱🔲🔳🔴�?🕑🕒🕓🕔🕕🕖🕗🕘🕙🕚🕛🗻🗼🗽�?😂😃😄😉😊😌�?�?😒😓😔😖😘😚😜�?😞😠😡😢😣😥😨😪😭😰😱😲😳😷🙅🙆🙇🙌�?🚀🚃🚄🚅🚇🚉🚌�?🚑🚒🚓🚕🚗🚙🚚🚢🚤🚥🚧🚬🚭🚲🚶🚹🚺🚻🚼🚽🚾🛀☺✨�?��?�✊✌✋�?☀☔�?⛄⚡☕〽♦♣♠⛳⚾⚽➿☎✂⛪⛺⛵⛲♨⚠⛽⃣⃣⃣⃣⃣⃣⃣⬅⬇⬆⃣⃣⃣⃣➡↗↖↘↙◀▶�?��?�♿✳㊗㊙✴♈♉♊♋♌�?♎⛎♓♒♑�?�?�?�⭕]");
	private static final Pattern hashRegexp = Pattern.compile("(#\\w+)", Pattern.CASE_INSENSITIVE);
	private static final Pattern mentionRegexp = Pattern.compile("(^|[^a-zA-Z0-9_]+)(@([a-zA-Z0-9_]+))", Pattern.CASE_INSENSITIVE);
	private static final Pattern whiteSpacePattern = Pattern.compile("\\s+");

	/*
	 * public static final SimpleDateFormat YEAR_FORMAT = new
	 * SimpleDateFormat("yyyy"); public static final SimpleDateFormat
	 * DATE_FORMAT = new SimpleDateFormat("MM-dd-yyyy"); public static final
	 * SimpleDateFormat HOUR_FORMAT = new SimpleDateFormat("hh:mm:ss");
	 */
	public static final SimpleDateFormat DATE_AND_HOUR_FORMAT_2 = new SimpleDateFormat("EEE, d MMM hh:mm:ss a");
	public static final SimpleDateFormat HOUR_FORMAT = new SimpleDateFormat("hh:mm aa");	

	public static String getCleanText(final CharSequence var0) {
		String var1 = var0.toString().trim();
		return whiteSpacePattern.matcher(var1).replaceAll(" ");
	}

	public static Matcher hashMatcher(final String str) {
		return hashRegexp.matcher(str);
	}

	public static boolean isNullOrEmpty(String var0) {
		boolean var1;
		if (var0 != null && var0.length() != 0) {
			var1 = false;
		} else {
			var1 = true;
		}

		return var1;
	}

	public static Matcher mentionMatcher(final String str) {
		return mentionRegexp.matcher(str);
	}

	/*
	 * public static String stripEmoji(String var0) { if(VERSION.SDK_INT < 16) {
	 * var0 = emojiRegexp.matcher(var0).replaceAll(""); }
	 * 
	 * return var0; }
	 */

	public static String stripNewLines(String var0) {
		return var0.replaceAll("\r\n", "");
	}

	public static boolean isDigits(final String str) {
		if (isEmpty(str)) {
			return false;
		}
		for (int i = 0; i < str.length(); i++) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean isEmpty(final CharSequence s) {
		return s == null || s.length() == 0;
	}	

	public static boolean isEmpty(final String s) {
		return s == null || s.length() == 0;
	}

	public static boolean isNotEmpty(final String s) {
		return !isEmpty(s);
	}

	public static String notNull(final String str) {
		if (str == null) {
			return "";
		} else {
			return str;
		}
	}

	public static String alternateCase(final String str) {

		if (isEmpty(str)) {
			return str;
		}

		final StringBuilder sb = new StringBuilder(str.length());
		for (int i = 0; i < str.length(); i++) {
			final Character c = str.charAt(i);

			if (i % 2 == 1) {
				sb.append(Character.toUpperCase(c));
			} else {
				sb.append(str.charAt(i));
			}
		}
		return sb.toString();
	}

	/**
	 * Strips string after the given character including it
	 * 
	 * @param str
	 * @param stripChar
	 * @return
	 */
	public static String stripString(final String str, final char stripChar) {
		final int i = str.indexOf(stripChar);
		return i != -1 ? str.substring(0, i) : str;
	}

	/**
	 * gets the last part of the url, so if http://www.aaa.com/blah/some.jpg it
	 * should return some.jpg
	 * 
	 * @param url
	 * @return
	 */
	public static String getFileFromUrl(final String url) {
		return url.substring(url.lastIndexOf('/') + 1);
	}

	/**
	 * only returns the alphabetical characters in this string
	 * 
	 * @param s
	 * @return
	 */
	public static String onlyAlpha(final String s) {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			if (Character.isLetter(s.charAt(i))) {
				sb.append(s.charAt(i));
			}
		}
		return sb.toString();
	}

	public static String stripOutNumbers(final String str) {
		if (isEmpty(str)) {
			return str;
		}
		final StringBuilder sb = new StringBuilder(str.length());
		for (int i = 0; i < str.length(); i++) {
			if (Character.isDigit(str.charAt(i))) {
				continue;
			} else {
				sb.append(str.charAt(i));
			}
		}
		return sb.toString();
	}

	public static String removeHTML(final String htmlString) {
		// Remove HTML tag from java String
		String noHTMLString = htmlString.replaceAll("\\<.*?\\>", "");

		// Remove Carriage return from java String
		noHTMLString = noHTMLString.replaceAll("\r", "<br/>");

		// Remove New line from java string and replace html break
		noHTMLString = noHTMLString.replaceAll("\n", " ");
		noHTMLString = noHTMLString.replaceAll("\'", "&#39;");
		noHTMLString = noHTMLString.replaceAll("\"", "&quot;");
		return noHTMLString;
	}

	/**
	 * This is useful when sending the String representation of JSON to logcat, as logcat has a character limit.
	 * @param longString The String to break into smaller Strings that can be logged to logcat.
	 * @return An ArrayList of Strings that are each small enough to be logged to logcat.
	 */
	public static ArrayList<String> splitLongString(final String longString) {
		ArrayList<String> result = new ArrayList<>();
		final int increment = 1000;
		for (int chunk = 0; chunk < longString.length(); chunk = chunk + increment) {
			result.add(longString.substring(chunk, Math.min(chunk + increment, longString.length())));
		}
		return result;
	}

	/**
	 * Strips surrounding single quotes from a string greater than
	 * 2 length.  Also, converts any sequences of 2 quotes to 1.  This method
	 * is the converse of android.database.DatabaseUtils.sqlEscapeString().
	 * <p/>
	 * Example: ''the fox''s tail was red'' -> 'the fox's tail was red'
	 * 'the fox''s tail was red' -> the fox's tail was red
	 * stacy''s hair is green -> stacy's hair is green
	 * stacy's hair is green -> stacy's hair is green
	 * my car is blue -> my car is blue
	 *
	 * @param s
	 * @return
	 */
	public static String unescapeQuotes(String s) {
		if (s == null)
			return s;
		s = s.replace("''", "'");
		if (s.startsWith("'") && s.endsWith("'") && s.length() > 2) {
			return s.substring(1, s.length() - 1);
		}
		return s;
	}
}
