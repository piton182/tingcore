package com.tingco.codechallenge.elevator.api.impl;

/**
 * Created by dmitry on 2017-01-27.
 */
public class ElevatorEvent {
    private int elevatorId;

    public ElevatorEvent(int elevatorId) {
        this.elevatorId = elevatorId;
    }

    public int getElevatorId() {
        return elevatorId;
    }
}
