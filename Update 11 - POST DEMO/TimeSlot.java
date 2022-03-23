
public class TimeSlot {
     
    //strictly for testing
    private final boolean testValue = true;
     
    String day;
    String startTime;
    String endTime;
     
    boolean lectureSlot; //true = lectures, false = labs
    boolean isEvening; //True = Evening Course slot
    boolean is13Slot; //true ONLY for TR 18:00
     
    //hard constraints
    int courseMax;
    int labMax;
     
    //soft constraints
    int courseMin;
    int labMin;
     
    public TimeSlot(String d, String start, int Max, int Min, boolean evening, boolean isLectureSlot)
    {
        if(((d == "T") || (d == "TR")) && (evening == true) && (isLectureSlot == false) && (start == "18:00"))
        {//Lab slot on Tuesday/Thursday at 18:00
            is13Slot = true;
        } else //not the special slot
        {
            is13Slot = false;
        }
        day = d;
        startTime = start;
         
        if(isLectureSlot)
        {
            courseMax = Max;
            courseMin = Min;
        }
         
        else
        {
            labMax = Max;
            labMin = Min;
        }
        isEvening = evening;
        lectureSlot = isLectureSlot;
    }
 
    public String toString()
    {
       return ("\t\t: " + day + ", " + startTime);
    }
     
    public boolean get13Slot()
    {
        return is13Slot;
    }
     
    private void checkSlot()
    {//for updating any hard coded values related to time slots, example: courses starting at 5 or later are evening
        //TODO
    }
     
    public String getDay() {
        return day;
    }
 
    public void setDay(String day) {
        this.day = day;
    }
 
    public String getStartTime() {
        return startTime;
    }
 
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
 
    public String getEndTime() {
        return endTime;
    }
 
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
 
    public boolean isLectureSlot() {
        return lectureSlot;
    }
 
    public void setLectureSlot(boolean lectureSlot) {
        this.lectureSlot = lectureSlot;
    }
 
    public boolean isEvening() {
        return isEvening;
    }
 
    public void setEvening(boolean isEvening) {
        this.isEvening = isEvening;
    }
 
    public int getCourseMax() {
        return courseMax;
    }
 
    public void setCourseMax(int courseMax) {
        this.courseMax = courseMax;
    }
 
    public int getLabMax() {
        return labMax;
    }
 
    public void setLabMax(int labMax) {
        this.labMax = labMax;
    }
 
 
    public int getCourseMin() {
        return courseMin;
    }
 
    public void setCourseMin(int courseMin) {
        this.courseMin = courseMin;
    }
 
    public int getLabMin() {
        return labMin;
    }
 
    public void setLabMin(int labMin) {
        this.labMin = labMin;
    }
 
    public void addConstraints()
    {//TODO
     
    }
     
    public int eval()
    {//TODO checks soft constraints for the penalty value
        return 0; 
    }
 
    public boolean constr(Schedule sched, int index, boolean isCourse)
    { 	
		boolean maxValid = constrMax(sched, index, isCourse);
		
		if(maxValid)		
		{
			return true;//contr check passed
		}
		else return false; //constr failed */
    }
    
    private boolean constrMax(Schedule sched, int i, boolean isCourse)
	{
    	if(isCourse) {
		        int temp = 0;
		        for(int j = 0; j < sched.courseList.length; j++)
		        {
		            if (sched.timeCheck(this, true, sched.courseList[j], true))
		            {
		                temp++;
		            }
		        }   
		        if(temp > getCourseMax()) return false;    
    	} else {
		        int temp = 0;
		        for(int j = 0; j < sched.labList.length; j ++)
		        {
		            if (sched.timeCheck(this, false, sched.labList[j], false))
		            {
		                temp++;
		            }
		        }
		        if(temp > getLabMax()) return false;    
    	}
		return true;
	}
}