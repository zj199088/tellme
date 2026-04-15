
package com.fitness.app.controller;

import com.fitness.app.entity.WorkoutRecord;
import com.fitness.app.service.WorkoutRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/workout")
public class WorkoutRecordController {

    @Autowired
    private WorkoutRecordService workoutRecordService;

    @GetMapping("/today")
    public Map&lt;String, Object&gt; getTodayWorkout(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Long userId = Long.parseLong(userDetails.getUsername());
        
        if (date == null) {
            date = LocalDate.now();
        }
        
        WorkoutRecord record = workoutRecordService.getTodayWorkout(userId, date);
        
        Map&lt;String, Object&gt; response = new HashMap&lt;&gt;();
        response.put("success", true);
        response.put("data", record);
        return response;
    }

    @GetMapping("/recent")
    public Map&lt;String, Object&gt; getRecentRecords(
            @RequestParam(defaultValue = "10") int limit,
            Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Long userId = Long.parseLong(userDetails.getUsername());
        
        List&lt;WorkoutRecord&gt; records = workoutRecordService.getRecentRecords(userId, limit);
        
        Map&lt;String, Object&gt; response = new HashMap&lt;&gt;();
        response.put("success", true);
        response.put("data", records);
        return response;
    }

    @PostMapping("/record")
    public Map&lt;String, Object&gt; createWorkoutRecord(
            @RequestBody WorkoutRecord record,
            Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Long userId = Long.parseLong(userDetails.getUsername());
        
        record.setUserId(userId);
        WorkoutRecord savedRecord = workoutRecordService.createWorkoutRecord(record);
        
        Map&lt;String, Object&gt; response = new HashMap&lt;&gt;();
        response.put("success", true);
        response.put("data", savedRecord);
        return response;
    }

    @PutMapping("/record/{id}")
    public Map&lt;String, Object&gt; updateWorkoutRecord(
            @PathVariable Long id,
            @RequestBody WorkoutRecord record,
            Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Long userId = Long.parseLong(userDetails.getUsername());
        
        record.setId(id);
        record.setUserId(userId);
        WorkoutRecord updatedRecord = workoutRecordService.updateWorkoutRecord(record);
        
        Map&lt;String, Object&gt; response = new HashMap&lt;&gt;();
        response.put("success", true);
        response.put("data", updatedRecord);
        return response;
    }
}

