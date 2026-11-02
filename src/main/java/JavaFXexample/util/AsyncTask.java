package JavaFXexample.util;

import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.beans.property.BooleanProperty;
import javafx.concurrent.Task;

public final class AsyncTask {
    private AsyncTask() {
    }

    public static <T> void run(Supplier<T> work, Consumer<T> onSuccess, Consumer<Throwable> onError) {
        run(work, onSuccess, onError, null);
    }

    public static void run(Runnable work, Runnable onSuccess, Consumer<Throwable> onError) {
        run(work, onSuccess, onError, null);
    }

    public static void run(Runnable work, Runnable onSuccess, Consumer<Throwable> onError, BooleanProperty loading) {
        run(() -> {
            work.run();
            return null;
        }, ignored -> onSuccess.run(), onError, loading);
    }

    public static <T> void run(
            Supplier<T> work,
            Consumer<T> onSuccess,
            Consumer<Throwable> onError,
            BooleanProperty loading) {
        Task<T> task = new Task<>() {
            @Override
            protected T call() {
                return work.get();
            }
        };

        if (loading != null) {
            loading.unbind();
            loading.bind(task.runningProperty());
        }

        task.setOnSucceeded(event -> onSuccess.accept(task.getValue()));
        task.setOnFailed(event -> {
            Throwable error = task.getException();
            if (onError != null) {
                onError.accept(error);
            }
        });

        Thread thread = new Thread(task, "db-task");
        thread.setDaemon(true);
        thread.start();
    }
}
