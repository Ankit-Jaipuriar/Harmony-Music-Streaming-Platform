package com.harmony.streaming_service.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class YoutubeStreamClient {

    private static final Logger log = LoggerFactory.getLogger(YoutubeStreamClient.class);
    private static final String YT_DLP_PATH = "C:\\Users\\ankit\\AppData\\Local\\Microsoft\\WinGet\\Packages\\yt-dlp.yt-dlp_Microsoft.Winget.Source_8wekyb3d8bbwe\\yt-dlp.exe";
    private static final List<ExtractionProfile> EXTRACTION_PROFILES = List.of(
            new ExtractionProfile("bestaudio", "youtube:player_client=web"),
            new ExtractionProfile("bestaudio/best", "youtube:player_client=android"),
            new ExtractionProfile("best", "youtube:player_client=android")
    );

    public String getStreamUrl(String videoId) {

        String url = "https://www.youtube.com/watch?v=" + videoId;
        List<String> failureMessages = new ArrayList<>();

        for (ExtractionProfile profile : EXTRACTION_PROFILES) {
            try {
                return extractStreamUrl(videoId, url, profile);
            } catch (IllegalStateException e) {
                failureMessages.add("[%s] %s".formatted(profile.name(), e.getMessage()));
                log.warn("yt-dlp extraction profile {} failed for video {}", profile.name(), videoId);
            } catch (Exception e) {
                failureMessages.add("[%s] %s".formatted(profile.name(), e.getMessage()));
                log.warn("yt-dlp extraction profile {} crashed for video {}", profile.name(), videoId, e);
            }
        }

        throw new IllegalStateException(
                "Unable to fetch stream URL for video %s. Attempts: %s"
                        .formatted(videoId, String.join(" || ", failureMessages))
        );
    }

    private String extractStreamUrl(String videoId, String url, ExtractionProfile profile) throws Exception {

        ProcessBuilder processBuilder = new ProcessBuilder(
                YT_DLP_PATH,
                "-f",
                profile.format(),
                "-g",
                "--no-playlist",
                "--extractor-args",
                profile.extractorArgs(),
                url
        );

        Process process = processBuilder.start();

        List<String> stdoutLines = new ArrayList<>();
        try (BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
             BufferedReader stderrReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {

            String line;
            while ((line = stdoutReader.readLine()) != null) {
                if (!line.isBlank()) {
                    stdoutLines.add(line.trim());
                }
            }

            List<String> stderrLines = new ArrayList<>();
            while ((line = stderrReader.readLine()) != null) {
                if (!line.isBlank()) {
                    stderrLines.add(line.trim());
                }
            }

            int exitCode = process.waitFor();

            for (String stdoutLine : stdoutLines) {
                if (stdoutLine.startsWith("http://") || stdoutLine.startsWith("https://")) {
                    if (!stderrLines.isEmpty()) {
                        log.warn("yt-dlp emitted warnings for video {} using profile {}: {}", videoId, profile.name(), String.join(" | ", stderrLines));
                    }
                    return stdoutLine;
                }
            }

            String errorMessage = stderrLines.isEmpty()
                    ? "yt-dlp did not return a playable stream URL"
                    : String.join(" | ", stderrLines);

            throw new IllegalStateException(
                    "Failed to resolve stream URL for video %s (exit code %d): %s"
                            .formatted(videoId, exitCode, errorMessage)
            );
        }
    }

    private record ExtractionProfile(String format, String extractorArgs) {
        private String name() {
            return format + " | " + extractorArgs;
        }
    }
}
