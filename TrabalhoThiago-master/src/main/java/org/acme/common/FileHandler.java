package org.acme.common;

import exceptions.ActivityNotFoundException;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.acme.model.Response;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FileHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileHandler.class);
    private final String path = this.getProjectDir() + File.separator + "problems";

    private String getProjectDir(){
        String dir = System.getProperty("user.dir");
        dir = dir.replaceAll(File.separator +"targ\\S+", "");
        LOGGER.debug("setting FileHandler path as " + dir);
        return dir;
    }
    public boolean problemDirectoryExists(String problem) {
        LOGGER.debug("validating problem " + problem + "exists");
        String problemPath = path + File.separator + problem;
        Boolean problemExists = Files.isDirectory(Path.of(problemPath));
        LOGGER.debug("problem exists" + problemExists);
        return problemExists;
    }

    public String writeProgram(String filename, String content, boolean is_encoded) throws IOException {
        String clientProgram = path + File.separator + filename;

        File file = new File(clientProgram);
        file.createNewFile();
        String codeDecoded;

        FileWriter fileWriter = new FileWriter(clientProgram);
        if (is_encoded){
            codeDecoded = new String(Base64.getDecoder().decode(content));
        } else {
            codeDecoded = content;
        }
        fileWriter.write(codeDecoded);
        fileWriter.write(System.getProperty( "line.separator" ));
        fileWriter.close();
        return clientProgram;
    }

    public void writeResponse(String problem, String content) throws IOException {
        String writeAt = path + File.separator + problem + File.separator + "responses.txt";
        File responsesFile = new File(writeAt);
        if (responsesFile.exists()) {
            BufferedWriter bufferedWriter =  new BufferedWriter(new FileWriter(writeAt, true));
            bufferedWriter.append("\n");
            bufferedWriter.append(content);
            bufferedWriter.close();
        } else {
            responsesFile.createNewFile();
            FileWriter fileWriter = new FileWriter(writeAt);
            fileWriter.write(content);
            fileWriter.close();
        }
    }

    public boolean execute(String pathToCodePython, String pathToInput, String pathToExpected) throws IOException, InterruptedException {
        File pythonFile = new File(pathToCodePython);
        File fileInput = new File(pathToInput);
        File fileExpected = new File(pathToExpected);
        File fileSaved = new File(path + File.separator + "output_received.txt");
        ProcessBuilder processBuilder = new ProcessBuilder("python3", pathToCodePython);
        processBuilder.redirectInput(fileInput);

        processBuilder.redirectOutput(
                fileSaved
        );
        processBuilder.start();

        TimeUnit.MILLISECONDS.sleep(200);
        return FileUtils.contentEquals(fileExpected, fileSaved);
    }

    public List<Response> createResponseListFromProblem(String problem) throws ActivityNotFoundException, IOException {
        ArrayList<Response> responseArrayList = new ArrayList<Response>();
        String responsesPath = path + File.separator + problem + File.separator + "responses.txt";
        File responsesFile = new File(responsesPath);
        if (!responsesFile.exists()) throw new ActivityNotFoundException();

        LineIterator it = FileUtils.lineIterator(responsesFile, "UTF-8");

        try {
            while (it.hasNext()) {
                String line = it.nextLine();
                Response response = new Response();
                response.setContent(line);
                responseArrayList.add(response);
            }
        } finally {
            it.close();
        }

        return  responseArrayList;
    }

    public void clean() {
        File dir = new File(path);
        for(File file: dir.listFiles())
            if (!file.isDirectory())
                file.delete();

    }

    public void deleteDir(String problem) throws IOException {
        FileUtils.deleteDirectory(new File(path + File.separator + problem));
    }

    public void createDir(String problem) throws IOException {
        File problemDir = new File(this.path + File.separator + problem);
        if (!problemDir.exists()) {
            problemDir.mkdir();
        }
    }

    public void removeStringFromFileResponse(String problem, String toRemove) throws IOException {
        String pathFile = path + File.separator + problem + File.separator + "responses.txt";
        File inputFile = new File(pathFile);
        if (!inputFile.isFile()) {
            System.out.println("File does not exist");
            return;
        }
        //Construct the new file that will later be renamed to the original filename.
        File tempFile = new File(pathFile + ".tmp");
        BufferedReader br = new BufferedReader(new FileReader(pathFile));
        PrintWriter pw = new PrintWriter(new FileWriter(tempFile));
        String line = null;

        //Read from the original file and write to the new
        //unless content matches data to be removed.
        while ((line = br.readLine()) != null) {
            if (!line.trim().equals(toRemove)) {
                pw.println(line);
                pw.flush();
            }
        }
        pw.close();
        br.close();

        //Delete the original file
        if (!inputFile.delete()) {
            System.out.println("Could not delete file");
            return;
        }

        //Rename the new file to the filename the original file had.
        if (!tempFile.renameTo(inputFile))
            System.out.println("Could not rename file");
    }
}
