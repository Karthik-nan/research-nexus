package com.researchnexus.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProjectRequest {

    private String name;
    private String description;
}