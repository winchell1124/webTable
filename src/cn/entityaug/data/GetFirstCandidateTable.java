package cn.entityaug.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.entityaug.excel.ExcelManage;
import cn.entityaug.qureytable.QueryTable;
import cn.entityaug.qureytable.ReadQueryTable;
import cn.entityaug.similarity.EditDistance;
import cn.entityaug.sort.HashMapSort;
import cn.entityaug.table.TableBean;

public class GetFirstCandidateTable {

    public static CandidateTable getSeedTables(QueryTable queryTable, String source, String topic) throws IOException {
        File file = new File(source);
        List<CandidateTable> tables = new ArrayList<CandidateTable>();
        String[] path = file.list();
        Map<String, Double> score = new HashMap<String, Double>();
        for (int i = 0; i < path.length; i++) {
            ExcelManage em = new ExcelManage();
            TableBean webtable = em.readFromExcel(source + "/" + path[i], "sheet1");
            CandidateTable cand = getTableScore(queryTable, webtable);
            score.put(cand.getId(), cand.getScore());
            tables.add(cand);
        }
        score = HashMapSort.sortMapByDouValue(score);
        File file1 = new File("DataSet/Experiments/" + topic + "_Score.txt");
        //将所有网络表和查询表的匹配分数都写入txt
        if (!file1.exists()) {
            BufferedWriter bw = new BufferedWriter(new FileWriter("DataSet/Experiments/" + topic + "_Score.txt"));

            Set<String> keyset1 = score.keySet();
            Iterator<String> it1 = keyset1.iterator();
            while (it1.hasNext()) {
                String str = (String) it1.next();
                double dou = score.get(str);
                bw.write(str + " " + dou);
                bw.newLine();
                bw.flush();
            }
            bw.close();
        }

        //选取匹配分数最高的表作为种子表
        String seed = null;
        BufferedReader br = new BufferedReader(new FileReader("DataSet/Experiments/" + topic + "_Score.txt"));
        String s = null;
        while ((s = br.readLine()) != null) {
            String[] arrs = s.split(" ");
            seed = arrs[0];
            break;
        }
        br.close();
        int flag = -1;
        for (int i = 0; i < tables.size(); i++) {
            if (tables.get(i).getId().equals(seed)) {
                flag = i;
                break;
            }
        }
        return tables.get(flag);
    }

    public static CandidateTable getTableScore(QueryTable querytable, TableBean webtable1) throws IOException {
        double SIMITHRED = 0.7;
        TableBean webtable = webtable1;
        List<Integer> log1 = new ArrayList<Integer>();
        List<Integer> log2 = new ArrayList<Integer>();
        List<String> query_entity = querytable.getEntity();
        List<String> webtable_entity = webtable.getEntity();
        List<String> query_attribute = querytable.getAttributes();
        List<String> webtable_attribute = webtable.getAttrubutes();
        List<Double> entitySimScore = new ArrayList<Double>();
        List<Double> attributeSimScore = new ArrayList<Double>();
        CandidateTable ct = new CandidateTable();
        List<String> ent = new ArrayList<String>();
        List<String> attri = new ArrayList<String>();
        FillCell fc = new FillCell();
        //查看查询表实体和网络表实体的相似度
        for (int i = 0; i < query_entity.size(); i++) {
            double max = 0.0;
            int temp = -1;
            String queryentity = StringTransform.stringTransform(query_entity.get(i));
            for (int j = 0; j < webtable_entity.size(); j++) {
                if (log1.contains(j))
                    continue;
                String webentity = StringTransform.stringTransform(webtable_entity.get(j));
                double sim = EditDistance.similarity(queryentity, webentity);
                if (sim > max) {
                    max = sim;
                    temp = j;
                }

            }

            if (max > SIMITHRED) {
                ent.add(webtable_entity.get(temp));
                log1.add(temp);
                entitySimScore.add(max);
            }
        }
        //查找相同的属性名
        for (int i = 0; i < query_attribute.size(); i++) {
            double max = 0.0;
            int temp = -1;
            String queryattribute = StringTransform.stringTransform(query_attribute.get(i));
            for (int j = 0; j < webtable_attribute.size(); j++) {
                if (log2.contains(j))
                    continue;
                String webattribute = StringTransform.stringTransform(webtable_attribute.get(j));
                double sim = EditDistance.similarity(queryattribute, webattribute);
                if (sim > max) {
                    max = sim;
                    temp = j;
                }
            }
            if (max > SIMITHRED) {
                attri.add(webtable_attribute.get(temp));
                log2.add(temp);
                attributeSimScore.add(max);
            }
        }
        fc.setAttributes(attri);
        fc.setEntitys(ent);
        double sum1 = 0.0;
        for (int i = 0; i < entitySimScore.size(); i++) {
            sum1 += entitySimScore.get(i);
        }
        double sum2 = 0.0;
        for (int i = 0; i < attributeSimScore.size(); i++) {
            sum2 += attributeSimScore.get(i);
        }
        double similarity = sum1 * sum2;
        double score0 = 0.0;
        Map<String, Double> con = new HashMap<String, Double>();
        BufferedReader br1 = new BufferedReader(new FileReader("DataSet/Experiments/query-table_Con.txt"));
        String s1 = null;
        while ((s1 = br1.readLine()) != null) {
            String[] arrs = s1.split(" ");
            double d = Double.parseDouble(arrs[1]);
            String[] path = webtable.getId().split("/");
            if (arrs[0].equals(path[path.length - 1].replace(".xls", ""))) {
                score0 = d;
            }

        }
        br1.close();
        ct.setScore(similarity * score0);
        ct.setFillcell(fc);
        ct.setId(webtable.getAbpath());
        return ct;
    }


}
