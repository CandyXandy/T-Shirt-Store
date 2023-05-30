public enum Neckline {

    CREW,V,SCOOP,HIGH,NA;

    /**
     * Prints out each Enum value as a string in a prettier format
     * @return Neckline in String format
     */
    public String toString(){
        return switch (this){
            case V -> "V - neck";
            case CREW -> "Crew neck";
            case HIGH -> "High neck";
            case SCOOP -> "Scoop neck";
            // instructional constant
            case NA -> "Select preferred neckline";
        };
    }

}
