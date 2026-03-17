//package com.tanmeyah.practice.Repository;
//import com.tanmeyah.practice.Entity.Task;
//import org.springframework.stereotype.Repository;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Repository
//public class TaskRepository {
//
//    private List<Task> tasks = new ArrayList<>();
//
//    public List<Task> findAll() {
//        return tasks;
//    }
//
//    public Task findById(Long id) {
//        for (Task task : tasks) {
//            if (task.getId().equals(id)) {
//                return task;
//            }
//        }
//        return null;
//    }
//
//    public void save(Task task) {
//        tasks.add(task);
//    }
//
//    public boolean deleteById(Long id) {
//        return tasks.removeIf(task -> task.getId().equals(id));
//    }
//}


package com.tanmeyah.practice.Repository;

import com.tanmeyah.practice.Entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // JpaRepository already provides:
    // - List<Task> findAll()
    // - Optional<Task> findById(Long id)
    // - Task save(Task task)
    // - void deleteById(Long id)
    // - boolean existsById(Long id)
}