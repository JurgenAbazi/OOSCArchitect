package de.rwth_aachen.swc.oosc.group13.http;

import java.io.IOException;

/**
 * Implementation of the circuit breaker design pattern.
 */
public class CircuitBreaker {
    /**
     * The states of the state machine.
     * CLOSED    - The circuit breaker works without any failures.
     * OPEN      - Requests from the services will fail immediately.
     * HALF_OPEN - Allows only a limited number of requests from the service to pass
     * through and invoke the operation.
     */
    enum State {
        CLOSED,
        OPEN,
        HALF_OPEN
    }

    private static final long NO_FAILURE_TIME = -1;
    private final RemoteService service;
    private final long timeout;
    private final long retryTimePeriod;
    private final int failureThreshold;

    private State state;
    private int failureCount;
    private long lastFailureTime;
    private String lastFailureResponse;

    /**
     * Constructor.
     *
     * @param service          A remote service which we want to call.
     * @param timeout          The timeout specified in milliseconds.
     * @param failureThreshold The threshold.
     * @param retryTimePeriod  The retry time period in milliseconds.
     */
    public CircuitBreaker(RemoteService service,
                          long timeout,
                          int failureThreshold,
                          long retryTimePeriod) {
        this.service = service;
        this.timeout = timeout;
        this.failureThreshold = failureThreshold;
        this.retryTimePeriod = retryTimePeriod;
        this.state = State.CLOSED;
    }

    /**
     * Set the current state of our circuit breaker.
     * Evaluates the state based on the failure count, threshold and last failure time.
     */
    private void evaluateState() {
        if (failureCount >= failureThreshold) {
            state = (System.currentTimeMillis() - lastFailureTime) > retryTimePeriod
                    ? State.HALF_OPEN
                    : State.OPEN;
        } else {
            state = State.CLOSED;
        }
    }

    /**
     * Executes the service call.
     *
     * @return Remote service response or a stale response.
     * @throws Exception Custom exception.
     */
    public String call() throws Exception {
        evaluateState();

        switch (state) {
            case HALF_OPEN:
            case CLOSED:
                try {
                    String result = callWithTimeout();
                    reset();
                    return result;
                } catch (Exception e) {
                    System.out.println("CircuitBreaker.call error: " + e.getMessage());
                    recordFailure(e.getMessage());
                    throw e;
                }
            case OPEN:
                System.out.println("Circuit breaker is in open state. Throwing cached response.");
                throw new Exception(lastFailureResponse);
            default:
                throw new Exception("Unexpected State in the state machine");
        }
    }

    /**
     * Resets everything to its default values (when the circuit is initialized).
     */
    public void reset() {
        failureCount = 0;
        lastFailureTime = NO_FAILURE_TIME;
        state = State.CLOSED;
    }

    /**
     * Should we get a timeout, we increment the failure counter.
     */
    public void recordFailure(String response) {
        failureCount++;
        lastFailureTime = System.currentTimeMillis();
        lastFailureResponse = response;
    }

    /**
     * Calls the <code>RemoteService</code> with a timeout, If it doesn't get an answer
     * before the timeout, it throws an exception.
     * Creates a runnable task, wraps it in a daemon thread and starts it. If after the
     * timeout time has passed and the thread is still active, it interrupts the thread
     * and throws an <code>Exception</code>.
     *
     * @return The result of the <code>service</code> call.
     * @throws Exception The call timed-out.
     */
    public String callWithTimeout() throws Exception {
        final String[] result = new String[1];
        Thread thread = new Thread(() -> {
            try {
                result[0] = service.call();
            } catch (IOException e) {
                System.out.println("CircuitBreaker.callWithTimeout thread: " + e.getMessage());
                System.out.println(e.getMessage());
                result[0] = e.getMessage();
            }
        });
        thread.setDaemon(true);
        thread.start();

        try {
            thread.join(timeout);
        } catch (InterruptedException ignored) {
        }
        if (thread.isAlive()) {
            thread.interrupt();
            throw new Exception("The remote service timed-out.");
        }

        return result[0];
    }

    /**
     * @return A string representation of the object.
     */
    @Override
    public String toString() {
        return "CircuitBreaker {" + "\n\t" +
                "timeout = " + timeout + "\n\t" +
                "retryTimePeriod = " + retryTimePeriod + "\n\t" +
                "failureThreshold = " + failureThreshold + "\n\t" +
                "state = " + state + "\n\t" +
                "failureCount = " + failureCount + "\n\t" +
                "lastFailureTime = " + lastFailureTime + "\n\t" +
                "lastFailureResponse = '" + lastFailureResponse + '\'' +
                "\n}";
    }
}
