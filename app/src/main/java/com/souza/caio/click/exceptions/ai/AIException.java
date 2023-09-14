package com.souza.caio.click.exceptions.ai;

import java.net.HttpURLConnection;

public class AIException extends RuntimeException {
    protected int statusCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_MESSAGE = "Erro ao se comunicar com Inteligência Artificial.";

    public AIException() {
        super(DEFAULT_MESSAGE);
    }

    public AIException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public AIException(String message) {
        super(message);
    }

    public int getStatusCode() {
        return statusCode;
    }
}
