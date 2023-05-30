public enum Material {
    // enum for categorising material types
    COTTON,WOOL_BLEND,POLYESTER,NA;

    /**
     * Prints out each Enum value as a string in a prettier format
     * @return Prettified String
     */
    public String toString(){
        return switch (this){
            case COTTON -> "Cotton";
            case POLYESTER -> "Polyester";
            case WOOL_BLEND -> "Wool blend";
            case NA -> "Skip...";
        };
    }
}
