package com.example.mycourseproject;

import com.example.mycourseproject.Jeeves.CompleteTaskJeeves;
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

        System.out.println("Инициализация начальной страницы...");
        ModelAndView start = new ModelAndView("index");
        Task task = new Task();
        task.setListWrapper(new ModuleListWrapper());

        start.addObject("task", task);
        System.out.println("Отображение начальной страницы...");
        return start;
    }

    @PostMapping("/send")
    public ModelAndView getTask(@ModelAttribute Task task, HttpSession session){
        System.out.println("Произведение расчетов...");
        System.out.println("Запуск метода Хука-Дживса...");
        CompleteTaskJeeves completeTaskJeevesJivs =  Jivs.getResult(task);
        System.out.println("Запуск Симплекс-метода...");
        CompleteTaskSimplex completeTaskSimplex = Simplex.getCompleteTask(task);
        System.out.println("Сохранение результатов...");
        session.setAttribute("task",task);
        session.setAttribute("jivs", completeTaskJeevesJivs);
        session.setAttribute("simplex",completeTaskSimplex);
        System.out.println("Результаты сохранены. Переход к отображению результатов");
        return new ModelAndView("redirect:/completeTask");
    }

    @GetMapping("/recalculate")
    public ModelAndView getNewResult( HttpSession session){
        System.out.println("Вызов перерасчета");
        Task task = (Task) session.getAttribute("task");

        return getTask(task,session);
    }

    @GetMapping("/completeTask")
    public ModelAndView getCompleteTask(HttpSession session){
        System.out.println("Выгрузка результатов");
        System.out.println("Выгрузка условий задачи");
        Task task = (Task) session.getAttribute("task");
        System.out.println("Выгрузка ответа симплекс-метода");
        CompleteTaskSimplex simplex = (CompleteTaskSimplex) session.getAttribute("simplex");
        System.out.println("Выгрузка ответа Хука-Дживса");
        CompleteTaskJeeves completeTaskJeevesJivs = (CompleteTaskJeeves) session.getAttribute("jivs");
        ModelAndView completeTask = new ModelAndView("completeTask");
        completeTask.addObject("function", CompleteTaskCreator.getFunction(task));
        completeTask.addObject("constraints", CompleteTaskCreator.getConstraints(task));

        completeTask.addObject("simplex",CompleteTaskCreator.getSimplexSolution(simplex));
        completeTask = CompleteTaskCreator.getHookSolution(completeTask, completeTaskJeevesJivs);
        completeTask = CompleteTaskCreator.setTask(completeTask,task);
        System.out.println("Отображение результатов");
        return completeTask;
    }
}
