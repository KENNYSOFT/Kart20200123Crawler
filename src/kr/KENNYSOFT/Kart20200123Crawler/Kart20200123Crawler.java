package kr.KENNYSOFT.Kart20200123Crawler;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpRetryException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Kart20200123Crawler
{
	public static JSONParser jsonParser = new JSONParser();
	public static final Map<Long, String> nameMap = Map.ofEntries(Map.entry(2138l, "나린 낭자 (무제한)"), Map.entry(2139l, "너울 도령 (무제한)"), Map.entry(2140l, "흑기사 X (무제한)"), Map.entry(2141l, "레전드 파츠 X (무제한)"), Map.entry(2142l, "스펙터 X (무제한)"), Map.entry(2143l, "베히모스 X (무제한)"), Map.entry(2144l, "황금 기어"), Map.entry(2145l, "플래티넘 캡슐 기어"), Map.entry(2146l, "코인"), Map.entry(2147l, "네오의 신상 카트 No.8"), Map.entry(2148l, "스트라이크 X (무제한)"), Map.entry(2149l, "마그마 X (무제한)"), Map.entry(2150l, "네오의 신상 카트 No.8"), Map.entry(2151l, "파츠 조각"), Map.entry(2152l, "슬롯 체인저"), Map.entry(2153l, "아이템 체인저"), Map.entry(2154l, "나린 낭자 풍선"), Map.entry(2155l, "너울 도령 풍선"), Map.entry(2156l, "복주머니 밴드"), Map.entry(2157l, "복조리 핸드봉 (10일)"), Map.entry(2158l, "복조리 핸드봉 (무제한)"), Map.entry(2159l, "세뱃돈 봉투"));
	public static final Map<Long, Integer> cntMap = Map.ofEntries(Map.entry(2144l, 5), Map.entry(2145l, 5), Map.entry(2146l, 200), Map.entry(2147l, 5), Map.entry(2150l, 2), Map.entry(2151l, 200), Map.entry(2152l, 50), Map.entry(2153l, 50), Map.entry(2154l, 50), Map.entry(2155l, 50), Map.entry(2156l, 30));
	public static final String ENC = "YOUR_ENC";
	public static final String KENC = "YOUR_KENC";
	public static final String NPP = "YOUR_NPP";
	
	public static void main(String[] args) throws Exception
	{
		CookieManager cookieManager = new CookieManager();
		CookieHandler.setDefault(cookieManager);
		HttpCookie cookie1 = new HttpCookie("ENC", ENC);
		cookie1.setDomain("nexon.com");
		cookie1.setPath("/");
		cookie1.setVersion(0);
		cookieManager.getCookieStore().add(new URI("http://kart.nexon.com/"), cookie1);
		HttpCookie cookie2 = new HttpCookie("KENC", KENC);
		cookie2.setDomain("kart.nexon.com");
		cookie2.setPath("/");
		cookie2.setVersion(0);
		cookieManager.getCookieStore().add(new URI("http://kart.nexon.com/"), cookie2);
		HttpCookie cookie3 = new HttpCookie("NPP", NPP);
		cookie3.setDomain("nexon.com");
		cookie3.setPath("/");
		cookie3.setVersion(0);
		cookieManager.getCookieStore().add(new URI("http://kart.nexon.com/"), cookie3);
		CSVPrinter csvPrinter = new CSVPrinter(Files.newBufferedWriter(Paths.get("coupon.csv")), CSVFormat.DEFAULT.withHeader("\uFEFF당첨일", "아이템", "수량", "유효기간", "쿠폰번호"));
		int position = 0;
		while (true)
		{
			JSONObject object = null;
			boolean completed;
			do
			{
				completed = true;
				try
				{
					object = play(position);
				}
				catch (HttpRetryException e)
				{
					cookieManager.getCookieStore().add(new URI("http://kart.nexon.com/"), cookie1);
					cookieManager.getCookieStore().add(new URI("http://kart.nexon.com/"), cookie2);
					cookieManager.getCookieStore().add(new URI("http://kart.nexon.com/"), cookie3);
					completed = false;
				}
			} while (!completed);
			switch ((int) (long) ((JSONObject) object.get("Return")).get("n4Return"))
			{
			case 0:
			case -202:
				position = (int) (long) ((JSONObject) ((JSONArray) object.get("Data")).get(0)).get("n1Position");
				continue;
			case -203:
			default:
				break;
			}
			break;
		}
		int pages = 1;
		for (int i = 1; i <= pages; ++i)
		{
			JSONObject object = null;
			boolean completed;
			do
			{
				completed = true;
				try
				{
					object = list(i);
				}
				catch (HttpRetryException e)
				{
					cookieManager.getCookieStore().add(new URI("http://kart.nexon.com/"), cookie1);
					cookieManager.getCookieStore().add(new URI("http://kart.nexon.com/"), cookie2);
					cookieManager.getCookieStore().add(new URI("http://kart.nexon.com/"), cookie3);
					completed = false;
				}
			} while (!completed);
			pages = (int) ((long) object.get("TotalCount") + 5) / 6;
			JSONArray array = (JSONArray) object.get("Data");
			for (Object obj : array)
			{
				JSONObject item = (JSONObject) obj;
				csvPrinter.printRecord(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.valueOf(((String) item.get("dtCreate")).replaceAll("[^\\d]", "")))), nameMap.get(item.get("n4ItemNo")), cntMap.containsKey(item.get("n4ItemNo")) ? cntMap.get(item.get("n4ItemNo")) : "-", "2020-02-26 23:59", item.get("strCouponSN"));
			}
		}
		csvPrinter.flush();
		csvPrinter.close();
	}
	
	public static JSONObject play(int position) throws Exception
	{
		HttpURLConnection conn = (HttpURLConnection) new URL("http://kart.nexon.com/events/2020/0123/AjaxYutNori.aspx").openConnection();
		Map<String, String> parameters = new HashMap<>();
		parameters.put("strType", "apply");
		parameters.put("PositionCur", String.valueOf(position));
		StringJoiner sj = new StringJoiner("&");
		for (Entry<String, String> entry : parameters.entrySet()) sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8"));
		byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
		conn.setRequestMethod("POST");
		conn.setFixedLengthStreamingMode(out.length);
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Referer", "http://kart.nexon.com/events/2020/0123/Event.aspx");
		conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
		conn.setDoOutput(true);
		DataOutputStream os = new DataOutputStream(conn.getOutputStream());
		os.write(out);
		os.flush();
		os.close();
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
		StringBuffer response = new StringBuffer();
		String line;
		while ((line = br.readLine()) != null) response.append(line);
		br.close();
		conn.disconnect();
		System.out.println(response);
		return (JSONObject) jsonParser.parse(response.toString());
	}
	
	public static JSONObject list(int page) throws Exception
	{
		HttpURLConnection conn = (HttpURLConnection) new URL("http://kart.nexon.com/events/2020/0123/AjaxYutNori.aspx").openConnection();
		Map<String, String> parameters = new HashMap<>();
		parameters.put("strType", "list");
		parameters.put("PageNo", String.valueOf(page));
		StringJoiner sj = new StringJoiner("&");
		for (Entry<String, String> entry : parameters.entrySet()) sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8"));
		byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
		conn.setRequestMethod("POST");
		conn.setFixedLengthStreamingMode(out.length);
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Referer", "http://kart.nexon.com/events/2020/0123/Event.aspx");
		conn.setDoOutput(true);
		DataOutputStream os = new DataOutputStream(conn.getOutputStream());
		os.write(out);
		os.flush();
		os.close();
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
		StringBuffer response = new StringBuffer();
		String line;
		while ((line = br.readLine()) != null) response.append(line);
		br.close();
		conn.disconnect();
		System.out.println(response);
		return (JSONObject) jsonParser.parse(response.toString());
	}
}