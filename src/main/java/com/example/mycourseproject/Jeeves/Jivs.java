package com.example.mycourseproject.Jeeves;


import com.example.mycourseproject.additional.Module;
import com.example.mycourseproject.additional.Task;

import java.util.*;
import java.util.stream.Collectors;

public class Jivs {
    private static final double EPSILON = 0.0000001;
    private static double D_X = 1;
    private static double D_Y = 1;
    private static Point point1 = new Point();
    private static List<Double> x_test = new ArrayList<>();
    private static List<Double> y_test = new ArrayList<>();
    private static List<Point> result = new ArrayList<>();
    private static List<CompleteTask> tasks;
    private static CompleteTask completeTask;

    private static Task task;

    private static boolean constraintsSatisfied(Point point) {
        for (Module module : task.getListWrapper().getModules()) {
            if (module.getKitA() * point.x + module.getKitB() * point.y > module.getTimeFund()) {
                return false;
            }
        }
        return true;
    }

    private static double f(Point point) {
        return task.getPriceA() * point.x + task.getPriceB() * point.y; // Целевая функция
    }

    public static void createGraphic() {
        completeTask.listForXGraphic = new ArrayList<>();
        completeTask.listForYGraphic = new ArrayList<>();
        completeTask.listForZGraphic = new ArrayList<>();
        List<List<Point>> constrainPoints = new ArrayList<>();
        for (double i = 0; i < 5; i++) {
            completeTask.listForXGraphic.add(i);
            completeTask.listForYGraphic.add(i);
        }

        for (double i = 0; i < 5; i++) {
            List<Double> tmp = new ArrayList<>();
            List<Point> points = new ArrayList<>();
            for (double j = 0; j < 5; j++) {
                if (constraintsSatisfied(new Point(i, j)))
                    points.add(new Point(i, j));
                tmp.add(f(new Point(i, j)));
            }
            if (!points.isEmpty())
                constrainPoints.add(points);


            completeTask.listForZGraphic.add(tmp);

        }
       List<Point> path = new ArrayList<>();
        constrainPoints.get(0).stream().forEach(p -> path.add(p));
        constrainPoints.stream().forEach(p -> path.add(p.get(p.size()-1)));
       Collections.reverse(constrainPoints.get(constrainPoints.size()-1));
        constrainPoints.get(constrainPoints.size()-1).forEach(p -> path.add(p));
        Collections.reverse(constrainPoints.get(constrainPoints.size()-1));
        Collections.reverse(constrainPoints);
        constrainPoints.stream().forEach(p -> path.add(p.get(0)));
       completeTask.constrainPoints = path;
    }

    public static CompleteTask getResult(Task task) {
        tasks = new ArrayList<>();
        Jivs.task = task;
        for (int i = 0; i < 1000; i++) {
            CompleteTask completeTask1 = new CompleteTask();
            completeTask1.path = new ArrayList<>();
            point1 = new Point();
            D_X = 1;
            D_Y = 1;
            Random random = new Random();
            point1.dx = D_X;
            point1.dy = D_Y;
            do {
                point1.x = random.nextDouble() * 4; // x1 в диапазоне [0, 4]
                point1.y = random.nextDouble() * 4; // x2 в диапазоне [0, 8]
            } while (!constraintsSatisfied(point1));
            // point1.x = 2.7; // x1 в диапазоне [0, 4]
            //  point1.y = 0.09;
            completeTask1.startPoint = point1;

            completeTask1.path.add(new Point(point1.x, point1.y, f(point1)));
            while (Math.abs(D_X) > EPSILON || Math.abs(D_Y) > EPSILON) {

                Point point2 = explorerSearch(point1);
                Point point3 = new Point();
                point2.f = f(point2);
                completeTask1.path.add(point2);
                point3.x = point1.x + 2 * (point2.x - point1.x);
                point3.y = point1.y + 2 * (point2.y - point1.y);
                point3.f = f(point3);
                if (constraintsSatisfied(point3))
                    completeTask1.path.add(point3);
                if (f(point2) > f(point1)) {
                    while (searchByPattern(point3).x != point3.x || searchByPattern(point3).y != point3.y) {
                        Point point4 = searchByPattern(point3);
                        point1 = point2;
                        point2 = point4;
                        point3 = new Point();
                        point3.x = point1.x + 2 * (point2.x - point1.x);
                        point3.y = point1.y + 2 * (point2.y - point1.y);
                        point3.f = f(point3);
                        if (constraintsSatisfied(point3))
                            completeTask1.path.add(point3);
                    }
                    point1 = point2;
                }


            }

            completeTask1.endPoint = point1;
            tasks.add(completeTask1);
        }

        completeTask = tasks.stream().max(Comparator.comparingDouble(x -> f(x.endPoint))).get();
        createGraphic();


        return completeTask;
    }

    public static Point searchByPattern(Point point3) {
        List<Point> points = new ArrayList<>();
        points.add(new Point(point3.x + D_X, point3.y, D_X, D_Y));
        points.add(new Point(point3.x - D_X, point3.y, -D_X, D_Y));
        points.add(new Point(point3.x, point3.y - D_Y, D_X, -D_Y));
        points.add(new Point(point3.x, point3.y + D_Y, D_X, D_Y));
        points.add(new Point(point3.x + D_X, point3.y + D_Y, D_X, D_Y));
        points.add(new Point(point3.x + D_X, point3.y - D_Y, D_X, -D_Y));
        points.add(new Point(point3.x - D_X, point3.y - D_Y, -D_X, -D_Y));
        points.add(new Point(point3.x - D_X, point3.y + D_Y, -D_X, D_Y));

        Point point4 = points.stream().filter(p -> constraintsSatisfied(p)).max(Comparator.comparingDouble(Jivs::f)).orElse(point3);
        if (f(point4) >= f(point3)) {
            point3 = point4;
            D_X = point4.dx;
            D_Y = point4.dy;
        }

        points.clear();
        points.add(new Point(point3.x + D_X, point3.y, D_X, D_Y));
        points.add(new Point(point3.x - D_X, point3.y, -D_X, D_Y));
        points.add(new Point(point3.x, point3.y - D_Y, D_X, -D_Y));
        points.add(new Point(point3.x, point3.y + D_Y, D_X, D_Y));
        points.add(new Point(point3.x + D_X, point3.y + D_Y, D_X, D_Y));
        points.add(new Point(point3.x + D_X, point3.y - D_Y, D_X, -D_Y));
        points.add(new Point(point3.x - D_X, point3.y - D_Y, -D_X, -D_Y));
        points.add(new Point(point3.x - D_X, point3.y + D_Y, -D_X, D_Y));
        point4 = points.stream().filter(p -> constraintsSatisfied(p)).max(Comparator.comparingDouble(Jivs::f)).orElse(point3);
        if (f(point4) >= f(point3)) {
            point3 = point4;
            D_X = point4.dx;
            D_Y = point4.dy;
        }

        return point3;
    }

    public static Point explorerSearch(Point point1) {
        List<Point> points = new ArrayList<>();
        points.add(new Point(point1.x + D_X, point1.y, D_X, D_Y));
        points.add(new Point(point1.x - D_X, point1.y, -D_X, D_Y));
        points.add(new Point(point1.x, point1.y - D_Y, D_X, -D_Y));
        points.add(new Point(point1.x, point1.y + D_Y, D_X, D_Y));
        points.add(new Point(point1.x + D_X, point1.y + D_Y, D_X, D_Y));
        points.add(new Point(point1.x + D_X, point1.y - D_Y, D_X, -D_Y));
        points.add(new Point(point1.x - D_X, point1.y - D_Y, -D_X, -D_Y));
        points.add(new Point(point1.x - D_X, point1.y + D_Y, -D_X, D_Y));
        Point point2 = points.stream().filter(p -> constraintsSatisfied(p)).max(Comparator.comparingDouble(Jivs::f)).orElse(point1);
        if (f(point2) <= f(point1)) {
            D_X /= 2;
            D_Y /= 2;
        } else {
            points.stream().forEach(p -> {
                x_test.add(p.x);
                y_test.add(p.y);
            });

            point1 = point2;
            D_X = point2.dx;
            D_Y = point2.dy;
            Point point3 = new Point(point1.x + D_X, point1.y + D_Y, D_X, D_Y);
            while (f(point3) > f(point1) && constraintsSatisfied(point3)) {
                point1 = point3;
                point3 = new Point(point1.x + D_X, point1.y + D_Y, D_X, D_Y);
            }
        }

        point1.f = f(point1);
        return point1;
    }
}
