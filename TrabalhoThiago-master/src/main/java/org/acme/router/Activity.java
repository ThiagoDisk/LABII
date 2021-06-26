package org.acme.router;

import exceptions.ActivityNotFoundException;
import exceptions.DuplicatedRegistryException;
import exceptions.ValidationException;
import io.vertx.core.json.JsonObject;
import org.acme.common.MessageCreator;
import org.acme.controller.ActivityController;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/activity")
@Transactional
public class Activity {

    @Inject
    EntityManager entityManager;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Object create(org.acme.model.Activity activity) {
        try {
            activity.validateForCreation();
            new ActivityController().create(
                    activity,
                    entityManager
            );
            return Response.status(201).build();
        } catch (ValidationException ex) {
            JsonObject response = new JsonObject();
            String message = "Missing fields: " + ex.getFields();
            response = MessageCreator.createMessage("E1", message);
            return Response.status(400).entity(response).build();

        } catch (Exception ex) {
            ex.printStackTrace();
            return Response.status(503).build();
        }
    }

    @Path("activities")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Object listAllActivities() {
        try {
            List<org.acme.model.Activity> activityList   = new ActivityController().getAllActivities(
                    entityManager
            );
            return Response.status(200).entity(activityList).build();
        } catch (Exception ex) {
            ex.printStackTrace();
            return Response.status(503).build();
        }
    }

    @Path("/response")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Object addResponse(org.acme.model.Response activityResponse) {
        try {
            activityResponse.validateForCreation();
            new ActivityController().addResponse(
                    activityResponse,
                    entityManager
            );
            return Response.status(201).build();
        } catch (ActivityNotFoundException ex) {
            return Response.status(400).entity(
                    MessageCreator.createMessage("E3", "Activity not found")).build();
        } catch (DuplicatedRegistryException ex) {
            return Response.status(400).entity(
                    MessageCreator.createMessage("E3", "Duplicated registry")).build();
        } catch (ValidationException ex) {
            JsonObject response = new JsonObject();
            String message = "Missing fields: " + ex.getFields();
            response = MessageCreator.createMessage("E1", message);
            return Response.status(400).entity(response).build();
        } catch (Exception e) {
            return Response.status(503).build();
        }
    }

    @Path("/responses/{problem}")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Object getResponsesByProblem(@PathParam String problem) {
        try {
            List<org.acme.model.Response> responseList = new ActivityController().getResponseByProblem(problem, entityManager);
            return Response.status(201).entity(responseList).build();
        } catch (Exception e) {
            return Response.status(503).build();
        }
    }

    @Path("/{problem}")
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Object deleteActivity(@PathParam String problem) {
        try {
            if (problem.equals("A") || problem.equals("B")) {
                return Response.status(403).build();
            }
            new ActivityController().deleteActivity(problem, entityManager);
            return Response.status(200).build();
        } catch (ActivityNotFoundException ex) {
            return Response.status(404).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(503).build();
        }
    }

    @Path("/response/{id}")
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Object deleteResponse(@PathParam Integer id) {
        try {
            if (id >= 996 && id <= 999) {
                return Response.status(403).build();
            }
            new ActivityController().deleteResponseById(id, entityManager);
            return Response.status(200).build();
        } catch (NoResultException ex) {
            return Response.status(404).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(503).build();
        }
    }
}
