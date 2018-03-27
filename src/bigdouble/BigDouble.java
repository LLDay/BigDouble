package bigdouble;

import java.lang.IllegalArgumentException;
import java.util.ArrayList;
import java.util.ListIterator;

import static java.lang.Math.*;


/**
 * The class {@code BigDouble} stores a large floating number
 * and contains a methods for working with them
 * A number stored in array of integer number
 * @author LLDay
 */
public class BigDouble
{
	/**
	 * Returns count a number of digits
	 * 0 has zero digits
	 * @param value some {@code long} number
	 * @return count digits of parameter
	 */
	private static int getCountDigits(long value)
	{
		int digits = 0;
		while (value != 0)
		{
			digits++;
			value /= 10;
		}
		return digits;
	}

	/**
	 * Returns a number equal 10 to the power e
	 * e >= 0
	 * @param e is power
	 * @return round number
	 */
	public static long pow10(long e)
	{
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
	private void zeroCleaner()
	{
		//---Right------------------------------------------------>
		ListIterator<Long> it = number.listIterator(number.size());

		int rightThrIndex = 0;
		while (it.hasPrevious() && it.previous() == 0)
			rightThrIndex++;

		int digitsShift = 0;
		int rightNonZeroIndex = number.size() - rightThrIndex - 1;

		if (rightNonZeroIndex < 0)
		{
			number = new ArrayList<>();
			number.add(0L);
			shift = 0;
			return;
		}

		long rightNonZeroElem = number.get(rightNonZeroIndex);

		while (rightNonZeroElem % 10 == 0)
		{
			rightNonZeroElem /= 10;
			digitsShift++;
		}


		//---Shifting-------------------------------->
		ArrayList<Long> newNumber = new ArrayList<>();

		if (digitsShift != 0)
		{
			long powRight = pow10(digitsShift);
			long powLeft = pow10(countDigits - digitsShift);

			newNumber.add(number.get(0) / powRight);

			for (int i = 1; i <= rightNonZeroIndex; i++)
			{
				long prev = number.get(i - 1);
				long next = number.get(i);
				newNumber.add((prev % powRight) * powLeft + next / powRight);
			}
		}
		else
			newNumber = number;

		//---Left-------------------->
		it = newNumber.listIterator();

		int leftThrIndex = 0;
		while (it.hasNext() && it.next() == 0)
			leftThrIndex++;

		//---Filling-------------------------------------------------------------------->
		number = new ArrayList<>(newNumber.subList(leftThrIndex, rightNonZeroIndex + 1));
		shift -= digitsShift + rightThrIndex * countDigits;
	}

	/**
	 * Adds zeroes on the right
	 * and shifts the dot to the left
	 * Used for plus
	 * @param expectedShift must be more than current
	 */
	private void changeShift(int expectedShift)
	{
		if (expectedShift < shift)
			throw new IllegalArgumentException("Wrong shifting");

		if (expectedShift == shift)
			return;


		final int digitsShift = (expectedShift - shift) % countDigits;
		final long powRight = pow10(digitsShift);
		final long powLeft = pow10(countDigits - digitsShift);

		ArrayList<Long> newNumber = new ArrayList<>();
		newNumber.add(0L);

		for (Long el : number)
		{
			int lastIndex = newNumber.size() - 1;
			newNumber.set(lastIndex, newNumber.get(lastIndex) + el / powLeft);
			newNumber.add((el % powLeft) * powRight);
		}

		for (int i = 0; i < (expectedShift - shift) / countDigits; i++)
			newNumber.add(0L);

		this.number = newNumber;
		this.shift = expectedShift;
	}


	/**
	 * Gets a substring for rounding
	 * @param precision is count of digits after dot
	 * @return rounding substring
	 */
	private String roundingNumber(int precision)
	{
		final String str = toString();
		final int dotIndex = toString().indexOf('.');

		if (dotIndex == -1)
			return str + ".0";

		if (str.length() > dotIndex + precision)
			return str.substring(0, dotIndex + precision + 1);
		else return str.substring(0, str.length()) + '0';
	}

	/**
	 * Copy constructor
	 * @param other must be not null
	 */
	public BigDouble(BigDouble other)
	{
		if (other == null)
			throw new NullPointerException("Copying uninitialized object");
		this.shift = other.shift;
		this.number = new ArrayList<>(other.number);
	}

	/**
	 * The number creates by a string
	 * @param str is some string
	 */
	public BigDouble(String str)
	{
		if (!str.matches("^-?(\\d+.?\\d*)|(\\d*.?\\d+)$"))
			throw new IllegalArgumentException("Wrong format of string");

		final boolean isNeg = str.charAt(0) == '-';
		final int dotIndex = str.indexOf('.');
		shift = dotIndex == -1 ? 0 : (str.length() - 1) - dotIndex;
		String inputString = str.replaceAll("[-.]", "");


		//---Filling-------------------------------------->
		final int inputStringLength = inputString.length();
		int leftThr = 0;
		int nextThr = inputStringLength % countDigits;
		if (nextThr == 0)
			nextThr = countDigits;

		while (leftThr < inputStringLength)
		{
			number.add(Long.parseLong(inputString.substring(leftThr, nextThr)));
			leftThr = nextThr;
			nextThr += countDigits;
		}

		zeroCleaner();

		//---Is negative--->>
		if (isNeg)
			toNegative();
	}

	/**
	 * Constructor by default
	 * The number equals zero
	 */
	public BigDouble()
	{
		number.add(0L);
	}

	/**
	 * The number creates by a long number
	 * @param value is some {@code long} number
	 */
	public BigDouble(long value)
	{
		long thr = pow10(countDigits);

		//if (abs(value) > thr) is incorrect
		//when value = Long.MIN_VALUE
		if (value >= thr || value <= -thr)
			number.add(value / thr);

		number.add(value % thr);
	}

	/**
	 * The number creates by a double number
	 * Warning: double format has low precision
	 * Better creating from string
	 * @param value is some {@code double} number
	 */
	public BigDouble(double value)
	{
		String strDouble = String.valueOf(value);
		String arrStr[] = strDouble.split("E", 2);

		BigDouble tmp = new BigDouble(arrStr[0]);

		if (arrStr.length == 2)
			tmp.shift -= Integer.parseInt(arrStr[1]);

		this.number = tmp.number;
		this.shift = tmp.shift;
		zeroCleaner();
	}

	/**
	 * Calculates sum of two numbers
	 * The numbers must be alignment by a number of digits after dot
	 * Uses method changeShift()
	 * @param other is some Number
	 * @return a sum of those numbers
	 */
	public BigDouble plus(BigDouble other)
	{
		if (other == null)
			throw new NullPointerException("Adding uninitialized object");

		BigDouble result = new BigDouble();

		//---Alignment---------------------------->
		int maxPow = max(this.shift, other.shift);
		if (this.shift >= other.shift)
			other.changeShift(this.shift);
		else this.changeShift(other.shift);

		result.shift = maxPow;

		ArrayList<Long> maxList;
		ArrayList<Long> minList;

		if (this.number.size() > other.number.size())
		{
			maxList = this.number;
			minList = other.number;
		}
		else
		{
			maxList = other.number;
			minList = this.number;
		}

		final int minListSize = minList.size();
		final int maxListSize = maxList.size();
		final int sizeDiff = maxListSize - minListSize;
		final long thr = pow10(countDigits);

		if (sizeDiff != 0)
			result.number = new ArrayList<>(maxList.subList(0, sizeDiff));

		for (int i = 0; i < minListSize; i++)
		{
			long buff = maxList.get(i + sizeDiff) + minList.get(i);

			if (abs(buff) > thr)
			{
				int lastIndex = result.number.size() - 1;
				result.number.set(lastIndex, result.number.get(lastIndex) + buff / abs(buff));
				buff %= thr;
			}

			result.number.add(buff);
		}

		result.zeroCleaner();

		boolean isPos = result.isPositive();
		final int resultNumSize = result.number.size();

		if (isPos)
		{
			for (int i = 1; i < resultNumSize; i++)
				if (result.number.get(i) < 0)
				{
					result.number.set(i - 1, result.number.get(i - 1) - 1);
					result.number.set(i, thr + result.number.get(i));
				}
		}
		else
			for (int i = 1; i < resultNumSize; i++)
				if (result.number.get(i) > 0)
				{
					result.number.set(i - 1, result.number.get(i - 1) + 1);
					result.number.set(i, result.number.get(i) - thr);
				}

		result.zeroCleaner();
		this.zeroCleaner();
		other.zeroCleaner();

		return result;
	}


	/**
	 * Calculates difference of two numbers
	 * Difference is sum with a invert sign number
	 * Uses method invert()
	 * @param other is some number
	 * @return a difference of the numbers
	 */
	public BigDouble minus(BigDouble other)
	{
		if (other == null)
			throw new NullPointerException("Subtracting uninitialized object");

		other.invert();
		BigDouble tmp = this.plus(other);
		other.invert();
		return tmp;
	}

	private ArrayList<Long> halfSplitting(ArrayList<Long> array)
	{
		ArrayList<Long> result = new ArrayList<>();
		long thr = pow10(countDigits / 2);

		for (Long el : array)
		{
			result.add(el / thr);
			result.add(el % thr);
		}
		return result;
	}

	/**
	 * Calculates a multiplications of two numbers
	 * Multiplications is repeating of a sum
	 * Uses method plus()
	 * @param other is some number
	 * @return a multiplications of the numbers
	 */
	public BigDouble times(BigDouble other)
	{
		if (other == null)
			throw new NullPointerException("Multiplication uninitialized object");

		int shortCountDigits = countDigits / 2;

		ArrayList<Long> firstNum = halfSplitting(this.number);
		ArrayList<Long> secondNum = halfSplitting(other.number);

		int zeroFirstNum = 0;
		for (Long el : firstNum)
			if (el == 0)
				zeroFirstNum++;

		int zeroSecondNum = 0;
		for (Long el : secondNum)
			if (el == 0)
				zeroSecondNum++;

		if (zeroSecondNum > zeroFirstNum)
		{
			ArrayList<Long> tmp = firstNum;
			firstNum = secondNum;
			secondNum = tmp;
		}

		BigDouble result = new BigDouble();

		final int firstNumSize = firstNum.size();
		final int secondNumSize = secondNum.size();
		final int sum = firstNumSize + secondNumSize - 2;

		for (int i = 0; i < firstNumSize; i++)
			for (int j = 0; j < secondNumSize; j++)
			{
				long firstElem = firstNum.get(i);
				long secondElem = secondNum.get(j);

				if (firstElem == 0)
				{
					if (++i == firstNumSize)
						break;

					j--;
					continue;
				}
				if (secondElem == 0)
					continue;

				BigDouble tmp = new BigDouble(firstElem * secondElem);
				tmp.shift = -shortCountDigits * (sum - (i + j));
				result = result.plus(tmp);
			}

		result.shift = this.shift + other.shift;
		result.zeroCleaner();
		return result;
	}

	/**
	 * Powering is repeating of a multiply
	 * @param pow is some non-negative number
	 * @return this number to the power pow
	 */
	public BigDouble toPower(long pow)
	{
		BigDouble res = new BigDouble(1);
		BigDouble base = new BigDouble(this);
		while (pow > 0)
		{
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
	public void invert()
	{
		int numberSize = number.size();
		for (int i = 0; i < numberSize; i++)
			number.set(i, -number.get(i));
	}

	/**
	 * Converts the number to negative
	 * Negative number remains negative
	 */
	public void toNegative()
	{
		if (!this.isNegative())
			invert();
	}

	/**
	 * Converts the number to positive
	 * Positive number remains positive
	 */
	public void toPositive()
	{
		if (!this.isPositive())
			invert();
	}

	/**
	 * Checks the number to positive
	 * @return is positive number
	 */
	public boolean isPositive()
	{
		return number.get(0) >= 0;
	}

	/**
	 * Checks the number to negative
	 * @return is negative number
	 */
	public boolean isNegative()
	{
		return number.get(0) < 0;
	}

	/**
	 * Rounding down
	 * @param precision is count digits after dot
	 * @return a rounding number
	 */
	public BigDouble floor(int precision)
	{
		if (precision < 0)
			throw new IllegalArgumentException("Wrong rounding");

		return new BigDouble(roundingNumber(precision));
	}

	/**
	 * Rounding on math rules
	 * @param precision is count digits after dot
	 * @return a rounding number
	 */
	public BigDouble round(int precision)
	{
		if (precision < 0)
			throw new IllegalArgumentException("Wrong rounding");

		final String str = roundingNumber(precision + 1);
		if (str.charAt(str.length() - 1) >= '5')
		{
			BigDouble plusVal = new BigDouble(0.1).toPower(precision);
			if (this.isNegative())
				plusVal.toNegative();

			return new BigDouble(str.substring(0, str.length() - 1)).plus(plusVal);
		}
		else return new BigDouble(str.substring(0, str.length() - 1));
	}

	/**
	 * @return string representation of a number
	 */
	@Override
	public String toString()
	{
		StringBuilder str = new StringBuilder();

		str.append(abs(number.get(0)));
		for (int i = 1; i < number.size(); i++)
			str.append(String.format("%0" + countDigits + "d", abs(number.get(i))));

		String strResult = str.toString();
		final int strLength = strResult.length();
		final String negativeStr = isNegative() ? "-" : "";

		if (shift == 0)
			return negativeStr + strResult;

		if (shift < 0)
			return negativeStr + strResult + String.format("%0" + (-shift) + "d", 0);

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
	public boolean equals(Object obj)
	{
		if (obj == null)
			return false;

		if (obj == this)
			return true;

		if (obj instanceof BigDouble && toString().equals(obj.toString()))
			return true;

		if (obj instanceof Long)
			return this.equals(new BigDouble((Long)obj));

		if (obj instanceof Integer)
			return this.equals(new BigDouble(((Integer)obj).longValue()));

		if (obj instanceof Double)
			return this.equals(new BigDouble((Double)obj));

		if (obj instanceof Float)
			return this.equals(new BigDouble(((Float)obj).doubleValue()));

		return false;
	}

	/**
	 * @return a hash code
	 */
	@Override
	public int hashCode()
	{
		return getCountDigits(number.get(0)) + countDigits * (number.size() - 1) - shift;
	}

	//---Private data-------------------------------------------------------->
	private static final int countDigits = getCountDigits(Long.MAX_VALUE) - 1;
	private ArrayList<Long> number = new ArrayList<>();
	private int shift = 0;
}
