package cli.agenda.tasks.exception;

public class TaskAlreadyCompletedException extends TaskValidationException {

    public TaskAlreadyCompletedException(String message) {
        super(message);
    }

    public TaskAlreadyCompletedException(String message, Throwable cause) {
        super(message, cause);
    }
}