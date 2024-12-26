package com.ulises.javasemiseniorcommerce.testUtils;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.util.Optional;

/**
 * @author ulide
 */
public class TestLoggerExtension implements TestWatcher {
    @Override
    public void testSuccessful(ExtensionContext context) {
        System.out.println("\u001B[32m[SUCCESS]\u001B[0m Test pasó correctamente: " + context.getDisplayName());
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        System.out.println("\u001B[31m[FAILURE]\u001B[0m Test falló: " + context.getDisplayName());
        System.out.println("Razón: " + cause.getMessage());
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        System.out.println("\u001B[33m[ABORTED]\u001B[0m Test abortado: " + context.getDisplayName());
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        System.out.println("\u001B[34m[DISABLED]\u001B[0m Test deshabilitado: " + context.getDisplayName() +
                reason.map(r -> " (" + r + ")").orElse(""));
    }
}
