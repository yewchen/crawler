package com.yewchen.crawler;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import ptt.crawler.Reader;
import ptt.crawler.Violation;
import ptt.crawler.model.Article;

@RestController
public class HelloController {
	@GetMapping("/")
	public String index() throws IOException, ParseException {
		
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
		
		/* 抓今天標題無分類文章 */
		violationList = new Violation().getNoTagByDate(result, Reader.laterDate(-0));
		sb.append("今日標題無分類清單：<br>");
		for ( Article article : violationList ) {
			sb.append(article.getDate()+" "+ article.getAuthor()+" "+article.getTitle()+" https://www.ptt.cc"+article.getUrl()+"<br>");
		}
		
		sb.append("<hr>");
		
		/* 抓昨天超貼違規文章 */
		violationList = new Violation().getExceedPostListByDate(result, Reader.laterDate(-1));
		sb.append("昨日交易文超貼清單：<br>");
		for ( Article article : violationList ) {
			sb.append(article.getDate()+" "+ article.getAuthor()+" "+article.getTitle()+" https://www.ptt.cc"+article.getUrl()+"<br>");
		}
		
		/* 抓昨天標題無分類文章 */
		violationList = new Violation().getNoTagByDate(result, Reader.laterDate(-1));
		sb.append("昨天標題無分類清單：<br>");
		for ( Article article : violationList ) {
			sb.append(article.getDate()+" "+ article.getAuthor()+" "+article.getTitle()+" https://www.ptt.cc"+article.getUrl()+"<br>");
		}
		
		return sb.toString();
	}
}
