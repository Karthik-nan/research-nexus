package com.researchnexus.service;

import com.researchnexus.dto.ActivityResponse;
import com.researchnexus.entity.Activity;
import com.researchnexus.entity.Project;
import com.researchnexus.entity.User;
import com.researchnexus.repository.ActivityRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public void createActivity(
            String action,
            String description,
            User user,
            Project project
    ) {

        Activity activity = Activity.builder()
                .action(action)
                .description(description)
                .createdAt(LocalDateTime.now())
                .user(user)
                .project(project)
                .build();

        activityRepository.save(activity);
    }

    public List<ActivityResponse> getProjectActivities(Long projectId) {

        return activityRepository
                .findByProjectIdOrderByCreatedAtDesc(projectId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ActivityResponse> getAllActivities() {

        return activityRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ActivityResponse toResponse(Activity activity) {

        return new ActivityResponse(
                activity.getId(),
                activity.getAction(),
                activity.getDescription(),
                activity.getCreatedAt(),
                activity.getUser().getId(),
                activity.getUser().getName(),
                activity.getProject().getId(),
                activity.getProject().getName()
        );
    }
}