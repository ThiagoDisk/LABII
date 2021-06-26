package org.acme.controller;

import exceptions.ActivityNotFoundException;
import exceptions.DuplicatedRegistryException;
import org.acme.common.FileHandler;
import org.acme.model.Activity;
import org.acme.model.Response;
import org.acme.model.Submission;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

@ApplicationScoped
@Transactional
public class ActivityController {

    public void create(Activity activity, EntityManager entityManager) throws IOException {
        entityManager.persist(activity);
        new FileHandler().createDir(activity.getProblem());
    }

    public void createSubmission(Submission submission, EntityManager entityManager) {
        entityManager.flush();
        entityManager.persist(submission);
    }

    public void addResponse(Response response, EntityManager entityManager) throws ActivityNotFoundException, DuplicatedRegistryException, IOException {
        boolean activity = new FileHandler().problemDirectoryExists(response.getProblem());
        if (!activity) {
            throw new ActivityNotFoundException();
        }
        new FileHandler().writeResponse(response.getProblem(), response.getContent());
        entityManager.persist(response);
    }

    public Activity getActivityByProblem(String problem, EntityManager entityManager) {
        try {
             return entityManager.createQuery(
                    "SELECT o from Activity o WHERE problem = :problem", Activity.class)
                    .setParameter("problem", problem)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }

    }

    public List<Response> getResponseByProblem(String problem, EntityManager entityManager) {
        try {
            return entityManager.createQuery(
                    "SELECT x from Response x WHERE problem = :problem", Response.class)
                    .setParameter("problem", problem)
                    .getResultList();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public List<Activity> getAllActivities(EntityManager entityManager){
        try {
            return entityManager.createQuery("SELECT a from Activity a", Activity.class)
                    .getResultList();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public void deleteActivity(String problem, EntityManager entityManager) throws ActivityNotFoundException, IOException {
        Activity activity = this.getActivityByProblem(problem, entityManager);
        if(activity == null) throw new ActivityNotFoundException();

        entityManager.remove(activity);
        List<Response> responseLit = this.getResponseByProblem(problem, entityManager);
        for (Response response: responseLit) {
            entityManager.remove(response);
        }

        new FileHandler().deleteDir(problem);
    };

    public void deleteResponseById(Integer id, EntityManager entityManager) throws IOException {
        Response response = entityManager.createQuery("SELECT r from Response r WHERE id = :id", Response.class)
                .setParameter("id", id)
                .getSingleResult();
        entityManager.remove(response);
        new FileHandler().removeStringFromFileResponse(response.getProblem(), response.getContent());
    };
}
