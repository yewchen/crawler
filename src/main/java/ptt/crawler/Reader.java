package ptt.crawler;

import org.jsoup.select.Elements;
import ptt.crawler.model.*;
import ptt.crawler.config.Config;

import okhttp3.*;
import org.jsoup.*;
import org.jsoup.nodes.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Reader {
	private ResponseBody body;
    private OkHttpClient okHttpClient;
    private OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
    private final Map<String, List<Cookie>> cookieStore; // 保存 Cookie
    private final CookieJar cookieJar;
    
    /* 今天日期 */
    String today = laterDate(-0);
    String today_later1 = laterDate(-1);
    String today_later2 = laterDate(-2);
    public static String laterDate(int x) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,x);
    	return new SimpleDateFormat("MM/dd").format(calendar.getTime());
    }

    public Reader() throws IOException {
        /* 初始化 */
        cookieStore = new HashMap<>();
        cookieJar = new CookieJar() {
            @Override
            public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
                List<Cookie> cookies = cookieStore.getOrDefault(
                    httpUrl.host(), 
                    new ArrayList<>()
                );
                cookies.addAll(list);
                cookieStore.put(httpUrl.host(), cookies);
            }
            
            /* 每次發送帶上儲存的 Cookie */
            @Override
            public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                return cookieStore.getOrDefault(
                    httpUrl.host(), 
                    new ArrayList<>()
                );
            }
        };
        
        /* 代理Proxy
        SocketAddress sa = new InetSocketAddress("proxy.cht.com.tw", 8080);
        okHttpClientBuilder.proxy(new Proxy(Proxy.Type.HTTP, sa));
        okHttpClient = okHttpClientBuilder.cookieJar(cookieJar).build();
        */
        
        /* 不需要Proxy */
        okHttpClient = new OkHttpClient.Builder().cookieJar(cookieJar).build();
        
        /* 獲得網站的初始 Cookie */
        Request request = new Request.Builder().get().url(Config.PTT_URL).build();
        body = okHttpClient.newCall(request).execute().body();
    }

    public List<Article> getList(String boardName) throws IOException, ParseException {
        Board board = Config.BOARD_LIST.get(boardName);

        /* 如果找不到指定的看板 */
        if (board == null) {
            return null;
        }

        /* 如果看板需要成年檢查 */
        if (board.getAdultCheck() == true) {
            runAdultCheck(board.getUrl());
        }
        
        /* 開始撈資料 */
        List<Article> result = new ArrayList<>();
        boolean chgDay = false;
        do {
        	/* 抓取目標頁面 */
            Request request = new Request.Builder()
                .url(Config.PTT_URL + board.getUrl())
                .get()
                .build();

            Response response = okHttpClient.newCall(request).execute();
            String body = response.body().string();
            
            /* 抓出上一頁的URL */
            Document doc = Jsoup.parse(body);
            Elements articleList = doc.select(".action-bar .btn-group.btn-group-paging .btn.wide");
            for (Element element: articleList) {
            	if ("‹ 上頁".contains(element.text())) {
            		board.setUrl(element.attr("href"));
            	}
            }
            
            /* 轉換文章列表 HTML 到 Article */
            List<Map<String, String>> articles = parseArticle(body);
            for (Map<String, String> article: articles) {
            	
            	/* 過濾置底文 */
            	if ( article.get("title").contains("[公告]") ) continue;
            	/* 過濾刪除文章 */
            	if ( article.get("author").equals("-") ) {
            		continue;
            	}
            	/* 跨日跳出 */
            	if ( today_later2.equals(article.get("date")) ) {
            		chgDay = true;
            		break;
            	}
            	
                String url = article.get("url");
                String title = article.get("title");
                String author = article.get("author");
                String date = article.get("date");
                
                result.add(new Article(board, url, title, author, date));
            }
        } while ( chgDay == false );

        body.close();
        return result;
    }

    /* 進行年齡確認 */
    private void runAdultCheck(String url) throws IOException {
        FormBody formBody = new FormBody.Builder()
            .add("from", url)
            .add("yes", "yes")
            .build();

        Request request = new Request.Builder()
            .url(Config.PTT_URL + "/ask/over18")
            .post(formBody)
            .build();

        okHttpClient.newCall(request).execute();
    }

    /* 解析看板文章列表 */
    private List<Map<String, String>> parseArticle(String body) {
        List<Map<String, String>> result = new ArrayList<>();
        Document doc = Jsoup.parse(body);
        Elements articleList = doc.select(".r-ent");

        for (Element element: articleList) {
            String url = element.select(".title a").attr("href");
            String title = element.select(".title a").text();
            String author = element.select(".meta .author").text();
            String date = element.select(".meta .date").text();

            result.add(new HashMap<String, String>(){{
                put("url", url);
                put("title", title);
                put("author", author);
                put("date", date);
            }});
        }

        return result;
    }
}
