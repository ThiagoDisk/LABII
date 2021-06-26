package org.acme.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Date;
import java.util.MissingFormatArgumentException;

@Entity
public class Submission {

    public Submission(){
        this.createdAt = new Date();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    public String filename;

    public String problem;

    public String source_code;

    public String autor;

    public Date createdAt;

    public String status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getSource_code() {
        return source_code;
    }

    public void setSource_code(String source_code) {
        this.source_code = source_code;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void validate() throws MissingFormatArgumentException{
        ArrayList<String> missingFields = new ArrayList<String>();

        if (filename == null) missingFields.add("filename");
        if (problem == null) missingFields.add("problem");
        if (source_code == null) missingFields.add("source_code");
        if (autor == null) missingFields.add("autor");

        if (!missingFields.isEmpty()){
            throw new MissingFormatArgumentException(missingFields.toString());
        }
    }
}
