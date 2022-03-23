package courseSchedulerCore;
import java.util.ArrayList;


public class Schedule {
	
		//strictly for testing
		//private final boolean testValue = true;
		
	boolean debug = false;
	
	private CourseAndTimeSlotsData courseAndTime = null;	
	Penalty penalty = null;
	
	public TimeSlot [] courseList;
	public TimeSlot [] labList;
	boolean isValid;
	double value;
	
	/**
	 * 
	 * @param parse
	 */
	public Schedule(CourseAndTimeSlotsData cat, Penalty pen)
	{//constructor
		setCourseAndTime(cat);
		if (pen != null) penalty = pen;
		else penalty = new Penalty(); //use defaults
		//debug = debugMode;
		//the schedule is valid until some assignment makes it invalid
		isValid = true;
		//schedule starts with no penalties until they are assigned
		value = 0;
		courseList = new TimeSlot[getCourseAndTime().getCoursesVector().size()];
		labList = new TimeSlot[getCourseAndTime().getLabsVector().size()];
	}
	
	public String toString()
	{
	    int y = 0;
	    String temp = "Eval-value: " + value + "\n";
	     
	    for (int x = 0; x < getCourseAndTime().getCoursesVector().size(); x++)
	    {
	        temp += getCourseAndTime().getCoursesVector().get(x) + "\t" + courseList[x] + "\n";
	         
	        // checking for duplicates in labs
	        for (int checker = 0; checker < x; checker++)
	            if (getCourseAndTime().getCoursesVector().get(checker).getLabIndex().equals(getCourseAndTime().getCoursesVector().get(x).getLabIndex()))
	                getCourseAndTime().getCoursesVector().get(x).clearLabIndex();
	         
	        // printing all the labs for a certain lecture
	        if (getCourseAndTime().getCoursesVector().get(x).getLabIndex().size() > 0)
	        {
	            y = getCourseAndTime().getCoursesVector().get(x).getLabIndex().get(0);
	             
	            for (int count = 0; count < getCourseAndTime().getCoursesVector().get(x).getLabIndex().size(); count++)
	            {
	                temp += getCourseAndTime().getLabsVector().get(y) + " " + labList[y] + "\n";
	                y++;
	            }
	        }
	    } 
	      
	    return temp;        
	}
	
	public String toStringReverse()
	{
	    int y = 0;
	    String temp = "Eval-value: " + value + "\n";
	     
	    for (int x = 0; x < getCourseAndTime().getCoursesVector().size(); x++)
	    {
	        temp += courseList[x].toStringReverse() + " " + getCourseAndTime().getCoursesVector().get(x) + "\n";
	         
	        // checking for duplicates in labs
	        for (int checker = 0; checker < x; checker++)
	            if (getCourseAndTime().getCoursesVector().get(checker).getLabIndex().equals(getCourseAndTime().getCoursesVector().get(x).getLabIndex()))
	                getCourseAndTime().getCoursesVector().get(x).clearLabIndex();
	         
	        // printing all the labs for a certain lecture
	        if (getCourseAndTime().getCoursesVector().get(x).getLabIndex().size() > 0)
	        {
	            y = getCourseAndTime().getCoursesVector().get(x).getLabIndex().get(0);
	             
	            for (int count = 0; count < getCourseAndTime().getCoursesVector().get(x).getLabIndex().size(); count++)
	            {
	                temp += labList[y].toStringReverse() + " " +  getCourseAndTime().getLabsVector().get(y) + "\n";
	                y++;
	            }
	        }
	    } 
	      
	    return temp;        
	}
	
	public boolean assign13(int index, TimeSlot slot)
	{
		courseList[index] = slot;
		isValid = getCourseAndTime().getCoursesVector().get(index).constr(this, index) && slot.constr(this, index, false);
		return isValid;
	}
	
	public boolean assign(int index, TimeSlot slot, boolean isCourse)
	{//this method assigns the time slot to the course/lab at the input index
		boolean isValid = false;
		
		if(isCourse)
		{
			courseList[index] = slot;
			//isValid = constr(); //old
			isValid = getCourseAndTime().getCoursesVector().get(index).constr(this, index) && slot.constr(this, index, isCourse);
		} else //its a lab
		{
			Labs target = getCourseAndTime().getLabsVector().get(index);
			labList[index] = slot;
			//isValid = constr(); //old
			isValid = getCourseAndTime().getLabsVector().get(index).constr(this, index) && slot.constr(this, index, isCourse);
			if (!isValid) labList[index] = null;
		}
		
		return isValid; //assignment failed because it is invalid
	}
	
	public TimeSlot getSlot(int index, boolean isCourse)
	{//returns the assigned timeSlot for the course/lab at the input index, or null if unassigned
		if (isCourse) return courseList[index];
		else return labList[index];
	}
	
	public boolean constr()
	{//this method checks to ensure the schedule does not violate any hard constraints
		for (int x = 0; x < courseList.length; x++)
			if (!getCourseAndTime().getCoursesVector().get(x).constr(this, x))
			{
				if(debug) System.out.println("Error on: " + getCourseAndTime().getCoursesVector().get(x) + ", Course index: " + x);
				return false;
			}

		for (int x = 0; x < labList.length; x++)
			if (!getCourseAndTime().getLabsVector().get(x).constr(this, x))
			{
				if(debug) System.out.println("Error on: " + getCourseAndTime().getLabsVector().get(x));
				return false;
			}
		return true;

		//if(constrMax() && constrNotCompatible() && constrUnwanted() && constrCourseLab() && constr500()) return true;
		
		//else return false;
	}//end constr
	
	public double eval()
    {
        return minEval() * penalty.getW_minfilled() + prefEval() * penalty.getW_pref() + pairEval() * penalty.getW_pair() + sectionEval() * penalty.getW_secdif();
    }
      
    public double minEval(){
        int missingCourses = 0;
        int missingLabs = 0;    
          
        int counter = 0;
      
        for (int x = 0; x < getCourseAndTime().getCourseSlotsVector().size(); x++)
        {
            TimeSlot slotChecker = getCourseAndTime().getCourseSlotsVector().get(x);
             
            if(slotChecker.getCourseMin() > 0)
            {
                for(int i = 0 ; i < courseList.length ; i++)
                    if (courseList[i].equals(slotChecker))
                        counter++;
                         
                if (counter < slotChecker.getCourseMin())
                    missingCourses += slotChecker.getCourseMin() - counter;
                     
                counter = 0;
            }
        }
      
        for (int x = 0; x < getCourseAndTime().getLabSlotsVector().size(); x++)
        {
            TimeSlot slotChecker = getCourseAndTime().getLabSlotsVector().get(x);
             
            if(slotChecker.getLabMin() > 0)
            {
                for(int i = 0 ; i < labList.length ; i++)
                    if (labList[i].equals(slotChecker))
                        counter++;
                         
                if (counter < slotChecker.getLabMin())
                    missingLabs += slotChecker.getLabMin() - counter;
                     
                counter = 0;
            }
        }
         
        //System.out.println(missingLabs);
        //System.out.println("min: " + (missingCourses + missingLabs));
        return (missingCourses * penalty.getCourseMinPenalty()) + (missingLabs * penalty.getLabMinPenalty());
    }
      
      
      
    public double prefEval()
    {
        int coursesPrefMissed = 0;
        int labsPrefMissed = 0;
         
 
        for (int x = 0; x < courseList.length; x++)
            for (int prefCount = 0; prefCount < getCourseAndTime().getCoursesVector().get(x).getPreference().size(); prefCount++)
                if (!(getCourseAndTime().getCourseSlotsVector().get(getCourseAndTime().getCoursesVector().get(x).getPreference().get(prefCount)).equals(courseList[x])))
                    coursesPrefMissed += getCourseAndTime().getCoursesVector().get(x).getPrefEval().get(prefCount);
         
         
        for (int x = 0; x < labList.length; x++)
            for (int prefCount = 0; prefCount < getCourseAndTime().getLabsVector().get(x).getPreference().size(); prefCount++)
                if (!(getCourseAndTime().getLabSlotsVector().get(getCourseAndTime().getLabsVector().get(x).getPreference().get(prefCount)).equals(labList[x])))
                    labsPrefMissed += getCourseAndTime().getLabsVector().get(x).getPrefEval().get(prefCount);
         
        //System.out.println("Preferred: " + coursesPrefMissed + labsPrefMissed);
        return (coursesPrefMissed + labsPrefMissed);
         
    }
 
    public double pairEval()
    {
        double notPair = 0;
        ArrayList<Integer> pairs;
             
        for(int i = 0; i< courseList.length; i++)
        {
            pairs = getCourseAndTime().getCoursesVector().get(i).getPairCourse();
             
            for (int j = 0; j < pairs.size(); j++)
                if(!timeCheck(i, true, pairs.get(j), true))
                    notPair++;          
 
            pairs = getCourseAndTime().getCoursesVector().get(i).getPairLab();
             
            for (int j = 0; j < pairs.size(); j++)
                if(!timeCheck(i, true, pairs.get(j), false)) 
                    notPair++;
             
        }
 
        for(int i = 0; i < labList.length; i++)
        {
            pairs = getCourseAndTime().getLabsVector().get(i).getPairLab();
             
            for (int j = 0; j < pairs.size(); j++)
                if(!timeCheck(i, false, pairs.get(j), false)) 
                    notPair++;          
        }
             
        //System.out.println("NotPair: " + notPair);
        return (notPair * penalty.getNotPairPenalty()) / 2;
    }
 
    public double sectionEval(){
        int overlap = 0;
         
        for (int x = 0; x < courseList.length; x++)
        {
            Courses course = getCourseAndTime().getCoursesVector().get(x);
             
            for (int y = 0; y < x; y++) 
                if (getCourseAndTime().getCoursesVector().get(y).getCourseNumber().compareTo(getCourseAndTime().getCoursesVector().get(x).getCourseNumber()) == 0)
                    if (courseList[x].equals(courseList[y]))
                        overlap++;
        }
         
        //System.out.println("sectionEval: " + overlap);
        return overlap * penalty.getSectionDiffPenalty();
    }
	
	public void setValue(double theValue)
	{
		value = theValue;
	}
	
	public double getValue()
	{
		return value;
	}
	
	public boolean timeCheck(int index1, boolean is1Course, int index2, boolean is2Course)
	{//checks to see if the time values overlap, returns true if they do
		TimeSlot first, second, tempLab, tempLec;
		
		if(is1Course)
		{
			first = courseList[index1]; 
		} else 
		{
			first = labList[index1];
		}
		
		if(is2Course)
		{
			second = courseList[index2];
		} else
		{
			second = labList[index2];
		}
		if((first == null) || (second == null)) return false;
		return timeCheck(first, is1Course, second, is2Course);
	}
	
	public boolean timeCheck(TimeSlot first, boolean is1Course, TimeSlot second, boolean is2Course)
	{//checks to see if the time values overlap, returns true if they do
		TimeSlot tempLab, tempLec;
		
		//check for nulls, return true if found -> one is not assigned yet
		if((first == null) || (second == null)) return false;
		
		if((is1Course && is2Course) || (!(is1Course) && !(is2Course)))
		{//check if both are courses or labs, if so just compare start times
			if((first.getDay().compareTo(second.getDay())==0) && ((first.getStartTime().compareTo(second.getStartTime())==0)))  return true;
			else return false;
		} else 
		{//one is a lab, the other is a course
			if (!is1Course) {
				tempLab = first;
				tempLec = second;
			} else
			{
				tempLab = second;
				tempLec = first;
			}
		}//end if else
		
		if(((tempLab.getDay().startsWith("M") || tempLab.getDay().startsWith("F")) && (tempLec.getDay().startsWith("M"))))
		{//check if they have the same start time
			if (tempLab.getStartTime().compareTo(tempLec.getStartTime())==0) return true;
			else if(tempLab.getDay().startsWith("F") && ((Integer.parseInt(tempLec.getStartTime().replaceAll(":", ""))) == (Integer.parseInt(tempLab.getStartTime().replaceAll(":", ""))+100))) return true;
			else return false;
		} else if ((tempLab.getDay().startsWith("T")) && (tempLec.getDay().startsWith("T")))
				{
					if (tempLab.getStartTime().compareTo(tempLec.getStartTime())==0) return true;
					else 
					{
						int x = Integer.parseInt(tempLec.getStartTime().replaceAll(":", ""));
						int y = Integer.parseInt(tempLab.getStartTime().replaceAll(":", ""));
						if (x + 70 == y) return true;
						if (x - 30 == y) return true;
						if (x + 100 == y) return true;
						
						/*if ((tempLab.getStartTime().compareTo("9:00")==0) && (tempLec.getStartTime().compareTo("8:00")==0)) return true;
					else if ((tempLab.getStartTime().compareTo("9:00")==0) && (tempLec.getStartTime().compareTo("9:30")==0)) return true;
					else if (((tempLab.getStartTime().compareTo("10:00")==0) ) && (tempLec.getStartTime().compareTo("9:30"))==0) return true;
					else 
						{
						int x = Integer.parseInt(tempLec.getStartTime().replaceAll(":", ""));
						int y = Integer.parseInt(tempLab.getStartTime().replaceAll(":", ""));
						if (x == y) return true; //time slot overlap
						//if (tempLab.getStartTime().substring(0, 1).compareTo( tempLec.getStartTime().substring(0, 1))==0) return true;						
					else if (tempLec.getStartTime().charAt(3) == '3') 
					{//ex: course at 12:30, lab at 1
						//int x = Integer.parseInt(tempLec.getStartTime().substring(0, 2)) +1 ;
						//int y = Integer.parseInt(tempLab.getStartTime().substring(0, 2));
						if (x + 100 == y) return true; //time slot overlap
					} } */}
				}
		return false;
	}

	public CourseAndTimeSlotsData getCourseAndTime() {
		return courseAndTime;
	}

	public void setCourseAndTime(CourseAndTimeSlotsData courseAndTime) {
		this.courseAndTime = courseAndTime;
	}
	
}//end Schedule
