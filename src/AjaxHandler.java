import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/AjaxHandler")
public class AjaxHandler extends HttpServlet {
private static final long serialVersionUID = 1L;

	public AjaxHandler() {
	  super();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	//  doPost(request, response);
		String id = request.getParameter("id");
		String apiURL = "https://www.acmicpc.net/user/"+id;
		
		Map<String, String> requestHeaders = new HashMap<>();
	    String responseBody = get(apiURL,requestHeaders);
	
	    System.out.println(responseBody);
	    
//	    response.setHeader("Access-Control-Allow-Headers", "Content-Type");
//	    response.setHeader("Access-Control-Allow-Origin", "*");
		
	    response.setContentType("text/text; charset=UTF-8");
	    response.getWriter().append(responseBody).flush();
	}
	
	private static String get(String apiUrl, Map<String, String> requestHeaders){
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
//            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
//                con.setRequestProperty(header.getKey(), header.getValue());
//            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 에러 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    private static HttpURLConnection connect(String apiUrl){
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    private static String readBody(InputStream body){
        InputStreamReader streamReader = new InputStreamReader(body);

        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();

            String line = "<h2>조회결과</h2>";
            int isSolved = 0;
            while ((line = lineReader.readLine()) != null) {
            	if(line.contains("<div class = \"panel-body\">")){
            		isSolved += 1;
            		continue;
            	}
            	if(isSolved==1) {
            		if(line.contains("<a href=\"/problem")) {
            			responseBody.append(line); 
            		}
            	}else if(isSolved==2) {
            		break;
            	}
            }
            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }
}