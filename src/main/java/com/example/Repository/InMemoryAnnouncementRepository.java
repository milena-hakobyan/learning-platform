package com.example.Repository;

import com.example.Model.Announcement;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryAnnouncementRepository implements AnnouncementRepository {

    private final Map<String, Announcement> store = new ConcurrentHashMap<>();

    @Override
    public void save(Announcement announcement) {
        if (announcement == null) {
            throw new IllegalArgumentException("Announcement cannot be null");
        }
        if (announcement.getAnnouncementId() == null) {
            throw new IllegalArgumentException("Announcement ID cannot be null");
        }
        store.put(announcement.getAnnouncementId(), announcement);
    }


    @Override
    public Optional<Announcement> findById(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        return Optional.ofNullable(store.get(id));
    }


    @Override
        public List<Announcement> findAll () {
            return new ArrayList<>(store.values());
        }

        @Override
        public void delete (String id){
            store.remove(id);
        }

        @Override
        public List<Announcement> findByCourseId (String courseId){
            List<Announcement> result = new ArrayList<>();
            for (Announcement a : store.values()) {
                if (a.getCourse() != null && courseId.equals(a.getCourse().getCourseId())) {
                    result.add(a);
                }
            }
            return result;
        }

        @Override
        public List<Announcement> findByInstructorId (String instructorId){
            List<Announcement> result = new ArrayList<>();
            for (Announcement a : store.values()) {
                if (a.getCourse() != null && instructorId.equals(a.getCourse().getInstructorId())) {
                    result.add(a);
                }
            }
            return result;
        }
    }
