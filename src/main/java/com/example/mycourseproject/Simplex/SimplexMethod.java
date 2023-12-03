package com.example.mycourseproject.Simplex;


import com.example.mycourseproject.Jeeves.CompleteTaskSimplex;
import com.example.mycourseproject.additional.Task;
import lombok.Data;

import java.text.DecimalFormat;
import java.util.*;

public class SimplexMethod {

    // Матрица коэффициентов, вектор свободных членов и вектор стоимостей
    private double[][] coeffMatrix;
    private double[] freeTerms;
    private double[] costVector;

    // Список для хранения значений дельта и индексов базисных переменных
    private List<Double> deltaValues = new ArrayList<>();
    private List<Integer> basisIndices = new ArrayList<>();

    // Объект для хранения результата
    private static CompleteTaskSimplex completeTaskSimplex;

    // Счетчик итераций
    private int iterationCount = 0;

    // Конструктор класса
    public SimplexMethod(double[][] coeffMatrix, double[] freeTerms, double[] costVector) {
        this.coeffMatrix = coeffMatrix;
        this.freeTerms = freeTerms;
        this.costVector = costVector;
    }

    // Инициализация базиса
    public void initializeBasis() {
        basisIndices.clear();
        for (int i = 0; i < costVector.length; i++) {
            if (costVector[i] == 0.0) {
                basisIndices.add(i);
            }
        }
    }

    // Вывод базиса
    public void printBasis() {
        for (int i = 0; i < basisIndices.size(); i++) {
            System.out.println(costVector[basisIndices.get(i)]);
        }
    }

    // Вычисление дельта
    public void calculateDelta() {
        deltaValues.clear();
        for (int i = 0; i < coeffMatrix[0].length; i++) {
            double delta = 0.0;
            for (int j = 0; j < coeffMatrix.length; j++) {
                delta += costVector[basisIndices.get(j)] * coeffMatrix[j][i];
            }
            delta -= costVector[i];
            deltaValues.add(delta);
        }
    }

    // Перестройка матрицы
    public void rebuildMatrix() {
        // Находим столбец с минимальным значением дельта
        int pivotColumnIndex = deltaValues.indexOf(deltaValues.stream().min(Double::compare).get());

        // Вычисляем значения Q для каждой строки
        List<Double> Q = new ArrayList<>();
        for (int i = 0; i < coeffMatrix.length; i++) {
            double tmp = coeffMatrix[i][pivotColumnIndex] > 0 ? freeTerms[i] / coeffMatrix[i][pivotColumnIndex] : -1;
            Q.add(tmp);
        }

        // Находим строку с минимальным положительным значением Q
        int pivotRowIndex = 0;
        double min = Double.MAX_VALUE;
        for (int i = 0; i < Q.size(); i++) {
            if (Q.get(i) != -1 && Q.get(i) < min) {
                min = Q.get(i);
                pivotRowIndex = i;
            }
        }

        // Нормализуем ведущую строку и свободный член по ведущему элементу
        double pivotElement = coeffMatrix[pivotRowIndex][pivotColumnIndex];
        for (int i = 0; i < coeffMatrix[0].length; i++) {
            coeffMatrix[pivotRowIndex][i] /= pivotElement;
        }
        freeTerms[pivotRowIndex] /= pivotElement;

        // Обновляем все остальные строки, чтобы ведущий столбец стал нулевым
        for (int i = 0; i < coeffMatrix.length; i++) {
            double k = coeffMatrix[i][pivotColumnIndex];
            if (i != pivotRowIndex) {
                for (int j = 0; j < coeffMatrix[0].length; j++) {
                    coeffMatrix[i][j] -= coeffMatrix[pivotRowIndex][j] * k;
                }
                freeTerms[i] -= freeTerms[pivotRowIndex] * k;
            }
        }

        // Добавляем ведущий столбец в список базисных индексов
        basisIndices.set(pivotRowIndex, pivotColumnIndex);
    }
    public void addGomoryCut() {
        // Находим строку с наибольшим дробным компонентом в векторе свободных членов
        int fractionalRowIndex = -1;
        double maxFractionalPart = 0;
        for (int i = 0; i < freeTerms.length; i++) {
            double fractionalPart = freeTerms[i] - Math.floor(freeTerms[i]) > 0.000001 ? freeTerms[i] - Math.floor(freeTerms[i]) : 0;
            if (fractionalPart > maxFractionalPart) {
                maxFractionalPart = fractionalPart;
                fractionalRowIndex = i;
            }
        }

        // Если все компоненты вектора свободных членов являются целыми числами, то нет необходимости в добавлении секущей плоскости
        if (fractionalRowIndex == -1) {
            return;
        }

        // Создаем новую секущую плоскость
        double[] cut = new double[coeffMatrix[0].length];
        for (int i = 0; i < coeffMatrix[0].length; i++) {
            double fractionalPart = coeffMatrix[fractionalRowIndex][i] - Math.floor(coeffMatrix[fractionalRowIndex][i]);
            cut[i] = fractionalPart;
        }

        // Добавляем секущую плоскость в матрицу коэффициентов и вектор свободных членов
        coeffMatrix = Arrays.copyOf(coeffMatrix, coeffMatrix.length + 1);
        coeffMatrix[coeffMatrix.length - 1] = cut;
        freeTerms = Arrays.copyOf(freeTerms, freeTerms.length + 1);
        freeTerms[freeTerms.length - 1] = maxFractionalPart;
    }
    // Выполнение итераций
    public void performIterations() {
        // Вычисляем дельта
        this.calculateDelta();

        // Если минимальное значение дельта меньше нуля, то план не оптимален, и таблица перестраивается
        if (deltaValues.stream().min(Double::compare).get() < 0) {
            System.out.println("План не оптимален. Перерасчет");
            this.rebuildMatrix();
            iterationCount++;
            addGomoryCut();
            this.performIterations();
        } else {
            // Если план оптимален, выводим ответ
            System.out.println("План оптимален. Ответ:");
            Map<String, Double> solution = new HashMap<>();
            for (int i = 0; i < basisIndices.size(); i++) {
                if (costVector[basisIndices.get(i)] != 0.0)
                    solution.put("x" + (basisIndices.get(i) + 1), freeTerms[i]);
            }
            int n = solution.size();
            for (int i = n + 1; i < Arrays.stream(costVector).filter(x -> x > 0.0).toArray().length + 1; i++) {
                solution.put("x" + i, 0.0);
            }
            Map<String, Double> sortedSolution = new LinkedHashMap<>();
            solution.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(x -> sortedSolution.put(x.getKey(), x.getValue()));
            sortedSolution.entrySet().stream().forEach(System.out::println);
            Double F = 0.0;
            for (int i = 0; i < sortedSolution.size(); i++) {
                String s = "x" + (i + 1);
                F += Arrays.stream(costVector).filter(x -> x > 0.0).toArray()[i] * solution.get(s);
            }
            System.out.println("F=" + F);
            completeTaskSimplex.setX1(solution.get("x1") != null ?solution.get("x1").intValue():0);
            completeTaskSimplex.setX2(solution.get("x2") != null ?solution.get("x2").intValue():0);
            completeTaskSimplex.setF(F.intValue());
        }
    }

    // Решение задачи симплекс-методом
    public static CompleteTaskSimplex solveSimplexMethod(Task task){
        int numModules = task.getListWrapper().getModules().size();
        double [][]coeffMatrix = new double[numModules][numModules+2];
        int count = 2;
        for (int i = 0; i < numModules; i++) {
            coeffMatrix[i][0] = task.getListWrapper().getModules().get(i).getKitA();
            coeffMatrix[i][1] = task.getListWrapper().getModules().get(i).getKitB();
            for (int j = 2; j < numModules+2; j++) {
                coeffMatrix[i][j] = 0;
            }
            coeffMatrix[i][count] =1;
            count++;
        }
        double []freeTerms = task.getListWrapper().getModules().stream().mapToDouble(x -> x.getTimeFund()).toArray();
        double []costVector = new double[numModules+2];
        for (int i = 0; i < costVector.length; i++) {
            costVector[i] = 0;
        }
        costVector[0] = task.getPriceA();
        costVector[1] = task.getPriceB();
        completeTaskSimplex = new CompleteTaskSimplex();
        SimplexMethod simplexMethod = new SimplexMethod(coeffMatrix,freeTerms,costVector);
        simplexMethod.initializeBasis();
        simplexMethod.performIterations();
        return completeTaskSimplex;
    }
}

