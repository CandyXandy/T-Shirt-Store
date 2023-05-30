public enum GarmentType {
    // enum for choosing between shirts and hoodies
    T_SHIRT,HOODIE,SELECT_TYPE;

    /**
     * Prints out each Enum value as a string in a prettier format
     * @return Prettified String
     */
    public String toString() {
        return switch (this) {
            case HOODIE -> "Hoodie";
            case T_SHIRT -> "T-shirt";
            // instructional constant for selection
            case SELECT_TYPE -> "Select garment type";
        };
    }
}
