package cn.entityaug.json;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;



//trie�ֵ�
public class CDict {
	CNode root = new CNode();
	public static final Integer DICT_STOP_WORD = 1;
	private class CNode {
		Integer mask = 0;
		Map<Character, CNode> map_char = new HashMap<Character, CNode>();
	}
	
	/**
	 * ��Ӵ�������ֵ�
	 * @param str_word��Ҫ��ӵ�·����
	 * @param type ��·�������ͣ�
	 */
	public void addWord(String str_word, Integer type) {
		CNode cur_node = root;
		for(int i=0; i<str_word.length(); i++) {
			CNode next_node;
			if(!cur_node.map_char.containsKey(str_word.charAt(i))) {
				next_node = new CNode();
				cur_node.map_char.put(str_word.charAt(i), next_node);
			}else {
				next_node = cur_node.map_char.get(str_word.charAt(i));
			}
			cur_node = next_node;
		}	
		cur_node.mask=type;
	}
	
	/**
	 * ����֪ʶ��
	 * @param str_file��֪ʶ���ļ���·����
	 * @param type��֪ʶ�������
	 */
	public void loadDict(String str_file, Integer type) {
		//System.out.println("loading "+str_file);
		try {
			String sline;
			int lcnt = 0;
			BufferedReader br = new BufferedReader(new FileReader(str_file));
			while((sline = br.readLine()) != null)
			{
				addWord(sline, type);
				lcnt++;
			}
			br.close();
			
			//System.out.println(lcnt + " stopwords"); //�ʵ��е�ͣ�ô���Ŀ
		}catch (IOException e) {
			System.out.println("File Read ERROR : " + str_file);
		}
	}
	/**
	 * ����֪ʶ��2
	 * @param contextpath=Table.get
	 * @param stopwordpath="D:\\Workspace\\Test\\stopword.txt"
	 * @return
	 */
	
	public void loadDictSet(String context,String stopwordpath) {
		Set<String> stops = new HashSet<String>();
		try {
			Scanner sc = new Scanner(new File(stopwordpath)); //��������ͣ�ô��ļ�
			while (sc.hasNextLine()) {
				stops.add(sc.nextLine());
			}
			sc.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * ��ѯ�����Ƿ���ͣ�ôʵ���
	 * @param str_text
	 * @return
	 */
	public int searchStopWord(String str_text){
		Integer offset=0;
		StringBuffer str_word = new StringBuffer();
		CNode cur_node = root;
		
		while(offset<str_text.length()) {
			CNode next_node = cur_node.map_char.get(str_text.charAt(offset));
			if(next_node==null)
				return 0;
			str_word.append(str_text.charAt(offset));		
			offset++;
			cur_node = next_node;
		}
		
		//�ж�ƥ�䵽������
		if(1==cur_node.mask){  
			return 1;
		}else{
			return 0;
		}	
	}
	
	/**
	 * ȥ��ͣ�ôʺ���
	 * @param context�������������ı��ַ�����������Ҫȥͣ�ôʵ��ַ���
	 * @return String��ȥͣ�ôʺ���ַ���������tf-idf��ȡ�ؼ���
	 */
	public String removeStopWord(String context){
		
		loadDict("stopword.txt", DICT_STOP_WORD);
		
		String str=context.replaceAll("[\\pP\\p{Punct}]", ""); //ȥ��������
		//str=str.toLowerCase(); //��дתΪСд
		String[] splitContext=str.split("\\s+"); //���ո�ָ��ַ���
		StringBuffer finalContext=new StringBuffer(); 
		
		for(int i=0;i<splitContext.length;i++){
			//System.out.println(splitContext[i]+"  "+i);
			if(searchStopWord(splitContext[i]) == 0){
				finalContext=finalContext.append(splitContext[i]).append(" ");
			}
		}	
		return finalContext.toString();
	}
	
	public static void main(String[] args) {
		CDict cd=new CDict();
		String line="I am A the   students. And you are a good person! Thank you very much China!";
		System.out.println("final:"+cd.removeStopWord(line));
	}

}