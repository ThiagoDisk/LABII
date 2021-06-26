package org.acme.model;

import exceptions.ValidationException;

import javax.persistence.*;
import java.util.ArrayList;

@Entity
public class Response {

    public Response(){};

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    public String problem;

    public String filename;

    public String content;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void validateForCreation() throws ValidationException {
        ArrayList<String> fieldsMissing = new ArrayList<String>();
        if(this.filename == null) fieldsMissing.add("filename");
        if(this.problem == null) fieldsMissing.add("problem");
        if(this.content == null) fieldsMissing.add("content");
        if (!fieldsMissing.isEmpty()){
            throw new ValidationException(fieldsMissing.toString());
        }
    }
}
