package com.minelittlepony.common.client.gui;

import org.jetbrains.annotations.NotNull;

public interface IField<T, V extends IField<T, V>> {
    V onChange(@NotNull IChangeCallback<T> var1);

    V setValue(T var1);

    T getValue();

    @FunctionalInterface
    public interface IChangeCallback<T> {
        static <T> T none(T t) {
            return t;
        }

        T perform(T var1);
    }
}