package com.example.mycourseproject.additional;

import com.example.mycourseproject.Jeeves.CompleteTaskSimplex;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public  class CompleteTaskCreator {
    public static List<String> getConstraints(Task task){
        List<String> constraints = new ArrayList<>();
        task.getListWrapper().getModules()
                .stream()
                .forEach(module -> constraints.add(MessageFormat.format("{0}x1 + {1}x2 ≤ {2}",module.getKitA(),module.getKitB(),module.getTimeFund())));
        return constraints;
    }
    public static String getFunction(Task task){
        return MessageFormat.format("F(X) -> {0}x1 + {1}x2 -> max",task.getPriceA(),task.getPriceB());
    }
    public static String getSimplexSolution(CompleteTaskSimplex simplex){
        return MessageFormat.format("""
                С учетом ограничений и целевой функции, 
                предприятию необходимо произвести {0} деталей типа A и 
                {1} деталей типа B, чтобы получить максимальную прибыль в размере {2} долларов
                """,simplex.getX1(),simplex.getX2(),simplex.getF());
    }
}
