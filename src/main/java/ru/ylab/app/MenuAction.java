package ru.ylab.app;

@FunctionalInterface
public interface MenuAction {
    void execute(int choice);
}
