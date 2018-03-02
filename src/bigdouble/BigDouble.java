package bigdouble;


import java.lang.IllegalArgumentException;
import java.util.ListIterator;
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


	private int getNumberSize()
	{
		int sizeFirst = getCountDigits(number.get(0));
		return sizeFirst + (number.size() - 1) * countDigits;
	}

	private long getPow_10(long e)
	{
		return (long)Math.pow(10, e);
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
			long powRight = getPow_10(digitsShift);
			long powLeft = getPow_10(countDigits - digitsShift);

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
		pow_10 -= digitsShift + rightThrIndex * countDigits;
	}

	private void powShift(int expectedPow)
	{
		if (expectedPow < pow_10)
			throw new IllegalArgumentException("Wrong shifting");

		if (expectedPow == pow_10)
			return;


		int numberSize = number.size();
		int digitsShift = (expectedPow - pow_10) % countDigits;
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
		pow_10 = str.length() - str.indexOf('.') - 1;
		String inputString = str.replaceAll("[-.]", "");


		//---Filling-------------------------------->
		int inputStringLength = inputString.length();
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

		str.append(Math.abs(number.get(0)));

		for (int i = 1; i < number.size(); i++)
			str.append(String.format("%0" + countDigits + "d", Math.abs(number.get(i))));

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
		BigDouble a = new BigDouble("0000000051561651516510054006001325.400001000000000000000");
		BigDouble b = new BigDouble("2.78");
		System.out.println(a.minus(b));
	}
}

