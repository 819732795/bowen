package test;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

public class IKAnalyzerTest {
	public static void main(String[] args) throws IOException {
		String string1 = "������ķִʿɲ����ԣ����ŷ������㡣";
		String string2 = "������ķִʱȽϷ��㣬���ŷ��㻹���ԡ�1123";
		Vector<String> T1 = participle(string1);
		Vector<String> T2 = participle(string2);
		try {
			double similarity = getSimilarity(T1, T2);
			System.out.println(similarity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Vector<String> participle(String str) {
		Vector<String> str1 = new Vector<String>();// ��������зִ�
		try {
			StringReader reader = new StringReader(str);
			IKSegmenter ik = new IKSegmenter(reader, true);// ��Ϊtrueʱ���ִ����������ʳ��з�
			Lexeme lexeme = null;
			while ((lexeme = ik.next()) != null) {
				str1.add(lexeme.getLexemeText());
			}
			if (str1.size() == 0) {
				return null;
			}
			// �ִʺ�
			System.out.println("str�ִʺ�" + str1);
		} catch (IOException e1) {
			System.out.println();
		}
		return str1;
	}

	// ��ֵ
	public static double YUZHI = 0.2;

	/**
	 * ���ذٷֱ�
	 * 
	 * @author: Administrator
	 * @Date: 2015��1��22��
	 * @param T1
	 * @param T2
	 * @return
	 */
	public static double getSimilarity(Vector<String> T1, Vector<String> T2) throws Exception {
		int size = 0, size2 = 0;
		if (T1 != null && (size = T1.size()) > 0 && T2 != null && (size2 = T2.size()) > 0) {

			Map<String, double[]> T = new HashMap<String, double[]>();

			// T1��T2�Ĳ���T
			String index = null;
			for (int i = 0; i < size; i++) {
				index = T1.get(i);
				if (index != null) {
					double[] c = T.get(index);
					c = new double[2];
					c[0] = 1; // T1���������Ci
					c[1] = YUZHI;// T2���������Ci
					T.put(index, c);
				}
			}

			for (int i = 0; i < size2; i++) {
				index = T2.get(i);
				if (index != null) {
					double[] c = T.get(index);
					if (c != null && c.length == 2) {
						c[1] = 1; // T2��Ҳ���ڣ�T2���������=1
					} else {
						c = new double[2];
						c[0] = YUZHI; // T1���������Ci
						c[1] = 1; // T2���������Ci
						T.put(index, c);
					}
				}
			}

			// ��ʼ���㣬�ٷֱ�
			Iterator<String> it = T.keySet().iterator();
			double s1 = 0, s2 = 0, Ssum = 0; // S1��S2
			while (it.hasNext()) {
				double[] c = T.get(it.next());
				Ssum += c[0] * c[1];
				s1 += c[0] * c[0];
				s2 += c[1] * c[1];
			}
			// �ٷֱ�
			return Ssum / Math.sqrt(s1 * s2);
		} else {
			throw new Exception("������������⣡");
		}
	}
}
