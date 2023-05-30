public enum HoodieStyle {
    PULLOVER,ZIP_UP,OVER_SIZED,ATHLETIC,NA;

    /**
     * Prints out each Enum value as a string in a prettier format
     * @return Prettified String
     */
    public String toString(){
        return switch (this){
            case OVER_SIZED -> "Over-sized";
            case PULLOVER -> "Pull-over";
            case ZIP_UP -> "Zip-up";
            case ATHLETIC -> "Athletic";
            // instruction constant
            case NA -> "Select preferred style";
        };
    }
}

