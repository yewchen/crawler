package ptt.crawler;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ptt.crawler.model.Article;

public class Violation {
	
	
	public List<Article> getNoTagByDate(List<Article> result, String date) {
		List<Article> violation = new ArrayList<>();
		try {
            
            /* 1. 無分類文檢查 */
            System.out.println(date+" 無分類文清單：");
            for ( Article article : result ) {
            	
            	/* 檢查日期 */
            	if ( !article.getDate().equals(date) ) continue;
            	
            	/* 檢查標題分類 */
            	if ( !article.getTitle().contains("[") ) {
            		violation.add(article);
            	}
            }
            for ( Article article : violation) {
            	System.out.println(article.getDate()+" "+ article.getAuthor()+" "+article.getTitle()+" https://www.ptt.cc"+article.getUrl());
            }
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		return violation;
	}
	
	public List<Article> getExceedPostListByDate(List<Article> result, String date) {
		
		List<Article> violation = new ArrayList<>();
		
        try {
            Set<String> checkedAuthor = new HashSet<>();
            
            /* 1. 交易文檢查 */
            System.out.println(date+" 交易文超貼清單：");
            for ( Article article : result ) {
            	
            	/* 檢查日期 */
            	if ( !article.getDate().equals(date) ) continue;
            	
            	/* 若該作者已檢查過, 則不再執行 */
            	if ( checkedAuthor.contains(article.getAuthor()) ) continue;
            	
            	/* 檢查交易文類 */
            	if ( article.getTitle().contains("[交易]") || article.getTitle().contains("[競標]") ) {
            		int tradeCnt=0;
            		/* 根據作者遍歷其所有交易文 */
            		List<Article> list = new ArrayList<>();
            		for ( Article chk : result ) {
            			/* 檢查日期 */
                    	if ( !chk.getDate().equals(date) ) continue;
            			if ( chk.getAuthor().equals(article.getAuthor()) ) {
            				if ( chk.getTitle().contains("[交易]") || chk.getTitle().contains("[競標]") ) {
            					tradeCnt++;
            					list.add(chk);
            				}
            			}
            		}
            		/* 若兩篇交易文皆為競標回文, 則跳過 */
            		if ( (tradeCnt == 2) &&
            				(list.get(0).getTitle().contains("Re:") || list.get(1).getTitle().contains("Re:"))	) {
            			continue;
            		}
            		/* 若交易文數量超過1 或 競標文超過2篇 */
            		if ( tradeCnt > 1 ) {
            			checkedAuthor.add(article.getAuthor());	//紀錄已查詢過之作者
            			for ( Article atc : list  ) {
            				violation.add(atc);
            			}
            		}
            	}
            }
            for ( Article article : violation) {
            	System.out.println(article.getDate()+" "+ article.getAuthor()+" "+article.getTitle()+" https://www.ptt.cc"+article.getUrl());
            }
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return violation;

	}
	
	public static void main(String[] args) throws IOException, ParseException {
		
		/* 抓資料(今天到昨天的所有文章) */
		Reader reader = new Reader();
		List<Article> result = reader.getList("Diablo");
		List<Article> violationList = new ArrayList<>();
		StringBuffer sb = new StringBuffer();
		
		/* 抓今天超貼違規文章 */
		violationList = new Violation().getExceedPostListByDate(result, Reader.laterDate(-0));
		sb.append("今日交易文超貼清單：<br>");
		for ( Article article : violationList ) {
			sb.append(article.getDate()+" "+ article.getAuthor()+" "+article.getTitle()+" https://www.ptt.cc"+article.getUrl()+"<br>");
		}
		
		/* 抓昨天超貼違規文章 */
		violationList = new Violation().getExceedPostListByDate(result, Reader.laterDate(-1));
		sb.append("昨日交易文超貼清單：<br>");
		for ( Article article : violationList ) {
			sb.append(article.getDate()+" "+ article.getAuthor()+" "+article.getTitle()+" https://www.ptt.cc"+article.getUrl()+"<br>");
		}
		
		sb.append("<hr>");
		
		/* 抓今天標題無分類文章 */
		violationList = new Violation().getNoTagByDate(result, Reader.laterDate(-0));
		sb.append("今日標題無分類清單：<br>");
		for ( Article article : violationList ) {
			sb.append(article.getDate()+" "+ article.getAuthor()+" "+article.getTitle()+" https://www.ptt.cc"+article.getUrl()+"<br>");
		}
		
		/* 抓昨天標題無分類文章 */
		violationList = new Violation().getNoTagByDate(result, Reader.laterDate(-1));
		sb.append("昨天標題無分類清單：<br>");
		for ( Article article : violationList ) {
			sb.append(article.getDate()+" "+ article.getAuthor()+" "+article.getTitle()+" https://www.ptt.cc"+article.getUrl()+"<br>");
		}
		
		System.out.println("*******");
		System.out.println(sb.toString());
		
    }
    
}

