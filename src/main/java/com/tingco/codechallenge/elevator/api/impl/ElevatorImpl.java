package com.tingco.codechallenge.elevator.api.impl;

import com.google.common.eventbus.Subscribe;
import com.tingco.codechallenge.elevator.api.Elevator;

import static java.lang.String.format;


/**
 * Created by dmitry on 2017-01-23.
 */
public class ElevatorImpl implements Elevator {

    private int id;

    private int currentFloor = 0;
    private int addressedFloor = 0;
    private Direction direction = Direction.NONE;
    private boolean busy = false;

    private final long TIME_PER_FLOOR = 500;

    public ElevatorImpl(int id) {
        this.id = id;
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public int getAddressedFloor() {
        return addressedFloor;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public synchronized void moveElevator(int toFloor) {
        busy = true;
        System.out.println(format("Elevator%s: got signal to move to floor %s", id, toFloor));
        addressedFloor = toFloor;
        direction = (addressedFloor == currentFloor) ? Direction.NONE : (addressedFloor < currentFloor) ? Direction.DOWN : Direction.UP;
        System.out.println(format("Elevator%s: on floor %s", id, currentFloor));
        try {
            while (addressedFloor != currentFloor) {
                Thread.sleep(TIME_PER_FLOOR);
                if (direction.equals(Direction.DOWN)) {
                    currentFloor--;
                } else {
                    currentFloor++;
                }
                System.out.println(format("Elevator%s: on floor %s", id, currentFloor));
            }
            direction = Direction.NONE;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isBusy() {
        return busy;
    }

    @Override
    public int currentFloor() {
        return currentFloor;
    }

    @Override
    public String toString() {
        return "ElevatorImpl{" +
                "id=" + id +
                ", currentFloor=" + currentFloor +
                ", addressedFloor=" + addressedFloor +
                ", direction=" + direction +
                ", busy=" + isBusy() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ElevatorImpl elevator = (ElevatorImpl) o;

        return id == elevator.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Subscribe
    public synchronized void handleRelease(ReleaseEvent event) {
        if (event.getElevatorId() == id) {
            busy = false;
        }
    }
}
