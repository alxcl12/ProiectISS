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
public class Manager implements Serializable {
    private String managerUsername;
    private String password;
    private String name;
    private Set<Employee> employees = new HashSet<>();
    private Set<Task> tasks = new HashSet<>();

    public Manager(String managerUsername, String password) {
        this.managerUsername = managerUsername;
        this.password = password;
    }
}
