import java.util.*;

public class GarmentSpecs {
    /*--------------------FIELDS---*/
    private final double minPrice;
    private final double maxPrice;
    private final Map<Filter,Object> filterMap;

    /**
     * Constructor for GarmentSpecs class, will create 'Search parameter' Garment objects
     * @param filterMap HashMap containing all the user's parameters : Map</Filter, /Object>
     * @param minPrice The specified minimum price : int
     * @param maxPrice The specified maximum price : int
     */
    public GarmentSpecs(Map<Filter,Object> filterMap, double minPrice, double maxPrice) {
        this.minPrice=minPrice;
        this.maxPrice=maxPrice;
        this.filterMap=new LinkedHashMap<>(filterMap);
    }

    /**
     * Secondary constructor for initialising a GarmentSpecs object without specifying minPrice or maxPrice
     * @param filterMap HashMap containing all the user's parameters : Map</Filter, /Object>
     */
    public GarmentSpecs(Map<Filter,Object> filterMap) {
        this.filterMap=new LinkedHashMap<>(filterMap);
        minPrice = -1;
        maxPrice = -1;
    }

    /**
     * Gets the user's specified minimum price
     * @return The specified minimum price : int
     */
    public double getMinPrice() {
        return minPrice;
    }
    /**
     * Gets the user's specified maximum price
     * @return The specified maximum price : int
     */
    public double getMaxPrice() {
        return maxPrice;
    }

    /**
     * Gets the HashMap containing all the user's specified criteria
     * @return The user's criteria : HashMap
     */
    public Map<Filter, Object> getAllFilters() {
        return new HashMap<>(filterMap);
    }

    /**
     * Method to return a specific value of the HashMap given the key
     * @param key The key to return the value from : Filter
     * @return The value from the key given : Object
     */
    public Object getFilter(Filter key){return getAllFilters().get(key);}

    /**
     * Method to return a String with information contained within the HashMap used in the GarmentSpecs object.
     * @return Description of a Garment's searchable parameters : String
     */
    public String getGarmentSpecInfo(){
        StringBuilder description = new StringBuilder();
        for(Filter key: filterMap.keySet()) description.append("\n").append(key).append(": ").append(getFilter(key));
        return description.toString();
    }


    /**
     * Method to compare information between two GarmentSpecs objects, their values, and returning true
     * if they both match, otherwise returning false
     * @param garmentSpecs The garment to compare to : GarmentSpecs
     * @return true if the information compared matches, false if not
     */
    public boolean matches(GarmentSpecs garmentSpecs){
        for(Filter key : garmentSpecs.getAllFilters().keySet()) {
            if(this.getAllFilters().containsKey(key)){  // if they have the same key, compare
                /* If both this garmentSpecs object's key value is a Set (or any other form of Collection), and the
                   compared to GarmentSpecs object's key value is also a Set, compare them */
                if(getFilter(key) instanceof Collection<?> && garmentSpecs.getFilter(key) instanceof Collection<?>){
                    Set<Object> intersect = new HashSet<>((Collection<?>) garmentSpecs.getFilter(key));
                    /* This colourful word spaghetti basically looks to see if there are any shared objects within the
                       sets that we are comparing */
                    intersect.retainAll((Collection<?>) getFilter(key));
                    if(intersect.size()==0) return false;
                }
                // If the garmentSpecs object's key value we are comparing TO is a set, and this is not, compare them
                else if(garmentSpecs.getFilter(key) instanceof Collection<?>){
                    // does the set contain any items that are present as the key: value in the other's object?
                    Set<Object> items = new HashSet<>((Collection<?>) garmentSpecs.getFilter(key));
                    if(!items.contains(this.getFilter(key))) return false;
                // If the GarmentSpecs object's key value we are comparing TO is NOT a set, and this is, compare them
                } else if (getFilter(key) instanceof Collection<?>) {
                    Set<Object> items = new HashSet<>((Collection<?>) getFilter(key));
                    if(!items.contains(garmentSpecs.getFilter(key))) return false;
                } // if the key's don't match up in the first place, we should move on to the next key
                else if(!getFilter(key).equals(garmentSpecs.getFilter(key))) return false;
            }

        }
        return true;
    }


}
