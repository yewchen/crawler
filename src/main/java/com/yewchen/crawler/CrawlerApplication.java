package com.yewchen.crawler;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
@SpringBootApplication
public class CrawlerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrawlerApplication.class, args);
	}
	
	@Component
	class EventSubscriber implements DisposableBean, Runnable {

	    private Thread thread;
	    private volatile boolean someCondition = true;

	    EventSubscriber(){
	        this.thread = new Thread(this);
	        this.thread.start();
	    }

	    @Override
	    public void run(){
	        while(someCondition){
	            try {
	            	System.out.println("***************");
					Thread.sleep(5000);
				} catch (Exception e) { e.printStackTrace(); }
	        }
	    }

	    @Override
	    public void destroy(){
	        someCondition = false;
	    }

	}
	
//	@Bean
//	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
//		return args -> {
//
//			System.out.println("Let's inspect the beans provided by Spring Boot:");
//
//			String[] beanNames = ctx.getBeanDefinitionNames();
//			Arrays.sort(beanNames);
//			for (String beanName : beanNames) {
//				System.out.println(beanName);
//			}
//
//		};
//	}


}
