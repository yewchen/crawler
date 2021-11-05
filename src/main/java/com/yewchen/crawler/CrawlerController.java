package com.yewchen.crawler;

import java.io.File;
import java.io.IOException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CrawlerController {
	
	 @GetMapping("/diabloBM")
	    public String hello( @RequestParam(name = "name", required = false, defaultValue = "World") String name, 
	    					  Model model) {
		 
		 	/* get file */
		 	try {
				File resource = new ClassPathResource("violation.txt").getFile();
				System.out.println("name="+resource.getName());
				System.out.println("path="+resource.getPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
		 	/* set and return */
	        model.addAttribute("name", name);
	        return "diabloBM";
	        
	    }
}