import java.text.DecimalFormat;

public class Garment {
    /*----------------FIELDS------------*/
    private final String name;
    private final long productCode;
    private final double price;
    private final String description;
    private final GarmentSpecs garmentSpecs;

    /*---------------CONSTRUCTOR-------*/
    /**
     * Constructor for Garment class, will create 'real' Garment objects.
     * @param name : The garment's name : String
     * @param productCode : The garment's product code : long
     * @param price : The garment's price : double
     * @param garmentSpecs : The garment's 'search specifications' containing information not unique to this garment : GarmentSpecs
     * @param description : The garment's description : String
     */
    public Garment(String name,long productCode, double price, String description, GarmentSpecs garmentSpecs) {
        this.name=name;
        this.productCode = productCode;
        this.price = price;
        this.description = description;
        this.garmentSpecs=garmentSpecs;
    }
    /*-----------------Getters----------*/
    /**
     * Gets the name of the garment
     * @return the garment's name : String
     */
    public String getName(){
        return name;
    }

    /**
     * Gets the product code of the garment
     * @return the garment's product code : long
     */
    public long getProductCode() {
        return productCode;
    }

    /**
     * Gets the price of the garment
     * @return the garment's price : double
     */
    public double getPrice() {
        return price;
    }

    /**
     * Gets the description of the garment
     * @return the garment's description : String
     */
    public String getDescription(){
        return description;
    }

    /**
     * Method used to return the GarmentSpecs object that specifies the search filters used to find this Garment object
     * @return GarmentSpecs object
     */
    public GarmentSpecs getGarmentSpecs() {
        return garmentSpecs;
    }

    /**
     * Method used to return a string description in the requested format, describing the Garment.
     * @return Description of Garment : String
     */
    public String getGarmentInformation(){
        DecimalFormat df = new DecimalFormat("0.00"); // enforces the two decimal places in the price
        return "\nItem name: "+this.getName()+"\nCaption: "+this.getDescription() +"\nProduct code: "
                +this.getProductCode()+this.getGarmentSpecs().getGarmentSpecInfo()+"\nPrice: $"
                +df.format(this.getPrice())+"\n";
    }
}
