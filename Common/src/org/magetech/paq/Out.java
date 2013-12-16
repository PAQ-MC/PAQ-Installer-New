package org.magetech.paq;

/**
 * Created by Aleksander on 06.12.13.
 */
public class Out<T> {
    private T _value = null;

    public Out() {}

    public Out(T defaultValue) {
        _value = defaultValue;
    }

    public void setValue(T value) {
        _value = value;
    }

    public T getValue() {
        return _value;
    }
}
