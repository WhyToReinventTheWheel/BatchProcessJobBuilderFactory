package com.mk;


import java.sql.BatchUpdateException;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {
	
	@Autowired
	Job importUserJob;
	
	@Autowired
	private JobLauncher jobLauncher;
	
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }
    
    @Override
    public void run(String... args) throws Exception {
    	
    	
    	JobParameters jobParameters = new JobParametersBuilder().addLong(
				"time", System.currentTimeMillis()).toJobParameters();
		
    	
    	try {
    		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ Start $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ ");
    		Thread.sleep(5000);
    		System.out.println("I have been scheduled with Spring scheduler");
    		JobExecution  jobExecution= jobLauncher.run(importUserJob, jobParameters);
    		BatchUpdateException batchUpdateException = (BatchUpdateException)jobExecution.getAllFailureExceptions().get(0).getCause();
    		System.out.println("$$$$$$$$$ getLargeUpdateCountsLength== "+ batchUpdateException.getLargeUpdateCounts().length);
    		
    		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ End $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ ");
    		
    		
    		//flowJobBuilder.build();
    	}catch(Exception exp) {
    		//exp.printStackTrace();
    		System.out.println("gdhghdhdhdhdhdhgdhgdhd ghException: "+ exp.getMessage());
    	}
    	
    }
}
