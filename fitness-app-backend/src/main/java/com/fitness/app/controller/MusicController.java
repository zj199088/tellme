package com.fitness.app.controller;

import com.fitness.app.entity.MusicTrack;
import com.fitness.app.service.MusicTrackService;
import com.fitness.app.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/music")
public class MusicController {
    
    private final MusicTrackService musicTrackService;
    
    @Autowired
    public MusicController(MusicTrackService musicTrackService) {
        this.musicTrackService = musicTrackService;
    }
    
    @GetMapping("/tracks")
    public Result<List<MusicTrack>> getMusicList() {
        List<MusicTrack> tracks = musicTrackService.getActiveTracks();
        return Result.success(tracks);
    }
    
    @PostMapping("/upload")
    public Result<MusicTrack> uploadMusic(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("artist") String artist,
            @RequestParam("album") String album,
            @RequestParam("genre") String genre
    ) throws IOException {
        MusicTrack track = musicTrackService.uploadMusic(file, name, artist, album, genre);
        return Result.success(track);
    }
    
    @DeleteMapping("/tracks/{id}")
    public Result<Void> deleteMusic(@PathVariable Integer id) {
        musicTrackService.removeById(id);
        return Result.success();
    }
}
