public class Geek {

    private final String name;
    private final String email;

    /**
     * Constructor for instantiating Geek objects
     * @param name : A string value signifying the Geek's full name
     * @param email : A string value signifying the Geek's email address
     */
    public Geek(String name, String email) {
        this.name = name;
        this.email = email;
    }

    /**
     * @return the Geek's name field : String
     */
    public String getName() {
        return name;
    }

    /**
     * @return the Geek's email field : String
     */
    public String getEmail() {
        return email;
    }
}
