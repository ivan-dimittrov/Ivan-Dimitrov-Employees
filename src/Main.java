import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

//EmpID, ProjectID, DateFrom, DateTo
//143, 12, 2013-11-01, 2014-01-05
//218, 10, 2012-05-16, NULL = today
//143, 10, 2009-01-01, 2011-04-27

public class Main {

	public static final String EMPLOYEES_DATA_PATH = "Employees Data/Data-employees.txt";

	public static void main(String[] args) {

		HashMap<Integer, HashSet<Employee>> employeeMap = new HashMap<>();
		String data = readData(EMPLOYEES_DATA_PATH);
		long maxValue = 0;

		if (!data.isEmpty()) {

			employeeMap = addEmployeeData(data);

			// The integer key is the projectID
			HashMap<Integer, ArrayList<Employee>> valuesSortedByDates = sortStartDates(employeeMap);
			HashMap<String, Long> result = getTheBestTeam(valuesSortedByDates);

			try {
				maxValue = result.values().stream().max(Long::compare).get();
			} catch (NoSuchElementException e) {
				System.out.println("It seems that there are no teams working on common projects");
			}

			for (Map.Entry<String, Long> employees : result.entrySet()) {
				if (employees.getValue() == maxValue) {
					String[] empIDs = employees.getKey().split("-");
					System.out.println("------------------------------");
					System.out.println(
							"The best team is -> Employee ID: " + empIDs[0] + " and " + "Employee ID: " + empIDs[1]);
					System.out.println("Together they have " + maxValue + " work days by common projects.");
				}
			}
		}
	}

	/**
	 * Returns a String with all of the data from a text file .
	 * 
	 * @param Location of the file
	 * @return String
	 */

	private static String readData(String filePath) {
		String data = "";
		StringBuilder sb = new StringBuilder();
		File employeesData = new File(filePath);

		try (Scanner sc = new Scanner(employeesData)) {
			while (sc.hasNextLine()) {
				sb.append(sc.nextLine() + "\n");
			}
		} catch (FileNotFoundException e) {
			System.out.println("Employee file missing. "
					+ "\nPlease paste Data-employees.txt file in the Employees Data directory.");
		}

		data = sb.toString();

		return data;

	}

	/**
	 * The method sorts each employee by projects and verifies the validity of the
	 * data. Invalid data will be skipped.
	 * 
	 * @param String containing data about employees.
	 * @return HashMap
	 */

	private static HashMap<Integer, HashSet<Employee>> addEmployeeData(String data) {
		String[] arr = data.split("\n");
		HashMap<Integer, HashSet<Employee>> map = new HashMap<Integer, HashSet<Employee>>();
		String[] row;
		boolean isDataCorrect = true;

		for (int i = 0; i < arr.length; i++) {
			row = arr[i].split(", ");

			if (row.length == 4) {

				try {
					int projectID = Integer.parseInt(row[1].trim());
					int employeeID = Integer.parseInt(row[0].trim());
					LocalDate dateFrom = LocalDate.parse(row[2].trim());
					LocalDate endDate;

					if (row[3].trim().equalsIgnoreCase("null")) {
						endDate = LocalDate.now();
					} else {
						endDate = LocalDate.parse(row[3]);
					}

					if (dateFrom.isAfter(endDate)) {
						throw new Exception();
					}

					long daysBetween = ChronoUnit.DAYS.between(dateFrom, endDate);
					Employee employee = new Employee(employeeID, projectID, dateFrom, endDate, daysBetween);

					if (!map.containsKey(projectID)) {
						map.put(projectID, new HashSet<Employee>());

					}
					map.get(projectID).add(employee);
				} catch (Exception e) {
					isDataCorrect = false;
				}

			} else {
				isDataCorrect = false;
			}
		}
		if (!isDataCorrect) {
			System.out.println("You have some invalid data. This may cause different results.");
			System.out.println("Example of valid data: 218, 10, 2012-05-16, 2013-05-01 or NULL = today ");
			System.out.println();
		}

		return map;
	}

	/**
	 * The method sorts the employees by start date.
	 * 
	 * @return HashMap
	 */

	private static HashMap<Integer, ArrayList<Employee>> sortStartDates(HashMap<Integer, HashSet<Employee>> map) {
		HashMap<Integer, ArrayList<Employee>> valuesSortedByDates = new HashMap<>();

		for (Map.Entry<Integer, HashSet<Employee>> entry : map.entrySet()) {
			HashSet<Employee> value = entry.getValue();
			ArrayList<Employee> list = new ArrayList<>(value);
			Collections.sort(list);
			valuesSortedByDates.put(entry.getKey(), list);
		}

		for (Map.Entry<Integer, ArrayList<Employee>> entry : valuesSortedByDates.entrySet()) {
			ArrayList<Employee> value = entry.getValue();
			for (Employee employee : value) {
				System.out.println(employee.toString());
			}
		}

		return valuesSortedByDates;

	}

	/**
	 * The method calculates and returns the team that has the most working days on
	 * common projects.
	 * 
	 * @return HashMap
	 */

	private static HashMap<String, Long> getTheBestTeam(HashMap<Integer, ArrayList<Employee>> valuesSortedByDates) {
		HashMap<String, Long> result = new HashMap<>();

		for (Map.Entry<Integer, ArrayList<Employee>> entry : valuesSortedByDates.entrySet()) {
			ArrayList<Employee> list = entry.getValue();

			for (int i = 0; i < list.size() - 1; i++) {
				Employee emp1 = list.get(i);
				Employee emp2 = list.get(i + 1);
				long togetherWorkDays = 0;

				if ((emp1.getStartDate().isBefore(emp2.getStartDate())
						|| emp1.getStartDate().isEqual(emp2.getStartDate()))
						&& (emp2.getEndDate().isBefore(emp1.getEndDate()))
						|| emp2.getEndDate().isEqual(emp1.getEndDate())) {

					long daysEmp1 = emp1.getWorkDays();
					long daysEmp2 = emp2.getWorkDays();
					togetherWorkDays = daysEmp1 >= daysEmp2 ? daysEmp2 : daysEmp1;
					String empIDs = String.valueOf(emp1.getEmpID() + "-" + emp2.getEmpID());
					insertTeams(result, empIDs, togetherWorkDays);

				} else if ((emp1.getStartDate().isBefore(emp2.getStartDate())
						|| emp1.getStartDate().isEqual(emp2.getStartDate()))
						&& (emp2.getEndDate().isAfter(emp1.getEndDate()))
						&& emp2.getStartDate().isBefore(emp1.getEndDate())) {

					String empIDs = String.valueOf(emp1.getEmpID() + "-" + emp2.getEmpID());
					togetherWorkDays = ChronoUnit.DAYS.between(emp2.getStartDate(), emp1.getEndDate());
					insertTeams(result, empIDs, togetherWorkDays);

				} else if ((emp1.getStartDate().isBefore(emp2.getStartDate())
						|| emp1.getStartDate().isEqual(emp2.getStartDate()))
						&& (emp2.getStartDate().isEqual(emp1.getEndDate()))) {

					String empIDs = String.valueOf(emp1.getEmpID() + "-" + emp2.getEmpID());
					togetherWorkDays = 1;
					insertTeams(result, empIDs, togetherWorkDays);
				}
			}
		}

		return result;
	}

	private static void insertTeams(HashMap<String, Long> result, String empIDs, long togetherDays) {
		if (!result.containsKey(empIDs)) {
			result.put(empIDs, togetherDays);
		} else {
			result.put(empIDs, result.get(empIDs) + togetherDays);
		}
	}
}
