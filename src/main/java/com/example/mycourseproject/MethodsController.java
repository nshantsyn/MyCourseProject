package com.example.mycourseproject;

import com.example.mycourseproject.Jeeves.CompleteTask;
import com.example.mycourseproject.Jeeves.CompleteTaskSimplex;
import com.example.mycourseproject.Jeeves.Jivs;
import com.example.mycourseproject.Simplex.Simplex;
import com.example.mycourseproject.additional.CompleteTaskCreator;
import com.example.mycourseproject.additional.ModuleListWrapper;
import com.example.mycourseproject.additional.Task;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class MethodsController {

    @GetMapping("/")
    public ModelAndView getStart(){
        ModelAndView start = new ModelAndView("index");
        Task task = new Task();
        task.setListWrapper(new ModuleListWrapper());

        start.addObject("task", task);
        return start;
    }

    @PostMapping("/send")
    public ModelAndView getTask(@ModelAttribute Task task, HttpSession session){

        CompleteTask completeTaskJivs =  Jivs.getResult(task);
        CompleteTaskSimplex completeTaskSimplex = Simplex.getCompleteTask(task);
        session.setAttribute("task",task);
        session.setAttribute("jivs",completeTaskJivs);
        session.setAttribute("simplex",completeTaskSimplex);
        return new ModelAndView("redirect:/completeTask");
    }

    @GetMapping("/completeTask")
    public ModelAndView getCompleteTask(HttpSession session){
        Task task = (Task) session.getAttribute("task");
        CompleteTaskSimplex simplex = (CompleteTaskSimplex) session.getAttribute("simplex");
        CompleteTask completeTaskJivs = (CompleteTask) session.getAttribute("jivs");
        ModelAndView completeTask = new ModelAndView("completeTask");
        completeTask.addObject("function", CompleteTaskCreator.getFunction(task));
        completeTask.addObject("constraints", CompleteTaskCreator.getConstraints(task));

        completeTask.addObject("simplex",CompleteTaskCreator.getSimplexSolution(simplex));
        completeTask = CompleteTaskCreator.getHookSolution(completeTask,completeTaskJivs);
        return completeTask;
    }
}
