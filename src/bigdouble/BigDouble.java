package bigdouble;


import java.util.ArrayList;
import java.lang.IllegalArgumentException;


 /************************************************************
 *A number is stored in array of long integer                *
 *"1011121314151617181920.12345678910" is                    *
 *{[10112], [13141], [51617], [18192], [01234], [56789], [1]}*
 ************************************************************/


public class BigDouble
{
    private static int countDigits(long value)
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


    //---Public data----------->
    public BigDouble(String str)
    {
        if (!str.matches("^-?(\\d+.?\\d*)|(\\d*.?\\d+)$"))
            throw new IllegalArgumentException("Wrong format of string");

        pow_10 = getDotPosition(str);
        isPositive = str.charAt(0) != '-';
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


        //---Filling------------------------------>
        final int digitsThr = countDigits(MAX_THR);

        for (int i = 0; i < inputStringLength / digitsThr; i++)
        {
            int leftThr = i * digitsThr;
            int rightThr = Math.min((i + 1) * digitsThr, inputStringLength);
            number.add(Long.parseLong(inputString.substring(leftThr, rightThr)));
        }


        //---Last iteration----------------->>
        int i = inputStringLength / digitsThr;
        int leftThr = i * digitsThr;
        int rightThr = Math.min((i + 1) * digitsThr, inputStringLength);

        if (leftThr != rightThr)
            number.add(Long.parseLong(inputString.substring(leftThr, rightThr)));
    }


    public BigDouble()
    {
        isPositive = true;
        number.add(0L);
    }


    public BigDouble(int value)
    {
        isPositive = value >= 0;
        number.add((long)Math.abs(value));
    }


    public BigDouble(long value)
    {
        isPositive = value >= 0;
        value = Math.abs(value);

        if (value > MAX_THR)
        {
            number.add(value % MAX_THR);
            number.add(value / MAX_THR);
        }
        else
            number.add(value);
    }


    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder();

        if(!isPositive)
            str.append('-');

        for (int i = 0; i < number.size() - 1; i++)
            str.append(String.format("%0" + countDigits(MAX_THR) + "d", number.get(i)));

        if (!number.isEmpty())
            str.append(number.get(number.size() - 1));

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


    //---Private data-------------------------------------->
    //will be changed to Long.MAX_VALUE / 2
    private final static long MAX_THR = 1000000000000000000L;
    private ArrayList<Long> number = new ArrayList<>();
    private int pow_10 = 0;
    private boolean isPositive;
}

