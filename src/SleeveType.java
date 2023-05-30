public enum SleeveType {
    LONG,SHORT,SLEEVELESS,BAT_WING,PUFFED,NA;

    /**
     * Prints out each Enum value as a string in a prettier format
     * @return Prettified String
     */
    public String toString(){
        return switch (this){
            case SHORT -> "Short";
            case LONG -> "Long";
            case PUFFED -> "Puffed";
            case BAT_WING -> "Bat-wing";
            case SLEEVELESS -> "Sleeveless";
            // instructional constant
            case NA -> "Select preferred sleeve";
        };
    }
}
