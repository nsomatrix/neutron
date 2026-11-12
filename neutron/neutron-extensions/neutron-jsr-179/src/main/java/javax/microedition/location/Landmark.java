package javax.microedition.location;

public class Landmark {
    private String name;
    private String description;
    private QualifiedCoordinates coordinates;
    private AddressInfo addressInfo;

    public Landmark(String name, String description, QualifiedCoordinates coordinates, AddressInfo addressInfo) {
        this.name = name;
        this.description = description;
        this.coordinates = coordinates;
        this.addressInfo = addressInfo;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public QualifiedCoordinates getQualifiedCoordinates() { return coordinates; }
    public void setQualifiedCoordinates(QualifiedCoordinates coordinates) { this.coordinates = coordinates; }
    public AddressInfo getAddressInfo() { return addressInfo; }
    public void setAddressInfo(AddressInfo addressInfo) { this.addressInfo = addressInfo; }
}

