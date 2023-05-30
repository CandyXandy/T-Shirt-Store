import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

public class GarmentSearcher {

    private static final String filePath = "./inventory.txt";
    private static Inventory allGarments; // our inventory class
    private static final String appName = "Garment Geek";
    private static final String iconPath = "images/icon.png";
    private static JFrame mainWindow = null;
    private static JComboBox<String> optionsCombo = null;
    private static JPanel searchView = null;
    private static Garment choice; // eventually becomes our determined Garment choice
    private static Map<String, Garment> optionsMap;
    // HashMap holding the String containing its name and product code, and the Garment that corresponds to the String
/*--------------------------METHODS-------------*/

    /**
     * main method - creates mainFrame and starts program, initialising key fields.
     * @param args - NA
     */
    public static void main(String[] args) {
        allGarments = loadInventory(filePath);
        mainWindow = new JFrame(appName);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ImageIcon icon = new ImageIcon(iconPath);
        mainWindow.setIconImage(icon.getImage());
        mainWindow.setMinimumSize(new Dimension(300, 300));
        searchView = generateSearchView();
        mainWindow.setContentPane(searchView);
        mainWindow.pack();
        mainWindow.setVisible(true);
    }

    /**
     * This method creates a new instance of SearchView, will refresh or set to empty all the
     * fields in the search-view
     * @return a searchView object with default values
     */
    public static SearchView refreshSearchView() {
        return new SearchView(allGarments.getAllBrands(), allGarments.findMaxPrice());
    }


    /**
     * Method to generate the order view of our app, using a UserDetailsView object
     */
    public static void generateOrderView(Garment choice) {
        JPanel orderWindow = new JPanel(); // contains our entry fields and a button
        orderWindow.setLayout(new BorderLayout()); // N E S W layout

        UserDetailsView userDetailsView = new UserDetailsView(choice); // instantiate new UserDetailsView object
        JPanel orderForm = userDetailsView.generateContactForm(); // generate the form and assign it to this JPanel
        orderWindow.add(orderForm, BorderLayout.CENTER); // place it in the centre of the window
        JButton submit = new JButton("Submit"); // submit button to write to a file
        ActionListener actionListener = e-> prepareOrder(userDetailsView); // calls this method when button clicked
        submit.addActionListener(actionListener); // adds the action listener to a button
        orderWindow.add(submit, BorderLayout.SOUTH); // add it to the south anchor of the layout
        orderWindow.add(Box.createRigidArea(new Dimension(20, 0)), BorderLayout.WEST); // pad to left
        orderWindow.add(Box.createRigidArea(new Dimension(20, 0)), BorderLayout.EAST); // pad to right
        mainWindow.setContentPane(orderWindow); // set the order window
        mainWindow.revalidate(); // makes the current content pane the "root" of all other components in view
    }

    /**
     * Method to "prepare" the order for use in the submitOrder method. Gathers the details needed from the
     * UserDetailsView class that were entered by the user when filling out the order form, and then instantiates a
     * new Geek object, which is passed along with the Garment to the submitOrder method.
     * @param orderView : The UserDetailsView object that has the information that the user entered.
     */
    public static void prepareOrder(UserDetailsView orderView) {
        String name = orderView.getName();
        if (name == null) {
            JOptionPane.showMessageDialog(mainWindow,
                    "You must enter a name to continue",
                    appName,
                    JOptionPane.INFORMATION_MESSAGE,
                    null);
            return; // go back to previous screen
        }
        String email = orderView.getEmail();
        if (email == null) {
            JOptionPane.showMessageDialog(mainWindow,
                    "You must enter your email to continue",
                    appName,
                    JOptionPane.INFORMATION_MESSAGE,
                    null);
            return; // go back to previous screen
        }
        String message = orderView.getMessage();
        Geek geek = new Geek(name, email);
        submitOrder(geek, choice, message);
    }

    /**
     * Method to generate the search view of our app, using a SearchView object
     * @return a JPanel representing the search screen with a new button in compass format
     */
    public static JPanel generateSearchView() {
        // contains search fields and button
        JPanel searchWindow = new JPanel();
        searchWindow.setLayout(new BorderLayout());
        // initialise SearchView object
        SearchView searchView = refreshSearchView();
        JPanel searchCriteriaPanel = searchView.generateSearchView();
        // add it to the main panel
        searchWindow.add(searchCriteriaPanel, BorderLayout.CENTER);
        // add a search button
        JButton search = new JButton("Search");
        ActionListener actionListener = e -> conductSearch(searchView); // creates a GarmentSpecs object with the input and searches with it
        search.addActionListener(actionListener);
        searchWindow.add(search, BorderLayout.SOUTH);
        // pad and return the searchWindow
        searchWindow.add(Box.createRigidArea(new Dimension(20, 0)), BorderLayout.WEST);
        searchWindow.add(Box.createRigidArea(new Dimension(20, 0)), BorderLayout.EAST);
        return searchWindow;
    }

    /**
     * method used to extract user-entered data to create a GarmentSpecs object which will be used to search the database of Garments for a match
     * @param searchView an instance of the SearchView class (used to generate JPanels for user to enter/select filters)
     */
    public static void conductSearch(SearchView searchView) {
        // map containing the Filter:Object pairs as we have done previously
        Map<Filter, Object> criteria = new LinkedHashMap<>();
        GarmentType type = searchView.getInputGarmentType();
        if (type == GarmentType.SELECT_TYPE) {
            JOptionPane.showMessageDialog(mainWindow,
                    "You must select a type of Garment!.\n",
                    "Invalid search",
                    JOptionPane.INFORMATION_MESSAGE, null);
            return;
        } // if the input was valid, add it to the map
        criteria.put(Filter.GARMENT_TYPE, type);
        // brands
        Set<String> brands = searchView.getUserBrands(); // must not be of size 1 AND only containing "NA" to go in the map
        if (!brands.isEmpty()) {
            brands.remove("NA");
            criteria.put(Filter.BRAND, brands);
        }
        // sizes
        Set<Size> sizes = searchView.getSizes();
        if (sizes.isEmpty()) {
            JOptionPane.showMessageDialog(mainWindow,
                    "You must select at least one size to continue. \n",
                    "invalid search",
                    JOptionPane.INFORMATION_MESSAGE,
                    null);
            return;
        }
        criteria.put(Filter.SIZE, sizes);
        // material
        Material material = searchView.getMaterial();
        if (!material.equals(Material.NA)) criteria.put(Filter.MATERIAL, material);
        // initialise price variables
        double userMinPrice = searchView.getUserMinPrice();
        double userMaxPrice = searchView.getUserMaxPrice();
        Neckline neckline; // initialise now to keep in scope
        SleeveType sleeve;
        HoodieStyle hoodieStyle;
        PocketType pocketType;
        if (type.equals(GarmentType.T_SHIRT)) {
            neckline = searchView.getNeckline(); // for all below - must not be NA to be added to the map
            if (!neckline.equals(Neckline.NA)) criteria.put(Filter.NECKLINE, neckline);
            sleeve = searchView.getSleeveType();
            if (!sleeve.equals(SleeveType.NA)) criteria.put(Filter.SLEEVE_TYPE, sleeve);
        } else if (type.equals(GarmentType.HOODIE)) {
            hoodieStyle = searchView.getHoodieStyle();
            if (!hoodieStyle.equals(HoodieStyle.NA)) criteria.put(Filter.HOODIE_STYLE, hoodieStyle);
            pocketType = searchView.getPocketType();
            if (!pocketType.equals(PocketType.NA)) criteria.put(Filter.POCKET_TYPE, pocketType);
        }
        // create a garmentSpecs object with the HashMap and the prices
        GarmentSpecs garmentSpecs = new GarmentSpecs(criteria, userMinPrice, userMaxPrice);
        List<Garment> potentialMatches = allGarments.findMatch(garmentSpecs); // find some matches
        showResults(potentialMatches); // show results with the matches
    }

    /**
     * This method presents the second screen of the program to the user, displaying results and allowing the user
     * to select a match from a dropdown list by calling other functions.
     * @param potentialMatches an ArrayList of potentially matching Garments
     */
    public static void showResults(List<Garment> potentialMatches) {
        if (potentialMatches.size() == 0) {
            noResults();
            return; // go back if there are no results
        }
        // create panel for the second view for viewing results
        JPanel results = new JPanel();
        results.setLayout(new BorderLayout());
        results.add(Box.createRigidArea(new Dimension(0,10)), BorderLayout.NORTH); // add padding to the top
        results.add(generateGarmentDescriptions(potentialMatches), BorderLayout.CENTER); // add the scroll pane with garment descriptions
        results.add(selectFromResultsPanel(), BorderLayout.SOUTH); // add the dropdown list and search again button
        results.add(Box.createRigidArea(new Dimension(20, 0)), BorderLayout.WEST); // padding on right
        results.add(Box.createRigidArea(new Dimension(20, 0)), BorderLayout.EAST); // padding on left
        mainWindow.setContentPane(results); // set main window to the results panel
        mainWindow.revalidate();
    }

    /**
     * a method used to generate a popup box informing the user that their search returned no results
     */
    public static void noResults(){
        JOptionPane.showMessageDialog(mainWindow,
                "Unfortunately your search returned no compatible garments.\n",
                "No Compatible Garments",
                JOptionPane.INFORMATION_MESSAGE,
                null);
        reGenerateSearchView(false); // return to search view - false = don't reset it
    }

    /*
     * TODO: No todo, just here to grab your attention as to my justification on including search results from previous searches.
     * In terms of the argument on whether you should wipe the SearchView clean when you return to it, I believe it's
     * a subjective matter, and not really a programming question. I have tried the program without remembering search
     * results and with remembering search results. I have tried entering T-Shirts after entering a Hoodie, and visa versa,
     * and nothing has broken that is visible to me yet. Obviously no program is bug free, but in terms of the question at hand,
     * I believe it is very annoying to make a simple mistake on your search, and then have the whole thing wiped and have
     * to start over compared to being given what I was already working on so I can make the adjustment.
     * Obviously it is also annoying to decide you want something else entirely and then having to reset each search
     * field manually, and as such I've included a "reset" boolean toggle that will reset the search field when, and
     * only when, we reach the submit order form.
     */

    /**
     * method to return to the Search view.
     * @param reset : boolean : resets the search view to default values
     */
    public static void reGenerateSearchView(boolean reset) {
        if (reset) { // if we toggle to true - resets the search view to default values
            searchView = generateSearchView();
        }
        mainWindow.setContentPane(searchView); // sets the search view to the current viewed content
        mainWindow.revalidate(); // revalidate the root component
    }


    /**
     * method to generate JScrollPane containing descriptions of matching garments
     * @param potentialMatches an arraylist of Garment objects that match the user's selection criteria
     * @return JScrollPane a scroll pane containing a collection of non-editable JTextAreas each representing
     * a description of 1 matching Garment
     */
    public static JScrollPane generateGarmentDescriptions(List<Garment> potentialMatches) {
        Map<String, Garment> optionsMap = new LinkedHashMap<>(); // create a mew HashMap
        optionsMap.put("Select garment", null); // place this key:value pair inside it
        // contains one text area per Garment
        JPanel descriptions = new JPanel();
        descriptions.setBorder(BorderFactory.createTitledBorder("Matches found! The following garments match your criteria: "));
        descriptions.setLayout(new BoxLayout(descriptions, BoxLayout.Y_AXIS)); // stack vertically
        descriptions.add(Box.createRigidArea(new Dimension(0, 10))); // padding
        // generates a description of each Garment
        for (Garment potentialGarment : potentialMatches) {
            descriptions.add(describeIndividualGarment(potentialGarment));
            optionsMap.put(potentialGarment.getName() + " (" + potentialGarment.getProductCode() + ")", potentialGarment); // for the drop-down list
        }
        // cast all the keys in the HashMap to strings to store them inside the JComboBox
        optionsCombo = new JComboBox<>(optionsMap.keySet().toArray(new String[0]));
        setOptionsMap(optionsMap); // since we're finished with the Map, and we need it stored as a field, we should use the set method
        // creates a scroll bar, so we can fit many options!
        JScrollPane scrollPane = new JScrollPane(descriptions);
        scrollPane.setPreferredSize(new Dimension(300, 450));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); // show the scroll bar only if we need it
        SwingUtilities.invokeLater(() -> scrollPane.getViewport().setViewPosition(new Point(0, 0))); // default to top of scrollable area
        return scrollPane;
    }

    /**
     * Helper method to describe each individual garment in the results view
     * @param garment The garment to be described
     * @return JTextArea object with the description of the garment.
     */
    public static JTextArea describeIndividualGarment(Garment garment) {
        JTextArea description = new JTextArea(garment.getGarmentInformation());
        description.setEditable(false); // cannot edit this field
        description.setLineWrap(true); // allows the line to flow over into the next line
        description.setWrapStyleWord(true); // keeps words from being split up in the flow
        return description;
    }

    /**
     * Method that facilitates the selecting of a Garment from the results view, and calls the necessary method
     * depending on the action taken. If the "search again" button is clicked, the search view is regenerated, and if
     * a Garment is chosen from the dropdown list, the particular garment is checked for validity.
     * @return JPanel containing a "search again" button and a drop-down list to select a chosen Garment
     */
    public static JPanel selectFromResultsPanel() {
        JLabel noneMessage = new JLabel("Don't like these results? Try searching again!");
        JButton searchAgain = new JButton("Search again");
        // action listener that regenerates and shows the search view
        ActionListener actionListener = e -> reGenerateSearchView(false);
        searchAgain.addActionListener(actionListener); // add it to the button
        Map<String, Garment> optionsMap = getOptionsMap(); // get our HashMap ensuring data encapsulation
        optionsCombo.setSelectedItem(optionsMap.get("Select garment")); // the default
        ActionListener actionListenerChoice = e -> checkUserGarmentSelection(optionsMap, optionsCombo); // we make the check here
        optionsCombo.addActionListener(actionListenerChoice);  // which is added to the drop-down box
        // create a panel to hold our buttons (FlowLayout[left to right])
        JPanel buttonOptionPanel = new JPanel();
        buttonOptionPanel.add(optionsCombo); // drop down list on the left
        buttonOptionPanel.add(searchAgain); // search again button on the right
        // create and return a new JPanel that holds a border, our instructional messages and the buttons/drop-down list
        JPanel selectionPanel = new JPanel();
        selectionPanel.setLayout(new BoxLayout(selectionPanel, BoxLayout.Y_AXIS)); // stack vertically
        selectionPanel.add(Box.createRigidArea(new Dimension(0, 10))); // padding
        // titled border that contains a message
        selectionPanel.setBorder(BorderFactory.createTitledBorder("Please select which garment you'd like to order"));
        selectionPanel.add(noneMessage); // add our messages and button/list panel
        selectionPanel.add(buttonOptionPanel);
        selectionPanel.add(Box.createRigidArea(new Dimension(10, 0))); // padding
        return selectionPanel;
    }

    /**
     * This method determines if the selected option from the dropDown list was a null option or if it is a Garment.
     * Makes nothing happen if it was a null option (what we want) and generates the order view if it was not.
     * @param options : The Map containing the String : Garment to compare our choice from the dropDown list
     * @param optionsCombo : The dropdown list with which we compare to the HashMap
     */
    public static void checkUserGarmentSelection(Map<String, Garment> options, JComboBox<String> optionsCombo) {
        String garmentName = (String) optionsCombo.getSelectedItem(); // get the choice from the dropdown list
        if (options.get(garmentName) != null) { // if it's a null Garment, then nothing happens
            choice = options.get(garmentName); // otherwise choice is the Garment we want
            generateOrderView(choice); // generate an order view with the Garment as the choice
        }
    }

    /**
     * Method to write the details of a particular order to a text file, for Greek Geek to then fulfill
     * @param geek : A geek object : The person who made the order
     * @param Garment : A Garment object : The garment being ordered
     * @param message : String : The message included with the order
     */
    public static void submitOrder(Geek geek, Garment Garment, String message) {
        String filePath = geek.getName().replace(" ","_")+"_"+Garment.getProductCode()+".txt";
        Path path = Path.of(filePath); // Alex_Robertson_222222222.txt is what is outputted from this
        String lineToWrite = "Order details:\n\t" + // Order details:
                "Name: "+geek.getName()+            // Alex
                "\n\tEmail Address: "+geek.getEmail()+ // geek@geekmail.com
                "\n\tItem: "+Garment.getName()+" ("+Garment.getProductCode()+")" + // walterWhite (22222222)
                "\n\tMessage from geek: " + message;  // I don't have any money!!!
        try {
            Files.writeString(path, lineToWrite); // tries to write the above string to the text file at the path
        }catch (IOException io){ // but can crash the program for myriad reasons
            JOptionPane.showMessageDialog(mainWindow, // Use a message dialog for this instead of println
                    "Order could not be placed. \nError message: "+io.getMessage(),
                    "Error with Order",
                    JOptionPane.ERROR_MESSAGE,
                    null);
            System.exit(0);
        }
        JOptionPane.showMessageDialog(mainWindow, // if success, Let the customer know, or they may be confused!
                "Message sent! \nOne of our friendly staff will be in touch shortly.",
                appName,
                JOptionPane.INFORMATION_MESSAGE,
                null);
        reGenerateSearchView(true); // go back to search view, resetting it in the process.
    }

    /**
     * This method reads in information from a text file, in this case generally our "Inventory.txt" file.
     * Stores all the pertinent information in a HashMap which is stored within a GarmentSpecs object, and anything
     * not placed in a HashMap (name, product code etc.) is stored inside a Garment object.
     * @param filePath String representation of the filepath where the text file in question is stored
     * @return Instantiation of inventory object, containing information on all stored Garments.
     */
    public static Inventory loadInventory(String filePath) {
        Inventory allGarments = new Inventory(); // instantiate an Inventory object
        Path path = Path.of(filePath); // look for file at the location specified by the argument
        List<String> fileContents = null; // holds each line of the text file
        try {
            fileContents = Files.readAllLines(path); // load the lines of the text file into the ArrayList
        }catch (IOException io){    // If an IO exception is thrown..
            System.out.println("File could not be found"); // Read a message
            System.exit(0); // terminate
        }
        for(int i=1;i<fileContents.size();i++){  // for every element in fileContents
            String[] info = fileContents.get(i).split("\\["); // split on opening brackets
            String[] singularInfo = info[0].split(",");     // and split that on commas
            String sizesRaw = info[1].replace("]","");  // replace all closing brackets with empty space
            String description = info[2].replace("]","");  // as above

            GarmentType garmentType = null;  // instantiate new Garment
            try {  // replace hyphens with underscores, and catch any IllegalArgumentException that may be caused by bad text
                garmentType = GarmentType.valueOf(singularInfo[0].replace("-","_").toUpperCase()); //error catching
            }catch (IllegalArgumentException e){
                System.out.println("Error in file. type data could not be parsed for garment on line "+(i+1)+". Terminating. \nError message: "+e.getMessage());
                System.exit(0);
            }
            String name = singularInfo[1];

            long productCode = 0;
            try{
                productCode = Long.parseLong(singularInfo[2]); // parse the product code into a Long
            }catch (NumberFormatException n) { // could cause a NumberFormatException
                System.out.println("Error in file. Product code could not be parsed for garment on line "+(i+1)+". Terminating. \nError message: "+n.getMessage());
                System.exit(0);
            }

            double price = 0;
            try{
                price = Double.parseDouble(singularInfo[3]); // parse the price into a Double
            }catch (NumberFormatException n){
                System.out.println("Error in file. Price could not be parsed for garment on line "+(i+1)+". Terminating. \nError message: "+n.getMessage());
                System.exit(0);
            }

            String brand = singularInfo[4];

            Material material = null;
            try{
                material = Material.valueOf(singularInfo[5].toUpperCase().replace(" ","_"));
            }catch (IllegalArgumentException e){ // bad input into parsing an Enum can cause this error
                System.out.println("Error in file. Material data could not be parsed for garment on line "+(i+1)+". Terminating. \nError message: "+e.getMessage());
                System.exit(0);
            }
            Neckline neckline = null;
            try{
                neckline = Neckline.valueOf(singularInfo[6].toUpperCase());
            }catch (IllegalArgumentException e){
                System.out.println("Error in file. Neckline data could not be parsed for t-shirt on line "+(i+1)+". Terminating. \nError message: "+e.getMessage());
                System.exit(0);
            }
            SleeveType sleeveType = null;
            try{
                sleeveType = SleeveType.valueOf(singularInfo[7].toUpperCase().replace(" ","_"));
            }catch (IllegalArgumentException e){
                System.out.println("Error in file. Sleeve type data could not be parsed for t-shirt on line "+(i+1)+". Terminating. \nError message: "+e.getMessage());
                System.exit(0);
            }
            PocketType pocketType = null;
            try{
                pocketType = PocketType.valueOf(singularInfo[8].toUpperCase());
            }catch (IllegalArgumentException e){
                System.out.println("Error in file. Pocket type data could not be parsed for hoodie on line "+(i+1)+". Terminating. \nError message: "+e.getMessage());
                System.exit(0);
            }
            HoodieStyle hoodieStyle = null;
            try{
                hoodieStyle = HoodieStyle.valueOf(singularInfo[9].toUpperCase().replace(" ","_"));
            }catch (IllegalArgumentException e){
                System.out.println("Error in file. Style data could not be parsed for hoodie on line "+(i+1)+". Terminating. \nError message: "+e.getMessage());
                System.exit(0);
            }

            Set<Size> sizes = new HashSet<>(); // instantiate new HashSet to hold available sizes
            for(String s: sizesRaw.split(",")){ // split the sizeRaw by commas and iterate through the Strings
                Size size = Size.S; // instantiate new Size object
                try {
                    size = Size.valueOf(s); // read in the correct Size constant through the string
                }catch (IllegalArgumentException e){  // if it doesn't match a constant we'll end up here
                    System.out.println("Error in file. Size data could not be parsed for t-shirt on line "+(i+1)+". Terminating. \nError message: "+e.getMessage());
                    System.exit(0);
                }
                sizes.add(size); // add the size to the HashSet
            }

            Map<Filter,Object> filterMap = new LinkedHashMap<>(); // this is the map we pass to the GarmentSpecs object
            filterMap.put(Filter.GARMENT_TYPE,garmentType); // place GarmentType
            filterMap.put(Filter.BRAND,brand); // place the brand
            filterMap.put(Filter.MATERIAL,material); // place the material constant
            filterMap.put(Filter.SIZE,sizes); // sizeSet
            if(!neckline.equals(Neckline.NA)) filterMap.put(Filter.NECKLINE,neckline); // only place if no NA
            if(!sleeveType.equals(SleeveType.NA)) filterMap.put(Filter.SLEEVE_TYPE,sleeveType); //
            if(!hoodieStyle.equals(HoodieStyle.NA)) filterMap.put(Filter.HOODIE_STYLE,hoodieStyle); //
            if(!pocketType.equals(PocketType.NA)) filterMap.put(Filter.POCKET_TYPE,pocketType); //

            GarmentSpecs dreamGarment = new GarmentSpecs(filterMap); // GarmentSpecs is instantiated with the HashMap

            Garment Garment = new Garment(name,productCode,price,description,dreamGarment); // everything else goes here, including the GarmentSpecs object
            allGarments.addGarment(Garment); // add it to the inventory object
        }
        return allGarments; // return the Inventory
    }

    /**
     * @return </String, Garment> map that holds the results from a search
     */
    public static Map<String, Garment> getOptionsMap() {
        return new LinkedHashMap<>(optionsMap);
    }

    /**
     * Method to set the HashMap when it changes, creating a new LinkedHashMap so as not to corrupt data
     * @param optionsMap The new HashMap with which to replace the old with.
     */
    public static void setOptionsMap(Map<String, Garment> optionsMap) {
        GarmentSearcher.optionsMap = new LinkedHashMap<>(optionsMap);
    }
}
