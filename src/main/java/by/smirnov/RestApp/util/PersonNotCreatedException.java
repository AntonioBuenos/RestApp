package by.smirnov.RestApp.util;

public class PersonNotCreatedException extends RuntimeException{

    public PersonNotCreatedException(String message) {
        super(message);
    }
}
