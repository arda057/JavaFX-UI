package JavaFXexample.model;

public class User {
    private int id;
    private String username, password_hash, salt;
    private byte[] photo;

    public User(int id, String username, String password_hash, String salt, byte[] photo) {
        this.id = id;
        this.username = username;
        this.password_hash = password_hash;
        this.salt = salt;
        this.photo = photo;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword_hash() {
        return password_hash;
    }

    public String getSalt() {
        return salt;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo){
        this.photo = photo;
    }
}
