package com.mk;

import java.sql.BatchUpdateException;
import java.util.List;

import org.springframework.batch.core.ItemWriteListener;

public class ItemFailureLoggerListener implements ItemWriteListener<Person> {

	@Override
	public void beforeWrite(List<? extends Person> items) {

	}

	@Override
	public void afterWrite(List<? extends Person> items) {
		System.out.println("================ afterWrite == " + items.size());
		for (Person person : items) {
			System.out.println("After Write: " + person);
		}
	}

	@Override
	public void onWriteError(Exception exception, List<? extends Person> items) {
		System.out.println("================ Error" + exception.getCause());
		System.out.println("================ Error== " + items.size());
		System.out.println("================ afterWrite");
		for (Person person : items) {
			System.out.println("@@@@@@@@@@@@@@ onWriteError: " + person);
		}

		Throwable rootCause = exception.getCause();
		if (rootCause instanceof BatchUpdateException) {

			BatchUpdateException bue = (BatchUpdateException) rootCause;
			int lastSuccessfullRow = bue.getUpdateCounts().length;
			int failurePoint = lastSuccessfullRow + 1;
			int continuePoint = lastSuccessfullRow + 2;

			// affectedCount+=lastSuccessfullRow;
			// failedCount++;

			System.out.println("======================================== Last successful row: " + lastSuccessfullRow);
			System.out.println("======================================== Failed row: " + failurePoint);
			System.out.println("======================================== continue point: " + continuePoint);
			System.out.println("======================================== Fail Person " + items.get(lastSuccessfullRow));
		}
		
	}
}
