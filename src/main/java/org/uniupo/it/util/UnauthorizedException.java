package org.uniupo.it.util;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String Message) {
        super(Message);
    }
}
