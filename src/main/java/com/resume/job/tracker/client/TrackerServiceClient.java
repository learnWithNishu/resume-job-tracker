package com.resume.job.tracker.client;

import com.resume.job.tracker.dto.RecentApplicationSummary;
import com.resume.job.tracker.dto.TrackerStatsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@Slf4j
public class TrackerServiceClient {

    private final RestClient restClient;

    public TrackerServiceClient(@Qualifier("trackerRestClient") RestClient restClient){
        this.restClient = restClient;
    }

    public TrackerStatsDto getApplicationStats(Long userId){
        try{
            return restClient.get()
                    .uri("/api/application/stats/"+ userId)
                    .retrieve()
                    .body(TrackerStatsDto.class);
        } catch (Exception e) {
            log.warn("Could not fetch application stats for userId: {}. "+
                    "Reason: {}", userId, e.getMessage());
            return new TrackerStatsDto(0L, new java.util.HashMap<>(), null, 0L, null , 0L);
        }
        }

        public List<RecentApplicationSummary> getRecentApplications(Long userId){
        try{
            return restClient.get()
                    .uri("/api/applications/recent/" + userId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<RecentApplicationSummary>>() {});
        } catch (Exception e) {
            log.warn("Could not fetch recent applications for userId: {}", userId);
            return List.of();
        }
        }
}
