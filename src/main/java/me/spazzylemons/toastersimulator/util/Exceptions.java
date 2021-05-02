package me.spazzylemons.toastersimulator.util;

public class Exceptions {
    @FunctionalInterface
    public interface ThrowingRunnable {
        void run() throws Exception;
    }

    /**
     * Try to run some code, and if it throws an exception, close the resource, and propagate the exception.
     * <br/>
     * Example use:
     * <pre>
     * {@code
     * // Allocate a resource.
     * SomeResource r = new SomeResource();
     * // If this code fails, r will be closed and the exception will propagate. If it succeeds, r will remain open.
     * Exceptions.closeOnFailure(someResource, () -> {
     *     // Do something with the resource.
     *     r.doSomething();
     *     r.doSomethingElse();
     * });
     * // Return the still-open resource.
     * return r;
     * }
     * </pre>
     * @param closeable The resource to close on failure.
     * @param runnable The code to execute.
     * @throws Exception if runnable throws an exception.
     */
    public static void closeOnFailure(AutoCloseable closeable, ThrowingRunnable runnable) throws Exception {
        try {
            runnable.run();
        } catch (Exception e) {
            closeable.close();
            throw e;
        }
    }

    /**
     * Run some code. If it fails with a checked exception, wrap it in a RuntimeException. If it fails with an unchecked
     * exception, rethrow it as-is.
     * @param runnable The code to run.
     */
    public static void wrapChecked(ThrowingRunnable runnable) {
        try {
            runnable.run();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
