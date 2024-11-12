import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {

    // Scanner for input
    static Scanner sc = new Scanner(System.in);

    // Map to store tasks with an index
    static Map<Integer, Task> tasks = new HashMap<>();
    static int ind = 0;
    static final String FILE_NAME = "tasks.json";

    public static void main(String[] args) {
        // Load tasks from the file if it exists
        loadTasks();

        // Instructions to the user
        System.out.println("Commands: add, update, delete, mark as done, mark as in progress, list, list done, list not done, list in progress");

        // Main loop for user commands
        while (true) {
            System.out.println("Enter command:");
            String input = sc.nextLine().toLowerCase();

            if (input.equals("add") || input.equals("update") || input.equals("delete") ||
                input.equals("mark as done") || input.equals("mark as in progress") ||
                input.equals("list") || input.equals("list done") || input.equals("list not done") || input.equals("list in progress")) {
                executeCommand(input);
            } else {
                System.out.println("Invalid command. Please try again.");
            }
        }
    }

    // Method to handle different commands
    public static void executeCommand(String input) {
        switch (input) {
            case "add":
                addTask();
                break;
            case "update":
                updateTask();
                break;
            case "delete":
                deleteTask();
                break;
            case "mark as done":
                markTaskAsDone();
                break;
            case "mark as in progress":
                markTaskAsInProgress();
                break;
            case "list":
                listAllTasks();
                break;
            case "list done":
                listDoneTasks();
                break;
            case "list not done":
                listNotDoneTasks();
                break;
            case "list in progress":
                listInProgressTasks();
                break;
            default:
                System.out.println("Unknown command.");
        }
    }

    // Load tasks from the JSON file
    public static void loadTasks() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return; // No tasks file, nothing to load
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            StringBuilder content = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }

            JSONArray taskArray = new JSONArray(content.toString());
            for (int i = 0; i < taskArray.length(); i++) {
                JSONObject taskJson = taskArray.getJSONObject(i);
                Task task = new Task(taskJson.getString("description"), taskJson.getString("status"));
                task.setCreatedAt(taskJson.getString("createdAt"));
                task.setUpdatedAt(taskJson.getString("updatedAt"));
                tasks.put(taskJson.getInt("id"), task);
                ind = Math.max(ind, taskJson.getInt("id"));
            }
        } catch (IOException e) {
            System.out.println("Error reading tasks file: " + e.getMessage());
        }
    }

    // Save tasks to the JSON file
    public static void saveTasks() {
        JSONArray taskArray = new JSONArray();
        for (Task task : tasks.values()) {
            JSONObject taskJson = new JSONObject();
            taskJson.put("id", task.getId());
            taskJson.put("description", task.getDescription());
            taskJson.put("status", task.getStatus());
            taskJson.put("createdAt", task.getCreatedAt());
            taskJson.put("updatedAt", task.getUpdatedAt());
            taskArray.put(taskJson);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            writer.write(taskArray.toString(4)); // Pretty print JSON with indentation
        } catch (IOException e) {
            System.out.println("Error saving tasks: " + e.getMessage());
        }
    }

    // Add a new task
    public static void addTask() {
        System.out.println("Enter task description:");
        String description = sc.nextLine();
        String timestamp = getCurrentTimestamp();
        Task task = new Task(description, "not done", timestamp, timestamp);
        tasks.put(++ind, task);
        System.out.println("Task added: " + description);
        saveTasks(); // Save after adding
    }

    // Update an existing task
    public static void updateTask() {
        System.out.println("Enter task index to update:");
        int taskIndex = sc.nextInt();
        sc.nextLine(); // Consume the newline

        if (tasks.containsKey(taskIndex)) {
            System.out.println("Enter new task description:");
            String newDescription = sc.nextLine();
            Task task = tasks.get(taskIndex);
            task.setDescription(newDescription);
            task.setUpdatedAt(getCurrentTimestamp());
            System.out.println("Task at index " + taskIndex + " updated.");
            saveTasks(); // Save after updating
        } else {
            System.out.println("Task not found.");
        }
    }

    // Delete a task
    public static void deleteTask() {
        System.out.println("Enter task index to delete:");
        int taskIndex = sc.nextInt();
        sc.nextLine(); // Consume the newline

        if (tasks.containsKey(taskIndex)) {
            tasks.remove(taskIndex);
            System.out.println("Task at index " + taskIndex + " deleted.");
            saveTasks(); // Save after deleting
        } else {
            System.out.println("Task not found.");
        }
    }

    // Mark a task as done
    public static void markTaskAsDone() {
        System.out.println("Enter task index to mark as done:");
        int taskIndex = sc.nextInt();
        sc.nextLine(); // Consume the newline

        if (tasks.containsKey(taskIndex)) {
            Task task = tasks.get(taskIndex);
            task.setStatus("done");
            task.setUpdatedAt(getCurrentTimestamp());
            System.out.println("Task at index " + taskIndex + " marked as done.");
            saveTasks(); // Save after marking as done
        } else {
            System.out.println("Task not found.");
        }
    }

    // Mark a task as in progress
    public static void markTaskAsInProgress() {
        System.out.println("Enter task index to mark as in progress:");
        int taskIndex = sc.nextInt();
        sc.nextLine(); // Consume the newline

        if (tasks.containsKey(taskIndex)) {
            Task task = tasks.get(taskIndex);
            task.setStatus("in progress");
            task.setUpdatedAt(getCurrentTimestamp());
            System.out.println("Task at index " + taskIndex + " marked as in progress.");
            saveTasks(); // Save after marking as in progress
        } else {
            System.out.println("Task not found.");
        }
    }

    // List all tasks
    public static void listAllTasks() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks available.");
        } else {
            tasks.forEach((index, task) -> {
                System.out.println("Index: " + index + ", Description: " + task.getDescription() + ", Status: " + task.getStatus() +
                        ", Created At: " + task.getCreatedAt() + ", Updated At: " + task.getUpdatedAt());
            });
        }
    }

    // List tasks by status
    public static void listByStatus(String status) {
        boolean found = false;
        for (Task task : tasks.values()) {
            if (task.getStatus().equals(status)) {
                System.out.println("Description: " + task.getDescription() + ", Status: " + task.getStatus() +
                        ", Created At: " + task.getCreatedAt() + ", Updated At: " + task.getUpdatedAt());
                found = true;
            }
        }
        if (!found) {
            System.out.println("No tasks with status: " + status);
        }
    }

    // Get the current timestamp
    private static String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }
}

// Task class to hold description, status, and timestamps
class Task {
    private int id;
    private String description;
    private String status;
    private String createdAt;
    private String updatedAt;

    public Task(String description, String status) {
        this.description = description;
        this.status = status;
        this.createdAt = this.updatedAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public Task(String description, String status, String createdAt, String updatedAt) {
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
