package com.example.mycourseproject.additional;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ModuleListWrapper {
    private List<Module> modules;

    public ModuleListWrapper() {
        modules = new ArrayList<>();
        modules.add(new Module(1,3,3,15));
        modules.add(new Module(2,2,6,18));
        modules.add(new Module(3,4,0,16));
        modules.add(new Module(4,1,2,8));
    }
    // getters and setters
}