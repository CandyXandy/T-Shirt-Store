import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Inventory {
    /*------------------FIELDS------*/
    private final Set<Garment> allGarments = new HashSet<>(); // HashSet that holds all Garments in the inventory

    /**
     * Method to add Garment objects to the set
     * @param Garment The garment with which we are adding
     */
    public void addGarment(Garment Garment){
        this.allGarments.add(Garment);
    }

    /**
     * @return A HashSet containing the Strings of all the available brands
     */
    public Set<String> getAllBrands(){
        Set<String> allBrands = new HashSet<>();
        for(Garment tee: allGarments){ // for every garment we have
            allBrands.add((String) tee.getGarmentSpecs().getFilter(Filter.BRAND)); // add the brand to the set
        } // thankfully sets don't allow for repeat elements, so that's not a concern here
        return allBrands;
    }

    /**
     * Method to find the maximum price that the inventory has, so we can predefine the maximum searching price.
     * @return double : maximum price of a garment in the inventory
     */
    public double findMaxPrice() {
        double maxPrice = 0;
        for (Garment garment : allGarments) { // simple sequential search to find max price
            if (garment.getPrice() > maxPrice) maxPrice = garment.getPrice();
        }
        return maxPrice;
    }

    /**
     * Method used to find matching garments to the user's specified search criteria
     * @param dreamGarment : The user's 'dream garment' : A GarmentSpecs object
     * @return matchingGarments : A List of Garment objects
     */
    public List<Garment> findMatch(GarmentSpecs dreamGarment){
        List<Garment> matchingGarments = new ArrayList<>();
        for(Garment Garment: allGarments){
            if(!dreamGarment.matches(Garment.getGarmentSpecs())) continue;
            if(Garment.getPrice()<dreamGarment.getMinPrice()||Garment.getPrice()>dreamGarment.getMaxPrice()) continue;
            matchingGarments.add(Garment);
        }
        return matchingGarments;
    }

}
