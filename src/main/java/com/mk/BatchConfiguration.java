package com.mk;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	private static int counter = 0; 
	
	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	public DataSource dataSource;

	// tag::readerwriterprocessor[]
	@Bean
	public FlatFileItemReader<Person> reader() {
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@  reader @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		FlatFileItemReader<Person> reader = new FlatFileItemReader<Person>();
		reader.setResource(new ClassPathResource("sample-data.csv"));

		reader.setLineMapper(new DefaultLineMapper<Person>() {
			{
				setLineTokenizer(new DelimitedLineTokenizer() {
					{
						setNames(new String[] { "firstName", "lastName" });
					}
				});

				setFieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {
					{
						setTargetType(Person.class);
					}
				});
			}
		});
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@  reader: End @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		return reader;
	}

	@Bean
	public PersonItemProcessor processor() {
		return new PersonItemProcessor();
	}

	@Bean
	public JdbcBatchItemWriter<Person> writer() {
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@  writer : @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ === " );
		JdbcBatchItemWriter<Person> writer = new JdbcBatchItemWriter<Person>();
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Person>());
		writer.setSql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)");
		writer.setDataSource(dataSource);
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@  writer End: @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ === ");
		return writer;
	}
	// end::readerwriterprocessor[]

	// tag::jobstep[]
	@Bean
	public Job importUserJob(JobCompletionNotificationListener listener) {
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@  importUserJob @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		
		//Job job =  jobBuilderFactory.get("importUserJob").incrementer(new RunIdIncrementer()).listener(listener).flow(step1()).next(step2()).end().build();
		Job job =  jobBuilderFactory.get("importUserJob").incrementer(new RunIdIncrementer()).listener(listener).flow(step1()).end().build();
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@  importUserJob: End @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		
		return job;
	}
/*
	@Bean
	public Step step2() {
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@  step2 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		TaskletStep taskletStep = stepBuilderFactory.get("step2").<Person, Person>chunk(8).listener(new ItemFailureLoggerListener()).reader(reader()).processor(processor())
				.writer(writer()).build();
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@  step2 : End @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		 return taskletStep;
	}
*/
	@Bean
	public Step step1() {
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@  step1 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		TaskletStep taskletStep = stepBuilderFactory.get("step1").<Person, Person>chunk(3).listener(new ItemFailureLoggerListener()).reader(reader()).processor(processor())
				.writer(writer()).build();
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@  step1 : End @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		 return taskletStep;
	}
	// end::jobstep[]
}
