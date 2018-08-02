package cn.entityaug.similarity;
import java.util.ArrayList;
import java.util.List;


/**
 * 模糊匹配两个集合间的Jaccard相似度—---—交集/查询表的实体数
 * @author 
 *
 */
public class JaccardSim {
	public double sim;
	public static int getCommonNum(List<String> column1,List<String> column2)
	{
		int m = column1.size();
		int n = column2.size();
		int commonNum = 0; //相同元素个数（交集）

		for(int i=0;i<m;i++){
			boolean flag=false;
			for(int j=0;j<n;j++){
				//System.out.println("column1:"+column1.get(i)+" column2:"+column2.get(j));
				//若两个属性列属性名为空，则视为相同
				if(column1.get(i).equals("")&&column2.get(j).equals("") ||EditDistance.similarity(column1.get(i).toLowerCase(), column2.get(j).toLowerCase()) >=0.7){
					flag=true;
					//System.out.println(flag);
				}
			}
			if(flag==true){
				commonNum++;
			}

		}
		return commonNum;
	}
	public static double getJac(List<String> column1,List<String> column2){
		double d = 0;

		int m = column1.size();
		int n = column2.size();
		//  System.out.println(m+" "+n);
		//2、如果为字符串型，则用编辑距离计算模糊相似度
		if(m < 2 && n < 2){
			if(column1.get(0).equals("")&&column2.get(0).equals("") || EditDistance.similarity(column1.get(0), column2.get(0))>=0.7)
				d = 1;

		}
		else{
			int commonNum = 0; //相同元素个数（交集）

			for(int i=0;i<m;i++){
				boolean flag=false;
				for(int j=0;j<n;j++){
					//System.out.println("column1:"+column1.get(i)+" column2:"+column2.get(j));
					//若两个属性列属性名为空，则视为相同
					if(column1.get(i).equals("")&&column2.get(j).equals("") ||EditDistance.similarity(column1.get(i).toLowerCase(), column2.get(j).toLowerCase()) >=0.7){
						flag=true;
						//System.out.println(flag);
					}
				}
				if(flag==true){
					commonNum++;
				}
			}
			//int mergeNum =m+n-commonNum; //并集元素个数
			d = (double)commonNum/m; 
			//     System.out.println(commonNum+" "+commonNum+"  "+m);
		}

		return d;
	}

	public static void main(String[] args){

		String s1="jordan james adcee a b c ";
		String s2="jorden james advee a b";
		//  String s1="  Event Category Date Points";
		//  String s2="  Event Category Date";
		List<String> str1=new ArrayList<String>();
		List<String> str2=new ArrayList<String>();
		String[] split1=s1.toLowerCase().split("\\s+");
		String[] split2=s2.toLowerCase().split("\\s+");
		//  System.out.println(split1.length);
		for(int i=0;i<split1.length;i++)
		{
			str1.add(split1[i]);
		}
		for(int i=0;i<split2.length;i++)
		{
			str2.add(split2[i]);
		}
		double ss=(double)3/5;
		System.out.println(str1);
		System.out.println(str2+" "+ss);
		System.out.println(JaccardSim.getCommonNum(str1, str2));
	}
}