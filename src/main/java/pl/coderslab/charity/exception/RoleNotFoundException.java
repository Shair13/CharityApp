package pl.coderslab.charity.exception;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException() {
        super("Nie znaleziono roli.");
    }
}
