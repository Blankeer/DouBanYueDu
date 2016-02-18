package com.douban.book.reader.controller;

public class TaskController extends AbsTaskController {

    private static final class SingletonHolder {
        static final TaskController INSTANCE;

        private SingletonHolder() {
        }

        static {
            INSTANCE = new TaskController();
        }
    }

    public static TaskController getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public static void run(Runnable runnable) {
        getInstance().execute(runnable, runnable);
    }
}
