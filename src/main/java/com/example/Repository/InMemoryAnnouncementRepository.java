package com.example.Repository;

import com.example.Model.Announcement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryAnnouncementRepository implements AnnouncementRepository {

    private final Map<String, Announcement> store = new HashMap<>();

    @Override
    public void save(Announcement announcement) {
        store.put(announcement.getAnnouncementId(), announcement);
    }

    @Override
    public Announcement findById(String id) {
        return store.get(id);
    }

    @Override
    public List<Announcement> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void delete(String id) {
        store.remove(id);
    }

    @Override
    public List<Announcement> findByCourseId(String courseId) {
        List<Announcement> result = new ArrayList<>();
        for (Announcement a : store.values()) {
            if (a.getCourse() != null && courseId.equals(a.getCourse().getCourseId())) {
                result.add(a);
            }
        }
        return result;
    }

    @Override
    public List<Announcement> findByInstructorId(String instructorId) {
        List<Announcement> result = new ArrayList<>();
        for (Announcement a : store.values()) {
            if (a.getCourse() != null && instructorId.equals(a.getCourse().getInstructorId())) {
                result.add(a);
            }
        }
        return result;
    }
}
