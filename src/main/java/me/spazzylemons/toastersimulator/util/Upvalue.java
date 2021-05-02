package me.spazzylemons.toastersimulator.util;

/**
 * A simple wrapper around a value. Useful for closed-over variables.
 * @param <T>
 */
public class Upvalue<T> {
    private T backing;

    public Upvalue() {}

    public Upvalue(T t) {
        backing = t;
    }

    public T get() {
        return backing;
    }

    public void set(T t) {
        backing = t;
    }
}
