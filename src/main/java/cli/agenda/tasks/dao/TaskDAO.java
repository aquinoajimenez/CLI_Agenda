package cli.agenda.tasks.dao;

import cli.agenda.tasks.model.Task;
import java.util.List;
import java.util.Optional;

public interface TaskDAO {

    /**
     * Inserts a new task into the database.
     * @param task the task to insert
     * @return the inserted task with any database-generated fields
     */
    Task insert(Task task);

    /**
     * Updates an existing task in the database.
     * @param task the task with updated values
     * @return true if updated, false if not found
     */
    boolean update(Task task);

    /**
     * Finds a task by its ID.
     * @param id the task ID
     * @return Optional containing the task if found
     */
    Optional<Task> findById(String id);

    /**
     * Finds all tasks.
     * @return list of all tasks
     */
    List<Task> findAll();

    /**
     * Finds tasks by status.
     * @param status the status to filter by
     * @return list of tasks with the given status
     */
    List<Task> findByStatus(cli.agenda.tasks.model.Status status);

    /**
     * Deletes a task by its ID.
     * @param id the task ID
     * @return true if deleted, false if not found
     */
    boolean deleteById(String id);
}
