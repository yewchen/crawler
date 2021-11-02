package ptt.crawler.config;

import ptt.crawler.model.Board;

import java.util.*;

public final class Config {
    public static final String PTT_URL = "https://www.ptt.cc";
    @SuppressWarnings("serial")
	public static final Map<String, Board> BOARD_LIST = new HashMap<String, Board>() {
	{
        put("Diablo", new Board(
            "/bbs/Diablo",
        	//"/bbs/Diablo/index4020.html",
            "·t¶ÂªO",
            "Diablo",
            true)
        );
    }};
}