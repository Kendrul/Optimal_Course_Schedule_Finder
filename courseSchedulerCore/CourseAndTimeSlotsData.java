package courseSchedulerCore;
import java.util.ArrayList;

public class CourseAndTimeSlotsData {
	
	private final boolean testValue = true;
	
    private ArrayList<Courses> coursesVector = null;
    private ArrayList<Labs> labsVector = null;
  
    private ArrayList<TimeSlot> courseSlotsVector = null;
    private ArrayList<TimeSlot> labSlotsVector = null;
    
    public CourseAndTimeSlotsData()
    {
    	coursesVector = new ArrayList<>();
    	labsVector = new ArrayList<>();
    	courseSlotsVector = new ArrayList<>();
    	labSlotsVector = new ArrayList<>();
    }

	public ArrayList<Courses> getCoursesVector() {
		return coursesVector;
	}

	public void setCoursesVector(ArrayList<Courses> coursesVector) {
		this.coursesVector = coursesVector;
	}

	public ArrayList<Labs> getLabsVector() {
		return labsVector;
	}

	public void setLabsVector(ArrayList<Labs> labsVector) {
		this.labsVector = labsVector;
	}

	public ArrayList<TimeSlot> getCourseSlotsVector() {
		return courseSlotsVector;
	}

	public void setCourseSlotsVector(ArrayList<TimeSlot> courseSlotsVector) {
		this.courseSlotsVector = courseSlotsVector;
	}

	public ArrayList<TimeSlot> getLabSlotsVector() {
		return labSlotsVector;
	}

	public void setLabSlotsVector(ArrayList<TimeSlot> labSlotsVector) {
		this.labSlotsVector = labSlotsVector;
	}
    
	public boolean hardCode(int index, boolean isCourse, Schedule child)
	{//this method checks for certain restrictions such as partAssign, or other hard coded values
		boolean isValid = testValue;

		if(isCourse)
		{
			Courses course = getCoursesVector().get(index);
			
			//for the CPSC 813/913 courses	
			if((course.getCourseNumber().equals("813") || course.getCourseNumber().equals("913")) && course.getDepartment().equals("CPSC"))
			{
				isValid = child.assign13(index, getLabSlotsVector().get(course.getPartAssign().get(0))) && course.constr(child, index) && getLabSlotsVector().get(course.getPartAssign().get(0)).constr(child, index, !isCourse);				
			}

			else if (course.getPartAssign().size() > 0)
			{//do the partial assignment
				isValid = child.assign(index, getCourseSlotsVector().get(course.getPartAssign().get(0)), isCourse) && course.constr(child, index) && getCourseSlotsVector().get(course.getPartAssign().get(0)).constr(child, index, isCourse);
			}

			//System.out.println(course);
		} 
		else //its a lab
		{
			Labs lab = getLabsVector().get(index) ;
		
			if (lab.getPartAssign().size() > 0)
			{//do the partial assignment
				isValid = child.assign(index, getLabSlotsVector().get(lab.getPartAssign().get(0)), isCourse) && lab.constr(child, index) && getLabSlotsVector().get(lab.getPartAssign().get(0)).constr(child, index, isCourse);
			}		

			//System.out.println(lab);
		}//end if-else (not a lecture)		
		
		return isValid;
	}

}
