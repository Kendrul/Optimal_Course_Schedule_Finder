package test;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class testRunner {

	public final static String pathString = new String("L:\\Java Projects - Eclipse\\workspace\\CourseScheduler\\src\\courseSchedulerCore\\example.txt");		

	public static void main(String[] args) {
	      Result result = JUnitCore.runClasses(parserTest.class,searchRandomTest.class, searchGeneticTest.class, searchControlTest.class);
			
	      for (Failure failure : result.getFailures()) {
	         System.out.println(failure.toString());
	      }
			
	      System.out.println(result.wasSuccessful());
	   }
	}

