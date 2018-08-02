package cn.entityaug.data;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * @author dell
 *
 */
public class StringTypeValidation {
	
	//ƥ�亯��
	private static boolean isMatch(String regex, String orginal){
		if (orginal == null || orginal.trim().equals("")) {
            return false;
        }
		Pattern pattern = Pattern.compile(regex);
		Matcher isNum = pattern.matcher(orginal);
		return isNum.matches();
	}

	//�Ƿ�Ϊ������
	public static boolean isPositiveInteger(String orginal) {
		return isMatch("^\\+{0,1}[1-9]\\d*(\\,\\d{3})*", orginal);
	}
	//�Ƿ�Ϊ������
	public static boolean isNegativeInteger(String orginal) {
		return isMatch("^-[1-9]\\d*(\\,\\d{3})*", orginal);
	}
	//�Ƿ�Ϊ����
	public static boolean isWholeNumber(String orginal) {
		return isMatch("[+-]{0,1}0", orginal) || isPositiveInteger(orginal) || isNegativeInteger(orginal);
	}
	//�Ƿ�Ϊ��С��
	public static boolean isPositiveDecimal(String orginal){
		return isMatch("\\+{0,1}[0]\\.[1-9]*|\\+{0,1}[1-9]\\d*(\\,\\d{3})*\\.\\d*", orginal);
	}
	//�Ƿ�Ϊ��С��
	public static boolean isNegativeDecimal(String orginal){
		return isMatch("^-[0]\\.\\d*|^-[1-9]\\d*(\\,\\d{3})*\\.\\d*", orginal);
	}
	//�Ƿ�ΪС��
	public static boolean isDecimal(String orginal){
		return isMatch("[+-]{0,1}0{1}\\.0+", orginal) || isPositiveDecimal(orginal) || isNegativeDecimal(orginal);
		//return isMatch("[-+]{0,1}\\d+\\.\\d*|[-+]{0,1}\\d*\\.\\d+", orginal);
	}
	//�Ƿ�Ϊʵ��
	public static boolean isRealNumber(String orginal){
		return isWholeNumber(orginal) || isDecimal(orginal);
	}
	
	//������ʽ�жϣ���bug��7-9��ʶ��Ϊtrue
	public static boolean isNumeric(String orginal){
		return isMatch("[+-]{0,1}[1-9][0-9]*.?[0-9]*|[+-]{0,1}[0].?[0-9]*", orginal);
	}
	
	//�Ƿ�Ϊ����,ֻ���ж�yyyy-mm-dd/yyyy.mm.dd hh:MM:ss
	public static int isDate1(String s)
	{
		SimpleDateFormat format=new SimpleDateFormat("MMM dd, yyyy",Locale.ENGLISH);
		boolean dateflag=true;
		Date date=null;
		try
		{
			format.setLenient(false);
			date=format.parse(s);
		}
		catch(ParseException e)
		{
			dateflag=false;
			return -1;
		}
		Calendar c=Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.YEAR);
	}
	public static int isDate4(String s)
	{
		SimpleDateFormat format=new SimpleDateFormat("MMM yyyy",Locale.ENGLISH);
		boolean dateflag=true;
		Date date=null;
		try
		{
			format.setLenient(false);
			date=format.parse(s);
		}
		catch(ParseException e)
		{
			dateflag=false;
			return -1;
		}
		Calendar c=Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.YEAR);
	}
	public static int isDate2(String s)
	{
		boolean isInt = Pattern.matches("\\d+", s);
		 if (isInt) {
			 int t=Integer.parseInt(s);
			 if((s.length() == 4)&&1400<=t&&t<=2050)
				 return Integer.parseInt(s);
		  }
		  
		  return -1;
	}
	public static int isDate3(String s)
	{
		SimpleDateFormat format=new SimpleDateFormat("MM/dd/yyyy");
		boolean dateflag=true;
		Date date=null;
		try
		{
			format.setLenient(false);
			date=format.parse(s);
		}
		catch(ParseException e)
		{
			dateflag=false;
			return -1;
		}
		Calendar c=Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.YEAR);
	}
	public static boolean isDate(String orginal)
	{
		String s=orginal.trim();
		try
		{
			int t1=isDate1(s);
			int t2=isDate2(s);
			int t3=isDate3(s);
			int t4=isDate4(s);
			int date=t1;
			if(date==-1)
			{
				date=t2;
				if(date==-1)
				{
					date=t3;
					if(date==-1)
						date=t4;
				}	
				
			}
			if(date!=-1)
				return true;
			else 
				return false;
		}
		catch(NumberFormatException nfe)
		{
			return false;
		}
	}

	/**
	 * @test
	 */
	public static void main(String args[]){
		String ss="430,400";
		ss=ss.replace(",", "");
//		System.out.println(ss);
		String s2="Jan 01,1989";
//		System.out.println(ss+"��������"+isPositiveInteger(ss));
//		System.out.println("��������"+isNegativeInteger(ss));
//		System.out.println("������"+isWholeNumber(ss));
//		System.out.println("��С����"+isPositiveDecimal(ss));
//		System.out.println("��С����"+isNegativeDecimal(ss));
//		System.out.println("С����"+isDecimal(ss));
//		System.out.println("ʵ����"+isRealNumber(ss));
//		System.out.println("shuzi��"+isNumeric(ss));
		System.out.println("���ڣ�"+isDate(s2));
	}

}
