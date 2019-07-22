package ru.sberbank.javaschool.edu.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.sberbank.javaschool.edu.domain.*;
import ru.sberbank.javaschool.edu.repository.TaskCommentRepository;
import ru.sberbank.javaschool.edu.repository.TaskRepository;
import ru.sberbank.javaschool.edu.repository.UserRepository;
import ru.sberbank.javaschool.edu.repository.UserTaskRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TaskCommentServiceTest {
    @Autowired
    private TaskCommentService taskCommentService;

    @MockBean
    private TaskRepository taskRepository;

    @MockBean
    private TaskCommentRepository taskCommentRepository;

    @MockBean
    private UserTaskRepository userTaskRepository;

    @MockBean
    private UserRepository userRepository;

    private final long ID = 1L;
    private Task task;
    private User user;
    private UserTask userTask;
    private Course course;

    @Before
    public void beforeTests() {
        task = new Task();
        user = new User();
        userTask = new UserTask();
        course = new Course();

        course.setId(ID);
        course.setCourseUsers(new HashSet<>(Collections.singletonList(new CourseUser(course, user, Role.TEACHER))));
        task.setId(ID);
        task.setCourse(course);
        user.setId(ID);
        userTask.setId(ID);
        userTask.setUser(user);
        userTask.setTask(task);
    }

    @Test
    public void addComment() {
        TaskComment taskComment = new TaskComment();

        Mockito.doReturn(task)
                .when(taskRepository)
                .getTaskById(ID);
        Mockito.doReturn(user)
                .when(userRepository)
                .findUserById(ID);
        Mockito.doReturn(userTask)
                .when(userTaskRepository)
                .findUserTaskByUserAndTask(user, task);

        boolean isAdded = taskCommentService.addComment(ID, ID, user, taskComment);

        Assert.assertTrue(isAdded);
        Assert.assertEquals(user, taskComment.getAuthor());
        Assert.assertNotNull(taskComment.getCreateDate());

        Mockito.verify(taskCommentRepository, Mockito.times(1)).save(taskComment);
    }

    @Test
    public void deleteComment() {
        TaskComment taskComment = new TaskComment();

        taskComment.setUserTask(userTask);
        taskComment.setId(ID);
        taskComment.setAuthor(user);

        Mockito.doReturn(taskComment)
                .when(taskCommentRepository)
                .findTaskCommentById(ID);

        boolean isDeleted = taskCommentService.deleteComment(user, ID);

        Assert.assertTrue(isDeleted);

        Mockito.verify(taskCommentRepository, Mockito.times(1)).delete(taskComment);
    }

    @Test
    public void deleteNonexistentComment() {
        TaskComment taskComment = new TaskComment();

        taskComment.setUserTask(userTask);
        taskComment.setId(ID);
        taskComment.setAuthor(user);

        Mockito.doReturn(null)
                .when(taskCommentRepository)
                .findTaskCommentById(ID);

        boolean isDeleted = taskCommentService.deleteComment(user, ID);

        Assert.assertFalse(isDeleted);

        Mockito.verify(taskCommentRepository, Mockito.times(0)).delete(taskComment);
    }

    @Test
    public void editComment() {
        TaskComment taskComment = new TaskComment();
        TaskComment newComment = new TaskComment();

        taskComment.setUserTask(userTask);
        taskComment.setId(ID);
        taskComment.setText("oldText");
        taskComment.setAuthor(user);
        newComment.setText("newText");

        Mockito.doReturn(taskComment)
                .when(taskCommentRepository)
                .findTaskCommentById(ID);

        boolean isEdited = taskCommentService.editComment(ID, newComment, user);

        Assert.assertTrue(isEdited);
        Assert.assertEquals("newText", taskComment.getText());

        Mockito.verify(taskCommentRepository, Mockito.times(1)).save(taskComment);
    }

    @Test
    public void editNonexistentComment() {
        TaskComment taskComment = new TaskComment();
        TaskComment newComment = new TaskComment();

        taskComment.setUserTask(userTask);
        taskComment.setId(ID);
        taskComment.setText("oldText");
        taskComment.setAuthor(user);
        newComment.setText("newText");

        Mockito.doReturn(null)
                .when(taskCommentRepository)
                .findTaskCommentById(ID);

        boolean isEdited = taskCommentService.editComment(ID, newComment, user);

        Assert.assertFalse(isEdited);

        Mockito.verify(taskCommentRepository, Mockito.times(0)).save(taskComment);
    }
}