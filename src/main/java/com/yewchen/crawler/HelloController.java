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
	
	@GetMapping("/test")
	public String test() throws IOException, ParseException {
		/* 抓資料(今天到昨天的所有文章) */
		Reader reader = new Reader();
		List<Article> result = reader.getAPList("steven101");
		return result.toString();
	}
	@GetMapping("/")
	public String index() throws IOException, ParseException {
		
	    /* 抓資料(今天到昨天的所有文章) */
		Reader reader = new Reader();
		List<Article> result = reader.getBMList("Diablo");
		List<Article> violationList = new ArrayList<>();
		StringBuffer sb = new StringBuffer();
		
		/* 今天自刪文 */
		violationList = new Violation().getDeleteSelfByDate(reader, result, Reader.laterDate(-0));
		sb.append("今日自刪文清單：<br>");
		for ( Article article : violationList ) {
			sb.append(article.getDate()+" "+ article.getAuthor()+" "+article.getTitle()+"  ");
			sb.append("<a href=\"https://www.ptt.cc"+article.getUrl()+"\">https://www.ptt.cc"+article.getUrl()+"</a><br>");
		}
		
		/* 抓今天超貼違規文章 */
		violationList = new Violation().getExceedPostListByDate(result, Reader.laterDate(-0));
		sb.append("今日交易文超貼清單：<br>");
		for ( Article article : violationList ) {
			sb.append(article.getDate()+" "+ article.getAuthor()+" "+article.getTitle()+"  ");
			sb.append("<a href=\"https://www.ptt.cc"+article.getUrl()+"\">https://www.ptt.cc"+article.getUrl()+"</a><br>");
		}
		
		/* 抓今天標題無分類文章 */
		violationList = new Violation().getNoTagByDate(result, Reader.laterDate(-0));
		sb.append("今日標題無分類清單：<br>");
		for ( Article article : violationList ) {
			sb.append(article.getDate()+" "+ article.getAuthor()+" "+article.getTitle()+"  ");
			sb.append("<a href=\"https://www.ptt.cc"+article.getUrl()+"\">https://www.ptt.cc"+article.getUrl()+"</a><br>");
		}
		
		sb.append("<hr>");
		
		/* 昨日自刪文 */
		violationList = new Violation().getDeleteSelfByDate(reader, result, Reader.laterDate(-1));
		sb.append("昨日自刪文清單：<br>");
		for ( Article article : violationList ) {
			sb.append(article.getDate()+" "+ article.getAuthor()+" "+article.getTitle()+"  ");
			sb.append("<a href=\"https://www.ptt.cc"+article.getUrl()+"\">https://www.ptt.cc"+article.getUrl()+"</a><br>");
		}
		
		/* 抓昨天超貼違規文章 */
		violationList = new Violation().getExceedPostListByDate(result, Reader.laterDate(-1));
		sb.append("昨日交易文超貼清單：<br>");
		for ( Article article : violationList ) {
			sb.append(article.getDate()+" "+ article.getAuthor()+" "+article.getTitle()+"  ");
			sb.append("<a href=\"https://www.ptt.cc"+article.getUrl()+"\">https://www.ptt.cc"+article.getUrl()+"</a><br>");
		}
		
		/* 抓昨天標題無分類文章 */
		violationList = new Violation().getNoTagByDate(result, Reader.laterDate(-1));
		sb.append("昨天標題無分類清單：<br>");
		for ( Article article : violationList ) {
			sb.append(article.getDate()+" "+ article.getAuthor()+" "+article.getTitle()+"  ");
			sb.append("<a href=\"https://www.ptt.cc"+article.getUrl()+"\">https://www.ptt.cc"+article.getUrl()+"</a><br>");
		}
		
		return sb.toString();
	}
}
