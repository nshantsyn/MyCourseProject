package com.example.mycourseproject.Jeeves;

import lombok.Data;

import java.util.List;

@Data
public class CompleteTaskJeeves {
    Point startPoint;
    Point endPoint;
    List<List<Double>> listForZGraphic;
    List<Double> listForXGraphic;
    List<Double> listForYGraphic;
    List<Point> path;
    List<Point> constrainPoints;

}
