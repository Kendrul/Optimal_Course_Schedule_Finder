package courseSchedulerCore;

public class Penalty {

	private final double defaultPenalty = 0;
	private final double defaultWeighting = 0;
	
    private double notPairPenalty = defaultPenalty;
    private double courseMinPenalty = defaultPenalty;
    private double labMinPenalty = defaultPenalty;
    private double sectionDiffPenalty = defaultPenalty;
     
    private double w_minfilled = defaultWeighting;
    private double w_pref = defaultWeighting;
    private double w_pair = defaultWeighting;
    private double w_secdif = defaultWeighting;
    
    public Penalty()
    {
    	//use default weight and penalty
    }
    
	public double getNotPairPenalty() {
		return notPairPenalty;
	}
	public void setNotPairPenalty(double notPairPenalty) {
		this.notPairPenalty = notPairPenalty;
	}
	public double getCourseMinPenalty() {
		return courseMinPenalty;
	}
	public void setCourseMinPenalty(double courseMinPenalty) {
		this.courseMinPenalty = courseMinPenalty;
	}
	public double getLabMinPenalty() {
		return labMinPenalty;
	}
	public void setLabMinPenalty(double labMinPenalty) {
		this.labMinPenalty = labMinPenalty;
	}
	public double getSectionDiffPenalty() {
		return sectionDiffPenalty;
	}
	public void setSectionDiffPenalty(double sectionDiffPenalty) {
		this.sectionDiffPenalty = sectionDiffPenalty;
	}
	public double getW_minfilled() {
		return w_minfilled;
	}
	public void setW_minfilled(double w_minfilled) {
		this.w_minfilled = w_minfilled;
	}
	public double getW_pref() {
		return w_pref;
	}
	public void setW_pref(double w_pref) {
		this.w_pref = w_pref;
	}
	public double getW_pair() {
		return w_pair;
	}
	public void setW_pair(double w_pair) {
		this.w_pair = w_pair;
	}
	public double getW_secdif() {
		return w_secdif;
	}
	public void setW_secdif(double w_secdif) {
		this.w_secdif = w_secdif;
	}
	
}
