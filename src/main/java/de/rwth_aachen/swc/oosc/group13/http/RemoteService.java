package de.rwth_aachen.swc.oosc.group13.http;

import java.io.IOException;

/**
 * Functional Interface for a remote call.
 * It is useful on the circuit breaker.
 */
@FunctionalInterface
public interface RemoteService {
    String call() throws IOException;
}
