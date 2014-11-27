package orip.orip;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

	public static File f2;
	public static BufferedWriter w2;
	public static FileOutputStream out2;
	
	public static void main(String[] args) throws Exception {
		baz();
	}

	public static void printTotalFrequencies() throws Exception {
		Map<String, Integer> total = new HashMap<String, Integer>();

		Calendar current = Calendar.getInstance();
		current.add(Calendar.DATE, -3);
		Calendar dest = Calendar.getInstance();
		dest.set(2010, 5, 7);
		for (; current.getTime().after(dest.getTime()); current.add(Calendar.DATE, -1)) {
			System.out.println(current.getTime());
			Map<String, Integer> oneDay = getFrequencyForDay(current.getTime());
			total = merge(total, oneDay);
		}

		printTable(total);

	}

	public static Map<String, Integer> getFrequencyForDay(Date date) throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		String dateString = format.format(date);
		URL url = new URL("http://hckrnews.com/data/" + dateString + ".js");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		
		String line;
		StringBuilder sb = new StringBuilder();
		while ((line = in.readLine()) != null) {
			sb.append(line);
		}
		in.close();
		String jsonString = sb.toString();
		

		Pattern p = Pattern.compile("\"\\s*link\\s*\"\\s*:\\s*\"[^\"]*\"");
		Matcher matcher = p.matcher(jsonString);
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		while (matcher.find()) {
			String entry = matcher.group(0);
			String link = entry.split("\"\\s*link\\s*\"\\s*:\\s*")[1].replaceAll("\"", "");
//			w2.write(String.format("%s%n", link));
			String host = new URL(link).getHost();
//			w2.write(String.format("%s%n", host));
			
			Integer num;
			if ((num = map.get(host)) == null) {
				map.put(host, 1);
			} else {
				map.put(host, num + 1);
			}

		}
		return map;

	}

	public static void printTable(Map<String, Integer> map) throws IOException {
		File file = new File("results.txt");
		FileOutputStream out = new FileOutputStream(file);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
		for (Entry<String, Integer> e : map.entrySet()) {
			// writer.write((String.format("%30.30s %10d%n", e.getKey(),
			// e.getValue())));
			writer.write((String.format("%5d %s%n", e.getValue(), e.getKey())));
		}

		out.close();
	}
	
	public static Map<String, Integer> merge(Map<String, Integer> m1, Map<String, Integer> m2) {
		HashMap<String, Integer> m3 = new HashMap<String, Integer>();
		m3.putAll(m1);
		for (Entry<String, Integer> e : m2.entrySet()) {
			Integer num;
			if ((num = m3.get(e.getKey())) != null) {
				m3.put(e.getKey(), e.getValue() + num);
			} else {
				m3.put(e.getKey(), e.getValue());
			}
		}
		return m3;
	}
	
	public static void baz() throws Exception {
		f2 = new File("list.txt");
		out2 = new FileOutputStream(f2);
		w2 = new BufferedWriter(new OutputStreamWriter(out2));
		printTotalFrequencies();
		out2.close();
	}
}
