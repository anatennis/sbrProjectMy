package ru.sberbank.javaschool.edu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sberbank.javaschool.edu.domain.*;
import ru.sberbank.javaschool.edu.repository.CourseRepository;
import ru.sberbank.javaschool.edu.repository.TaskRepository;
import ru.sberbank.javaschool.edu.repository.UserRepository;
import ru.sberbank.javaschool.edu.repository.UserTaskRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserTaskService {
    @Autowired
    private UserTaskRepository userTaskRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private MailSender mailSender;

    public UserTask createUserTask(User user, Task task) {
        UserTask userTask = new UserTask();
        userTask.setUser(user);
        userTask.setCurMark(new Long(0));
        userTask.setTask(task);
        //userTask.setTaskState("UNCOMPLETE");
        userTaskRepository.save(userTask);
        return userTask;
    }

    public boolean submitTask(long idTask, User user, long idCourse) {
        Task task = taskRepository.getTaskById(idTask);
        UserTask userTask = userTaskRepository.findUserTaskByUserAndTask(user, task);
        if (userTask == null) {
            return false;
        }
        userTask.setTaskState("COMPLETE");
        userTask.setSubmittedDate(LocalDateTime.now());
        userTaskRepository.save(userTask);

        //send mail

        Course course = courseRepository.findCourseById(idCourse);
        Set<CourseUser> teachers = course.getCourseUsers()
                .stream()
                .filter(u -> u.getRole() == Role.TEACHER)
                .collect(Collectors.toSet());

        String messageTitle = userTask.getUser().getName() + " submit task " + userTask.getTask().getTitle();
        String link = "http://localhost:8080/course/" + idCourse + "/tasks/"
                + idTask + "/" + task.getTitle() + "/" + user.getId();
        String message = String.format(
                "Здравствуйте!\n Ученик %s сдал задание %s .\n" +
                        "Посмотреть на то, как он пытался, можно здесь: " + link,
                user.getName(),
                task.getTitle()
        );
        for (CourseUser teacher : teachers) {
            if (teacher.getUser().getEmail() != null) {
                mailSender.send(teacher.getUser().getEmail(), messageTitle, message);
            }
        }


        return true;

    }


    public boolean setMarkToUser(long idTask, long idUser, long curMark) {
        Task task = taskRepository.getTaskById(idTask);
        User user = userRepository.findUserById(idUser);
        UserTask userTask = userTaskRepository.findUserTaskByUserAndTask(user, task);
        if (userTask == null || userTask.getTaskState() == null) {
            return false;
        }
        userTask.setCurMark(curMark);
        userTaskRepository.save(userTask);
        return true;
    }
}
