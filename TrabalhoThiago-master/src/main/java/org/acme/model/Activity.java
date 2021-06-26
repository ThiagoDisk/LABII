package org.acme.model;

import exceptions.ValidationException;


import javax.persistence.*;
import java.util.ArrayList;

@Entity
public class Activity {

    public Activity(){};

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    public String problem;

    public String filename;

    public String lps;

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

    public String getLps() {
        return lps;
    }

    public void setLps(String lps) {
        this.lps = lps;
    }

    public void validateForCreation() throws ValidationException {
        ArrayList<String> fieldsMissing = new ArrayList<String>();
        if(this.filename == null) fieldsMissing.add("filename");
        if(this.problem == null) fieldsMissing.add("problem");
        if(this.lps == null) fieldsMissing.add("lps");
        if (!fieldsMissing.isEmpty()){
            throw new ValidationException(fieldsMissing.toString());
        }
    }
}
