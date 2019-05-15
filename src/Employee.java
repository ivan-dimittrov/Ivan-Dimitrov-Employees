import java.time.LocalDate;

public class Employee implements Comparable<Employee> {

	private int empID;
	private int projectID;
	private LocalDate startDate;
	private LocalDate endDate;
	private long workDays;

	public Employee(int empID, int projectID, LocalDate date1, LocalDate date2, long workDays) {
		super();
		this.empID = empID;
		this.projectID = projectID;
		this.startDate = date1;
		this.endDate = date2;
		this.workDays = workDays;
	}

	public int getEmpID() {
		return empID;
	}

	public int getProjectID() {
		return projectID;
	}

	public long getWorkDays() {
		return workDays;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + empID;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Employee other = (Employee) obj;
		if (empID != other.empID)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "EmpID: " + this.empID + "| ProjID: " + this.projectID + "| Start: " + this.startDate + "| End: "
				+ this.endDate + "| Work Days: " + this.workDays;
	}

	@Override
	public int compareTo(Employee o) {
		return this.startDate.compareTo(o.startDate);
	}

}
