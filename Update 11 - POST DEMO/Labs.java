import java.util.ArrayList;
 
/**
 * Created by Gorman on 2015-11-23.
 */
public class Labs {
 
    //strictly for testing
    private final boolean testValue = true;
     
    private String department;
    private String courseNumber;
    private String lectureNumber;
    private String labNumber;
    private int cIndex = -1;
     
    //hard constraint
    private ArrayList<Integer> nonCompatibleC;    //hard constraint non compatible courses. Index to coursesVector
    private ArrayList<Integer> nonCompatibleL;       //hard constraint non compatible labs. Index to labsVector
  
    //soft constraint
    private ArrayList<Integer> unwanted;        //index to courseSlotsVector
    private ArrayList<Integer> partAssign;      //index to courseSlotsVector
  
    private ArrayList<Integer> preference;      //index to labSlotsVector
    private ArrayList<Integer> evalValue;       //eval value to corresponding index.
  
    private ArrayList<Integer> pairC;      //index to coursesVector
    private ArrayList<Integer> pairL;         //index to labsVector
  
    private ArrayList<Integer> lectureTimeDiff;             //index to coursesVector
 
    public Labs()
    {
        //default constructor
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
 
    public Labs(String department, String courseNumber, String lectureNumber, String labNumber)
    {
        this.department = department;
        this.courseNumber = courseNumber;
        this.lectureNumber = lectureNumber;
        this.labNumber = labNumber;
         
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
        String temp = "";
         
        if (department != "")
            temp += department + " ";
        if (courseNumber != "")
            temp += courseNumber;
        if (lectureNumber != "")
            temp += " Lec " + lectureNumber + " ";
        else
            temp += " ";
        if (labNumber != "")
            temp += "TUT " + labNumber;
             
        return temp;   
    }
     
    public boolean constr()
    {//TODO
        return testValue; //returns false (as in failed) for now
    }
     
    public int eval()
    {//TODO
        //checks the soft constraints for this lab
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
  
    public void setLabNumber(String labNumber)
    {
        this.labNumber = labNumber;
    }
  
    public String getLabNumber()
    {
        return labNumber;
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
		if(constrUnwanted(sched, index) && constrNotCompatible(sched, index) && constrCL(sched, index))		
		{
			return true;//contr check passed
		}
		else return false; //constr failed */
    }
    
    private boolean constrCL(Schedule sched, int index)
    {
    	//return true;
    	//if (cIndex != -1) return !(sched.timeCheck(cIndex, true, index, false));
    	for(int i = 0; i < sched.parser.coursesVector.size(); i++ ) 
    	{//look through courses
    		Courses course = sched.parser.coursesVector.get(i);
    		if((course.getCourseNumber().compareTo(courseNumber)==0) && (course.getDepartment().compareTo(department)==0) && (course.getLectureNumber().compareTo(lectureNumber)==0))
    		{//found the correct course-lecture
	    		for(int j = 0; j < course.getLabIndex().size(); j++)
	    		{//look through the labs
	    			
	    			if(j == index)
	    			{//found the lab
	    				boolean isValid = !(sched.timeCheck(i, true, index, false));
	    				cIndex = i;
	    				return isValid;
	    			}
	    		}  				
    		}//end if-then
    	}
  	
    	return false; //not found
    }
    
	private boolean constrUnwanted(Schedule sched, int i)
	{//checks the unwanted values
			
			if(sched.labList[i] == null) return true; //not assigned yet
			for(int j = 0; j < unwanted.size(); j++)
			{
				if(sched.labList[i].getDay().matches(sched.parser.labSlotsVector.get(unwanted.get(j)).getDay())){
					if(sched.labList[i].getStartTime().matches(sched.parser.labSlotsVector.get(unwanted.get(j)).getStartTime()))
						{return false;} //unwanted failed
				}
			}//end for loop j
		
		return true; 
	}//end unwanted
	
	private boolean constrNotCompatible(Schedule sched, int i)
	{//checks notcompatible
		boolean valid = true;
		
			ArrayList<Integer> nc = getNonCompatibleCourses();
			if(sched.labList[i] == null) return true; //not assigned yet
			
			for(int j = 0; j < nc.size(); j++)
			{
				if(sched.timeCheck(i, false, nc.get(j), true)) return false; //check failed
			}//end for loop j
			
			nc = getNonCompatibleLabs();
			
			for(int j = 0; j < nc.size(); j++)
			{//check against labs
				if(sched.timeCheck(i, false, nc.get(j), false)) return false; //check failed
			}//end for loop j
			
		return valid;
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
	
	public void setCIndex(int i)
	{
		cIndex = i;
	}
}