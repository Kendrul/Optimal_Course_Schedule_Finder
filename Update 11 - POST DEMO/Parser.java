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
  
    public ArrayList<Courses> coursesVector = new ArrayList<>();
    public ArrayList<Labs> labsVector = new ArrayList<>();
  
    public ArrayList<TimeSlot> courseSlotsVector = new ArrayList<>();
    public ArrayList<TimeSlot> labSlotsVector = new ArrayList<>();
     
    public double notPairPenalty = 0;
    public double courseMinPenalty = 0;
    public double labMinPenalty = 0;
    public double sectionDiffPenalty = 0;
     
    public double w_minfilled = 1;
    public double w_pref = 1;
    public double w_pair = 1;
    public double w_secdif = 1;
     
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
        FileReader file = new FileReader(args);
        BufferedReader buffer = new BufferedReader(file);
        String line = null;
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
                //System.out.println("Course slots");
                while(!line.equals(""))
                {
                    line = buffer.readLine();
                    line = line.replaceAll(" ","");
                    //System.out.println("\t" + line);
  
  
                    List<String> splitList = Arrays.asList(line.split(","));
                    if(splitList.size() > 1) {
  
                        //ADD A CHECK FOR DAY OR EVENING LATER
                        boolean evening;
  
                        String time = splitList.get(1);
                        time = time.replaceAll(":", "");
  
                        //System.out.println(time);
                        int timeInt = Integer.parseInt(time);
  
                        if(timeInt >= 1800)
                        {
                            evening = true;
                        }
                        else
                        {
                            evening = false;
                        }
  
                        TimeSlot timeSlot = new TimeSlot(splitList.get(0), splitList.get(1), Integer.parseInt(splitList.get(2)), Integer.parseInt(splitList.get(3)), evening, true);
                        courseSlotsVector.add(timeSlot);
                    }
                }
            }
  
            /*----------------------------------------------- PARSING LAB SLOTS INTO AN ARRAY ------------------------------------------------------------------*/
            else if(line.equals("Lab slots:"))
            {
                //System.out.println("Lab slots");
                while(!line.equals(""))
                {
                    line = buffer.readLine();
                    line = line.replaceAll(" +", "");
                    //System.out.println("\t" + line);
  
                    List<String> splitList = Arrays.asList(line.split(","));
                    if(splitList.size() > 1) {
  
                        boolean evening;
  
                        String time = splitList.get(1);
                        time = time.replaceAll(":", "");
  
                        //System.out.println(time);
                        int timeInt = Integer.parseInt(time);
                        //System.out.println(timeInt);
  
                        if(timeInt >= 1800)
                        {
                            evening = true;
                        }
                        else
                        {
                            evening = false;
                        }
  
                        //System.out.println(evening);
  
                        //ADD A CHECK FOR DAY OR EVENING LATER
                        TimeSlot timeSlot = new TimeSlot(splitList.get(0), splitList.get(1), Integer.parseInt(splitList.get(2)), Integer.parseInt(splitList.get(3)), evening, false);
                        labSlotsVector.add(timeSlot);
                    }
  
  
                }
            }
  
            /*--------------------------------------------- PARSING COURSES ---------------------------------------------------------------------------------*/
            else if(line.equals("Courses:"))
            {
                //System.out.println("Courses");
                while(!line.equals(""))
                {
                    line = buffer.readLine();
                    line = line.replaceAll(" +", " ");
                    //System.out.println("\t" + line);
  
  
                    List<String> splitList = Arrays.asList(line.split(" "));
                    if(splitList.size() > 1)
                    {
                        Courses courses = new Courses(splitList.get(0),splitList.get(1),splitList.get(3));
                        coursesVector.add(courses);
                         
                        if(splitList.get(1).matches("313") && splitList.get(0).matches("CPSC"))
							found313Bool = true;
							
                        else if (splitList.get(1).matches("413") && splitList.get(0).matches("CPSC"))
							found413Bool = true;
                    }
  
                }
                sortCourses();
                
                if (found313Bool)
					for (int x = 0; x < coursesVector.size(); x++)
						if (coursesVector.get(x).getCourseNumber().equals("313") && coursesVector.get(x).getDepartment().equals("CPSC"))
						{
							found313 = x;
							break;
						}
                
                if (found413Bool)
					for (int x = 0; x < coursesVector.size(); x++)
						if (coursesVector.get(x).getCourseNumber().equals("413") && coursesVector.get(x).getDepartment().equals("CPSC"))
						{
							found413 = x;
							break;
						}                
                
            }
  
            /*----------------------------------------------- PARSING LABS -----------------------------------------------------------------------------*/
            else if(line.equals("Labs:"))
            {
                //System.out.println("Labs");
                while(!line.equals(""))
                {
                    line = buffer.readLine();
                    line = line.replaceAll(" +", " ");
                    //System.out.println("\t" + line);
  
                    List<String> splitList = Arrays.asList(line.split(" "));
                    if(splitList.size() == 6)   //labs with a specific course
                    {
                        String dept = splitList.get(0);
                        String courseNum = splitList.get(1);
                        String lecNum = splitList.get(3);
                        String labNum = splitList.get(5);
                        //iterate through courses to find corresponding course
                        for(int i = 0; i < coursesVector.size(); i++)
                        {
                            Courses course = coursesVector.get(i);
  
                            if(dept.equals(course.getDepartment()) && courseNum.equals(course.getCourseNumber()) && lecNum.equals(course.getLectureNumber()))
                            {
                                Labs labs = new Labs(dept,courseNum,lecNum,labNum);
                                labsVector.add(labs);
                                coursesVector.get(i).addLabIndex(labsVector.size()-1);
                            }
                        }
                    }
                    else if(splitList.size() == 4)  //tutorial is open to all lectures
                    {
                        String dept = splitList.get(0);
                        String courseNum = splitList.get(1);
                        String labNum = splitList.get(3);
                        //String lecNum = course.getLectureNumber();
                        Labs labs = new Labs(dept,courseNum,"",labNum);
                        labsVector.add(labs);
                        for(int i = 0; i < coursesVector.size(); i++)
                        {
                            Courses course = coursesVector.get(i);
  
                            if(dept.equals(course.getDepartment()) && courseNum.equals(course.getCourseNumber()))
                            {
  
                                coursesVector.get(i).addLabIndex(labsVector.size()-1);
                            }
                        }
                    }
                }
            }
  
    /*------------------------------------------------------- PARSING NON COMPATIBLES ------------------------------------------------------*/
            else if(line.equals("Not compatible:"))
            {
                //System.out.println("Not compatible");
                while(!line.equals(""))
                {
                    line = buffer.readLine();
                    line = line.replaceAll(" +", " ");
                    List<String> splitList = Arrays.asList(line.split(","));
  
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
  
  
                            //second course
                            String dept2 = splitNonComp2.get(0);
                            String course2 = splitNonComp2.get(1);
                            String lecNum2 = splitNonComp2.get(3);
  
                            for(int i = 0; i < coursesVector.size(); i++)
                            {
                                Courses course = coursesVector.get(i);
  
                                //find first course
                                if(course.getDepartment().equals(dept1) && course.getCourseNumber().equals(course1) && course.getLectureNumber().equals(lecNum1))
                                {
                                    indexOfFirstNonComp = i;
                                    //System.out.println(indexOfFirstNonComp);
                                }
                                //find second course
                                else if(course.getDepartment().equals(dept2) && course.getCourseNumber().equals(course2) && course.getLectureNumber().equals(lecNum2))
                                {
                                    indexOfSecondNonComp = i;
                                    //System.out.println(indexOfSecondNonComp);
                                }
  
                            }
  
                            coursesVector.get(indexOfFirstNonComp).addNonCompatibleCourse(indexOfSecondNonComp);  //add the index of second lab to non compatible array of first lab
                            coursesVector.get(indexOfSecondNonComp).addNonCompatibleCourse(indexOfFirstNonComp);  //and vice versa
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
  
  
                            for(int i = 0; i < labsVector.size(); i++)
                            {
                                Labs lab = labsVector.get(i);
  
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
  
                            }
  
                            labsVector.get(indexOfFirstNonComp).addNonCompatibleLab(indexOfSecondNonComp);  //add the index of second lab to non compatible array of first lab
                            labsVector.get(indexOfSecondNonComp).addNonCompatibleLab(indexOfFirstNonComp);  //and vice versa
  
                        }
  
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
                            for(int i = 0; i < coursesVector.size(); i++)
                            {
                                Courses course = coursesVector.get(i);
  
                                if(course.getDepartment().equals(dept1) && course.getCourseNumber().equals(course1) && course.getLectureNumber().equals(lecNum1))
                                {
                                    indexOfFirstNonComp = i;
                                    //System.out.println(indexOfFirstNonComp);
                                }
                            }
  
                            //find lab
                            for(int j = 0; j < labsVector.size(); j++)
                            {
                                Labs lab = labsVector.get(j);
  
                                if(lab.getDepartment().equals(dept2) && lab.getCourseNumber().equals(course2) && lab.getLectureNumber().equals(lecNum2) && lab.getLabNumber().equals(labNum2))
                                {
                                indexOfSecondNonComp = j;
                                //System.out.println(indexOfSecondNonComp);
                                }
  
                            }
  
                            coursesVector.get(indexOfFirstNonComp).addNonCompatibleLab(indexOfSecondNonComp);   //add non compatible lab index to course
                            labsVector.get(indexOfSecondNonComp).addNonCompatibleCourse(indexOfFirstNonComp);   //add non compatible course index to lab
  
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
                            for(int i = 0; i < labsVector.size(); i++)
                            {
                                Labs lab = labsVector.get(i);
  
                                if(lab.getDepartment().equals(dept1) && lab.getCourseNumber().equals(course1) && lab.getLectureNumber().equals(lecNum1) && lab.getLabNumber().equals(labNum1))
                                {
                                    indexOfFirstNonComp = i;
                                    //System.out.println(indexOfFirstNonComp);
                                }
                            }
  
                            //find second course
                            for(int j = 0; j < coursesVector.size(); j++)
                            {
                                Courses course = coursesVector.get(j);
                                if(course.getDepartment().equals(dept2) && course.getCourseNumber().equals(course2) && course.getLectureNumber().equals(lecNum2))
                                {
                                    indexOfSecondNonComp = j;
                                    //System.out.println(indexOfFirstNonComp);
                                }
                            }
  
                            coursesVector.get(indexOfSecondNonComp).addNonCompatibleLab(indexOfFirstNonComp);   //add non compat lab to course
                            labsVector.get(indexOfFirstNonComp).addNonCompatibleCourse(indexOfSecondNonComp);   //add non compat course to lab
  
                        }
  
                    }
                    //System.out.println("\t" + line);
                }
            }
  
            /*-------------------------------------------------- PARSING UNWANTED --------------------------------------------------------*/
            else if(line.equals("Unwanted:"))
            {
                 //System.out.println("Unwanted");
                while(!line.equals(""))
                {
                    line = buffer.readLine();
                    line = line.replaceAll(" +", " ");
                   // System.out.println("\t" + line);
  
                    List<String> splitList = Arrays.asList(line.split(","));
  
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
                            for(int i = 0; i < coursesVector.size(); i++)
                            {
                                Courses course = coursesVector.get(i);
  
                                if(course.getDepartment().equals(dept) && course.getCourseNumber().equals(courseNum) && course.getLectureNumber().equals(lecNum))
                                {
                                    courseIndex = i;
                                   // System.out.println(courseIndex);
                                }
                            }
  
                            //now find the time
                            for (int i = 0; i < courseSlotsVector.size(); i++)
                            {
                                TimeSlot courseSlots = courseSlotsVector.get(i);
                                {
                                    if(courseSlots.getDay().equals(day) && courseSlots.getStartTime().equals(time))
                                    {
                                        timeIndex = i;
                                        //System.out.println(timeIndex);
                                    }
  
                                }
                            }
  
                            coursesVector.get(courseIndex).addUnwanted(timeIndex);
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
                            for(int i = 0; i < labsVector.size(); i++)
                            {
                                Labs lab = labsVector.get(i);
  
                                if(lab.getDepartment().equals(dept) && lab.getCourseNumber().equals(courseNum) && lab.getLectureNumber().equals(lecNum) && lab.getLabNumber().equals(labNum))
                                {
                                    courseIndex = i;
                                    //System.out.println(courseIndex);
                                }
                            }
  
                            //now find the time
                            for (int i = 0; i < labSlotsVector.size(); i++)
                            {
                                TimeSlot labSlots = labSlotsVector.get(i);
                                {
                                    if(labSlots.getDay().equals(day) && labSlots.getStartTime().equals(time))
                                    {
                                        timeIndex = i;
                                        //System.out.println(timeIndex);
                                    }
  
                                }
                            }
  
                            labsVector.get(courseIndex).addUnwanted(timeIndex);
  
                        }
  
  
                    }
                }
            }
  
            /*----------------------------------------------------- PARSING PREFERENCES -------------------------------------------------------- */
            else if(line.equals("Preferences:"))
            {
                //System.out.println("Preferences");
                while(!line.equals(""))
                {
                    line = buffer.readLine();
                    line = line.replaceAll(" +", " ");
                    //System.out.println("\t" + line);
  
                    List<String> splitList = Arrays.asList(line.split(","));
  
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
                            for(int i = 0; i < coursesVector.size(); i++)
                            {
                                dept = splitClass.get(0);
                                courseNum = splitClass.get(1);
                                lecNum = splitClass.get(3);
  
                                Courses course = coursesVector.get(i);
  
                                if(course.getDepartment().equals(dept) && course.getCourseNumber().equals(courseNum) && course.getLectureNumber().equals(lecNum))
                                {
                                    courseIndex = i;
                                    //System.out.println(courseIndex);constrUnwanted(sched, index)
                                }
                            }
  
                            for (int i = 0; i < courseSlotsVector.size(); i++)
                            {
                                TimeSlot courseSlots = courseSlotsVector.get(i);
                                {
                                    if(courseSlots.getDay().equals(day) && courseSlots.getStartTime().equals(time))
                                    {
                                        timeIndex = i;
                                        //System.out.println(timeIndex);
                                    }
  
                                }
                            }
  
                            if (timeIndex > -1)
                            {
                                coursesVector.get(courseIndex).addPreference(timeIndex);
                                coursesVector.get(courseIndex).addPrefEval(eval);
                            }
                             
                            if (timeIndex == -1)
                                System.out.println("TIME PREFERENCE FOR A COURSE IS NOT A TIME SLOT");
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
                            for(int i = 0; i < labsVector.size(); i++)
                            {
                                Labs lab = labsVector.get(i);
  
                                if(lab.getDepartment().equals(dept) && lab.getCourseNumber().equals(courseNum) && lab.getLectureNumber().equals(lecNum) && lab.getLabNumber().equals(labNum))
                                {
                                    courseIndex = i;
                                    //System.out.println(courseIndex);
                                }
                            }
  
                            //now find the time
                            for (int i = 0; i < labSlotsVector.size(); i++)
                            {
                                TimeSlot labSlots = labSlotsVector.get(i);
                                {
                                    if(labSlots.getDay().equals(day) && labSlots.getStartTime().equals(time))
                                    {
                                        timeIndex = i;
                                        //System.out.println(timeIndex);
                                    }
  
                                }
                            }
  
                            if (timeIndex > -1)
                            {
                                labsVector.get(courseIndex).addPreference(timeIndex);
                                labsVector.get(courseIndex).addPrefEval(eval);
                            }
                             
                            if (timeIndex == -1)
                                System.out.println("TIME PREFERENCE FOR A LAB NOT A TIME SLOT");
                        }
  
  
                    }
                }
            }
  
            //-------------------------------------------------------------- PARSING PAIRS ---------------------------------------------------------------------//
            else if(line.equals("Pair:"))
            {
                //System.out.println("Pair");
                while(!line.equals(""))
                {
                    line = buffer.readLine();
                    line = line.replaceAll(" +", " ");
                    List<String> splitList = Arrays.asList(line.split(","));
  
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
  
  
                            //second course
                            String dept2 = splitNonComp2.get(0);
                            String course2 = splitNonComp2.get(1);
                            String lecNum2 = splitNonComp2.get(3);
  
                            for(int i = 0; i < coursesVector.size(); i++)
                            {
                                Courses course = coursesVector.get(i);
  
                                //find first course
                                if(course.getDepartment().equals(dept1) && course.getCourseNumber().equals(course1) && course.getLectureNumber().equals(lecNum1))
                                {
                                    indexOfFirstNonComp = i;
                                    //System.out.println(indexOfFirstNonComp);
                                }
                                //find second course
                                else if(course.getDepartment().equals(dept2) && course.getCourseNumber().equals(course2) && course.getLectureNumber().equals(lecNum2))
                                {
                                    indexOfSecondNonComp = i;
                                    //System.out.println(indexOfSecondNonComp);
                                }
  
                            }
  
                            coursesVector.get(indexOfFirstNonComp).addCoursePair(indexOfSecondNonComp);  //add the index of second lab to non compatible array of first lab
                            coursesVector.get(indexOfSecondNonComp).addCoursePair(indexOfFirstNonComp);  //and vice versa
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
  
  
                            for(int i = 0; i < labsVector.size(); i++)
                            {
                                Labs lab = labsVector.get(i);
  
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
  
                            }
  
                            labsVector.get(indexOfFirstNonComp).addLabPair(indexOfSecondNonComp);  //add the index of second lab to non compatible array of first lab
                            labsVector.get(indexOfSecondNonComp).addLabPair(indexOfFirstNonComp);  //and vice versa
  
                        }
  
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
                            for(int i = 0; i < coursesVector.size(); i++)
                            {
                                Courses course = coursesVector.get(i);
  
                                if(course.getDepartment().equals(dept1) && course.getCourseNumber().equals(course1) && course.getLectureNumber().equals(lecNum1))
                                {
                                    indexOfFirstNonComp = i;
                                    //System.out.println(indexOfFirstNonComp);
                                }
                            }
  
                            //find lab
                            for(int j = 0; j < labsVector.size(); j++)
                            {
                                Labs lab = labsVector.get(j);
  
                                if(lab.getDepartment().equals(dept2) && lab.getCourseNumber().equals(course2) && lab.getLectureNumber().equals(lecNum2) && lab.getLabNumber().equals(labNum2))
                                {
                                    indexOfSecondNonComp = j;
                                    //System.out.println(indexOfSecondNonComp);
                                }
  
                            }
  
                            coursesVector.get(indexOfFirstNonComp).addLabPair(indexOfSecondNonComp);   //add non compatible lab index to course
                            labsVector.get(indexOfSecondNonComp).addCoursePair(indexOfFirstNonComp);   //add non compatible course index to lab
  
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
                            for(int i = 0; i < labsVector.size(); i++)
                            {
                                Labs lab = labsVector.get(i);
  
                                if(lab.getDepartment().equals(dept1) && lab.getCourseNumber().equals(course1) && lab.getLectureNumber().equals(lecNum1) && lab.getLabNumber().equals(labNum1))
                                {
                                    indexOfFirstNonComp = i;
                                    //System.out.println(indexOfFirstNonComp);
                                }
                            }
  
                            //find second course
                            for(int j = 0; j < coursesVector.size(); j++)
                            {
                                Courses course = coursesVector.get(j);
                                if(course.getDepartment().equals(dept2) && course.getCourseNumber().equals(course2) && course.getLectureNumber().equals(lecNum2))
                                {
                                    indexOfSecondNonComp = j;
                                    //System.out.println(indexOfFirstNonComp);
                                }
                            }
  
                            coursesVector.get(indexOfSecondNonComp).addLabPair(indexOfFirstNonComp);   //add non compat lab to course
                            labsVector.get(indexOfFirstNonComp).addCoursePair(indexOfSecondNonComp);   //add non compat course to lab
  
                        }
  
                    }
                    //System.out.println("\t" + line);
                }
  
  
            }
            else if(line.equals("Partial assignments:"))
            {
                //System.out.println("Partial Assignments");
                while(line!=null)
                {
                    line = buffer.readLine();
  
  
                    if(line != null) {
                        //System.out.println("\t" + line);
                        line = line.replaceAll(" +", " ");
                        System.out.println("\t" + line);
  
                        List<String> splitList = Arrays.asList(line.split(","));
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
	  
	  
								for(int i = 0; i < coursesVector.size(); i++)
								{
									Courses course = coursesVector.get(i);
	  
									if(course.getDepartment().equals(dept) && course.getCourseNumber().equals(courseNum) && course.getLectureNumber().equals(lecNum))
									{
										courseIndex = i;
										//System.out.println(courseIndex);
									}
	  
	  
								}
	  
								for (int i = 0; i < courseSlotsVector.size(); i++)
								{
									TimeSlot courseSlots = courseSlotsVector.get(i);
									{
										if(courseSlots.getDay().equals(day) && courseSlots.getStartTime().equals(time))
										{
											timeIndex = i;
											//System.out.println(timeIndex);
										}
	  
									}
								}
	  
								coursesVector.get(courseIndex).addPartAssign(timeIndex);
							}
							else if(splitClass.size()==6)
							{
								lecNum = splitClass.get(3).trim();
								labNum = splitClass.get(5).trim();
	  
								for(int i = 0; i < labsVector.size(); i++)
								{
									Labs lab = labsVector.get(i);
	  
									if(lab.getDepartment().equals(dept) && lab.getCourseNumber().equals(courseNum) && lab.getLectureNumber().equals(lecNum) && lab.getLabNumber().equals(labNum))
									{
										courseIndex = i;
										//System.out.println(courseIndex);
									}
								}
	  
								for (int i = 0; i < labSlotsVector.size(); i++)
								{
									TimeSlot labSlots = labSlotsVector.get(i);
									{
										if(labSlots.getDay().equals(day) && labSlots.getStartTime().equals(time))
										{
											timeIndex = i;
											//System.out.println(timeIndex);
										}
	  
									}
								}
	  
								labsVector.get(courseIndex).addPartAssign(timeIndex);
							}
							else if(splitClass.size() == 4)
							{
								lecNum = "";
								labNum = splitClass.get(3).trim();
	  
								for(int i = 0; i < labsVector.size(); i++)
								{
									Labs lab = labsVector.get(i);
	  
									if(lab.getDepartment().equals(dept) && lab.getCourseNumber().equals(courseNum) && lab.getLectureNumber().equals(lecNum) && lab.getLabNumber().equals(labNum))
									{
										courseIndex = i;
										//System.out.println(courseIndex);
									}
								}
	  
								for (int i = 0; i < labSlotsVector.size(); i++)
								{
									TimeSlot labSlots = labSlotsVector.get(i);
									{
										if(labSlots.getDay().equals(day) && labSlots.getStartTime().equals(time))
										{
											timeIndex = i;
											//System.out.println(timeIndex);
										}
	  
									}
								}
	  
								labsVector.get(courseIndex).addPartAssign(timeIndex);
							}
						}
					}
  
                }
            }
            line = buffer.readLine();
        }
        
  
        //need to add the 813/913 special case courses
        if(found313Bool)
        {
            Courses course813 = new Courses("CPSC","813","01");
            Courses course313 = coursesVector.get(found313);
            index813 = coursesVector.size() - 1;

            for (int x = 0; x < course313.getNonCompatibleCourses().size(); x++)
                course813.addNonCompatibleCourse(course313.getNonCompatibleCourses().get(x));
            for (int x = 0; x < coursesVector.size(); x++)
                if (coursesVector.get(x).getCourseNumber().equals("313") && coursesVector.get(x).getDepartment().equals("CPSC"))
                    course813.addNonCompatibleCourse(x);

            for (int x = 0; x < course313.getNonCompatibleLabs().size(); x++)
                course813.addNonCompatibleLab(course313.getNonCompatibleLabs().get(x));
            for (int x = 0; x < labsVector.size(); x++)
                if (labsVector.get(x).getCourseNumber().equals("313") && labsVector.get(x).getDepartment().equals("CPSC"))
                    course813.addNonCompatibleLab(x);

            for (int x = 0; x < course313.getUnwanted().size(); x++)
                course813.addUnwanted(course313.getUnwanted().get(x));

            for (int x = 0; x < labSlotsVector.size(); x++)
                if (labSlotsVector.get(x).getDay().equals("TU") && labSlotsVector.get(x).getStartTime().equals("18:00"))
                    course813.addPartAssign(x);

            coursesVector.add(course813);
        }

        if(found413Bool)
        {
            Courses course913 = new Courses("CPSC","913","01");   
            Courses course413 = coursesVector.get(found413);
            index913 = coursesVector.size() - 1;

            for (int x = 0; x < course413.getNonCompatibleCourses().size(); x++)
                course913.addNonCompatibleCourse(course413.getNonCompatibleCourses().get(x));
            for (int x = 0; x < coursesVector.size(); x++)
                if (coursesVector.get(x).getCourseNumber().equals("413") && coursesVector.get(x).getDepartment().equals("CPSC"))
                    course913.addNonCompatibleCourse(x);

            for (int x = 0; x < course413.getNonCompatibleLabs().size(); x++)
                course913.addNonCompatibleLab(course413.getNonCompatibleLabs().get(x));
            for (int x = 0; x < labsVector.size(); x++)
                if (labsVector.get(x).getCourseNumber().equals("413") && labsVector.get(x).getDepartment().equals("CPSC"))
                    course913.addNonCompatibleLab(x);

            for (int x = 0; x < course413.getUnwanted().size(); x++)
                course913.addUnwanted(course413.getUnwanted().get(x));

            for (int x = 0; x < labSlotsVector.size(); x++)
                if (labSlotsVector.get(x).getDay().equals("TU") && labSlotsVector.get(x).getStartTime().equals("18:00"))
                    course913.addPartAssign(x);

            coursesVector.add(course913);
        }
         
        file.close();
        buffer.close();
    }
          
    /*private Courses found(Courses newCourse, int find13, boolean isCourse)
    {
        if(isCourse) {
            Courses course = coursesVector.get(find13);         
            ArrayList<Integer> nc = course.getNonCompatibleCourses();
                 
            for(int j = 0; j < nc.size(); j++)
            {
                newCourse.addNonCompatibleCourse(nc.get(j));
            }
             
            nc = course.getNonCompatibleLabs();
             
            for(int j = 0; j < nc.size(); j++)
            {
                newCourse.addNonCompatibleLab(nc.get(j));
            }
             
            for(int i = 0; i < coursesVector.size(); i++)
            {           
                nc = coursesVector.get(i).getNonCompatibleCourses();
                     
                for(int j = 0; j < nc.size(); j++)
                {
                    if(nc.get(j) == find13) newCourse.addNonCompatibleCourse(i);
                }  
                nc = coursesVector.get(i).getNonCompatibleLabs();
                for(int j = 0; j < nc.size(); j++)
                {
                    if(nc.get(j) == find13) newCourse.addNonCompatibleLab(i);
                }
            }//end for loop
             
            for(int i = 0; i < labsVector.size(); i++)
            {           
                nc = labsVector.get(i).getNonCompatibleCourses();
                     
                for(int j = 0; j < nc.size(); j++)
                {
                    if(nc.get(j) == find13) newCourse.addNonCompatibleCourse(i);
                }  
                nc = labsVector.get(i).getNonCompatibleLabs();
                for(int j = 0; j < nc.size(); j++)
                {
                    if(nc.get(j) == find13) newCourse.addNonCompatibleLab(i);
                }
            }//end for loop
        }//end if(course)
        else {
            Labs lab = labsVector.get(find13);          
            ArrayList<Integer> nc = lab.getNonCompatibleCourses();
                 
            for(int j = 0; j < nc.size(); j++)
            {
                newCourse.addNonCompatibleCourse(nc.get(j));
            }
             
            nc = lab.getNonCompatibleLabs();
             
            for(int j = 0; j < nc.size(); j++)
            {
                newCourse.addNonCompatibleLab(nc.get(j));
            }
             
            for(int i = 0; i < coursesVector.size(); i++)
            {           
                nc = coursesVector.get(i).getNonCompatibleCourses();
                     
                for(int j = 0; j < nc.size(); j++)
                {
                    if(nc.get(j) == find13) newCourse.addNonCompatibleCourse(i);
                }  
                nc = coursesVector.get(i).getNonCompatibleLabs();
                for(int j = 0; j < nc.size(); j++)
                {
                    if(nc.get(j) == find13) newCourse.addNonCompatibleLab(i);
                }
            }//end for loop
             
            for(int i = 0; i < labsVector.size(); i++)
            {           
                nc = labsVector.get(i).getNonCompatibleCourses();
                     
                for(int j = 0; j < nc.size(); j++)
                {
                    if(nc.get(j) == find13) newCourse.addNonCompatibleCourse(i);
                }  
                nc = labsVector.get(i).getNonCompatibleLabs();
                for(int j = 0; j < nc.size(); j++)
                {
                    if(nc.get(j) == find13) newCourse.addNonCompatibleLab(i);
                }
            }//end for loop
        }//end if-else
         
        return newCourse;
    }
     
    private int find13Slot()
    {//finds the lab slot that will hold 813/913
         
            for(int i = 0; i < labSlotsVector.size(); i++)
            {
                if(labSlotsVector.get(i).get13Slot())
                {//find the 813/913 slot, and assign the class
                    if((found313 != -1) && (found413 != -1))
                    {//need two spots
                        if(labSlotsVector.get(i).getCourseMax() >= 2) return i;
                        else return -1;                     
                    }else if((found313 != -1) || (found413 != -1))
                    {//only one spot needed
                        if(labSlotsVector.get(i).getCourseMax() >= 1) return i;
                        else return -1;     
                    }
            }                           
        } //not found
            return -2;
    }//end find13Slot*/
     
    /*private Courses find13Course(boolean is313)
    {
    	if(is313)
    	{
    		for (int i = 0; i < coursesVector.size(); i++)
    		{
    			if((coursesVector.get(i).getDepartment().matches("CPSC")) && (coursesVector.get(i).getCourseNumber().matches("313")))
    			{
    				
    			}
    		}
    	}
    }*/
    
    private void sortCourses()
    {
        Courses temp;
          
        for(int i = 0; i < coursesVector.size(); i++)
            for(int j = 1; j < (coursesVector.size()-i); j++)
                if(coursesVector.get(j-1).getDepartment().compareTo(coursesVector.get(j).getDepartment()) > 0 || ((coursesVector.get(j-1).getDepartment().compareTo(coursesVector.get(j).getDepartment()) == 0) && ((Integer.parseInt(coursesVector.get(j-1).getCourseNumber())) > (Integer.parseInt(coursesVector.get(j).getCourseNumber())))))
                    Collections.swap(coursesVector, j, j-1); 
    }
    
    
}
