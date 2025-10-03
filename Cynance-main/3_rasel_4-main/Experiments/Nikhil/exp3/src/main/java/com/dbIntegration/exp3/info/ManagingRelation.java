package com.dbIntegration.exp3.info;

import org.apache.catalina.Manager;

public class ManagingRelation {
    private Employee employee;
    private Building building;

    public ManagingRelation(Employee employee, Building building) {
        this.employee = employee;
        this.building = building;
    }
}
