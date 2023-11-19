package com.example.mycourseproject.Simplex;


import com.example.mycourseproject.Jeeves.CompleteTaskSimplex;
import com.example.mycourseproject.additional.Task;
import lombok.Data;

import java.util.*;

public class Simplex {

    public Simplex(double[][] arrayX, double[] arrayB, double[] c) {
        this.arrayX = arrayX;
        this.arrayB = arrayB;
        C = c;
    }

    double arrayX[][] ;
    double arrayB[] ;

    double C[] ;
    List<Double> del = new ArrayList<>();
    List<Integer> formulaC = new ArrayList<>();
   static CompleteTaskSimplex completeTaskSimplex;

    public void createFormula() {
        formulaC.clear();
        for (int i = 0; i < C.length; i++) {
            if (C[i] == 0.0) {
                formulaC.add(i);

            }
        }
    }

    public void PrintFormula() {
        for (int i = 0; i < formulaC.size(); i++) {
            System.out.println(C[formulaC.get(i)]);
        }
    }

    public void foundDel() {
        del.clear();
        for (int i = 0; i < arrayX[0].length; i++) {
            double del = 0.0;
            for (int j = 0; j < arrayX.length; j++) {
                del += C[formulaC.get(j)] * arrayX[j][i];

            }
            del -= C[i];
            this.del.add(del);
        }

    }

    public void RebuildMatrix() {

        int index_column = del.indexOf(del.stream().min(Double::compare).get());


        List<Double> Q = new ArrayList<>();
        for (int i = 0; i < arrayX.length; i++) {
            double tmp = arrayX[i][index_column] > 0 ? arrayB[i] / arrayX[i][index_column] : -1;
            Q.add(tmp);
        }
        int index_row = 0;
        double min = Double.MAX_VALUE;
        for (int i = 0; i < Q.size(); i++) {
            if (Q.get(i) != -1 && Q.get(i) < min) {
                min = Q.get(i);
                index_row = i;

            }
        }

        double test = arrayX[index_row][index_column];
        for (int i = 0; i < arrayX[0].length; i++) {
            arrayX[index_row][i] = arrayX[index_row][i] / test;

        }
        arrayB[index_row] = arrayB[index_row] / test;

        for (int i = 0; i < arrayX.length; i++) {
            double k = arrayX[i][index_column];
            if (i != index_row) {
                for (int j = 0; j < arrayX[0].length; j++) {

                    arrayX[i][j] = arrayX[i][j] - (arrayX[index_row][j] * k);
                }
                arrayB[i] = arrayB[i] - (arrayB[index_row] * k);
            }


        }
        formulaC.set(index_row, index_column);

    }

    public void PrintDel() {
        System.out.println(del.toString());
    }

    public void PrintMatrix() {
        for (int i = 0; i < arrayX.length; i++) {
            System.out.println(Arrays.toString(arrayX[i]));
        }
    }

    public void PrintB() {
        System.out.println(Arrays.toString(arrayB));
    }

    public void Cycle() {
        this.foundDel();
        if (del.stream().min(Double::compare).get() < 0) {

            this.RebuildMatrix();
            this.Cycle();
        } else {
            Map<String, Double> map = new HashMap<>();
            for (int i = 0; i < formulaC.size(); i++) {
                if (C[formulaC.get(i)] != 0.0)
                    map.put("x" + (formulaC.get(i) + 1), arrayB[i]);
            }
            int n = map.size();

            for (int i = n + 1; i < Arrays.stream(C).filter(x -> x > 0.0).toArray().length + 1; i++) {
                map.put("x" + i, 0.0);
            }

            Map<String, Double> map1 = new LinkedHashMap<>();
            map.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(x -> map1.put(x.getKey(), x.getValue()));
            map1.entrySet().stream().forEach(System.out::println);
            Double F = 0.0;
            for (int i = 0; i < map1.size(); i++) {
                String s = "x" + (i + 1);
                F += Arrays.stream(C).filter(x -> x > 0.0).toArray()[i] * map.get(s);

            }
            System.out.println("F=" + F);
            completeTaskSimplex.setX1(map.get("x1") != null ?map.get("x1").intValue():0);
            completeTaskSimplex.setX2(map.get("x2") != null ?map.get("x2").intValue():0);
            completeTaskSimplex.setF(F.intValue());
        }

    }
    public static CompleteTaskSimplex getCompleteTask(Task task){
        double [][]arrX = new double[task.getListWrapper().getModules().size()][task.getListWrapper().getModules().size()+2];
        int count = 2;
        for (int i = 0; i < task.getListWrapper().getModules().size(); i++) {
            arrX[i][0] = task.getListWrapper().getModules().get(i).getKitA();
            arrX[i][1] = task.getListWrapper().getModules().get(i).getKitB();
            for (int j = 2; j < task.getListWrapper().getModules().size()+2; j++) {
                arrX[i][j] = 0;

            }
            arrX[i][count] =1;
            count++;
        }
        double []arrayB = task.getListWrapper().getModules().stream().mapToDouble(x -> x.getTimeFund()).toArray();
        double []C = new double[task.getListWrapper().getModules().size()+2];
        for (int i = 0; i < C.length; i++) {
            C[i] = 0;
        }
        C[0] = task.getPriceA();
        C[1] = task.getPriceB();
        completeTaskSimplex = new CompleteTaskSimplex();
        Simplex simplex = new Simplex(arrX,arrayB,C);
        simplex.createFormula();
        simplex.Cycle();
        return completeTaskSimplex;
    }

}
