package com.prj301.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "departmentname", nullable = false, length = 100)
    private String departmentName;

    public Department() {}

    public Department(String departmentName) {
        this.departmentName = departmentName;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
}
