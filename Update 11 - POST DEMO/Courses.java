import java.util.ArrayList;
 
/**
 * Created by Gorman on 2015-11-23.
 */
public class Courses {
     
    //strictly for testing
    private final boolean testValue = true;
 
    private String department;
    private String courseNumber;
    private String lectureNumber;
    private ArrayList<Integer> labIndex;
     
    //hard constraint
    private ArrayList<Integer> nonCompatibleC;    //hard constraint non compatible courses. Index to coursesVector
    private ArrayList<Integer> nonCompatibleL;       //hard constraint non compatible labs. Index to labsVector
  
    private ArrayList<Integer> unwanted;        //index to courseSlotsVector
    private ArrayList<Integer> partAssign;      //index to courseSlotsVector
     
    //soft constraint
    private ArrayList<Integer> preference;      //index to labSlotsVector
    private ArrayList<Integer> evalValue;       //eval value to corresponding index.
  
    private ArrayList<Integer> pairC;      //index to coursesVector
    private ArrayList<Integer> pairL;         //index to labsVector
  
    private ArrayList<Integer> lectureTimeDiff;             //index to coursesVector
  
    public Courses()
    {
        labIndex = new ArrayList<>();
        nonCompatibleC = new ArrayList<>();
        nonCompatibleL = new ArrayList<>();
        partAssign = new ArrayList<>();
        unwanted = new ArrayList<>();
        pairC = new ArrayList<>();
        pairL = new ArrayList<>();
        preference = new ArrayList<>();
        evalValue = new ArrayList<>();
        lectureTimeDiff = new ArrayList<>();
    }
 
    public Courses(String department, String courseNumber, String lectureNumber)
    {
        this.department = department;
        this.courseNumber = courseNumber;
        this.lectureNumber = lectureNumber;
         
        labIndex = new ArrayList<>();
        nonCompatibleC = new ArrayList<>();
        nonCompatibleL = new ArrayList<>();
        partAssign = new ArrayList<>();
        unwanted = new ArrayList<>();
        pairC = new ArrayList<>();
        pairL = new ArrayList<>();
        preference = new ArrayList<>();
        evalValue = new ArrayList<>();
        lectureTimeDiff = new ArrayList<>();
    }
 
    public String toString()
    {
        return (department + " " + courseNumber + " Lec " + lectureNumber);    
    }
     
    public int eval()
    {//TODO
        //checks the soft constraints for this course
        return 0; //returns 0 penalty for now
    }
     
    public void setDepartment(String department)
    {
        this.department = department;
    }
  
    public String getDepartment()
    {
        return department;
    }
  
    public void setCourseNumber(String courseNumber)
    {
        this.courseNumber = courseNumber;
    }
  
    public String getCourseNumber()
    {
        return courseNumber;
    }
  
    public void setLectureNumber(String lectureNumber)
    {
        this.lectureNumber = lectureNumber;
    }
  
    public String getLectureNumber()
    {
        return lectureNumber;
    }
  
    public void addLabIndex(int labIndex)
    {
        this.labIndex.add(labIndex);
    }
     
    public void clearLabIndex()
    {
        labIndex.clear();
    }
  
    public ArrayList<Integer> getLabIndex()
    {
        return labIndex;
    }
  
    public void addNonCompatibleCourse(int nonCompIndex)
    {
        this.nonCompatibleC.add(nonCompIndex);
    }
  
    public ArrayList<Integer> getNonCompatibleCourses()
    {
        return nonCompatibleC;
    }
  
    public void addNonCompatibleLab(int nonCompIndex)
    {
        this.nonCompatibleL.add(nonCompIndex);
    }
  
    public ArrayList<Integer> getNonCompatibleLabs()
    {
        return nonCompatibleL;
    }
  
    public void addUnwanted(int unwantedIndex)
    {
        this.unwanted.add(unwantedIndex);
    }
  
    public ArrayList<Integer> getUnwanted()
    {
        return unwanted;
    }
  
    public void addPreference(int preferenceIndex)
    {
        this.preference.add(preferenceIndex);
    }
  
    public ArrayList<Integer> getPreference()
    {
        return preference;
    }
  
    public void addPrefEval(int prefEval)
    {
        this.evalValue.add(prefEval);
    }
  
    public ArrayList<Integer> getPrefEval()
    {
        return evalValue;
    }
  
    public void addCoursePair(int pairIndex)
    {
        this.pairC.add(pairIndex);
    }
  
    public void addLabPair(int pairIndex)
    {
        this.pairL.add(pairIndex);
    }
  
    public ArrayList<Integer> getPairCourse()
    {
        return pairC;
    }
  
    public ArrayList<Integer> getPairLab()
    {
        return pairL;
    }
  
    public void addPartAssign(int partAssignIndex)
    {
        this.partAssign.add(partAssignIndex);
    }
  
    public ArrayList<Integer> getPartAssign()
    {
        return partAssign;
    }
    
    public boolean constr(Schedule sched, int index)
    { 	
		boolean nc, uw = false, cl = false, c500 = false;
		nc = constrNotCompatible(sched, index);
		if(nc) uw = constrUnwanted(sched, index);
		if(uw) cl = constrCourseLab(sched, index);
		if(cl) c500 = constr500(sched, index);
		if(nc & uw & cl & c500)		
		{
			return true;//contr check passed
		}
		else return false; //constr failed */
    }
    
	private boolean constrUnwanted(Schedule sched, int i)
	{//checks the unwanted values
			
			if(sched.courseList[i] == null) return true; //not assigned yet
			for(int j = 0; j < unwanted.size(); j++)
			{
				if(sched.courseList[i].getDay().matches(sched.parser.courseSlotsVector.get(unwanted.get(j)).getDay())){
					if(sched.courseList[i].getStartTime().matches(sched.parser.courseSlotsVector.get(unwanted.get(j)).getStartTime()))
						{return false;} //unwanted failed
				}
			}//end for loop j
		
		return true; 
	}//end unwanted
	
	private boolean constr500(Schedule sched, int i)
	{//this checks to ensure that none of the 500 level courses are in the same slot
		if(sched.courseList[i] == null) return true;
		if(!(getCourseNumber().startsWith("5"))) return true;
		int firstIndex = 0;
		ArrayList<Integer> slot500 = new ArrayList<>();
		
		for(int j = 0; j < sched.parser.coursesVector.size(); j++)
		{
			if(sched.parser.coursesVector.get(j).getCourseNumber().startsWith("5"))
			{
				slot500.add(j);
			}
		}//end for loop
		
		if(slot500.size() == 1) return true; //just this one
	
			for(int j = 0; j < slot500.size(); j++)
			{//compare the timeslots for all the 500 level courses
				if(i == slot500.get(j)) continue;
				if(sched.courseList[slot500.get(j)] == null) continue;
				if(sched.timeCheck(sched.courseList[i], true, sched.courseList[slot500.get(j)], true)) return false;
			}//end for-loop(j)


		return true;
	}//end constr500
	
	private boolean constrCourseLab(Schedule sched, int i)
	{//this function ensures that the lab sections for a course are not in the same time slots
		boolean valid = true;
		
			ArrayList<Integer> lab = getLabIndex();
			if(sched.courseList[i] == null) return true; //not assigned yet
			
			for(int j = 0; j < lab.size(); j++)
			{//check against labs
				if(sched.timeCheck(i, true, lab.get(j), false)) return false; //check failed
			}//end for loop j

		return valid;
	}//end constrCourseLab
	
	private boolean constrNotCompatible(Schedule sched, int i)
	{//checks notcompatible
		boolean valid = true;
		
			ArrayList<Integer> nc = getNonCompatibleCourses();
			if(sched.courseList[i] == null) return true; //not assigned yet
			
			for(int j = 0; j < nc.size(); j++)
			{
				if(sched.timeCheck(i, true, nc.get(j), true)) return false; //check failed
			}//end for loop j
			
			nc = getNonCompatibleLabs();
			
			for(int j = 0; j < nc.size(); j++)
			{//check against labs
				if(sched.timeCheck(i, true, nc.get(j), false)) return false; //check failed
			}//end for loop j
			
		return valid;
	}
	
    public Courses clone13()
	{
		Courses newCourse = new Courses(department, courseNumber, "1");
		newCourse.setNonCompatibleC(nonCompatibleC);
		newCourse.setNonCompatibleL(nonCompatibleL);
		
		return newCourse;
	}

	public ArrayList<Integer> getNonCompatibleC() {
		return nonCompatibleC;
	}

	public void setNonCompatibleC(ArrayList<Integer> nonCompatibleC) {
		this.nonCompatibleC = nonCompatibleC;
	}

	public ArrayList<Integer> getNonCompatibleL() {
		return nonCompatibleL;
	}

	public void setNonCompatibleL(ArrayList<Integer> nonCompatibleL) {
		this.nonCompatibleL = nonCompatibleL;
	}
	
	
	
}