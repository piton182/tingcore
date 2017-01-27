package com.tingco.codechallenge.elevator.api.impl;

import com.google.common.eventbus.EventBus;
import com.tingco.codechallenge.elevator.api.Elevator;
import com.tingco.codechallenge.elevator.api.ElevatorController;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * Created by dmitry on 2017-01-27.
 */
public class ElevatorControllerImpl implements ElevatorController {

    @Autowired
    private Executor executor;

    @Autowired
    private EventBus eventBus;

    private List<Elevator> elevators;

    public ElevatorControllerImpl(int numberOfElevators) {
        elevators = new ArrayList<>(numberOfElevators);
        for (int i = 1; i < numberOfElevators + 1; i++) {
            Elevator elevator = new ElevatorImpl(i);
            elevators.add(elevator);
        }
    }

    @PostConstruct
    void init() throws Exception {
        for (Elevator elevator : elevators) {
            eventBus.register(elevator);
        }
    }

    private void holdUntilElevatorStartsMoving(Elevator elevator) {
        while (!elevator.isBusy()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public synchronized Elevator requestElevator(int toFloor) {
        // TODO: look for the closest elevator
        List<Elevator> availableElevators = elevators.stream().filter(elevator -> !elevator.isBusy()).collect(Collectors.toList());
        if (availableElevators.isEmpty()) {
            return null;
        } else {
            final Elevator elevator = availableElevators.get(0);
            executor.execute(() -> elevator.moveElevator(toFloor));
            holdUntilElevatorStartsMoving(elevator);
            return elevator;
        }
    }

    @Override
    public List<Elevator> getElevators() {
        return Collections.unmodifiableList(elevators);
    }

    @Override
    public void releaseElevator(Elevator elevator) {
        eventBus.post(new ReleaseEvent(elevator.getId()));
    }
}
