package org.acme.controller;

import exceptions.ActivityNotFoundException;
import io.vertx.core.json.JsonObject;
import org.acme.common.FileHandler;
import org.acme.model.Response;
import org.acme.model.Submission;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
@Transactional
public class MarathonController {

    public JsonObject runProgram(Submission submission, EntityManager entityManager) throws IOException, InterruptedException, ActivityNotFoundException {

        List<Response> responseList = new FileHandler().createResponseListFromProblem(submission.getProblem());

        if (responseList == null) throw new IOException();

        FileHandler fileHandler = new FileHandler();
        boolean isCorrect = true;
        String submissionPath = fileHandler.writeProgram(submission.filename, submission.source_code, true);
        for (Response response: responseList) {
            if (isCorrect) {
                String inputAndOutput = new String(java.util.Base64.getDecoder().decode(response.getContent()));
                String input = inputAndOutput.split("\\|")[0];
                String expectedOutput = inputAndOutput.split("\\|")[1];

                String inputPath = fileHandler.writeProgram("input.txt", input,false);
                String outputPath = fileHandler.writeProgram("expected_output.txt", expectedOutput,false);
                TimeUnit.MILLISECONDS.sleep(200);

                isCorrect = fileHandler.execute(submissionPath, inputPath, outputPath);
            }
        }

        String status = "FAIL";
        if (isCorrect) {
            status = "SUCCESS";
        }

        fileHandler.clean();
        submission.setStatus(status);
        entityManager.persist(submission);
        return this.parseSubmission(submission, isCorrect);
    }

    public List<Submission> getHistoric(
            EntityManager entityManager,
            String status,
            Date startDateTime,
            Date endDateTime,
            List<Integer> idList
    ) {
        if (status != null) {
            return entityManager.
                    createQuery("SELECT s FROM Submission s WHERE status = :status", Submission.class)
                    .setParameter("status", status)
                    .getResultList();
        }

        if (!idList.isEmpty()) {
            return entityManager.createQuery("SELECT s FROM Submission s WHERE id in :idList", Submission.class)
                    .setParameter("idList", idList)
                    .getResultList();
        }

        if (startDateTime != null && endDateTime != null) {
            return entityManager.createQuery("SELECT s FROM Submission s WHERE createdAt >= :start and createdAt <= :end", Submission.class)
                    .setParameter("start", startDateTime)
                    .setParameter("end", endDateTime)
                    .getResultList();
        }
        return entityManager.
                createQuery("SELECT s FROM Submission s", Submission.class)
                .getResultList();
    }

    private JsonObject parseSubmission(Submission submission, Boolean isCorrect) {
        JsonObject response = new JsonObject();

        String status = "FAIL";
        if (isCorrect) {
            status = "SUCCESS";
        }

        response.put("autor", submission.autor);
        response.put("filename", submission.filename);
        response.put("problem", submission.problem);
        response.put("status", status);
        return response;
    }
}
