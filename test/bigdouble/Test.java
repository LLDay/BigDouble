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
		assertTrue(new BigDouble("1324.").equals(1324));
		assertTrue(new BigDouble(".1374").equals(0.1374));
		assertTrue(new BigDouble(1234567891011121314L).equals(1234567891011121314L));
		assertTrue(new BigDouble("0.00000000000000000000000001349435").
				toString().equals("0.00000000000000000000000001349435"));

		double d1 = 0.0;
		double d2 = 123465.789126;
		double d3 = -894531846513423.0;
		double d4 = -0.0000000000564316845316006;

		assertTrue(new BigDouble(d1).equals(zero));
		assertTrue(new BigDouble(d2).equals(new BigDouble("123465.789126")));
		assertTrue(new BigDouble(d3).equals(new BigDouble(-894531846513423L)));
		assertTrue(new BigDouble(d4).toString().equals("-0.0000000000564316845316006"));


		assertTrue(isThrowingCase(""));
		assertTrue(isThrowingCase("."));
		assertTrue(isThrowingCase("12345.123456789.123"));
		assertTrue(isThrowingCase("123,456"));

		try
		{
			BigDouble err = null;
			new BigDouble(err);
			assertTrue(false);
		}
		catch(NullPointerException e)
		{
			assertTrue(true);
		}

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
		BigDouble b = new BigDouble("16321650516208466109702645626.0004565168654561065464541984956448040600");
		//invert values
		BigDouble c = new BigDouble("23168.612315614198706531204987463213216054648947531024616132168456413216777");
		BigDouble d = new BigDouble("78946513241651213216542154065046513006430.016531208546321605420615642103041065489");

		//from plus test
		assertTrue(b.minus(c).equals(new BigDouble("16321650516208466109702622457.388140902666749575341466735282428749411052468975383867831543586783223")));
		assertTrue(b.minus(d).equals(new BigDouble("-78946513241634891566025945598936810360804.016074691680865498874161443607396261429")));
	}

	@org.junit.jupiter.api.Test
	public void multiply()
	{
		BigDouble a = new BigDouble("101998877665544332211.112233445566778899");
		BigDouble b = new BigDouble("909112233445566778899.998877665544332211");
		BigDouble c = new BigDouble("-561908403154187.51302498032168045341321");
		BigDouble d = new BigDouble("99999999.99999999");
		BigDouble minus = new BigDouble(-1);
		BigDouble zero = new BigDouble();

		BigDouble negativeA = new BigDouble(a);
		negativeA.toNegative();

		//calculated by Calculator.net
		assertTrue(a.times(b).toString().equals("92728427483464146389789695102918447474114." +
				"321732098387541709777903741240815689"));
		assertTrue(b.times(c).toString().equals("-510837803383335370497109744599955802." +
				"89128622771707455487405029739654509590731"));
		assertTrue(d.times(d).toString().equals("9999999999999998.0000000000000001"));

		assertTrue(a.times(zero).equals(zero));
		assertTrue(a.times(minus).equals(negativeA));
	}

	@org.junit.jupiter.api.Test
	public void power()
	{
		BigDouble two = new BigDouble(2);
		assertTrue(two.toPower(10).equals(1024));

		//calculated by Calculator.net
		assertTrue(two.toPower(100).toString().equals("1267650600228229401496703205376"));
		assertTrue(two.toPower(1000).toString().equals(
				"10715086071862673209484250490600018105614048117055336074" +
				"437503883703510511249361224931983788156958581275946729175" +
				"531468251871452856923140435984577574698574803934567774824230985421" +
				"074605062371141877954182153046474983581941267398767559165" +
				"543946077062914571196477686542167660429831652624386837205668069376"));
		assertTrue(new BigDouble(-3.21).toPower(21).toString().equals(
				"-43311744900.090301286724494444108707461503586489350721"));

		BigDouble nTwo = new BigDouble(-2);
		assertTrue(nTwo.toPower(3).plus(nTwo.toPower(2)).equals(-4));



		BigDouble someNum = new BigDouble("01651560606031.0653165419000");
		assertTrue(someNum.toPower(0).equals(1));
		assertTrue(someNum.toPower(1).equals(someNum));
	}

	@org.junit.jupiter.api.Test
	public void floor()
	{
		BigDouble a = new BigDouble(123.456789);
		BigDouble b = new BigDouble(-43);
		BigDouble c = new BigDouble(-12.18);

		assertTrue(a.floor(0).equals(123));
		assertTrue(a.floor(1).equals(123.4));
		assertTrue(a.floor(2).equals(123.45));
		assertTrue(a.floor(3).equals(123.456));
		assertTrue(a.floor(4).equals(123.4567));
		assertTrue(a.floor(5).equals(123.45678));
		assertTrue(a.floor(6).equals(123.456789));
		assertTrue(a.floor(7).equals(123.456789));

		assertTrue(b.floor(0).equals(-43));
		assertTrue(b.floor(1).equals(-43));

		assertTrue(c.floor(0).equals(-12));
		assertTrue(c.floor(1).equals(-12.1));
		assertTrue(c.floor(2).equals(-12.18));
		assertTrue(c.floor(3).equals(-12.18));
	}

	@org.junit.jupiter.api.Test
	public void round()
	{
		BigDouble a = new BigDouble(12.3456);
		BigDouble b = new BigDouble(-43);
		BigDouble c = new BigDouble(-12.538);

		assertTrue(a.round(0).equals(12));
		assertTrue(a.round(1).equals(12.3));
		assertTrue(a.round(2).equals(12.35));
		assertTrue(a.round(3).equals(12.346));
		assertTrue(a.round(4).equals(12.3456));
		assertTrue(a.round(5).equals(12.3456));

		assertTrue(b.round(0).equals(-43));
		assertTrue(b.round(1).equals(-43));

		assertTrue(c.round(0).equals(-13));
		assertTrue(c.round(1).equals(-12.5));
		assertTrue(c.round(2).equals(-12.54));
		assertTrue(c.round(3).equals(-12.538));
		assertTrue(c.round(4).equals(-12.538));
	}
}
