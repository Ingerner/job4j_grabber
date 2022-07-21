package ru.job4j;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {
    public static void main(String[] args) {

        try (Connection connection = getDatabaseConnection()) {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap dataMap = new JobDataMap();
            dataMap.put("connection", connection);
            JobDetail job = JobBuilder
                    .newJob(Rabbit.class)
                    .usingJobData(dataMap)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(Integer.parseInt(countTime().getProperty("rabbit.interval")))
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(5000);
            scheduler.shutdown();
            System.out.println(connection);
        } catch (SchedulerException se) {
            se.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context)  {
            System.out.println("Rabbit runs here ...");
            Connection connection = (Connection) context.getJobDetail().getJobDataMap().get("store");
            try (PreparedStatement statement = connection
                    .prepareStatement("insert into rabbit(created_date) values (?)")) {
                    //(Statement statement = connection.createStatement()) {
                //statement.setDate(1, );

            } catch (SQLException e) {
            }
        }
    }

    public static Properties countTime() {
        Properties cfg = new Properties();
        try (InputStream in = Rabbit.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            cfg.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cfg;
    }

    public static Connection getDatabaseConnection() throws ClassNotFoundException, SQLException {
        Properties properties = countTime();
        Class.forName(properties.getProperty("jdbc.driver"));
        String url = properties.getProperty("jdbc.url");
        String login = properties.getProperty("jdbc.username");
        String password = properties.getProperty("jdbc.password");
        return DriverManager.getConnection(url, login, password);
    }
}
