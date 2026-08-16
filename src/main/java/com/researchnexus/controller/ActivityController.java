package com.researchnexus.controller;

import com.researchnexus.dto.ActivityResponse;
import com.researchnexus.service.ActivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping
    public ResponseEntity<List<ActivityResponse>> getAllActivities() {

        return ResponseEntity.ok(
                activityService.getAllActivities()
        );
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<ActivityResponse>> getProjectActivities(
            @PathVariable Long projectId
    ) {

        return ResponseEntity.ok(
                activityService.getProjectActivities(projectId)
        );
    }
}