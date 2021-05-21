/*
 *  @author albua
 *  created on 20/05/2021
 */
package networking;

import model.Employee;
import model.Manager;
import service.Service;
import utils.Observable;
import utils.Observer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server implements Observable {
    private int port;
    private final Service service;
    private List<Observer> observers = new ArrayList<>();

    public Server(int port, Service service) {
        this.port = port;
        this.service = service;
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server started! Awaiting incoming connections...");
        while (true) {
            Socket socket = serverSocket.accept();
            Worker worker = new Worker(this, service, socket);
            addObserver(worker);
            Thread thread = new Thread(worker);
            thread.start();
        }
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Manager manager) {
        for (Observer observer : observers) {
            observer.update(manager);
        }
    }

    public void notifyEmployeeNewTask(Employee employee) {
        for (Observer observer : observers) {
            observer.updateEmployeeNewTask(employee);
        }
    }
}
