package pl.coderslab.charity.exception;

public class UserExistsException extends RuntimeException {
    public UserExistsException(String email) {
        super("Użytkownik " + email + " już istnieje.");
    }
}
