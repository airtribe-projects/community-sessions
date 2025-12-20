package org.example.models;

import org.example.enums.Direction;
import org.example.enums.RequestType;

import java.time.LocalDateTime;

/**
 * Represents a request for elevator service
 * Encapsulates what changes: request details can vary
 */
public class Request {
    private final int sourceFloor;
    private final Integer destinationFloor; // Nullable for hall requests
    private final Direction direction;
    private final RequestType requestType;
    private final LocalDateTime timestamp;

    private Request(Builder builder) {
        this.sourceFloor = builder.sourceFloor;
        this.destinationFloor = builder.destinationFloor;
        this.direction = builder.direction;
        this.requestType = builder.requestType;
        this.timestamp = LocalDateTime.now();
    }

    public int getSourceFloor() {
        return sourceFloor;
    }

    public Integer getDestinationFloor() {
        return destinationFloor;
    }

    public Direction getDirection() {
        return direction;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Request{" +
                "type=" + requestType +
                ", from=" + sourceFloor +
                ", to=" + destinationFloor +
                ", direction=" + direction +
                ", time=" + timestamp +
                '}';
    }

    // Builder pattern for flexible object creation (SOLID: Single Responsibility)
    public static class Builder {
        private int sourceFloor;
        private Integer destinationFloor;
        private Direction direction;
        private RequestType requestType;

        public Builder sourceFloor(int sourceFloor) {
            this.sourceFloor = sourceFloor;
            return this;
        }

        public Builder destinationFloor(Integer destinationFloor) {
            this.destinationFloor = destinationFloor;
            return this;
        }

        public Builder direction(Direction direction) {
            this.direction = direction;
            return this;
        }

        public Builder requestType(RequestType requestType) {
            this.requestType = requestType;
            return this;
        }

        public Request build() {
            return new Request(this);
        }
    }
}
