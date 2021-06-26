package org.acme.router;

import io.vertx.core.json.JsonObject;
import org.acme.common.MessageCreator;
import org.acme.controller.ActivityController;
import org.acme.controller.MarathonController;
import org.acme.model.Submission;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.MissingFormatArgumentException;

@Path("/maratona")
@Transactional
public class Marathon {

    @Inject
    EntityManager entityManager;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Object submit(Submission submission) {
        JsonObject response = new JsonObject();

        try {
            submission.validate();
            ActivityController activityController = new ActivityController();
            response = new MarathonController().runProgram(submission, entityManager);
            return Response.ok(response).build();
        } catch (MissingFormatArgumentException e) {

            String message = "Missing fields: " + e.getFormatSpecifier();
            response = MessageCreator.createMessage("E1", message);
            return Response.status(400).entity(response).build();
        } catch (IOException | InterruptedException e) {

            String message = "Problem not found";
            response = MessageCreator.createMessage("E2", message);
            return Response.status(404).entity(response).build();
        } catch (Exception e) {
            e.printStackTrace();
            String message = "Service unavailable";
            response = MessageCreator.createMessage("E3", message);
            return Response.serverError().entity(response).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public List<Submission> consultExecutions(
            @QueryParam("status")  String status,
            @QueryParam("startDateTime")  Date startDateTime,
            @QueryParam("endDateTime")  Date endDateTime,
            @QueryParam("idList")  List<Integer> idList
    ){
        return new MarathonController().getHistoric(
                entityManager,
                status,
                startDateTime,
                endDateTime,
                idList);
    }
}
