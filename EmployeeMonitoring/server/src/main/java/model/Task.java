/*
 *  @author albua
 *  created on 19/05/2021
 */
package model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class Task implements Serializable {
    private int taskId;
    private String description;
    private String status;
    private Manager manager;
    private Employee employee;
    private String employeeUsername;

    public void setEmployee(Employee employee){
        this.employee = employee;
        this.employeeUsername = employee.getEmployeeUsername();
    }

    @Override
    public boolean equals(Object o){
        Task other = (Task) o;
        return (this.employeeUsername.equals(other.employeeUsername) && this.description.equals(other.description));
    }
}
