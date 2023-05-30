public enum PocketType {
    // enum for categorising our hoodie pocket types
    KANGAROO,PATCH,ZIPPER,SLASH,FAUX,NA;

    /**
     * Prints out each Enum value as a string in a prettier format
     * @return prettified String
     */
    public String toString(){
        return switch (this){
            case KANGAROO -> "Kangaroo";
            case FAUX -> "Faux";
            case PATCH -> "Patch";
            case SLASH -> "Slash";
            case ZIPPER -> "Zipper";
            // instructional constant
            case NA -> "Select preferred pocket type";
        };
    }
}


