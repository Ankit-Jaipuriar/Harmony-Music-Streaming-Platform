package com.harmony.music_search_service.service.client;

import com.harmony.music_search_service.dto.SongResponse;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class YoutubeClient {

    public List<SongResponse> searchSongs(String query) {

        List<SongResponse> songs = new ArrayList<>();

        try {

            ProcessBuilder processBuilder = new ProcessBuilder(
                    "C:\\Users\\ankit\\AppData\\Local\\Microsoft\\WinGet\\Packages\\yt-dlp.yt-dlp_Microsoft.Winget.Source_8wekyb3d8bbwe\\yt-dlp.exe",
                    "ytsearch5:" + query,
                    "--print",
                    "%(id)s|%(title)s|%(uploader)s|%(thumbnail)s"
            );

            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String line;

            while ((line = reader.readLine()) != null) {

                System.out.println("OUTPUT: " + line);

                String[] parts = line.split("\\|");

                if (parts.length >= 2) {

                    String id = parts[0];

                    String thumbnail = "";
                    int thumbnailIndex = -1;

                    // find thumbnail (starts with https)
                    for (int i = parts.length - 1; i >= 0; i--) {
                        if (parts[i].startsWith("http")) {
                            thumbnail = parts[i];
                            thumbnailIndex = i;
                            break;
                        }
                    }

                    // uploader is just before thumbnail
                    String uploader = "";
                    if (thumbnailIndex > 0) {
                        uploader = parts[thumbnailIndex - 1];
                    }

                    // title = everything between id and artist
                    StringBuilder titleBuilder = new StringBuilder();
                    for (int i = 1; i < thumbnailIndex - 1; i++) {
                        titleBuilder.append(parts[i]);
                        if (i != thumbnailIndex - 2) {
                            titleBuilder.append(" | ");
                        }
                    }

                    String title = titleBuilder.toString();

                    songs.add(new SongResponse(
                            id,
                            title,
                            uploader,
                            thumbnail
                    ));
                }
            }

            process.waitFor();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return songs;
    }
}