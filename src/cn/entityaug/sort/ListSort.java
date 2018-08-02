package cn.entityaug.sort;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import cn.entityaug.data.CandidateRows;
/**
 * @param  l1���������List����
 * @author 73671
 *
 */
public class ListSort {
	public static LinkedList<String> sortBySim(LinkedList<String> l1)
	{
		LinkedList<String> sortedList=l1;
		if(l1!=null&&l1.size()>1)
		{
			 Collections.sort(sortedList, new Comparator<String>()
			 {
		 		public int compare(String c1, String c2) 
		 		{
		 			String[] arrs1=c1.split("  ");
		 			String[] arrs2=c2.split("  ");
		 			double d1=Double.parseDouble(arrs1[1]);
		 			double d2=Double.parseDouble(arrs2[1]);
		 			double temp=d2 - d1;
					if(temp<0)
						return -1;
					else if(temp>0)
						return 1;
					else 
						return 0;
		 		}
			 });
		}
		return sortedList;
	}

}
