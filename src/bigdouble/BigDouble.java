package bigdouble;


import java.lang.IllegalArgumentException;
import java.util.Vector;


/***************************************************************
 * A number is stored in array of long integer				   *
 * "1011121314151617181920.12345678910" is			 		   *
 * {[10], [11121], [31415], [16171],[81920], [12345], [67891]} *
 **************************************************************/


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

	private static int getDotPosition(String str)
	{
		int dotIndex = str.indexOf('.');
		if (dotIndex == -1) return 0;

		final int strLength = str.length();
		int zeroRight = strLength - 1;

		if (str.charAt(zeroRight) == '0')
			while (zeroRight >= 0 && str.charAt(zeroRight) == '0')
				zeroRight--;

		return zeroRight - dotIndex;
	}

	private int getNumberSize()
	{
		int sizeFirst = getCountDigits(number.get(0));
		return sizeFirst + (number.size() - 1) * countDigits;
	}

	private long getPow_10(long e)
	{
		return (long)Math.pow(10, e);
	}

	private int getDotPosition()
	{
		return getNumberSize() - pow_10;
	}

	public void powShift(int expectedPow)
	{
		if (expectedPow < pow_10)
			throw new IllegalArgumentException("Wrong shifting");

		if (expectedPow == pow_10)
			return;


		int numberSize = number.size();
		int digitsShift = expectedPow % countDigits;
		long powBeg = getPow_10(digitsShift);
		long powEnd = getPow_10(countDigits - digitsShift);

		Vector<Long> newNumber = new Vector<>();
		newNumber.add(0L);


		for (int i = 0; i < numberSize; i++)
		{
			long numb = number.get(i);

			if (getCountDigits(numb) + digitsShift > countDigits)
			{
				newNumber.set(i, newNumber.get(i) + numb / powBeg);
				newNumber.add((numb % powBeg) * powEnd);
			}
			else newNumber.set(i, newNumber.get(i) + numb * powBeg);
		}

		for (int i = 0; i < expectedPow / countDigits; i++)
			newNumber.add(0L);

		this.number = newNumber;
		this.pow_10 = expectedPow;
	}


	public BigDouble(String str)
	{
		if (!str.matches("^-?(\\d+.?\\d*)|(\\d*.?\\d+)$"))
			throw new IllegalArgumentException("Wrong format of string");

		boolean isNeg = str.charAt(0) == '-';
		pow_10 = getDotPosition(str);
		String inputString = str.replaceAll("[-.]", "");


		//---Zero cleaner--------------------------->
		int inputStringLength = inputString.length();
		int zeroLeft = 0;
		while (zeroLeft < inputStringLength - 1 && str.charAt(zeroLeft) == '0')
			zeroLeft++;

		int zeroRight = inputStringLength - 1;
		if (pow_10 != 0)
		{
			if (str.charAt(zeroRight) == '0')
				while (zeroRight > zeroLeft && str.charAt(zeroRight) == '0')
					zeroRight--;
		}

		if (zeroLeft != 0 || zeroRight != inputStringLength - 1)
		{
			inputString = inputString.substring(zeroLeft, zeroRight);
			if (inputString.isEmpty())
				inputString = "0";
			inputStringLength = inputString.length();
		}


		//---Filling--------------------------------->
		int leftThr = inputStringLength % countDigits;

		if (leftThr != 0)
			number.add(
					Long.parseLong(
							inputString.substring(
									0, leftThr)));

		while (leftThr < inputStringLength)
		{
			int nextThr = leftThr + countDigits;
			number.add(
					Long.parseLong(
							inputString.substring(
									leftThr, nextThr)));
			leftThr = nextThr;
		}

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
		number.add(value);
	}


	public BigDouble plus(BigDouble _Other)
	{
		BigDouble result = new BigDouble();

		//---Alignment----------------------------------->
		int maxPow = Math.max(this.pow_10, _Other.pow_10);
		if (this.pow_10 >= _Other.pow_10)
			_Other.powShift(this.pow_10);
		else this.powShift(_Other.pow_10);

		result.pow_10 = maxPow;

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


		final long isPossiblePositive = minList.get(0) > 0 ? 1 : -1;
		final int minListSize = minList.size();
		final int maxListSize = maxList.size();
		final int sizeDiff = maxListSize - minListSize;

		for (int i = 0; i < sizeDiff; i++)
			result.number.add(maxList.get(i));

		for (int i = 0; i < minListSize; i++)
			if (Math.abs((minList.get(i) / 2) + (maxList.get(i + sizeDiff) / 2)) > Long.MAX_VALUE / 2)
			{
				result.number.set(i, result.number.lastElement() + isPossiblePositive);
				result.number.add((-isPossiblePositive * Long.MAX_VALUE + minList.get(i)) + maxList.get(i + sizeDiff));
			}
			else result.number.add(minList.get(i) + maxList.get(i + sizeDiff));

		return result;
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

		long negShift = isNegative() ? Long.MIN_VALUE : 0;

		for (int i = 0; i < number.size() - 1; i++)
			str.append(String.format("%0" + countDigits + "d", number.get(i) + negShift));

		if (!number.isEmpty())
			str.append(number.get(number.size() - 1) + negShift);

		String strResult = str.toString();
		int strLength = strResult.length();

		if (pow_10 <= 0)
			return strResult;

		if (pow_10 > strLength)
			return String.format("0.%0" + (pow_10 - strLength + 1) + "d", 0) + strResult;

		if (pow_10 == strLength)
			return "0." + strResult;

		return strResult.substring(0, strLength - pow_10) + "." + strResult.substring(strLength - pow_10);
	}

	@Override
	public boolean equals(Object _Obj)
	{
		return _Obj != null &&
				(_Obj == this ||
						_Obj instanceof BigDouble &&
								this.toString().equals(_Obj.toString()));
	}

	//---Private data--------------------------------------->
	private int countDigits = getCountDigits(Long.MAX_VALUE);
	private Vector<Long> number = new Vector<>();
	private int pow_10 = 0;


	public static void main(String[] args)
	{
		BigDouble a = new BigDouble("5641615641654611564.12345");
		BigDouble b = new BigDouble("2");
		System.out.println(a.plus(b));
	}
}

