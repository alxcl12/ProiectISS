/*
 *  @author albua
 *  created on 19/05/2021
 */
package model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


@Data
@NoArgsConstructor
public class Employee implements Serializable {
    private int employeeId;
    private String employeeUsername;
    private String password;
    private String name;
    private String status;
    private String time;
    private Manager manager;
    private Set<Task> tasks = new HashSet<>();

    public Employee(String employeeUsername, String password) {
        this.employeeUsername = employeeUsername;
        this.password = password;
    }

    public Employee(String employeeUsername, String password, String name, Manager manager) {
        this.employeeUsername = employeeUsername;
        this.password = password;
        this.name = name;
        this.status = "logged_out";
        this.time = null;
        this.manager = manager;
    }


}
