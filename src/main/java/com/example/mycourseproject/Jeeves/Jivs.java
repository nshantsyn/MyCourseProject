package com.example.mycourseproject.Jeeves;


import com.example.mycourseproject.additional.Module;
import com.example.mycourseproject.additional.Task;

import java.util.*;

public class Jivs {
    private static final double EPSILON = 0.0000001;
    private static double D_X = 1;
    private static double D_Y = 1;
    private static Point point1 = new Point();
    private static List<Double> x_test = new ArrayList<>();
    private static List<Double> y_test = new ArrayList<>();
    private static List<Point> result = new ArrayList<>();
    private static List<CompleteTaskJeeves> tasks;
    private static CompleteTaskJeeves completeTaskJeeves;

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
        System.out.println("Создание графика Хука-Дживса");
        completeTaskJeeves.listForXGraphic = new ArrayList<>();
        completeTaskJeeves.listForYGraphic = new ArrayList<>();
        completeTaskJeeves.listForZGraphic = new ArrayList<>();
        List<List<Point>> constrainPoints = new ArrayList<>();
        for (double i = 0; i < 5; i++) {
            completeTaskJeeves.listForXGraphic.add(i);
            completeTaskJeeves.listForYGraphic.add(i);
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


            completeTaskJeeves.listForZGraphic.add(tmp);

        }
       List<Point> path = new ArrayList<>();
        constrainPoints.get(0).stream().forEach(p -> path.add(p));
        constrainPoints.stream().forEach(p -> path.add(p.get(p.size()-1)));
       Collections.reverse(constrainPoints.get(constrainPoints.size()-1));
        constrainPoints.get(constrainPoints.size()-1).forEach(p -> path.add(p));
        Collections.reverse(constrainPoints.get(constrainPoints.size()-1));
        Collections.reverse(constrainPoints);
        constrainPoints.stream().forEach(p -> path.add(p.get(0)));
       completeTaskJeeves.constrainPoints = path;
    }

    public static CompleteTaskJeeves getResult(Task task) {
        tasks = new ArrayList<>();
        Jivs.task = task;
        for (int i = 0; i < 1000; i++) {
            CompleteTaskJeeves completeTaskJeeves1 = new CompleteTaskJeeves();
            completeTaskJeeves1.path = new ArrayList<>();
            point1 = new Point();
            System.out.println("Выбор случайной начальной точки");
            D_X = 1;
            D_Y = 1;
            Random random = new Random();
            point1.dx = D_X;
            point1.dy = D_Y;
            int count = 0;
            do {
                point1.x = random.nextDouble() * 4; // x1 в диапазоне [0, 4]
                point1.y = random.nextDouble() * 4; // x2 в диапазоне [0, 4]
                count++;
            } while (!constraintsSatisfied(point1) && count <1000);
            if(count >=1000){
                point1.x = 0;
                point1.y = 0;
            }
            System.out.println("Точка выбрана. Начало решения");
            completeTaskJeeves1.startPoint = point1;

            completeTaskJeeves1.path.add(new Point(point1.x, point1.y, f(point1)));
            while (Math.abs(D_X) > EPSILON || Math.abs(D_Y) > EPSILON) {

                Point point2 = explorerSearch(point1);
                Point point3 = new Point();
                point2.f = f(point2);
                completeTaskJeeves1.path.add(point2);
                point3.x = point1.x + 2 * (point2.x - point1.x);
                point3.y = point1.y + 2 * (point2.y - point1.y);
                point3.f = f(point3);
                if (constraintsSatisfied(point3))
                    completeTaskJeeves1.path.add(point3);
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
                            completeTaskJeeves1.path.add(point3);
                    }
                    point1 = point2;
                }


            }

            completeTaskJeeves1.endPoint = point1;
            tasks.add(completeTaskJeeves1);
        }

        completeTaskJeeves = tasks.stream().max(Comparator.comparingDouble(x -> f(x.endPoint))).get();
        createGraphic();
        System.out.println("Экстремум найден : " + completeTaskJeeves.endPoint +"\n Значение функции в точке: " + f(completeTaskJeeves.endPoint));

        return completeTaskJeeves;
    }

    public static Point searchByPattern(Point point3) {
        System.out.println("Запуск поиска по образцу ");
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
        if (f(point4) > f(point3)) {
            System.out.println("Поиск по образцу: переход в точку -> " + point4 +"\n Значение функции в точке: " + f(point4));
            point3 = point4;
            D_X = point4.dx;
            D_Y = point4.dy;
        }

        return point3;
    }

    public static Point explorerSearch(Point point1) {
        System.out.println("Запуск исследующего поиска");
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
            System.out.println("Исследующий поиск: точка не найдена, уменьшение шага");
        } else {
            points.stream().forEach(p -> {
                x_test.add(p.x);
                y_test.add(p.y);
            });
            System.out.println("Исследующий поиск: переход в точку -> "+ point2  +"\n Значение функции в точке: " + f(point2));
            point1 = point2;
            D_X = point2.dx;
            D_Y = point2.dy;
            Point point3 = new Point(point1.x + D_X, point1.y + D_Y, D_X, D_Y);
            while (f(point3) > f(point1) && constraintsSatisfied(point3)) {
                System.out.println("Исследующий поиск: переход в точку -> "+ point3  +"\n Значение функции в точке: " + f(point3));
                point1 = point3;
                point3 = new Point(point1.x + D_X, point1.y + D_Y, D_X, D_Y);
            }
        }

        point1.f = f(point1);
        System.out.println("Была получена точка: "+ point1);
        return point1;
    }
}
