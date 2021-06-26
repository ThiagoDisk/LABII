package exceptions;

public class ValidationException extends Exception {

    private final String fields;

    public ValidationException(String str) {
        fields = str;
    }

    public String getFields(){
        return fields;
    }
}
