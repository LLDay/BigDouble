package bigdouble;

import java.lang.IllegalArgumentException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static java.lang.Math.*;


/**
 * The class {@code BigDouble} stores a large floating number
 * and contains a methods for working with them
 * A number stored in array of integer number
 *
 * @author LLDay
 */
public class BigDouble {
	/**
	 * Returns count a number of digits
	 * 0 has zero digits
	 *
	 * @param value some {@code long} number
	 * @return count digits of parameter
	 */
	private static int getCountDigits(long value) {
		int digits = 0;

		while (value != 0) {
			digits++;
			value /= 10;
		}

		return digits;
	}

	/**
	 * Returns a number equal 10 to the power e
	 * e >= 0
	 *
	 * @param e is power
	 * @return round number
	 */
	private static long pow10(long e) {
		if (e < 0)
			throw new IllegalArgumentException("Unacceptable power");

		long result = 1;
		for (int i = 0; i < e; i++)
			result *= 10;

		return result;
	}

	/**
	 * Erases zeroes from the array
	 * on the right and the left
	 */
	private void zeroCleaner() {
		//---Right------------------------------------------------>
		ListIterator<Long> it = number.listIterator(number.size());

		int rightThrIndex = 0;
		while (it.hasPrevious() && it.previous() == 0)
			rightThrIndex++;

		int rightNonZeroIndex = number.size() - rightThrIndex;
		if (rightNonZeroIndex == 0) {
			number = new ArrayList<>();
			number.add(0L);
			shift = 0;
			return;
		}

		//---Left----------------->
		it = number.listIterator();

		int leftThrIndex = 0;
		while (it.hasNext() && it.next() == 0)
			leftThrIndex++;

		//---Filling----------------------------------------------------------------->
		number = new ArrayList<>(number.subList(leftThrIndex, rightNonZeroIndex));
		shift -= rightThrIndex * digitThr;
	}

	/**
	 * Sets shift value multiples of 18
	 */
	private void normalization() {
		if (this.shift % digitThr == 0)
			return;

		final int digitsShift = (shift >= 0) ?
				digitThr - shift % digitThr :
				-shift % digitThr;

		final long powRight = pow10(digitsShift);
		final long powLeft = pow10(digitThr - digitsShift);

		ArrayList<Long> newNumber = new ArrayList<>();
		newNumber.add(0L);

		for (Long el : number) {
			int lastIndex = newNumber.size() - 1;
			newNumber.set(lastIndex, newNumber.get(lastIndex) + el / powLeft);
			newNumber.add((el % powLeft) * powRight);
		}

		this.number = newNumber;
		this.shift += digitsShift;
	}

	/**
	 * Gets a substring for rounding
	 *
	 * @param precision is count of digits after dot
	 * @return rounding substring
	 */
	private String roundingNumber(int precision) {
		final String str = toString();
		final int dotIndex = toString().indexOf('.');

		if (dotIndex == -1)
			return str + ".0";

		if (str.length() > dotIndex + precision)
			return str.substring(0, dotIndex + precision + 1);

		return str.substring(0, str.length()) + '0';
	}

	/**
	 * Copy constructor
	 *
	 * @param other must be not null
	 */
	public BigDouble(BigDouble other) {
		if (other == null)
			throw new NullPointerException("Copying uninitialized object");

		this.shift = other.shift;
		this.number = new ArrayList<>(other.number);
	}

	/**
	 * The number creates by a string
	 *
	 * @param str is some string
	 */
	public BigDouble(String str) {
		if (!str.matches("^-?(\\d+.?\\d*)|(\\d*.?\\d+)$"))
			throw new IllegalArgumentException("Wrong format of string");

		final boolean isNeg = str.charAt(0) == '-';
		final int dotIndex = str.indexOf('.');

		shift = (dotIndex == -1) ? 0 : (str.length() - 1) - dotIndex;
		String inputString = str.replaceAll("[-.]", "");

		final int inputStringLength = inputString.length();
		int leftThr = 0;
		int nextThr = inputStringLength % digitThr;

		if (nextThr == 0)
			nextThr = digitThr;

		while (leftThr < inputStringLength) {
			number.add(Long.parseLong(inputString.substring(leftThr, nextThr)));
			leftThr = nextThr;
			nextThr += digitThr;
		}

		normalization();
		zeroCleaner();

		if (isNeg) toNegative();
	}

	/**
	 * Constructor by default
	 * The number equals zero
	 */
	public BigDouble() {
		number.add(0L);
	}

	/**
	 * The number creates by a long number
	 *
	 * @param value is some {@code long} number
	 */
	public BigDouble(long value) {
		long thr = pow10(digitThr);

		//if (abs(value) > thr) is incorrect
		//when value = Long.MIN_VALUE
		if (value >= thr || value <= -thr)
			number.add(value / thr);

		number.add(value % thr);
		zeroCleaner();
	}

	/**
	 * The number creates by a double number
	 * Warning: double format has low precision
	 * Better creating from string
	 *
	 * @param value is some {@code double} number
	 */
	public BigDouble(double value) {
		String strDouble = String.valueOf(value);
		String arrStr[] = strDouble.split("E", 2);

		BigDouble tmp = new BigDouble(arrStr[0]);

		if (arrStr.length == 2)
			tmp.shift -= Integer.parseInt(arrStr[1]);

		this.number = tmp.number;
		this.shift = tmp.shift;

		normalization();
		zeroCleaner();
	}

	/**
	 * Calculates sum of two numbers
	 * The numbers must be alignment by a number of digits after dot
	 * Uses method changeShift()
	 *
	 * @param other is some Number
	 * @return a sum of those numbers
	 */
	public BigDouble plus(BigDouble other) {
		if (other == null)
			throw new NullPointerException("Adding uninitialized object");

		final int thisIntegerPart = (this.number.size() * digitThr - this.shift) / digitThr;
		final int otherIntegerPart = (other.number.size() * digitThr - other.shift) / digitThr;

		final boolean thisIntegerIsMore = thisIntegerPart >= otherIntegerPart;
		final BigDouble maxIntBig = thisIntegerIsMore ? this : other;
		final BigDouble minIntBig = thisIntegerIsMore ? other : this;

		final int maxListSize = maxIntBig.number.size();
		final int minListSize = minIntBig.number.size();

		final int maxIntegerPart = (maxListSize * digitThr - maxIntBig.shift) / digitThr;
		final int minIntegerPart = (minListSize * digitThr - minIntBig.shift) / digitThr;

		final int diffPart = maxIntegerPart - minIntegerPart;

		BigDouble result = new BigDouble();
		result.number.addAll(maxIntBig.number);
		result.shift = max(this.shift, other.shift);

		final int unionIndex = min(minListSize, maxListSize - diffPart);
		for (int i = 0; i < unionIndex; i++)
			result.number.set(i + 1 + diffPart, result.number.get(i + 1 + diffPart) + minIntBig.number.get(i));

		for (int i = maxListSize - diffPart; i < 0; i++)
			result.number.add(0L);

		final int onlyMinListStartIndex = max(result.number.size() - 1 - diffPart, 0);
		for (int i = onlyMinListStartIndex; i < minListSize; i++)
			result.number.add(minIntBig.number.get(i));

		final int isPosCoeff = result.isPositive() ? 1 : -1;
		final int resultNumSize = result.number.size();
		final long thr = pow10(digitThr);

		for (int i = 1; i < resultNumSize; i++) {
			long currElem = result.number.get(i);
			//if current element and all number have different sign
			if (currElem * isPosCoeff < 0) {
				result.number.set(i, isPosCoeff * thr + currElem);
				result.number.set(i - 1, result.number.get(i - 1) - isPosCoeff);
			}

			currElem = result.number.get(i);
			//handling of overflows
			if (abs(currElem) > thr) {
				result.number.set(i - 1, result.number.get(i - 1) + currElem / thr);
				result.number.set(i, currElem % thr);
			}
		}

		result.zeroCleaner();
		return result;
	}

	/**
	 * Calculates difference of two numbers
	 * Difference is sum with a invert sign number
	 * Uses method invert()
	 *
	 * @param other is some number
	 * @return a difference of the numbers
	 */
	public BigDouble minus(BigDouble other) {
		if (other == null)
			throw new NullPointerException("Subtracting uninitialized object");

		if (other == this)
			return new BigDouble();

		other.invert();
		BigDouble tmp = this.plus(other);
		other.invert();

		return tmp;
	}

	/**
	 * Splits array of numbers with 18 digits to array with 9 digits
	 * Used in multiplication
	 *
	 * @param array is some List
	 * @return split array in half
	 */
	private ArrayList<Long> halfSplitting(List<Long> array) {
		ArrayList<Long> result = new ArrayList<>();
		long thr = pow10(digitThr / 2);

		for (Long el : array) {
			result.add(el / thr);
			result.add(el % thr);
		}

		return result;
	}

	/**
	 * Calculates a multiplications of two numbers
	 * Multiplications is repeating of a sum
	 * Uses method plus()
	 *
	 * @param other is some number
	 * @return a multiplications of the numbers
	 */
	public BigDouble times(BigDouble other) {
		if (other == null)
			throw new NullPointerException("Multiplication uninitialized object");

		ArrayList<Long> firstNum = halfSplitting(this.number);
		ArrayList<Long> secondNum = halfSplitting(other.number);

		final int firstNumSize = firstNum.size();
		final int secondNumSize = secondNum.size();
		final int sumLastIndex = (firstNumSize - 1) + (secondNumSize - 1);
		final int halfDigitThr = digitThr / 2;

		BigDouble result = new BigDouble();

		for (int i = 0; i < firstNumSize; i++) {
			long firstNumElem = firstNum.get(i);
			if (firstNumElem == 0)
				continue;

			for (int j = 0; j < secondNumSize; j++) {
				long secondNumElem = secondNum.get(j);
				if (secondNumElem == 0)
					continue;

				BigDouble tmp = new BigDouble(firstNumElem * secondNumElem);
				tmp.shift = -halfDigitThr * (sumLastIndex - (i + j));
				tmp.normalization();
				result = result.plus(tmp);
			}
		}

		result.shift += this.shift + other.shift;
		result.zeroCleaner();

		return result;
	}

	/**
	 * Powering is repeating of a multiply
	 *
	 * @param pow is some non-negative number
	 * @return this number to the power pow
	 */
	public BigDouble toPower(long pow) {
		if (pow < 0)
			throw new IllegalArgumentException("Wrong powering");

		BigDouble res = new BigDouble(1);
		BigDouble base = new BigDouble(this);

		while (pow > 0) {
			if (pow % 2 == 1)
				res = res.times(base);

			base = base.times(base);
			pow /= 2;
		}

		return res;
	}

	/**
	 * Inverts a sign of the number
	 */
	public void invert() {
		int numberSize = number.size();
		for (int i = 0; i < numberSize; i++)
			number.set(i, -number.get(i));
	}

	/**
	 * Converts the number to negative
	 * Negative number remains negative
	 */
	public void toNegative() {
		if (!this.isNegative())
			invert();
	}

	/**
	 * Converts the number to positive
	 * Positive number remains positive
	 */
	public void toPositive() {
		if (!this.isPositive())
			invert();
	}

	/**
	 * Checks the number to positive
	 *
	 * @return is positive number
	 */
	public boolean isPositive() {
		for (long el : number)
			if (el != 0)
				return el >= 0;

		//if zero
		return true;
	}

	/**
	 * Checks the number to negative
	 *
	 * @return is negative number
	 */
	public boolean isNegative() {
		return !isPositive();
	}

	/**
	 * Rounding down
	 *
	 * @param precision is count digits after dot
	 * @return a rounding number
	 */
	public BigDouble floor(int precision) {
		if (precision < 0)
			throw new IllegalArgumentException("Wrong rounding");

		return new BigDouble(roundingNumber(precision));
	}

	/**
	 * Rounding on math rules
	 *
	 * @param precision is count digits after dot
	 * @return a rounding number
	 */
	public BigDouble round(int precision) {
		if (precision < 0)
			throw new IllegalArgumentException("Wrong rounding");

		final String str = roundingNumber(precision + 1);
		final int lastIndex = str.length() - 1;

		if (str.charAt(lastIndex) >= '5') {
			BigDouble plusVal = new BigDouble(0.1).toPower(precision);

			if (this.isNegative())
				plusVal.toNegative();

			return new BigDouble(str.substring(0, lastIndex)).plus(plusVal);
		}
		return new BigDouble(str.substring(0, lastIndex));
	}

	/**
	 * @return string representation of a number
	 */

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();

		str.append(abs(number.get(0)));
		for (int i = 1; i < number.size(); i++)
			str.append(String.format("%0" + digitThr + "d", abs(number.get(i))));

		String strResult = str.toString();
		final String negativeStr = isNegative() ? "-" : "";

		if (shift == 0)
			return negativeStr + strResult;

		if (shift < 0)
			return negativeStr + strResult + String.format("%0" + (-shift) + "d", 0);

		int rightZero = strResult.length() - 1;
		while (rightZero > 0 && strResult.charAt(rightZero) == '0')
			rightZero--;

		strResult = strResult.substring(0, rightZero + 1);
		final int strLength = str.length();

		if (shift > strLength)
			return negativeStr + String.format("0.%0" + (shift - strLength) + "d", 0) + strResult;

		if (shift == strLength)
			return "0." + strResult;

		return negativeStr + strResult.substring(0, strLength - shift) + "." + strResult.substring(strLength - shift);
	}

	/**
	 * @param obj is some number
	 * @return are two numbers equal
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;

		if (obj == this)
			return true;

		if (obj instanceof BigDouble)
		{
			BigDouble other = (BigDouble) obj;
			if (other.shift == this.shift && this.number.equals(other.number))
				return true;
		}

		return false;
	}

	/**
	 * @return a hash code
	 */
	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	//---Private data----------------------------------------------------->
	private static final int digitThr = getCountDigits(Long.MAX_VALUE) - 1;
	private ArrayList<Long> number = new ArrayList<>();
	private int shift = 0;
}