package database.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import database.Database;
import model.Client;
import model.Project;
import model.ProjectWorker;
import model.Worker;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabasePopulateService {
    private static final String INSERT_WORKER = "INSERT INTO worker (name, birthday, level, salary) VALUES (?, ?, ?, ?)";
    private static final String INSERT_CLIENT = "INSERT INTO client (name) VALUES (?)";
    private static final String INSERT_PROJECT = "INSERT INTO project (name, client_id, start_date, finish_date) VALUES (?, ?, ?, ?)";
    private static final String INSERT_PROJECT_WORKER = "INSERT INTO project_worker (project_id , worker_id) VALUES (?, ?)";
    private static final String WORKER_JSON = "src/main/resources/worker.json";
    private static final String CLIENT_JSON = "src/main/resources/client.json";
    private static final String PROJECT_JSON = "src/main/resources/project.json";
    private static final String PROJECT_WORKER_JSON = "src/main/resources/projectWorker.json";
    private final Connection connection = Database.getInstance().getConnection();

    public void insertWorkerData() {
        List<Worker> workerList = getEntityListFromJson(Worker.class, WORKER_JSON);
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_WORKER)){
            for (Worker worker : workerList) {
                preparedStatement.setString(1, worker.getName());
                preparedStatement.setString(2, worker.getBirthday());
                preparedStatement.setString(3, worker.getLevel());
                preparedStatement.setInt(4, worker.getSalary());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void insertClientData() {
        List<Client> clientList = getEntityListFromJson(Client.class, CLIENT_JSON);
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_CLIENT)){
            for (Client client : clientList) {
                preparedStatement.setString(1, client.getName());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertProjectData() {
        List<Project> projectList = getEntityListFromJson(Project.class, PROJECT_JSON);
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_PROJECT)){
            for (Project project : projectList) {
                preparedStatement.setString(1, project.getName());
                preparedStatement.setLong(2, project.getClientId());
                preparedStatement.setString(3, project.getStartDate());
                preparedStatement.setString(4, project.getFinishDate());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertProjectWorkerData() {
        List<ProjectWorker> projectWorkerList = getEntityListFromJson(ProjectWorker.class, PROJECT_WORKER_JSON);
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_PROJECT_WORKER)){
            for (ProjectWorker projectWorker : projectWorkerList) {
                preparedStatement.setLong(1, projectWorker.getProjectId());
                preparedStatement.setLong(2, projectWorker.getWorkerId());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private  <T>List<T> getEntityListFromJson(Class<T> entityClass, String filePath) {
        List<T> entityList = new ArrayList<>();
        try (Reader fileReader = new FileReader(filePath)) {
            Type type = TypeToken.getParameterized(List.class, entityClass).getType();
            entityList = new Gson().fromJson(fileReader, type);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return entityList;
    }
}
