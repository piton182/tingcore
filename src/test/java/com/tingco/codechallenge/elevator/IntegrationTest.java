package com.tingco.codechallenge.elevator;

import com.tingco.codechallenge.elevator.api.Elevator;
import com.tingco.codechallenge.elevator.api.ElevatorController;
import com.tingco.codechallenge.elevator.api.impl.ElevatorImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tingco.codechallenge.elevator.config.ElevatorApplication;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * Boiler plate test class to get up and running with a test faster.
 *
 * @author Sven Wesley
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ElevatorApplication.class)
public class IntegrationTest {

    @Autowired
    ElevatorController elevatorController;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @PostConstruct
    public void init() throws Exception {
        scheduler.scheduleAtFixedRate(() -> {
            for (Elevator elevator : elevatorController.getElevators()) {
                System.out.println(
                        elevator.toString()
                );
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
    }

    @Test
    public void simulateAnElevatorShaft() throws InterruptedException {
        User user1 = new User("Adam", 5, 3);
        User user2 = new User("Carin", 0, 4);
        User user3 = new User("Olle", 1, 4);
        User user4 = new User("Anna", 1, 0);

        new Thread(user1).start();
        new Thread(user2).start();
        new Thread(user3).start();
        new Thread(user4).start();

        while (!(user1.isArrived() && user2.isArrived() && user3.isArrived() && user4.isArrived())) {
            Thread.sleep(500);
        }
        assertEquals(user1.getToFloor(), user1.getOnFloor());
        assertEquals(user2.getToFloor(), user2.getOnFloor());
        assertEquals(user3.getToFloor(), user3.getOnFloor());
        assertEquals(user4.getToFloor(), user4.getOnFloor());
    }

    class User implements Runnable {

        private String name;
        private int fromFloor;
        private int toFloor;
        private int onFloor;
        boolean arrived = false;

        User(String name, int fromFloor, int toFloor) {
            this.name = name;
            this.fromFloor = fromFloor;
            this.onFloor = fromFloor;
            this.toFloor = toFloor;
        }

        @Override
        public synchronized void run() {
            System.out.println("" + name + ": on floor " + fromFloor);
            System.out.println("" + name + ": requesting an elevator");
            Elevator elevator = elevatorController.requestElevator(fromFloor);
            if (elevator == null) {
                System.out.println("" + name + ": no available elevators; waiting...");
            }
            while (elevator == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                elevator = elevatorController.requestElevator(fromFloor);
            }
            if (elevator.currentFloor() == fromFloor) {
                System.out.println("" + name + ": elevator " + elevator.getId() + " is on my floor!");
            } else {
                System.out.println("" + name + ": elevator " + elevator.getId() + " is going " + elevator.getDirection().toString() + " to pick me up; wating...");
            }
            while (elevator.currentFloor() != fromFloor) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("" + name + " going to floor " + 3 +  " with elevator " + elevator.getId());
            elevator.moveElevator(toFloor);
            System.out.println("" + name + ": on floor " + elevator.currentFloor() + "; getting off elevator " + elevator.getId());
            onFloor = elevator.currentFloor();
            elevatorController.releaseElevator(elevator);
            arrived = true;
        }

        boolean isArrived() {
            return arrived;
        }

        int getToFloor() {
            return toFloor;
        }

        int getOnFloor() {
            return onFloor;
        }
    }

}
