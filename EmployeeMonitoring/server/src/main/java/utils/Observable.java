/*
 *  @author albua
 *  created on 20/05/2021
 */
package utils;

import model.Manager;

public interface Observable {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers(Manager manager);
}
