package bigdouble;

import static org.junit.jupiter.api.Assertions.*;


public class Test
{
	private boolean isThrowingCase(String str)
	{
		try
		{
			new BigDouble(str);
		}
		catch(Exception e)
		{
			return true;
		}

		return false;
	}


	@org.junit.jupiter.api.Test
	public void constructor()
	{
		BigDouble zero = new BigDouble(0);
		assertTrue(new BigDouble().equals(zero));
		assertTrue(new BigDouble("-0").equals(zero));
		assertTrue(new BigDouble("00000000000.0000000000000").equals(zero));
		assertTrue(new BigDouble("1324.").toString().equals("1324"));
		assertTrue(new BigDouble(".1374").toString().equals("0.1374"));
		assertTrue(new BigDouble(1234567891011121314L).toString().equals("1234567891011121314"));

		assertTrue(isThrowingCase(""));
		assertTrue(isThrowingCase("."));
		assertTrue(isThrowingCase("12345.123456789.123"));
		assertTrue(isThrowingCase("123,456"));

		assertFalse(isThrowingCase("99999999999999999999999999.99999999999999999999999999"));
	}

	@org.junit.jupiter.api.Test
	public void zeroCleaner()
	{
		BigDouble bd = new BigDouble("000123456789.987654321000");
		assertTrue("123456789.987654321".equals(bd.toString()));
		assertTrue(bd.equals(new BigDouble("00000000000000000000000123456789.987654321")));
		assertTrue(bd.equals(new BigDouble("123456789.98765432100000000000000000000000")));

		assertTrue(new BigDouble("100000000000000000000000000000").toString().equals("100000000000000000000000000000"));
	}

	@org.junit.jupiter.api.Test
	public void plus()
	{
		BigDouble max = new BigDouble(Long.MAX_VALUE);
		BigDouble min = new BigDouble(Long.MIN_VALUE);
		BigDouble zero = new BigDouble(0);
		BigDouble one = new BigDouble(1);
		BigDouble nine = new BigDouble("999999999999999999");
		BigDouble cir = new BigDouble("100000000000000000000000");
		BigDouble a = new BigDouble("112233445566778899.998877665544332211");
		BigDouble b = new BigDouble("16321650516208466109702645626.0004565168654561065464541984956448040600");
		BigDouble c = new BigDouble("-23168.612315614198706531204987463213216054648947531024616132168456413216777");
		BigDouble d = new BigDouble("-78946513241651213216542154065046513006430.016531208546321605420615642103041065489");

		assertTrue(max.plus(min).equals(new BigDouble(Long.MAX_VALUE + Long.MIN_VALUE)));
		assertTrue(max.plus(one).toString().equals("9223372036854775808"));
		assertTrue(nine.plus(one).toString().equals("1000000000000000000"));
		assertTrue(a.plus(cir).toString().equals("100000112233445566778899.998877665544332211"));
		assertTrue(zero.plus(zero).equals(zero));
		assertTrue(zero.plus(b).equals(b));

		//calculated by Calculator.net
		assertTrue(b.plus(c).equals(new BigDouble("16321650516208466109702622457.388140902666749575341466735282428749411052468975383867831543586783223")));
		assertTrue(b.plus(d).equals(new BigDouble("-78946513241634891566025945598936810360804.016074691680865498874161443607396261429")));
		assertTrue(c.plus(d).equals(new BigDouble("-78946513241651213216542154065046513029598." +
				"628846822745028136625603105316257120137947531024616132168456413216777")));

		assertTrue(b.plus(c).equals(c.plus(b)));
	}

	@org.junit.jupiter.api.Test
	public void minus()
	{

	}

}
