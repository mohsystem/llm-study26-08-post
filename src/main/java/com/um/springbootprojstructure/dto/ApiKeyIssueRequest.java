package com.um.springbootprojstructure.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ApiKeyIssueRequest {

    @NotBlank
    @Size(max = 120)
    private String name;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
