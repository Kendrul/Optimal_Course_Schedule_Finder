package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.Test;
import java.io.IOException;

import courseSchedulerCore.Courses;
import courseSchedulerCore.Driver;
import courseSchedulerCore.Labs;
import courseSchedulerCore.Parser;
import courseSchedulerCore.Schedule;
import courseSchedulerCore.TimeSlot;

public class parserTest {
	
	private boolean debug = false;
	
	public parserTest()
	{
		//empty constructor
	}

	//ParseDocument Method tests
	
	private Parser testParser;
	
	@Test
	public void testParseDocumentLoadExample() {
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
	
	//make sure the parser read things in properly
	@Test
	public void testParseDocumentCourses() {
		
		testParseDocumentLoadExample();
		
		//should be noted that 813 and 913 do not appear in the example file, 
		//but the assignment noted a special case that they exist whenever 313/413 exist
		String [] testCourseNumber = {"313", "413", "433","433", "567","311","813","913"};
		String [] testCourseDepartment = {"CPSC", "CPSC","CPSC","CPSC","CPSC","SENG","CPSC","CPSC"};
		String [] testLectureNumber = {"01", "01", "01","02", "01","01","01","01"};
		String [] testCoursePair = {"0", "0","0","0","311","567","0","0"};
		String [] testNotCompatible = {"433","567"};
		String [] testLabs = {"0", "0", "01", "02", "01", "01", "0", "0"};
		String [] testNotWanted = {"MO", "8:00"};
		String [] testPreference =  { "TU", "9:00", "10"};
		
		if (debug) System.out.println(testParser.getCoursesAndTime().getCoursesVector().size());
		
		//courses
		for(int i = 0; i < testParser.getCoursesAndTime().getCoursesVector().size();i++)
		{
			Courses testCourse = testParser.getCoursesAndTime().getCoursesVector().get(i);
			if (debug) System.out.println(testCourse.getCourseNumber().toString());
			//name and department
			assertEquals(testCourse.getCourseNumber(), testCourseNumber[i]);
			assertEquals(testCourse.getLectureNumber(), testLectureNumber[i]);
			assertEquals(testCourse.getDepartment(), testCourseDepartment[i]);
			
			//paired courses
			if (testCourse.getPairCourse().size() == 0) assertEquals("0", testCoursePair[i]);
			else assertEquals(testParser.getCoursesAndTime().getCoursesVector().get(testCourse.getPairCourse().get(0)).getCourseNumber(), testCoursePair[i]);
			
			//non-compatibles
			if (testCourse.getCourseNumber() == "567")
			{
				assertEquals(testParser.getCoursesAndTime().getCoursesVector().get(testCourse.getNonCompatibleCourses().get(0)).getCourseNumber(), testNotCompatible[0]);
				assertEquals(testParser.getCoursesAndTime().getCoursesVector().get(testCourse.getNonCompatibleCourses().get(1)).getCourseNumber(), testNotCompatible[0]);
			}
			
			//not wanted and non-compatible
			if ((testCourse.getCourseNumber() == "433") && (testCourse.getLectureNumber() == "01"))
			{
				assertEquals(testParser.getCoursesAndTime().getCoursesVector().get(testCourse.getNonCompatibleLabs().get(0)).getCourseNumber(), testNotCompatible[1]);
				assertEquals(testParser.getCoursesAndTime().getCourseSlotsVector().get(testCourse.getUnwanted().get(0)).getDay(), testNotWanted[0]);
				assertEquals(testParser.getCoursesAndTime().getCourseSlotsVector().get(testCourse.getUnwanted().get(0)).getStartTime(), testNotWanted[1]);
			}
			//labs and tutorials
			if (testCourse.getLabIndex().size() == 0) assertEquals("0", testLabs[i]);
			else assertEquals(testParser.getCoursesAndTime().getLabsVector().get(testCourse.getLabIndex().get(0)).getLabNumber(), testLabs[i]);
			
			//preferences
			if (testCourse.getCourseNumber() == "433")
			{
				assertEquals(testParser.getCoursesAndTime().getCourseSlotsVector().get(testCourse.getPreference().get(0)).getDay(), testPreference[0]);
				assertEquals(testParser.getCoursesAndTime().getCourseSlotsVector().get(testCourse.getPreference().get(0)).getStartTime(), testPreference[1]);
				assertEquals(testCourse.getPrefEval().get(0), testPreference[2]);
			}
		}
	}
	
	//make sure the parser read things in properly
	@Test
	public void testParseDocumentLabs() {
	
		//labs
		testParseDocumentLoadExample();
		
		String [] testCourseNumber = {"433","433", "311","567"};
		String [] testCourseDepartment = {"CPSC","CPSC","SENG","CPSC"};
		String [] testLectureNumber = {"01", "02","01", "01"};
		String [] testLabNumber = {"01","02", "01","01"};
		Integer testCoursePair = 0;
		String [] testNotCompatible = {"02","01","567"};
		String [][] testPreference = {{ "MO", "8:00", "3"},{ "MO", "8:00", "1"},{ "MO", "10:00", "7"}};
		
		
		for(int i = 0; i < testParser.getCoursesAndTime().getLabsVector().size();i++)
		{
			Labs testLab = testParser.getCoursesAndTime().getLabsVector().get(i);
			assertEquals(testLab.getCourseNumber(), testCourseNumber[i]);
			assertEquals(testLab.getDepartment(), testCourseDepartment[i]);
			assertEquals(testLab.getLectureNumber(), testLectureNumber[i]);
			assertEquals(testLab.getLabNumber(), testLabNumber[i]);
			assertTrue(testLab.getPairCourse().size() == testCoursePair);
			
			if(testLab.getCourseNumber() == "433") {
				//notcompatible check
				if (testLab.getLabNumber() == "02") assertEquals(testLab.getNonCompatibleLabs(), testNotCompatible[1]);
				if(testLab.getLabNumber() == "01") assertEquals(testLab.getNonCompatibleLabs(), testNotCompatible[0]);
				
				//preferences
				if (testLab.getLabNumber() == "01") 
				{
					assertEquals(testParser.getCoursesAndTime().getCourseSlotsVector().get(testLab.getPreference().get(0)).getDay(), testPreference[0][0]);
					assertEquals(testParser.getCoursesAndTime().getCourseSlotsVector().get(testLab.getPreference().get(0)).getStartTime(), testPreference[0][1]);
					assertEquals(testLab.getPrefEval().get(0), testPreference[0][2]);
				} else if (testLab.getLabNumber() == "02")
				{
					assertEquals(testParser.getCoursesAndTime().getCourseSlotsVector().get(testLab.getPreference().get(0)).getDay(), testPreference[1][0]);
					assertEquals(testParser.getCoursesAndTime().getCourseSlotsVector().get(testLab.getPreference().get(0)).getStartTime(), testPreference[1][1]);
					assertEquals(testLab.getPrefEval().get(0), testPreference[1][2]);
					assertEquals(testParser.getCoursesAndTime().getCourseSlotsVector().get(testLab.getPreference().get(1)).getDay(), testPreference[2][0]);
					assertEquals(testParser.getCoursesAndTime().getCourseSlotsVector().get(testLab.getPreference().get(1)).getStartTime(), testPreference[2][1]);
					assertEquals(testLab.getPrefEval().get(1), testPreference[2][2]);
				}
			}//end if 433
			
			if(testLab.getCourseNumber() == "567") assertEquals(testLab.getNonCompatibleCourses(), testNotCompatible[3]);			
		}//end for loop		
	}
	
	//make sure the parser read things in properly
		@Test
		public void testParseDocumentLabSlots() {
		
			//labs
			testParseDocumentLoadExample();
			
			boolean testIsNotLecture = false;
			String [] testDay = {"MO","TU","FR","TU"};
			String [] testStartTime = {"8:00", "10:00","10:00", "18:00"};
			Integer [] testCourseMax = {4,2,2,2};
			Integer [] testCourseMin = {2,1,1,1};
			
			
			for(int i = 0; i < testParser.getCoursesAndTime().getLabSlotsVector().size();i++)
			{
				TimeSlot testLabSlot = testParser.getCoursesAndTime().getLabSlotsVector().get(i);
				assertEquals(testLabSlot.isLectureSlot(), testIsNotLecture);
				assertEquals(testLabSlot.getDay(), testDay[i]);
				assertEquals(testLabSlot.getStartTime(), testStartTime[i]);
				assertTrue(testLabSlot.getLabMax() == testCourseMax[i]);
				assertTrue(testLabSlot.getLabMin() == testCourseMin[i]);
						
			}//end for loop		
		}
		
		@Test
		public void testParseDocumentLectureSlots() {
		
			//labs
			testParseDocumentLoadExample();
			
			boolean testIsNotLecture = true;
			String [] testDay = {"MO","MO","TU","TU"};
			String [] testStartTime = {"8:00", "9:00","9:30", "18:00"};
			Integer [] testCourseMax = {3,3,2,1};
			Integer [] testCourseMin = {2,2,1,1};
			
			
			for(int i = 0; i < testParser.getCoursesAndTime().getCourseSlotsVector().size();i++)
			{
				TimeSlot testCourseSlot = testParser.getCoursesAndTime().getCourseSlotsVector().get(i);
				assertEquals(testCourseSlot.isLectureSlot(), testIsNotLecture);
				assertEquals(testCourseSlot.getDay(), testDay[i]);
				assertEquals(testCourseSlot.getStartTime(), testStartTime[i]);
				assertTrue(testCourseSlot.getCourseMax() == testCourseMax[i]);
				assertTrue(testCourseSlot.getCourseMin() == testCourseMin[i]);
						
			}//end for loop		
		}
/*	
	//test parser with a longer example file
	@Test
	public void testParseDocumentLongExample1() {
		String testString = new String("L:\\Java Projects - Eclipse\\workspace\\CourseScheduler\\src\\courseSchedulerCore\\exampleLong1.txt");		
		try {
			testParser = new Parser();
		testParser.getCoursesAndTime().parseDocument(testString);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("threw an exception");
		}
	}
	
	//test parser with another long example file
	@Test
	public void testParseDocumentLongExample2() {
		String testString = new String("L:\\Java Projects - Eclipse\\workspace\\CourseScheduler\\src\\courseSchedulerCore\\exampleLong2.txt");		
		try {
			testParser = new Parser();
			testParser.getCoursesAndTime().parseDocument(testString);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail("threw an exception");
		}
	}
*/
}
