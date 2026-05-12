package vn.edu.tdtu.edocument.service.review;

import vn.edu.tdtu.edocument.model.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReviewPipeline {
    private final List<ReviewStation> stations = new ArrayList<>();

    public void setStations(List<ReviewStation> newStations) {
        stations.clear();
        stations.addAll(newStations);
    }

    public void addStation(ReviewStation station) {
        stations.add(station);
    }

    public void clearStations() {
        stations.clear();
    }

    public List<ReviewStation> getStations() {
        return Collections.unmodifiableList(stations);
    }

    public void execute(Document document) throws ReviewException {
        for (ReviewStation station : stations) {
            System.out.println("[KIEM DUYET] Tram: " + station.getName());
            station.review(document);
        }
    }
}
