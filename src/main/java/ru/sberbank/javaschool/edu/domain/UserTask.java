package ru.sberbank.javaschool.edu.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "edu_user_task")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class UserTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "submitteddate")
    private LocalDateTime submittedDate;
    @Column(name = "taskstate")
    private String taskState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task")
    protected Task task;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user")
    protected User user;
}