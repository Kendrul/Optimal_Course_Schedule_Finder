package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.Test;
import java.io.IOException;

import courseSchedulerCore.Courses;
import courseSchedulerCore.Driver;
import courseSchedulerCore.Labs;
import courseSchedulerCore.Parser;
import courseSchedulerCore.Penalty;
import courseSchedulerCore.Schedule;
import courseSchedulerCore.SearchControl;
import courseSchedulerCore.SearchInstanceRandom;
import courseSchedulerCore.TimeSlot;

public class searchControlTest {
	
	private boolean debug = true;
	private Penalty defaultPenalty = null;
	private final String [] penaltyArgs = {"1.0","1.0","1.0","1.0","1.0","1.0","1.0","1.0"};
	
	public searchControlTest()
	{
		//empty constructor
	}

	//ParseDocument Method tests
	
	private Parser testParser;
	
	public void parseDocumentLoadExample() {
		String testString = testRunner.pathString;

		try {
			testParser = new Parser();
			testParser.parseDocument(testString);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("threw an exception");
		}
		//test otherstuff 
	}
	
	@Test
	public void testSearchControl() {
		parseDocumentLoadExample();
		SearchControl sc = new SearchControl(testParser, defaultPenalty);
		Schedule sample = sc.begin();
		
		if(debug) {
			System.out.println("Full Search Result (No Penalty): ");
			System.out.println(sample.toStringReverse());
		}
		
		String [][] testArrayCourse = {{"CPSC", "313", "01", "TU", "9:30"},{"CPSC", "413", "01", "MO", "8:00"},{"CPSC", "433", "01", "MO", "9:00"},
				{"CPSC", "433", "02", "TU", "9:30"},{"CPSC", "567", "01", "MO", "8:00"},{"SENG", "311", "01", "MO", "8:00"},{"CPSC", "813", "01", "TU", "18:00"},{"CPSC", "913", "01", "TU", "18:00"}};
		String [][] testArrayLab = {{"CPSC", "433", "01", "01", "TU", "10:00"},{"CPSC", "433", "02","02", "MO", "8:00"},
				{"SENG", "311", "01","01", "FR", "10:00"},{"CPSC", "567", "01","01", "FR", "10:00"}};
		//compare courses
		for (int i = 0; i < sample.courseList.length; i++)
		{
			Courses c = testParser.getCoursesAndTime().getCoursesVector().get(i);
			TimeSlot ts = sample.courseList[i];
			assertEquals(c.getDepartment(), testArrayCourse[i][0]);
			assertEquals(c.getCourseNumber(), testArrayCourse[i][1]);
			assertEquals(c.getLectureNumber(), testArrayCourse[i][2]);
			assertEquals(ts.getDay(), testArrayCourse[i][3]);
			assertEquals(ts.getStartTime(), testArrayCourse[i][4]);
		}
		
		//compare labs
		for (int i = 0; i < sample.labList.length; i++)
		{
			Labs L = testParser.getCoursesAndTime().getLabsVector().get(i);
			TimeSlot ts = sample.labList[i];
			assertEquals(L.getDepartment(), testArrayLab[i][0]);
			assertEquals(L.getCourseNumber(), testArrayLab[i][1]);
			assertEquals(L.getLectureNumber(), testArrayLab[i][2]);
			assertEquals(L.getLabNumber(), testArrayLab[i][3]);
			assertEquals(ts.getDay(), testArrayLab[i][4]);		
			assertEquals(ts.getStartTime(), testArrayLab[i][5]);
		}
		
	}
	
	@Test
	public void testSearchControlSomePenalty() {
		createPenalty();
		parseDocumentLoadExample();
		SearchControl sc = new SearchControl(testParser, defaultPenalty);
		Schedule sample = sc.begin();
		
		if(debug) {
			System.out.println("Full Search Result (Some Penalty): ");
			System.out.println(sample.toStringReverse());
		}
		
		String [][] testArrayCourse = {{"CPSC", "313", "01", "MO", "8:00"},{"CPSC", "413", "01", "MO", "9:00"},{"CPSC", "433", "01", "MO", "9:00"},
				{"CPSC", "433", "02", "TU", "18:00"},{"CPSC", "567", "01", "TU", "9:30"},{"SENG", "311", "01", "MO", "8:00"},{"CPSC", "813", "01", "TU", "18:00"},{"CPSC", "913", "01", "TU", "18:00"}};
		String [][] testArrayLab = {{"CPSC", "433", "01", "01", "TU", "10:00"},{"CPSC", "433", "02","02", "MO", "8:00"},
				{"SENG", "311", "01","01", "FR", "10:00"},{"CPSC", "567", "01","01", "MO", "8:00"}};
		//compare courses
		for (int i = 0; i < sample.courseList.length; i++)
		{
			Courses c = testParser.getCoursesAndTime().getCoursesVector().get(i);
			TimeSlot ts = sample.courseList[i];
			assertEquals(c.getDepartment(), testArrayCourse[i][0]);
			assertEquals(c.getCourseNumber(), testArrayCourse[i][1]);
			assertEquals(c.getLectureNumber(), testArrayCourse[i][2]);
			assertEquals(ts.getDay(), testArrayCourse[i][3]);
			assertEquals(ts.getStartTime(), testArrayCourse[i][4]);
		}
		
		//compare labs
		for (int i = 0; i < sample.labList.length; i++)
		{
			Labs L = testParser.getCoursesAndTime().getLabsVector().get(i);
			TimeSlot ts = sample.labList[i];
			assertEquals(L.getDepartment(), testArrayLab[i][0]);
			assertEquals(L.getCourseNumber(), testArrayLab[i][1]);
			assertEquals(L.getLectureNumber(), testArrayLab[i][2]);
			assertEquals(L.getLabNumber(), testArrayLab[i][3]);
			assertEquals(ts.getDay(), testArrayLab[i][4]);		
			assertEquals(ts.getStartTime(), testArrayLab[i][5]);
		}
		

		System.out.println(sample.toString());
	}
	
	private void createPenalty()
	{
		defaultPenalty = new Penalty();
		defaultPenalty.setCourseMinPenalty(Double.parseDouble(penaltyArgs[0]));
		defaultPenalty.setLabMinPenalty(Double.parseDouble(penaltyArgs[1]));
		defaultPenalty.setSectionDiffPenalty(Double.parseDouble(penaltyArgs[2]));
		defaultPenalty.setNotPairPenalty(Double.parseDouble(penaltyArgs[3]));
		defaultPenalty.setW_minfilled(Double.parseDouble(penaltyArgs[4]));
		defaultPenalty.setW_pref(Double.parseDouble(penaltyArgs[5]));
		defaultPenalty.setW_pair(Double.parseDouble(penaltyArgs[6]));
		defaultPenalty.setW_secdif(Double.parseDouble(penaltyArgs[7]));
	}
}
