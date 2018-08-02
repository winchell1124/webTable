package cn.entityaug.data;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * This class transfers an integer number into a string :
 * <p />
 * 
 * <pre>
 * {@code
 * // an example :
 *   NumberText ns = NumberText.getInstance(NumberText.Lang.English);
 *   ns.getText(123) // one hundred and twenty-three
 *   ns.getOrdinalText(320) // three hundred and twentieth
 * }
 * </pre>
 * 
 * @date 09/02/2012
 * @author na.shi.wu.you (raistlic@gmail.com)
 */
public abstract class NumberText {

	/*----------------------------------------------------------------------------
	* not designed to be inherented outside this file
	* no public constructors provided, -- use factory method
	---------------------------------------------------------------------------*/
	private NumberText() {
	}

	/**
	 * Transfers an integer number into a String, specifically in which language
	 * depends on the implementation.
	 * <p />
	 * e.g. in English,
	 * <p />
	 * 100 -> one hundred <br />
	 * -976083854 -> minus nine hundred and seventy-six million and eighty-three
	 * thousand eight hundred and fifty-four
	 * 
	 * @param number
	 *            the integer number to be transfered
	 * @return the result String
	 */
	public abstract String getText(long number);

	/**
	 * Transfers an integer number into a String of its ordinal representation,
	 * specifically in which language depends on the implementation.
	 * <p />
	 * e.g. in English,
	 * <p />
	 * 100 -> one hundredth <br />
	 * 8331125 -> eight million three hundred and thirty-one thousand one
	 * hundred and twenty-fifth
	 * 
	 * @param number
	 *            the integer number to be transfered
	 * @return the result String
	 */
	public abstract String getOrdinalText(long number);

	/**
	 * This enumeration type is typically named under a natural language name,
	 * and is to mark a specific implementation name; it is used as an argument
	 * to call the factory method
	 * {@link NumberText#getInstance(NumberText.Lang)}.
	 */
	public static enum Lang {
		English, EnglishWithCleanSpaceOnly, ChineseSimplified, ChineseTraditional,;
	}

	/**
	 * Exports a {@code NumberText} implementation instance, based on a natural
	 * language argument. {@see Lang}
	 * 
	 * @param lang
	 * @return a NumberText instance.
	 */
	public static NumberText getInstance(Lang lang) {

		if (lang == null)
			throw new NullPointerException();

		NumberText result = null;
		synchronized (_InstancePool) {
			result = _InstancePool.get(lang);
			if (result == null) {
				switch (lang) {
				case English:
					result = new NumberTextEnglish();
					break;
				case EnglishWithCleanSpaceOnly:
					result = new NumberTextEnglishCleanSpaceOnly();
					break;
				case ChineseSimplified:
					result = new NumberTextChinese(NumberTextChinese.Type.Simplified);
					break;
				case ChineseTraditional:
					result = new NumberTextChinese(NumberTextChinese.Type.Traditional);
					break;
				default:
					throw new UnsupportedOperationException("Language not supported yet : " + lang);
				}
				_InstancePool.put(lang, result);
			}
		}
		return result;
	}

	private static final Map<Lang, NumberText> _InstancePool = new EnumMap<Lang, NumberText>(Lang.class);

	private static boolean checkNumber(String number) {

		return number != null && number.matches("[0-9]+");
	}

	/*----------------------------------------------------------------------------
	* English Implementation
	---------------------------------------------------------------------------*/
	private static class NumberTextEnglish extends NumberText {

		static enum Connect {

			Minus("minus"), Hundred("hundred"), And("and"), AfterMinus(" "), AfterNumber(" "), AfterPower(
					" "), AfterHundred(" "), AfterAnd(" "), AfterTen("-"),;

			final String display;

			Connect(String display) {
				this.display = display;
			}

			private static boolean isConnect(char c) {
				return c == ' ' || c == '-';
			}
		}

		static enum Power {

			Thousand("thousand"), // 10 ^ 3
			Million("million"), // 10 ^ 6
			Billion("billion"), // 10 ^ 9
			Trillion("trillion"), // 10 ^ 12
			Quadrillion("quadrillion"), // 10 ^ 15
			Quintillion("quintillion"), // 10 ^ 18 (enough for Long.MAX_VALUE)
			Sextillion("sextillion"), // 10 ^ 21
			Septillion("septillion"), // 10 ^ 24
			Octillion("octillion"), // 10 ^ 27
			Nonillion("nonillion"), // 10 ^ 30
			Decillion("decillion"), // 10 ^ 33
			Undecillion("undecillion"), // 10 ^ 36
			Duodecillion("duodecillion"), // 10 ^ 39
			Tredecillion("tredecillion"), // 10 ^ 42
			Quattuordecillion("quattuordecillion"), // 10 ^ 45
			Quindecillion("quindecillion"), // 10 ^ 48
			Sexdecillion("sexdecillion"), // 10 ^ 51
			Septendecillion("septendecillion"), // 10 ^ 54
			Octodecillion("octodecillion"), // 10 ^ 57
			Novemdecillion("novemdecillion"), // 10 ^ 60
			Vigintillion("vigintillion"), // 10 ^ 63
			;

			final String display;

			Power(String display) {
				this.display = display;
			}
		}

		static enum Digit {

			Zero("zero", "zeroth", "ten", ""), One("one", "first", "eleven", "ten"), Two("two", "second", "twelve",
					"twenty"), Three("three", "third", "thirteen", "thirty"), Four("four", "fourth", "fourteen",
							"fourty"), Five("five", "fifth", "fifteen", "fifty"), Six("six", "sixth", "sixteen",
									"sixty"), Seven("seven", "seventh", "seventeen", "seventy"), Eight("eight",
											"eighth", "eighteen",
											"eighty"), Nine("nine", "nineth", "nineteen", "ninety"),;

			final String display, displayOrdinal, plusTen, multiTen;

			Digit(String display, String displayOrdinal, String plusTen, String multiTen) {
				this.display = display;
				this.displayOrdinal = displayOrdinal;
				this.plusTen = plusTen;
				this.multiTen = multiTen;
			}
		}

		private static final Map<String, String> _Ordinals;

		static {
			_Ordinals = new HashMap<String, String>();
			for (Digit d : Digit.values())
				_Ordinals.put(d.display, d.displayOrdinal);
		}

		@Override
		public String getText(long number) {
			StringBuilder builder = new StringBuilder();
			buildText(builder, number);
			return builder.toString();
		}

		@Override
		public String getOrdinalText(long number) {
			StringBuilder builder = new StringBuilder();
			buildText(builder, number);
			replaceLastTokenWithOrdinal(builder);
			return builder.toString();
		}

		private void buildText(StringBuilder builder, long number) {

			assert builder != null;

			if (number < 0) {
				builder.append(getConnectDisplay(Connect.Minus)).append(getConnectDisplay(Connect.AfterMinus));
				number = -number;
			}

			String numString = Long.toString(number);

			int power = 0;
			while (numString.length() > (power + 1) * 3)
				power++;

			while (power > 0) {
				boolean modified = extendToken(builder, numString, power * 3);
				if (modified)
					builder.append(getConnectDisplay(Connect.AfterNumber))
							.append(getPowerDisplay(Power.values()[power - 1]));
				power--;
			}
			extendToken(builder, Long.toString(number), 0);
		}

		private boolean extendToken(StringBuilder builder, String number, int suffix) {

			assert builder != null && checkNumber(number) && suffix < number.length();

			int len = number.length() - suffix;
			int hundreds = len > 2 ? (int) (number.charAt(len - 3) - '0') : -1;
			int tens = len > 1 ? (int) (number.charAt(len - 2) - '0') : -1;
			int inds = (int) (number.charAt(len - 1) - '0');

			if (hundreds <= 0 && tens <= 0 && inds <= 0 && suffix > 0)
				return false;
			else if (len > 3)
				builder.append(getConnectDisplay(Connect.AfterPower));

			if (hundreds == 0) {
				if (len > 3 && (tens > 0 || inds > 0))
					builder.append(getConnectDisplay(Connect.And)).append(getConnectDisplay(Connect.AfterAnd));
			} else if (hundreds > 0) {
				builder.append(getDigitName(Digit.values()[hundreds])).append(getConnectDisplay(Connect.AfterNumber))
						.append(getConnectDisplay(Connect.Hundred));
				if (tens > 0 || inds > 0)
					builder.append(getConnectDisplay(Connect.AfterHundred)).append(getConnectDisplay(Connect.And))
							.append(getConnectDisplay(Connect.AfterAnd));
			}

			if (tens > 1) {
				builder.append(getDigitMultiTen(Digit.values()[tens]));
				if (inds > 0)
					builder.append(getConnectDisplay(Connect.AfterTen));
			}

			if (tens == 1)
				builder.append(getDigitPlusTen(Digit.values()[inds]));
			else if (inds > 0 || number.length() == 1)
				builder.append(getDigitName(Digit.values()[inds]));

			return true;
		}

		private void replaceLastTokenWithOrdinal(StringBuilder builder) {

			assert builder != null && builder.length() > 0;

			int suffix = builder.length() - 1;
			while (suffix >= 0 && !isConnect(builder.charAt(suffix)))
				suffix--;
			String lastToken = builder.substring(suffix + 1);
			builder.delete(suffix + 1, builder.length()).append(toOrdinal(lastToken));
		}

		String getPowerDisplay(Power power) {

			assert power != null;

			return power.display;
		}

		String getConnectDisplay(Connect connect) {

			assert connect != null;

			return connect.display;
		}

		String getDigitName(Digit digit) {

			assert digit != null;

			return digit.display;
		}

		String toOrdinal(String name) {

			assert name != null && !name.isEmpty();

			String result = _Ordinals.get(name);
			if (result == null) {
				if (name.charAt(name.length() - 1) == 'y')
					result = name.substring(0, name.length() - 1) + "ieth";
				else
					result = name + "th";
			}
			return result;
		}

		String getDigitPlusTen(Digit digit) {

			assert digit != null;

			return digit.plusTen;
		}

		String getDigitMultiTen(Digit digit) {

			assert digit != null;

			return digit.multiTen;
		}

		boolean isConnect(char c) {
			return Connect.isConnect(c);
		}
	}

	/*----------------------------------------------------------------------------
	* English with only Clean Space Connectors
	---------------------------------------------------------------------------*/
	private static class NumberTextEnglishCleanSpaceOnly extends NumberTextEnglish {

		@Override
		String getConnectDisplay(Connect connect) {

			return connect == Connect.AfterTen ? " " : super.getConnectDisplay(connect);
		}
	}

	/*----------------------------------------------------------------------------
	* Chinese Implementation
	---------------------------------------------------------------------------*/
	private static class NumberTextChinese extends NumberText {

		static enum Type {
			Simplified, Traditional;
		}

		static enum Connect {
			Di("��", "��"), Fu("��", "ؓ"), Ling("��", "��"), Shi("ʮ", "ʰ"), Bai("��", "��"), Qian("ǧ", "Ǫ"),;

			final String display, displayTraditional;

			Connect(String display, String displayTraditional) {
				this.display = display;
				this.displayTraditional = displayTraditional;
			}
		}

		static enum Power {

			Wan("��", "�f"), // 10^4
			Yi("��", "�|"), // 10^8
			Zhao("��", "��"), // 10^12
			Jing("��", "��"), // 10^16 (enough for Long.MAX_VALUE)
			Gai("��", "��"), // 10^20
			Zi("��", "��"), // 10^24
			Rang("�", "�"), // 10^28
			Gou("��", "��"), // 10^32
			Jian("��", "��"), // 10^36
			Zheng("��", "��"), // 10^40
			Zai("��", "�d"), // 10^44
			;

			final String display, displayTraditional;

			Power(String display, String displayTraditional) {
				this.display = display;
				this.displayTraditional = displayTraditional;
			}
		}

		static enum Digit {

			Ling("��", "��"), // just to occupy this position
			Yi("һ", "Ҽ"), Er("��", "��"), San("��", "��"), Si("��", "��"), Wu("��", "��"), Liu("��", "½"), Qi("��", "��"), Ba("��",
					"��"), Jiu("��", "��"),;

			final String display, displayTraditional;

			Digit(String display, String displayTraditional) {
				this.display = display;
				this.displayTraditional = displayTraditional;
			}
		}

		private final Type type;

		private NumberTextChinese(Type type) {
			assert type != null;

			this.type = type;
		}

		@Override
		public String getText(long number) {

			StringBuilder builder = new StringBuilder();
			buildText(builder, number);
			return builder.toString();
		}

		@Override
		public String getOrdinalText(long number) {

			StringBuilder builder = new StringBuilder().append("Di");
			buildText(builder, number);
			return builder.toString();
		}

		private void buildText(StringBuilder builder, long number) {

			assert builder != null;

			if (number < 0) {
				builder.append(getConnectDisplay(Connect.Fu));
				number = -number;
			}
			String numString = Long.toString(number);

			int power = 0;
			while (numString.length() > (power + 1) * 4)
				power++;

			while (power > 0) {
				if (extendToken(builder, numString, power * 4))
					builder.append(getPowerDisplay(Power.values()[power - 1]));
				power--;
			}
			extendToken(builder, numString, 0);
		}

		private boolean extendToken(StringBuilder builder, String number, int suffix) {

			assert builder != null && checkNumber(number) && number.length() > suffix;

			int len = number.length() - suffix;
			int qian = len > 3 ? (int) (number.charAt(len - 4) - '0') : -1;
			int bai = len > 2 ? (int) (number.charAt(len - 3) - '0') : -1;
			int shi = len > 1 ? (int) (number.charAt(len - 2) - '0') : -1;
			int ind = (int) (number.charAt(len - 1) - '0');

			boolean nonZero = false; // true if any of the digits is not zero
			if (qian == 0) {
				if (bai > 0 || shi > 0 || ind > 0)
					builder.append(getConnectDisplay(Connect.Ling));
			} else if (qian > 0) {
				builder.append(getDigitDisplay(Digit.values()[qian])).append(getConnectDisplay(Connect.Qian));
				nonZero = true;
			}

			if (bai == 0) {
				if (qian > 0 && (shi > 0 || ind > 0))
					builder.append(getConnectDisplay(Connect.Ling));
			} else if (bai > 0) {
				builder.append(getDigitDisplay(Digit.values()[bai])).append(getConnectDisplay(Connect.Bai));
				nonZero = true;
			}

			if (shi == 0) {
				if (bai > 0 && ind > 0)
					builder.append(getConnectDisplay(Connect.Ling));
			} else if (shi > 0) {
				if (number.length() > 2 || shi != 1)
					builder.append(getDigitDisplay(Digit.values()[shi]));
				builder.append(getConnectDisplay(Connect.Shi));
				nonZero = true;
			}

			if (ind == 0) {
				boolean addZero = len == 1;
				for (int i = 1; addZero && i <= suffix; i++) {
					if (number.charAt(i) != '0')
						addZero = false;
				}
				if (addZero)
					builder.append(getConnectDisplay(Connect.Ling));
			} else {
				builder.append(getDigitDisplay(Digit.values()[ind]));
				nonZero = true;
			}
			return nonZero;
		}

		String getConnectDisplay(Connect connect) {

			assert connect != null;

			return type == Type.Simplified ? connect.display : connect.displayTraditional;
		}

		String getPowerDisplay(Power power) {

			assert power != null;

			return type == Type.Simplified ? power.display : power.displayTraditional;
		}

		String getDigitDisplay(Digit digit) {

			assert digit != null;

			return type == Type.Simplified ? digit.display : digit.displayTraditional;
		}
	}
}
