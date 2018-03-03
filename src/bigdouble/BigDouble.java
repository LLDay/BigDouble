package bigdouble;


import java.lang.IllegalArgumentException;
import java.util.ListIterator;
import java.util.Vector;

import static java.lang.Math.*;

/**
A number is stored in array of long integer
"1011121314151617181920.12345678910" is
{[10], [11121], [31415], [16171],[81920], [12345], [67891]}
*/


public class BigDouble
{
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

	private static long pow10(long e)
	{
		if (e < 0)
			throw new IllegalArgumentException("Unacceptable power");

		long result = 1;
		for (int i = 0; i < e; i++)
			result *= 10;

		return result;
	}


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
			number = new Vector<>();
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


		//---Shifting-------------------------->
		Vector<Long> newNumber = new Vector<>();

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

		//---Filling----------------------------------------------------------------->
		number = new Vector<>(newNumber.subList(leftThrIndex, rightNonZeroIndex + 1));
		shift -= digitsShift + rightThrIndex * countDigits;
	}

	private void changeShift(int expectedPow)
	{
		if (expectedPow < shift)
			throw new IllegalArgumentException("Wrong shifting");

		if (expectedPow == shift)
			return;


		final int numberSize = number.size();
		final int digitsShift = (expectedPow - shift) % countDigits;
		final long powRight = pow10(digitsShift);
		final long powLeft = pow10(countDigits - digitsShift);

		Vector<Long> newNumber = new Vector<>();
		if (getCountDigits(number.get(0)) + digitsShift >= countDigits)
			newNumber.add((number.get(0) / powRight) * powRight);
		else
			newNumber.add(0L);

		for (int i = 0; i < numberSize; i++)
		{
			long numb = number.get(i);

			newNumber.set(newNumber.size() - 1, newNumber.lastElement() + numb / powLeft);
			newNumber.add((numb % powLeft) * powRight);
		}

		for (int i = 0; i < (expectedPow - shift) / countDigits; i++)
			newNumber.add(0L);

		this.number = newNumber;
		this.shift = expectedPow;
	}


	public BigDouble(String str)
	{
		if (!str.matches("^-?(\\d+.?\\d*)|(\\d*.?\\d+)$"))
			throw new IllegalArgumentException("Wrong format of string");

		final boolean isNeg = str.charAt(0) == '-';
		final int dotIndex = str.indexOf('.');
		shift = dotIndex == -1 ? 0 : (str.length() - 1) - dotIndex;
		String inputString = str.replaceAll("[-.]", "");


		//---Filling-------------------------------->
		final int inputStringLength = inputString.length();
		int leftThr = 0;
		int nextThr = inputStringLength % countDigits;
		if (nextThr == 0)
			nextThr = countDigits;

		while (leftThr < inputStringLength)
		{
			number.add(
					Long.parseLong(
							inputString.substring(
									leftThr, nextThr)));
			leftThr = nextThr;
			nextThr += countDigits;
		}

		zeroCleaner();

		//---Is negative--->>
		if (isNeg)
			toNegative();
	}

	public BigDouble()
	{
		number.add(0L);
	}

	public BigDouble(long value)
	{
		long thr = pow10(countDigits);

		//if (abs(value) > thr) is incorrect
		//when value = Long.MIN_VALUE
		if (value >= thr || value <= -thr)
			number.add(value / thr);

		number.add(value % thr);
	}


	public BigDouble plus(BigDouble _Other)
	{
		BigDouble result = new BigDouble();

		//---Alignment---------------------------->
		int maxPow = max(this.shift, _Other.shift);
		if (this.shift >= _Other.shift)
			_Other.changeShift(this.shift);
		else this.changeShift(_Other.shift);

		result.shift = maxPow;

		Vector<Long> maxList;
		Vector<Long> minList;

		if (this.number.size() > _Other.number.size())
		{
			maxList = this.number;
			minList = _Other.number;
		}
		else
		{
			maxList = _Other.number;
			minList = this.number;
		}

		final int minListSize = minList.size();
		final int maxListSize = maxList.size();
		final int sizeDiff = maxListSize - minListSize;
		final long thr = pow10(countDigits);

		if (sizeDiff != 0)
			result.number = new Vector<>(maxList.subList(0, sizeDiff));

		for (int i = 0; i < minListSize; i++)
		{
			long buff = maxList.get(i + sizeDiff) + minList.get(i);

			if (abs(buff) > thr)
			{
				result.number.set(result.number.size() - 1, result.number.lastElement() + buff / abs(buff));
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
		_Other.zeroCleaner();

		return result;
	}

	public BigDouble minus(BigDouble _Other)
	{
		_Other.invert();
		BigDouble tmp = this.plus(_Other);
		_Other.invert();
		return tmp;
	}

	public void invert()
	{
		int numberSize = number.size();
		for (int i = 0; i < numberSize; i++)
			number.set(i, -number.get(i));
	}

	public void toNegative()
	{
		if (!this.isNegative())
			invert();
	}

	public void toPositive()
	{
		if (!this.isPositive())
			invert();
	}


	public boolean isPositive()
	{
		return number.get(0) >= 0;
	}

	public boolean isNegative()
	{
		return number.get(0) < 0;
	}


	@Override
	public String toString()
	{
		StringBuilder str = new StringBuilder();

		if (this.isNegative())
			str.append('-');

		str.append(abs(number.get(0)));
		for (int i = 1; i < number.size(); i++)
			str.append(String.format("%0" + countDigits + "d", abs(number.get(i))));


		String strResult = str.toString();
		final int strLength = strResult.length();

		if (shift == 0)
			return strResult;

		if (shift < 0)
			return strResult + String.format("%0" + (-shift) + "d", 0);

		if (shift > strLength)
			return String.format("0.%0" + (shift - strLength + 1) + "d", 0) + strResult;

		if (shift == strLength)
			return "0." + strResult;

		return strResult.substring(0, strLength - shift) + "." + strResult.substring(strLength - shift);
	}

	@Override
	public boolean equals(Object _Obj)
	{
		if (_Obj == null)
			return false;

		if (_Obj == this)
			return true;

		if (_Obj instanceof BigDouble && toString().equals(_Obj.toString()))
			return true;

		return false;
	}

	//---Private data-------------------------------------------------------->
	private static final int countDigits = getCountDigits(Long.MAX_VALUE) - 1;
	private Vector<Long> number = new Vector<>();
	private int shift = 0;
}

