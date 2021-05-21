/*
 *  @author albua
 *  created on 20/05/2021
 */
package utils;

import model.Employee;
import model.Manager;

public interface Observer {
    void update(Manager manager);
    void updateEmployeeNewTask(Employee employee);
}
