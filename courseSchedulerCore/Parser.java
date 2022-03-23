package courseSchedulerCore;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
 
 
/**
 * Created by Gorman on 2015-11-23.
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
  
  
/**
 * Created by Gorman on 2015-11-23.
 */
public class Parser {
	
	private boolean debug = false;
	private final boolean isCourse = true;
	private String line; 
  
	private CourseAndTimeSlotsData courseAndTime = null;
     
    int found313 = -1;
    int found413 = -1;
    
	boolean found313Bool = false;
    boolean found413Bool = false;

    int index813 = -1;
    int index913 = -1;
  
    public Parser()
    {
        //default constructor
    }
  
    public void parseDocument(String args) throws IOException
    {
    	courseAndTime = new CourseAndTimeSlotsData();
        FileReader file = new FileReader(args);
        BufferedReader buffer = new BufferedReader(file);
        line = null;
        line = buffer.readLine();
  
  
        if(line == null)
        {
            System.out.println("Empty input file");
            System.exit(0);
        }
  
        System.out.println("Reading file");
  
        while (line != null)
        {
            /*-------------------------------------------------------------------------- PARSING NAME ----------------------------------------------------------------------*/
            if(line.equals("Name:"))
            {
                //System.out.println("Name");
                while(!line.equals(""))
                {
                    line = buffer.readLine();
                    //System.out.println("\t" + line);
                }
            }
  
            /*-------------------------------------- PARSING COURSE SLOTS INTO AN ARRAY ------------------------------------------------------------------------*/
            else if(line.equals("Course slots:"))
            {
                parseCourseTimeSlots(buffer);
            }  
            /*----------------------------------------------- PARSING LAB SLOTS INTO AN ARRAY ------------------------------------------------------------------*/
            else if(line.equals("Lab slots:"))
            {
            	parseLabTimeSlots(buffer);
            }
            /*--------------------------------------------- PARSING COURSES ---------------------------------------------------------------------------------*/
            else if(line.equals("Courses:"))
            {
               parseCourses(buffer);
            }
            /*----------------------------------------------- PARSING LABS -----------------------------------------------------------------------------*/
            else if(line.equals("Labs:"))
            {
                parseLabs(buffer);
            }
    /*------------------------------------------------------- PARSING NON COMPATIBLES ------------------------------------------------------*/
            else if(line.equals("Not compatible:"))
            {
                parseNotCompatibles(buffer);
            }  
            /*-------------------------------------------------- PARSING UNWANTED --------------------------------------------------------*/
            else if(line.equals("Unwanted:"))
            {
                parseUnwanted(buffer);
            }
            /*----------------------------------------------------- PARSING PREFERENCES -------------------------------------------------------- */
            else if(line.equals("Preferences:"))
            {
                parsePreferences(buffer);
            }
            //-------------------------------------------------------------- PARSING PAIRS ---------------------------------------------------------------------//
            else if(line.equals("Pair:"))
            {
               parsePairs(buffer);
            }
            else if(line.equals("Partial assignments:"))
            {
                parsePartialAssignments(buffer);
            }
            line = buffer.readLine();
        }
        
  
        //need to add the 813/913 special case courses
        if(found313Bool)
        {
        	case313Add813();
        }

        if(found413Bool)
        {
        	case413Add913();
        }
         
        file.close();
        buffer.close();
    }
    
    private void sortCourses()
    {   
        for(int i = 0; i < courseAndTime.getCoursesVector().size(); i++)
            for(int j = 1; j < (courseAndTime.getCoursesVector().size()-i); j++)
                if(courseAndTime.getCoursesVector().get(j-1).getDepartment().compareTo(courseAndTime.getCoursesVector().get(j).getDepartment()) > 0 || ((courseAndTime.getCoursesVector().get(j-1).getDepartment().compareTo(courseAndTime.getCoursesVector().get(j).getDepartment()) == 0) && ((Integer.parseInt(courseAndTime.getCoursesVector().get(j-1).getCourseNumber())) > (Integer.parseInt(courseAndTime.getCoursesVector().get(j).getCourseNumber())))))
                    Collections.swap(courseAndTime.getCoursesVector(), j, j-1); 
    }
 //-------------------------------------------------------------------------------------------------------------------   
    //Refactoring! Extract Method
    //---------------------------------------------------------------------------------------------------------
    private void parseCourseTimeSlots(BufferedReader buffer) throws IOException
    {
    	//System.out.println("Course slots");
        while(!line.equals(""))
        {
        	List<String> splitList = readLineAndSplitAtComma(buffer);
        	createTimeSlot(splitList, true); //true because isCourse
        }
    }//end method
    
    private void parseLabTimeSlots(BufferedReader buffer) throws IOException
    {
    	//System.out.println("Lab slots");
        while(!line.equals(""))
        {
        	List<String> splitList = readLineAndSplitAtComma(buffer);
        	createTimeSlot(splitList, false); //false because NOT isCourse
        }
    }//end method
    
    private void parseCourses(BufferedReader buffer) throws IOException
    {
    	 //System.out.println("Courses");
        while(!line.equals(""))
        {
            List<String> splitList = readLineAndSplitAtSpaceRemovePlusSign(buffer);
            if(splitList.size() > 1)
            {
                Courses courses = new Courses(splitList.get(0),splitList.get(1),splitList.get(3));
                courseAndTime.getCoursesVector().add(courses);
                 
                if(splitList.get(1).matches("313") && splitList.get(0).matches("CPSC"))
					found313Bool = true;
					
                else if (splitList.get(1).matches("413") && splitList.get(0).matches("CPSC"))
					found413Bool = true;
            }
        }
        sortCourses();
        
        if (found313Bool) found313 = findIndexToACourse("CPSC", "313", null); //null because lecture number is irrelevant in this case
        if (found413Bool) found413 = findIndexToACourse("CPSC", "413", null); //null because lecture number is irrelevant in this case
    }//end method
    
    private void parseLabs(BufferedReader buffer) throws IOException
    {
        while(!line.equals(""))
        {
        	List<String> splitList = readLineAndSplitAtSpaceRemovePlusSign(buffer);
            if(splitList.size() == 6)   //labs with a specific course
            {
                String dept = splitList.get(0);
                String courseNum = splitList.get(1);
                String lecNum = splitList.get(3);
                String labNum = splitList.get(5);
                //iterate through courses to find corresponding course
                int x = findIndexToACourse(dept, courseNum, lecNum);
                if(x != -1) {
                	Labs labs = new Labs(dept,courseNum,lecNum,labNum);
                	courseAndTime.getLabsVector().add(labs);
                	courseAndTime.getCoursesVector().get(x).addLabIndex(courseAndTime.getLabsVector().size()-1);	
                }
            }else if(splitList.size() == 4)  //tutorial is open to all lectures
            {
                String dept = splitList.get(0);
                String courseNum = splitList.get(1);
                String labNum = splitList.get(3);
                //String lecNum = course.getLectureNumber();
                Labs labs = new Labs(dept,courseNum,"",labNum);
                courseAndTime.getLabsVector().add(labs);
                for(int i = 0; i < courseAndTime.getCoursesVector().size(); i++)
                {
                    Courses course = courseAndTime.getCoursesVector().get(i);

                    if(dept.equals(course.getDepartment()) && courseNum.equals(course.getCourseNumber()))
                    {
                        courseAndTime.getCoursesVector().get(i).addLabIndex(courseAndTime.getLabsVector().size()-1);
                    }
                }
            }
        }
    }//end method
    
    private void parseNotCompatibles(BufferedReader buffer) throws IOException
    {
    	//System.out.println("Not compatible");
        while(!line.equals(""))
        {
            List<String> splitList = readLineAndSplitAtCommaRemovePlusSign(buffer);

            if(splitList.size()>1)  //list must contain something
            {
                //get first
                String nonComp1 = splitList.get(0).trim();
                String nonComp2 = splitList.get(1).trim();
                List<String> splitNonComp1 = Arrays.asList(nonComp1.split(" "));    //split first half
                List<String> splitNonComp2 = Arrays.asList(nonComp2.split(" "));    //split second half

                int indexOfFirstNonComp = -1;
                int indexOfSecondNonComp = -1;

                //both are courses
                if(splitNonComp1.get(splitNonComp1.size()-2).equals("LEC") && splitNonComp2.get(splitNonComp2.size()-2).equals("LEC"))
                {
                    //first course
                    String dept1 = splitNonComp1.get(0);
                    String course1 = splitNonComp1.get(1);
                    String lecNum1 = splitNonComp1.get(3);
                    indexOfFirstNonComp = findIndexToACourse(dept1, course1, lecNum1);

                    //second course
                    String dept2 = splitNonComp2.get(0);
                    String course2 = splitNonComp2.get(1);
                    String lecNum2 = splitNonComp2.get(3);
                    indexOfSecondNonComp = findIndexToACourse(dept2, course2, lecNum2);

                    courseAndTime.getCoursesVector().get(indexOfFirstNonComp).addNonCompatibleCourse(indexOfSecondNonComp);  //add the index of second lab to non compatible array of first lab
                    courseAndTime.getCoursesVector().get(indexOfSecondNonComp).addNonCompatibleCourse(indexOfFirstNonComp);  //and vice versa
                }//if both courses

                //both are labs
                else if ((splitNonComp1.get(splitNonComp1.size()-2).equals("TUT") || splitNonComp1.get(splitNonComp1.size()-2).equals("LAB")) && (splitNonComp2.get(splitNonComp2.size()-2).equals("TUT") || splitNonComp2.get(splitNonComp2.size()-2).equals("LAB")))
                {

                    //first lab
                    String dept1 = splitNonComp1.get(0);
                    String course1 = splitNonComp1.get(1);
                    String lecNum1 = "";
                    String labNum1 = "";

                    if(splitNonComp1.size() == 6) {
                        lecNum1 = splitNonComp1.get(3);
                        labNum1 = splitNonComp1.get(5);
                    }
                    else if(splitNonComp1.size() == 4)
                    {
                        lecNum1 = "";
                        labNum1 = splitNonComp1.get(3);
                    }

                    //second lab
                    String dept2 = splitNonComp2.get(0);
                    String course2 = splitNonComp2.get(1);
                    String lecNum2 = "";
                    String labNum2 = "";

                    if(splitNonComp2.size() == 6) {
                        lecNum2 = splitNonComp2.get(3);
                        labNum2 = splitNonComp2.get(5);
                    }
                    else if(splitNonComp2.size() == 4)
                    {
                        lecNum2 = "";
                        labNum2 = splitNonComp2.get(3);
                    }

                    indexOfFirstNonComp = findIndexToALab(dept1, course1, lecNum1, labNum1);
                    indexOfSecondNonComp = findIndexToALab(dept2, course2, lecNum2, labNum2);
                    
                    /*
                    for(int i = 0; i < courseAndTime.getLabsVector().size(); i++)
                    {
                        Labs lab = courseAndTime.getLabsVector().get(i);
                        //find first lab
                        if(lab.getDepartment().equals(dept1) && lab.getCourseNumber().equals(course1) && lab.getLectureNumber().equals(lecNum1) && lab.getLabNumber().equals(labNum1))
                        {
                            indexOfFirstNonComp = i;
                            //System.out.println(indexOfFirstNonComp);
                        }
                        //find second lab
                        else if(lab.getDepartment().equals(dept2) && lab.getCourseNumber().equals(course2) && lab.getLectureNumber().equals(lecNum2) && lab.getLabNumber().equals(labNum2))
                        {
                            indexOfSecondNonComp = i;
                            //System.out.println(indexOfSecondNonComp);
                        }
                    }*/
                    courseAndTime.getLabsVector().get(indexOfFirstNonComp).addNonCompatibleLab(indexOfSecondNonComp);  //add the index of second lab to non compatible array of first lab
                    courseAndTime.getLabsVector().get(indexOfSecondNonComp).addNonCompatibleLab(indexOfFirstNonComp);  //and vice versa
                }//if both labs
                //first is course, second is lab
                else if(splitNonComp1.get(splitNonComp1.size()-2).equals("LEC") && (splitNonComp2.get(splitNonComp2.size()-2).equals("TUT") || splitNonComp2.get(splitNonComp2.size()-2).equals("LAB")))
                {
                    //first course
                    String dept1 = splitNonComp1.get(0);
                    String course1 = splitNonComp1.get(1);
                    String lecNum1 = splitNonComp1.get(3);

                    //second lab
                    String dept2 = splitNonComp2.get(0);
                    String course2 = splitNonComp2.get(1);
                    String lecNum2 = "";
                    String labNum2 = "";

                    if(splitNonComp2.size() == 6) {
                        lecNum2 = splitNonComp2.get(3);
                        labNum2 = splitNonComp2.get(5);
                    }
                    else if(splitNonComp2.size() == 4)
                    {
                        lecNum2 = "";
                        labNum2 = splitNonComp2.get(3);
                    }
                    //find course
                    indexOfFirstNonComp = findIndexToACourse(dept1, course1, lecNum1);
                   /* for(int i = 0; i < courseAndTime.getCoursesVector().size(); i++)
                    {
                        Courses course = courseAndTime.getCoursesVector().get(i);

                        if(course.getDepartment().equals(dept1) && course.getCourseNumber().equals(course1) && course.getLectureNumber().equals(lecNum1))
                        {
                            indexOfFirstNonComp = i;
                            //System.out.println(indexOfFirstNonComp);
                        }
                    }*/
                    //find lab
                    indexOfSecondNonComp = findIndexToALab(dept2, course2, lecNum2, labNum2);
                    /*for(int j = 0; j < courseAndTime.getLabsVector().size(); j++)
                    {
                        Labs lab = courseAndTime.getLabsVector().get(j);

                        if(lab.getDepartment().equals(dept2) && lab.getCourseNumber().equals(course2) && lab.getLectureNumber().equals(lecNum2) && lab.getLabNumber().equals(labNum2))
                        {
                        indexOfSecondNonComp = j;
                        //System.out.println(indexOfSecondNonComp);
                        }
                    }*/
                    courseAndTime.getCoursesVector().get(indexOfFirstNonComp).addNonCompatibleLab(indexOfSecondNonComp);   //add non compatible lab index to course
                    courseAndTime.getLabsVector().get(indexOfSecondNonComp).addNonCompatibleCourse(indexOfFirstNonComp);   //add non compatible course index to lab
                }
                //first is lab, second is course
                else if((splitNonComp1.get(splitNonComp1.size()-2).equals("TUT") || splitNonComp1.get(splitNonComp1.size()-2).equals("LAB")) && splitNonComp2.get(splitNonComp2.size()-2).equals("LEC"))   //first is lab, second is course
                {
                 //first lab
                    String dept1 = splitNonComp1.get(0);
                    String course1 = splitNonComp1.get(1);
                    String lecNum1 = "";
                    String labNum1 = "";

                    if(splitNonComp1.size() == 6) {
                        lecNum1 = splitNonComp1.get(3);
                        labNum1 = splitNonComp1.get(5);
                    }
                    else if(splitNonComp1.size() == 4)
                    {
                        lecNum1 = "";
                        labNum1 = splitNonComp1.get(3);
                    }

                    //second course
                    String dept2 = splitNonComp2.get(0);
                    String course2 = splitNonComp2.get(1);
                    String lecNum2 = splitNonComp2.get(3);

                    //find first lab
                    indexOfFirstNonComp = findIndexToALab(dept1, course1, lecNum1, labNum1);
                    //find second course
                    indexOfSecondNonComp = findIndexToACourse(dept2, course2, lecNum2);
                    
                    courseAndTime.getCoursesVector().get(indexOfSecondNonComp).addNonCompatibleLab(indexOfFirstNonComp);   //add non compat lab to course
                    courseAndTime.getLabsVector().get(indexOfFirstNonComp).addNonCompatibleCourse(indexOfSecondNonComp);   //add non compat course to lab
                }
            }
            //System.out.println("\t" + line);
        }
    }//end method
    
    private void parseUnwanted(BufferedReader buffer) throws IOException
    {
    	 //System.out.println("Unwanted");
        while(!line.equals(""))
        {
            List<String> splitList = readLineAndSplitAtCommaRemovePlusSign(buffer);

            //list has gotta have something
            if(splitList.size()>1)
            {
                String clas = splitList.get(0).trim();
                String day = splitList.get(1).trim();
                String time = splitList.get(2).trim();

                List<String> splitClass = Arrays.asList(clas.split(" "));

                String dept = null;
                String courseNum = null;
                String lecNum = null;
                String labNum = null;

                int courseIndex = -1;
                int timeIndex = -1;

                //if it is a lecture
                if(splitClass.get(splitClass.size()-2).equals("LEC"))
                {
                    dept = splitClass.get(0);
                    courseNum = splitClass.get(1);
                    lecNum = splitClass.get(3);

                    //find the lecture
                    courseIndex = findIndexToACourse(dept, courseNum, lecNum);

                    //now find the time
                    timeIndex = findIndexToATimeSlot(day, time, isCourse);
                    courseAndTime.getCoursesVector().get(courseIndex).addUnwanted(timeIndex);
                }

                //otherwise it is a lab
                else
                {
                    dept = splitClass.get(0);
                    courseNum = splitClass.get(1);

                    if(splitClass.size() == 6) {
                        lecNum = splitClass.get(3);
                        labNum = splitClass.get(5);
                    }
                    else if(splitClass.size() == 4)
                    {
                        lecNum = "";
                        labNum = splitClass.get(3);
                    }
                    //find the lab
                    courseIndex = findIndexToALab(dept, courseNum, lecNum, labNum);

                    //now find the time
                    timeIndex = findIndexToATimeSlot(day, time, !isCourse); //false because lab
                    courseAndTime.getLabsVector().get(courseIndex).addUnwanted(timeIndex);
                }
            }
        }
    }//end method
    
    private void parsePreferences(BufferedReader buffer) throws IOException
    {
    	 //System.out.println("Preferences");
        while(!line.equals(""))
        {
            List<String> splitList = readLineAndSplitAtCommaRemovePlusSign(buffer);

            if(splitList.size()>1)
            {
                String day = splitList.get(0).trim();
                String time = splitList.get(1).trim();
                String clas = splitList.get(2).trim();
                int eval = Integer.parseInt(splitList.get(3).trim());

                List<String> splitClass = Arrays.asList(clas.split(" "));
                String dept = null;
                String courseNum = null;
                String lecNum = null;
                String labNum = null;

                int courseIndex = -1;
                int timeIndex = -1;

                //if its a lecture. Find lecture
                if(splitClass.get(splitClass.size()-2).equals("LEC"))
                {                       
                	dept = splitClass.get(0);
                    courseNum = splitClass.get(1);
                    lecNum = splitClass.get(3);
                	courseIndex = findIndexToACourse(dept, courseNum, lecNum);
                	timeIndex = findIndexToATimeSlot(day, time, isCourse);

                    if (timeIndex > -1)
                    {
                        courseAndTime.getCoursesVector().get(courseIndex).addPreference(timeIndex);
                        courseAndTime.getCoursesVector().get(courseIndex).addPrefEval(eval);
                    }
                     
                    if (timeIndex == -1)
                        if (debug) System.out.println("TIME SLOT FOR PREFERENCE BY A COURSE DOES NOT EXIST");
                }

                //otherwise its a lab
                else
                {
                    dept = splitClass.get(0);
                    courseNum = splitClass.get(1);

                    if(splitClass.size() == 6) {
                        lecNum = splitClass.get(3);
                        labNum = splitClass.get(5);
                    }
                    else if(splitClass.size() == 4)
                    {
                        lecNum = "";
                        labNum = splitClass.get(3);
                    }
                    //find the lab
                 	courseIndex = findIndexToALab(dept, courseNum, lecNum, labNum);
                    //now find the time
                	timeIndex = findIndexToATimeSlot(day, time, !isCourse);
                    if (timeIndex > -1)
                    {
                        courseAndTime.getLabsVector().get(courseIndex).addPreference(timeIndex);
                        courseAndTime.getLabsVector().get(courseIndex).addPrefEval(eval);
                    }                    
                    if (timeIndex == -1)
                    	if(debug) System.out.println("TIME PREFERENCE FOR A LAB DOES NOT EXIST");
                }
            }
        }
    }//end method
    
    private void parsePairs(BufferedReader buffer) throws IOException
    {
    	//System.out.println("Pair");
        while(!line.equals(""))
        {
            /*line = buffer.readLine();
            line = line.replaceAll(" +", " ");
            List<String> splitList = Arrays.asList(line.split(","));*/
            List<String> splitList = readLineAndSplitAtCommaRemovePlusSign(buffer);

            if(splitList.size()>1)  //list must contain something
            {
                //get first
                String nonComp1 = splitList.get(0).trim();
                String nonComp2 = splitList.get(1).trim();
                List<String> splitNonComp1 = Arrays.asList(nonComp1.split(" "));    //split first half
                List<String> splitNonComp2 = Arrays.asList(nonComp2.split(" "));    //split second half

                int indexOfFirstNonComp = -1;
                int indexOfSecondNonComp = -1;


                //both are courses
                if(splitNonComp1.get(splitNonComp1.size()-2).equals("LEC") && splitNonComp2.get(splitNonComp2.size()-2).equals("LEC"))
                {
                    //first course
                    String dept1 = splitNonComp1.get(0);
                    String course1 = splitNonComp1.get(1);
                    String lecNum1 = splitNonComp1.get(3);
                    indexOfFirstNonComp = findIndexToACourse(dept1, course1, lecNum1);

                    //second course
                    String dept2 = splitNonComp2.get(0);
                    String course2 = splitNonComp2.get(1);
                    String lecNum2 = splitNonComp2.get(3);
                    indexOfSecondNonComp = findIndexToACourse(dept2, course2, lecNum2);

                    courseAndTime.getCoursesVector().get(indexOfFirstNonComp).addCoursePair(indexOfSecondNonComp);  //add the index of second lab to non compatible array of first lab
                    courseAndTime.getCoursesVector().get(indexOfSecondNonComp).addCoursePair(indexOfFirstNonComp);  //and vice versa
                }

                //both are labs
                else if ((splitNonComp1.get(splitNonComp1.size()-2).equals("TUT") || splitNonComp1.get(splitNonComp1.size()-2).equals("LAB")) && (splitNonComp2.get(splitNonComp2.size()-2).equals("TUT") || splitNonComp2.get(splitNonComp2.size()-2).equals("LAB")))
                {

                    //first lab
                    String dept1 = splitNonComp1.get(0);
                    String course1 = splitNonComp1.get(1);
                    String lecNum1 = "";
                    String labNum1 = "";

                    if(splitNonComp1.size() == 6) {
                        lecNum1 = splitNonComp1.get(3);
                        labNum1 = splitNonComp1.get(5);
                    }
                    else if(splitNonComp1.size() == 4)
                    {
                        lecNum1 = "";
                        labNum1 = splitNonComp1.get(3);
                    }

                    indexOfFirstNonComp = findIndexToALab(dept1, course1, lecNum1, labNum1);
                    
                    //second lab
                    String dept2 = splitNonComp2.get(0);
                    String course2 = splitNonComp2.get(1);
                    String lecNum2 = "";
                    String labNum2 = "";

                    if(splitNonComp2.size() == 6) {
                        lecNum2 = splitNonComp2.get(3);
                        labNum2 = splitNonComp2.get(5);
                    }
                    else if(splitNonComp2.size() == 4)
                    {
                        lecNum2 = "";
                        labNum2 = splitNonComp2.get(3);
                    }
                    
                    indexOfSecondNonComp = findIndexToALab(dept2, course2, lecNum2, labNum2);

                    courseAndTime.getLabsVector().get(indexOfFirstNonComp).addLabPair(indexOfSecondNonComp);  //add the index of second lab to non compatible array of first lab
                    courseAndTime.getLabsVector().get(indexOfSecondNonComp).addLabPair(indexOfFirstNonComp);  //and vice versa

                }

                //first is course, second is lab
                else if(splitNonComp1.get(splitNonComp1.size()-2).equals("LEC") && (splitNonComp2.get(splitNonComp2.size()-2).equals("TUT") || splitNonComp2.get(splitNonComp2.size()-2).equals("LAB")))
                {

                    //first course
                    String dept1 = splitNonComp1.get(0);
                    String course1 = splitNonComp1.get(1);
                    String lecNum1 = splitNonComp1.get(3);
                    indexOfFirstNonComp = findIndexToACourse(dept1, course1, lecNum1);

                    //second lab
                    String dept2 = splitNonComp2.get(0);
                    String course2 = splitNonComp2.get(1);
                    String lecNum2 = "";
                    String labNum2 = "";

                    if(splitNonComp2.size() == 6) {
                        lecNum2 = splitNonComp2.get(3);
                        labNum2 = splitNonComp2.get(5);
                    }
                    else if(splitNonComp2.size() == 4)
                    {
                        lecNum2 = "";
                        labNum2 = splitNonComp2.get(3);
                    }

                    indexOfFirstNonComp = findIndexToALab(dept2, course2, lecNum2, labNum2);

                    courseAndTime.getCoursesVector().get(indexOfFirstNonComp).addLabPair(indexOfSecondNonComp);   //add non compatible lab index to course
                    courseAndTime.getLabsVector().get(indexOfSecondNonComp).addCoursePair(indexOfFirstNonComp);   //add non compatible course index to lab

                }

                //first is lab, second is course
                else if((splitNonComp1.get(splitNonComp1.size()-2).equals("TUT") || splitNonComp1.get(splitNonComp1.size()-2).equals("LAB")) && splitNonComp2.get(splitNonComp2.size()-2).equals("LEC"))   //first is lab, second is course
                {
                    //first lab
                    String dept1 = splitNonComp1.get(0);
                    String course1 = splitNonComp1.get(1);
                    String lecNum1 = "";
                    String labNum1 = "";

                    if(splitNonComp1.size() == 6) {
                        lecNum1 = splitNonComp1.get(3);
                        labNum1 = splitNonComp1.get(5);
                    }
                    else if(splitNonComp1.size() == 4)
                    {
                        lecNum1 = "";
                        labNum1 = splitNonComp1.get(3);
                    }
                    
                    indexOfFirstNonComp = findIndexToALab(dept1, course1, lecNum1, labNum1);

                    //second course
                    String dept2 = splitNonComp2.get(0);
                    String course2 = splitNonComp2.get(1);
                    String lecNum2 = splitNonComp2.get(3);
                    indexOfSecondNonComp = findIndexToACourse(dept2, course2, lecNum2);

                    courseAndTime.getCoursesVector().get(indexOfSecondNonComp).addLabPair(indexOfFirstNonComp);   //add non compat lab to course
                    courseAndTime.getLabsVector().get(indexOfFirstNonComp).addCoursePair(indexOfSecondNonComp);   //add non compat course to lab

                }

            }
            //System.out.println("\t" + line);
        }
    }//end method
    
    private void parsePartialAssignments(BufferedReader buffer) throws IOException
    {
    	//System.out.println("Partial Assignments");
        while(line!=null)
        {
            //line = buffer.readLine();

            List<String> splitList = readLineAndSplitAtCommaRemovePlusSign(buffer);
            if(line != null) {
                //System.out.println("\t" + line);
                //line = line.replaceAll(" +", " ");
              if(debug)  System.out.println("\t" + line);

                //List<String> splitList = Arrays.asList(line.split(","));
                if(splitList.size()>1)
                {
					String day = splitList.get(1).trim();
					String time = splitList.get(2).trim();

					List<String> splitClass = Arrays.asList(splitList.get(0).split(" "));
					String dept = splitClass.get(0).trim();
					String courseNum = splitClass.get(1).trim();
					String lecNum = null;
					String labNum = null;

					int courseIndex = -1;
					int timeIndex = -1;

					if(splitClass.get(splitClass.size()-2).equals("LEC"))
					{
						lecNum = splitClass.get(3).trim();
	                    courseIndex = findIndexToACourse(dept, courseNum, lecNum);
	                    timeIndex = findIndexToATimeSlot(day, time, isCourse);
						courseAndTime.getCoursesVector().get(courseIndex).addPartAssign(timeIndex);
					}
					else if(splitClass.size()==6)
					{
						lecNum = splitClass.get(3).trim();
						labNum = splitClass.get(5).trim();
	                    courseIndex = findIndexToALab(dept, courseNum, lecNum, labNum);
	                    timeIndex = findIndexToATimeSlot(day, time, !isCourse); //false because lab

						courseAndTime.getLabsVector().get(courseIndex).addPartAssign(timeIndex);
					}
					else if(splitClass.size() == 4)
					{//tutorial
						lecNum = "";
						labNum = splitClass.get(3).trim();

	                    courseIndex = findIndexToALab(dept, courseNum, lecNum, labNum);
	                    timeIndex = findIndexToATimeSlot(day, time, !isCourse); //false because lab

						courseAndTime.getLabsVector().get(courseIndex).addPartAssign(timeIndex);
					}
				}
			}

        }
    }//end method
    
    private void case313Add813()
    {
        Courses course813 = new Courses("CPSC","813","01");
        Courses course313 = courseAndTime.getCoursesVector().get(found313);
        index813 = courseAndTime.getCoursesVector().size() - 1;

        for (int x = 0; x < course313.getNonCompatibleCourses().size(); x++)
            course813.addNonCompatibleCourse(course313.getNonCompatibleCourses().get(x));
        for (int x = 0; x < courseAndTime.getCoursesVector().size(); x++)
            if (courseAndTime.getCoursesVector().get(x).getCourseNumber().equals("313") && courseAndTime.getCoursesVector().get(x).getDepartment().equals("CPSC"))
                course813.addNonCompatibleCourse(x);

        for (int x = 0; x < course313.getNonCompatibleLabs().size(); x++)
            course813.addNonCompatibleLab(course313.getNonCompatibleLabs().get(x));
        for (int x = 0; x < courseAndTime.getLabsVector().size(); x++)
            if (courseAndTime.getLabsVector().get(x).getCourseNumber().equals("313") && courseAndTime.getLabsVector().get(x).getDepartment().equals("CPSC"))
                course813.addNonCompatibleLab(x);

        for (int x = 0; x < course313.getUnwanted().size(); x++)
            course813.addUnwanted(course313.getUnwanted().get(x));

        for (int x = 0; x < courseAndTime.getLabSlotsVector().size(); x++)
            if (courseAndTime.getLabSlotsVector().get(x).getDay().equals("TU") && courseAndTime.getLabSlotsVector().get(x).getStartTime().equals("18:00"))
                course813.addPartAssign(x);

        courseAndTime.getCoursesVector().add(course813);
    }//end method
    
    private void case413Add913()
    {
        Courses course913 = new Courses("CPSC","913","01");   
        Courses course413 = courseAndTime.getCoursesVector().get(found413);
        index913 = courseAndTime.getCoursesVector().size() - 1;

        for (int x = 0; x < course413.getNonCompatibleCourses().size(); x++)
            course913.addNonCompatibleCourse(course413.getNonCompatibleCourses().get(x));
        for (int x = 0; x < courseAndTime.getCoursesVector().size(); x++)
            if (courseAndTime.getCoursesVector().get(x).getCourseNumber().equals("413") && courseAndTime.getCoursesVector().get(x).getDepartment().equals("CPSC"))
                course913.addNonCompatibleCourse(x);

        for (int x = 0; x < course413.getNonCompatibleLabs().size(); x++)
            course913.addNonCompatibleLab(course413.getNonCompatibleLabs().get(x));
        for (int x = 0; x < courseAndTime.getLabsVector().size(); x++)
            if (courseAndTime.getLabsVector().get(x).getCourseNumber().equals("413") && courseAndTime.getLabsVector().get(x).getDepartment().equals("CPSC"))
                course913.addNonCompatibleLab(x);

        for (int x = 0; x < course413.getUnwanted().size(); x++)
            course913.addUnwanted(course413.getUnwanted().get(x));

        for (int x = 0; x < courseAndTime.getLabSlotsVector().size(); x++)
            if (courseAndTime.getLabSlotsVector().get(x).getDay().equals("TU") && courseAndTime.getLabSlotsVector().get(x).getStartTime().equals("18:00"))
                course913.addPartAssign(x);

        courseAndTime.getCoursesVector().add(course913);
    }//end method
    
    //-----------------------------------------------------------------------------------------------------------   
    //Refactor! Duplicate Code Extraction
    //--------------------------------------------------------------------------------------------
    private List<String> readLineAndSplitAtCommaRemovePlusSign(BufferedReader buffer) throws IOException
    {
        line = buffer.readLine();
        if (line != null)
        {
        	line = line.replaceAll(" +", " ");
        	return Arrays.asList(line.split(","));
    	} else return null;
    }
    private List<String> readLineAndSplitAtComma(BufferedReader buffer) throws IOException
    {
        line = buffer.readLine();
        if (line != null)
        {
        	line = line.replaceAll(" ", "");
        	return Arrays.asList(line.split(","));
    	} else return null;
    }
    private List<String> readLineAndSplitAtSpaceRemovePlusSign(BufferedReader buffer) throws IOException
    {
        line = buffer.readLine();
        if (line != null)
        {
        	line = line.replaceAll(" +", " ");
        	return Arrays.asList(line.split(" "));
    	} else return null;
    }
    
    private void createTimeSlot(List<String> splitList, boolean isCourse)
    {
        if(splitList.size() > 1) {
	
	        //ADD A CHECK FOR DAY OR EVENING LATER
	        boolean evening;
	
	        String time = splitList.get(1);
	        time = time.replaceAll(":", "");
	
	            //System.out.println(time);
	        int timeInt = Integer.parseInt(time);
	    	if(timeInt >= 1800) evening = true;
	        else  evening = false;
	    	
	    	if (isCourse) {
		    	TimeSlot timeSlot = new TimeSlot(splitList.get(0), splitList.get(1), Integer.parseInt(splitList.get(2)), Integer.parseInt(splitList.get(3)), evening, true);
	    		courseAndTime.getCourseSlotsVector().add(timeSlot);
	    	}
	    	else
	    	{
		    	TimeSlot timeSlot = new TimeSlot(splitList.get(0), splitList.get(1), Integer.parseInt(splitList.get(2)), Integer.parseInt(splitList.get(3)), evening, false);
	    		courseAndTime.getLabSlotsVector().add(timeSlot);
	    	}
        
        }//end if
       }//end method
    
    private int findIndexToACourse(String dept, String course, String lecNum)
    {
		for (int x = 0; x < courseAndTime.getCoursesVector().size(); x++) {
			if (courseAndTime.getCoursesVector().get(x).getCourseNumber().equals(course) && courseAndTime.getCoursesVector().get(x).getDepartment().equals(dept) && ((lecNum == null) || (lecNum.equals(courseAndTime.getCoursesVector().get(x).getLectureNumber()))))
			{
                // System.out.println(x);
				return x;
			} 
		}
		return -1; //not found
    }
    
    private int findIndexToALab(String dept, String course, String lecNum, String labNum)
    {
		for (int x = 0; x < courseAndTime.getLabsVector().size(); x++) {
			if (courseAndTime.getLabsVector().get(x).getCourseNumber().equals(course) && courseAndTime.getLabsVector().get(x).getDepartment().equals(dept) && ((lecNum == null) || (lecNum.equals(courseAndTime.getLabsVector().get(x).getLectureNumber())) && ((labNum == null) || (labNum.equals(courseAndTime.getLabsVector().get(x).getLabNumber())))))
			{
				// System.out.println(x);
				return x;
			} 
		}
		return -1; //not found
    }
    
    private int findIndexToATimeSlot(String day, String time, boolean isCourseSlot)
    {
    	if (isCourseSlot)
    	{
            for (int i = 0; i < courseAndTime.getCourseSlotsVector().size(); i++)
            {
                TimeSlot courseSlots = courseAndTime.getCourseSlotsVector().get(i);
                {
                    if(courseSlots.getDay().equals(day) && courseSlots.getStartTime().equals(time))
                    {
                        //System.out.println(timeIndex);
                        return i;
                    }
                }
            }
    	}else
    	{
            for (int i = 0; i < courseAndTime.getLabSlotsVector().size(); i++)
            {
                TimeSlot labSlots = courseAndTime.getLabSlotsVector().get(i);
                {
                    if(labSlots.getDay().equals(day) && labSlots.getStartTime().equals(time))
                    {
                        //System.out.println(timeIndex);
                        return i;
                    }
                }
            }
    	}
    	return -1; //not found
    }//end method
    
    //-------------------------------------------------------------------------------------------------------------------   
    //Refactoring! Extract Class
    //---------------------------------------------------------------------------------------------------------
    public CourseAndTimeSlotsData getCoursesAndTime()
    {
    	return courseAndTime;
    }
    
}//end class
