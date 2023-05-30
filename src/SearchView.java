import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.HashSet;
import java.util.Set;

public class SearchView {
    /*---------APPLICATION FIELDS-------*/
    private final CardLayout cardLayout = new CardLayout(); // so we can use a cardLayout with our selection type
    // cards
    private final String HOODIE_PANEL = "Hoodie";
    private final String TSHIRT_PANEL = "T-Shirt";
    private final String IMAGE_PANEL = "garment images";
    private JPanel inputCriteriaTypePanel; // holds our cards for us. How nice!
    private final double maxPrice;
    private final JLabel feedbackMin = new JLabel(" ");
    private final JLabel feedbackMax = new JLabel(" ");
    /*------USER INPUT FIELDS------*/
    private GarmentType inputGarmentType; // stores our selected garment type
    private Set<Size> sizes; // Set to hold all the input sizes;
    private Material material;
    private double userMinPrice;
    private double userMaxPrice;
    private final Set<String> availableBrands;
    private Set<String> userBrands;
    // hoodie specific
    private PocketType pocketType;
    private HoodieStyle hoodieStyle;
    // t-shirt specific
    private Neckline neckline;
    private SleeveType sleeveType;

    /**
     * Constructor used to initialise the search view
     * @param availableBrands : All the brand names a user can choose from - derived from inventory.txt
     * @param maxPrice : The maximum price of a garment - derived from inventory.txt
     */
    public SearchView(Set<String> availableBrands, double maxPrice) {
        if (availableBrands != null) {
            this.availableBrands = new HashSet<>(availableBrands);
        } else {
            this.availableBrands = new HashSet<>();
        }
        this.maxPrice = maxPrice;
        this.userBrands = new HashSet<>();
        this.sizes = new HashSet<>();
    }
    /*---------------MAIN------------*/
    /**
     * This method generates the main JPanel that represents our search view. Initialises the input criteria panel to a
     * card layout, so it can swap out the type specific panels as we need them.
     * @return JPanel object
     */
    public JPanel generateSearchView() {
        // JPanel to hold the other JPanels
        JPanel criteria = new JPanel();
        // stack them vertically
        criteria.setLayout(new BoxLayout(criteria, BoxLayout.Y_AXIS));
        // add GarmentType, then generic, then some padding
        JPanel typeOf = this.userInputTypeofGarment();
        typeOf.setAlignmentX(0);
        criteria.add(typeOf);
        JPanel generic = this.userInputGenericPanel();
        generic.setAlignmentX(0);
        criteria.add(generic);
        // pad it
        criteria.add(Box.createRigidArea(new Dimension(0, 20)));
        //initialise the JPanel that contains the type specific filters
        inputCriteriaTypePanel = new JPanel();
        // card layout lets us switch between the type specific panels as we need them, the String constants help us keep track of them
        inputCriteriaTypePanel.setAlignmentX(0);
        inputCriteriaTypePanel.setLayout(cardLayout);
        inputCriteriaTypePanel.add(this.generateImagePanel(), IMAGE_PANEL);
        inputCriteriaTypePanel.add(this.userInputShirt(), TSHIRT_PANEL);
        inputCriteriaTypePanel.add(this.userInputHoodie(), HOODIE_PANEL);
        // add the card panel to the main panel
        criteria.add(inputCriteriaTypePanel);
        return criteria;
    }
    /*--------------------------FILTER PANELS---------*/
    /**
     * Method that enables users to select the type of garment they're after. Uses a drop-down list.
     * Checks for changes in selection with an ItemListener.
     * @return JPanel object with our GarmentType panel
     */
    public JPanel userInputTypeofGarment() {
        // create a drop-down list populated with the different types of garments the user can browse.
        JComboBox<GarmentType> garmentTypeJComboBox = new JComboBox<>(GarmentType.values());
        // makes the program focus on the combo-box first.
        garmentTypeJComboBox.requestFocusInWindow();
        // sets the default choice to "select_type" so that the user knows to actually choose a type.
        garmentTypeJComboBox.setSelectedItem(GarmentType.SELECT_TYPE);
        // initialises our data field with the selected item in the drop-down list.
        inputGarmentType = (GarmentType) garmentTypeJComboBox.getSelectedItem();
        // points to the ifTypeSelected method when a new value in the drop-down list is selected
        garmentTypeJComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) ifTypeSelected(garmentTypeJComboBox);
        });
        // creates our JPanel object to hold the drop-down list.
        return generateJComboBoxPanel(garmentTypeJComboBox);
    }
    public void ifTypeSelected(JComboBox<GarmentType> garmentTypeJComboBox) {
        inputGarmentType = (GarmentType) garmentTypeJComboBox.getSelectedItem(); // update the field to current selection
        assert inputGarmentType != null; // enforces no null value passed in.
        // uses CardLayout to show the appropriate panel based on the selection
        if (inputGarmentType.equals(GarmentType.SELECT_TYPE)) cardLayout.show(inputCriteriaTypePanel, IMAGE_PANEL);
        else if (inputGarmentType.equals(GarmentType.HOODIE)) cardLayout.show(inputCriteriaTypePanel, HOODIE_PANEL);
        else if (inputGarmentType.equals(GarmentType.T_SHIRT)) cardLayout.show(inputCriteriaTypePanel, TSHIRT_PANEL);
    }
    /**
     * Method that allows the user to select a preferred size, or multiple.
     * @return a JPanel object containing our dropdown list and instructional label
     */
    public JPanel userInputSizes() {
        // create a JList of all the sizes
        JList<Size> selectSizes = new JList<>(Size.values());
        // enable multi-selection mode
        selectSizes.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        // scroll pane to limit the visible size of the JList and enable scrolling
        JScrollPane scrollPane = generateJListScrollPane(selectSizes);
        // update the sizes' field if a new item is selected
        ListSelectionListener listSelectionListener = e -> sizes = new HashSet<>(selectSizes.getSelectedValuesList());
        selectSizes.addListSelectionListener(listSelectionListener);
        // add the dropdown list, and some instructions to a panel then return it
        return generateJScrollPanel("Please select your preferred sizes (Mandatory)", scrollPane);
    }
    /**
     * Method to create a JPanel that allows the user to select their preferred material type.
     * Uses an action listener, that changes the field to the indicated value.
     * @return a JPanel object radio buttons and some clarification text.
     */
    public JPanel userInputMaterial() {
        ButtonGroup materialButtonGroup = new ButtonGroup(); // bound each button to each other to allow only one selection
        JRadioButton cotton = new JRadioButton(Material.COTTON.toString()); // creates a button for each option
        JRadioButton wool = new JRadioButton(Material.WOOL_BLEND.toString());
        JRadioButton poly = new JRadioButton(Material.POLYESTER.toString());
        JRadioButton na = new JRadioButton(Material.NA.toString(), true); // selected by default
        // material selection is not mandated, so it is defaulted to NA.
        material = Material.NA;
        // add each button to the group
        materialButtonGroup.add(cotton);
        materialButtonGroup.add(wool);
        materialButtonGroup.add(poly);
        materialButtonGroup.add(na);
        // set our action commands to give the buttons values when clicked
        cotton.setActionCommand(Material.COTTON.name());
        wool.setActionCommand(Material.WOOL_BLEND.name());
        poly.setActionCommand(Material.POLYESTER.name());
        na.setActionCommand(Material.NA.name());
        // action listener added to each button, updates the field if the user changes their selection
        ActionListener actionListener = e -> material = Material.valueOf(materialButtonGroup.getSelection().getActionCommand().toUpperCase());
        cotton.addActionListener(actionListener);
        wool.addActionListener(actionListener);
        poly.addActionListener(actionListener);
        na.addActionListener(actionListener);
        // create and return new JPanel
        JPanel materialPanel = new JPanel();
        materialPanel.setAlignmentX(0);
        // titled border
        materialPanel.setBorder(BorderFactory.createTitledBorder("Which material do you prefer?"));
        // clarification text with special font
        JLabel clarification = new JLabel("(Optional: Select 'Skip...' to allow for all options)");
        clarification.setFont(new Font("",Font.ITALIC, 11));
        materialPanel.add(clarification);
        materialPanel.add(na);
        materialPanel.add(cotton);
        materialPanel.add(wool);
        materialPanel.add(poly);

        return materialPanel;
    }
    /**
     * Method that takes a price range in JTextField boxes, and validating input with DocumentListeners.
     * Places the input boxes in a JPanel which is returned inside another JPanel.
     * @return JPanel object with our instructions, text fields for input and feedback validating input
     */
    public JPanel userInputPriceRange() {
        // labels for text boxes
        JLabel minLabel = new JLabel("Min. ");
        JLabel maxLabel = new JLabel("Max. ");
        // create the input boxes
        JTextField min = new JTextField(4);
        JTextField max = new JTextField(4);
        // set default values for the text boxes
        min.setText(String.valueOf(getMinPrice()));
        setUserMinPrice(Double.parseDouble(min.getText())); // set's user min price, so they don't have to enter anything
        max.setText(String.valueOf(getMaxPrice()));         // which allows them to just click search with default values
        setUserMaxPrice(Double.parseDouble(max.getText()));
        // set font and colour of feedback messages
        feedbackMin.setFont(new Font("", Font.ITALIC, 11));
        feedbackMin.setForeground(Color.RED);
        feedbackMax.setFont(new Font("", Font.ITALIC, 11));
        feedbackMax.setForeground(Color.RED);
        // add our document listeners
        min.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if(!checkMin(min)) min.requestFocus(); // if false, focus text box so user can address input
                checkMax(max); // check that max is okay
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                if(!checkMin(min))min.requestFocus();
                checkMax(max); // same as above
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                // pass
            }
        });
        max.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if(!checkMax(max)) max.requestFocus();
                checkMin(min); // make the same checks as min
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                if(!checkMax(max))max.requestFocus();
                checkMin(min);
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                // pass
            }
        });
        // add our text fields and labels to a panel
        JPanel priceRangePanel = new JPanel();
        priceRangePanel.add(minLabel);
        priceRangePanel.add(min);
        priceRangePanel.add(maxLabel);
        priceRangePanel.add(max);
        // create a panel that holds the above panel
        JPanel overPanel = new JPanel();
        overPanel.setBorder(BorderFactory.createTitledBorder("Input desired price range"));
        overPanel.setLayout(new BoxLayout(overPanel, BoxLayout.Y_AXIS)); // stacks elements vertically
        overPanel.setAlignmentX(0);
        overPanel.add(priceRangePanel);
        feedbackMin.setAlignmentX(0);
        feedbackMax.setAlignmentX(0);
        overPanel.add(feedbackMin); // places the feedback labels below the text boxes
        overPanel.add(feedbackMax);
        return overPanel;
    }
    /**
     * validates user input for min price
     * @param minEntry the JTextField to enter the min price
     * @return true if valid price, false if invalid
     */
    private boolean checkMin(JTextField minEntry) {
        feedbackMin.setText("");
        try {
            double tempMin = Double.parseDouble(minEntry.getText());
            setUserMinPrice(getMinPrice());  // stop the user from putting in a good value, removing it, and then putting in a bad value and getting past validation
            if (tempMin < 0) { // message for negative double
                feedbackMin.setText("Min price must be greater than 0! Defaulting to 0 - " + getMaxPrice() + ".");
                minEntry.selectAll(); // highlight text
                setUserMinPrice(getMinPrice());  // stop the user from putting in a good value, removing it, and then putting in a bad value and getting past validation
                return false;
            } else if (tempMin > getMaxPrice()) { // don't allow userMin to be greater than possible max price
                feedbackMin.setText("Min price must be below $" + getMaxPrice() +"! Defaulting to 0 - " + getMaxPrice() + ".");
                minEntry.selectAll();
                setUserMinPrice(getMinPrice());  // stop the user from putting in a good value, removing it, and then putting in a bad value and getting past validation
                return false;
            } else if (tempMin > getUserMaxPrice()) { // user min must be below user max # POSSIBLE EXCEPTION
                feedbackMin.setText("Min price must be below your max price! Defaulting to 0 - " + getMaxPrice() + ".");
                minEntry.selectAll();
                setUserMinPrice(getMinPrice());  // stop the user from putting in a good value, removing it, and then putting in a bad value and getting past validation
                return false;
            } else {
                setUserMinPrice(tempMin);
                feedbackMin.setText("");
                return true;
            }
        } catch (NumberFormatException e) { // possible exception when parsing text as a double
            feedbackMin.setText("Please enter a valid number for min price. Defaulting to 0 - " + getMaxPrice() + ".");
            minEntry.selectAll();
            setUserMinPrice(getMinPrice());  // stop the user from putting in a good value, removing it, and then putting in a bad value and getting past validation
            return false;
        }
    }
    /**
     * validates user input for max prices
     * @param maxEntry The JTextField to enter the max price
     * @return true if valid price, false if invalid
     */
    private boolean checkMax(JTextField maxEntry) {
        feedbackMax.setText("");
        try {
            double tempMax = Double.parseDouble(maxEntry.getText());
            if (tempMax < 0) { // must be greater than 0
                feedbackMax.setText("Max price must be above 0! Defaulting to 0 - " + getMaxPrice() + ".");
                maxEntry.selectAll();
                setUserMaxPrice(maxPrice); // stop the user from putting in a good value, removing it, and then putting in a bad value and getting past validation
                return false;
            } else if (tempMax < getUserMinPrice()) { // can't be less than your minimum price # POSSIBLE EXCEPTION
                feedbackMax.setText("Your max price must be less than your min price! Defaulting to 0 - " + getMaxPrice() + ".");
                maxEntry.selectAll();
                setUserMaxPrice(maxPrice); // stop the user from putting in a good value, removing it, and then putting in a bad value and getting past validation
                return false;
            } else {
                feedbackMax.setText("");
                setUserMaxPrice(tempMax);
                return true;
            }
        } catch (NumberFormatException e) {
            feedbackMax.setText("Please enter a valid number for max price. Defaulting to 0 - " + getMaxPrice() + ".");
            maxEntry.selectAll();
            setUserMaxPrice(maxPrice); // stop the user from putting in a good value, removing it, and then putting in a bad value and getting past validation
            return false;
        }
    }
    /**
     * This method generates the JPanel to allow the user to select their preferred brands. Stores the selection in a
     * HashSet. Uses a scrolling drop down list and calls the GenerateJScrollPanel method to finish up its work.
     * @return : JPanel object with our dropdown list and some info text
     */
    public JPanel userInputBrands() {
        JList<String> selectBrands = new JList<>(availableBrands.toArray(new String[0])); // cast our Brands set to the JList
        selectBrands.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); // allow multi-select
        JScrollPane scrollPane = generateJListScrollPane(selectBrands);
        // listSelectionListener that adds each selection to the field
        ListSelectionListener listSelectionListener = e -> userBrands = new HashSet<>(selectBrands.getSelectedValuesList());
        selectBrands.addListSelectionListener(listSelectionListener);
        // add the dropdown list, and some instructions to a panel then return it
        return generateJScrollPanel("Please select your preferred brand names (Optional)", scrollPane);
    }
    /*---------------------------HELPER METHODS-----------*/
    /**
     * Helper method to generate the JPanel when we're using a JScrollPane, generally used in a return statement
     * @param instruction : String : some instructions for the user
     * @param scrollPane : JScrollPane : The dropdown scrollable list to use
     * @return A JPanel object with some instructions and a scrollable list
     */
    public JPanel generateJScrollPanel(String instruction, JScrollPane scrollPane) {
        JLabel instructionLabel = new JLabel(instruction);
        instructionLabel.setAlignmentX(0);
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS)); // stack vertically
        itemPanel.add(Box.createRigidArea(new Dimension(0, 5))); // add some padding please
        itemPanel.add(instructionLabel);
        JLabel clarification = new JLabel("(To multi-select / de-select, hold CTRL)");
        clarification.setAlignmentX(0);
        clarification.setFont(new Font("", Font.ITALIC, 11)); // make the clarification stand out
        itemPanel.add(clarification);
        scrollPane.setAlignmentX(0);
        itemPanel.add(scrollPane);
        itemPanel.add(Box.createRigidArea(new Dimension(0, 5))); // more padding!
        return itemPanel;
    }
    /**
     * gives a JList scroll pane functionality
     * @param selectItems the JList to embed in the scroll pane
     * @return a scroll pane of specified size and behaviour, containing a JList
     */
    public JScrollPane generateJListScrollPane(JList<?> selectItems) {
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(selectItems);
        selectItems.setLayoutOrientation(JList.VERTICAL);
        scrollPane.setPreferredSize(new Dimension(250, 60));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        SwingUtilities.invokeLater(() -> scrollPane.getViewport().setViewPosition(new Point(0, 0)));
        return scrollPane;
    }
    /**
     * Helper method to create JComboBox JPanels for our GarmentType specific criteria.
     * @param jComboBox : A JComboBox with any input type
     * @return JPanel object with the specifications set, adding in our JComboBox passed in
     */
    public JPanel generateJComboBoxPanel(JComboBox<?> jComboBox) {
        JPanel panel = new JPanel();
        panel.setLayout (new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(0);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(jComboBox);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        return panel;
    }
    /*--------------------HOODIE SPECIFIC---------------*/
    /**
     * Method to generate a JPanel to show hoodie style options, uses a JComboBox dropdown list, updates the variable
     * with an ItemListener.
     * @return JPanel object with a dropdown list
     */
    public JPanel userInputHoodieStyle() {
        // Drop down list of our hoodie styles
        JComboBox<HoodieStyle> jComboBox = new JComboBox<>(HoodieStyle.values());
        jComboBox.setAlignmentX(0);
        jComboBox.setMaximumSize(new Dimension(2500, 40));
        jComboBox.setSelectedItem(HoodieStyle.NA);
        // initialise the field
        hoodieStyle = (HoodieStyle) jComboBox.getSelectedItem();
        // item listener updates the field if a new selection is made
        jComboBox.addItemListener( e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                hoodieStyle = (HoodieStyle) jComboBox.getSelectedItem();
            }
        });
        // create and return the JPanel containing our dropdown list and padding
        return generateJComboBoxPanel(jComboBox);
    }
    /**
     * Method to generate a JPanel to show hoodie pocket type options, uses a JComboBox dropdown list, updates the
     * variable with an ItemListener
     * @return JPanel object with a dropdown list
     */
    public JPanel userInputHoodiePocket() {
        //drop down list of hoodie pocket style
        JComboBox<PocketType> jComboBox = new JComboBox<>(PocketType.values());
        jComboBox.setAlignmentX(0);
        jComboBox.setMaximumSize(new Dimension(2500, 40));
        jComboBox.setSelectedItem(PocketType.NA);
        // initialise the field
        pocketType = (PocketType) jComboBox.getSelectedItem();
        // item listener updates the field if a new selection is made
        jComboBox.addItemListener( e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                pocketType = (PocketType) jComboBox.getSelectedItem();
            }
        });
        // create and return the JPanel containing our dropdown list and padding
        return generateJComboBoxPanel(jComboBox);
    }
    /*-------------------------TSHIRT SPECIFIC--------*/
    /**
     * Method to generate a JPanel to show shirt neckline options, uses a JComboBox dropdown list, updates the
     * variable with an ItemListener
     * @return JPanel object with a dropdown list
     */
    public JPanel userInputShirtNeckline() {
        // drop down list of shirt necklines
        JComboBox <Neckline> jComboBox = new JComboBox<>(Neckline.values());
        jComboBox.setAlignmentX(0);
        jComboBox.setMaximumSize(new Dimension(2500, 40));
        jComboBox.setSelectedItem(Neckline.NA);
        // initialise the field
        neckline = (Neckline) jComboBox.getSelectedItem();
        // item listener updates the field if a new selection is made
        jComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                neckline = (Neckline) jComboBox.getSelectedItem();
            }
        });
        // create and return the JPanel containing our dropdown list and padding
        return generateJComboBoxPanel(jComboBox);
    }
    /**
     * Method to generate a JPanel to show shirt sleeve type options, uses a JComboBox dropdown list, updates the
     * variable with an ItemListener
     * @return JPanel object with a dropdown list
     */
    public JPanel userInputShirtSleeve() {
        //drop down list of shirt sleeves
        JComboBox<SleeveType> jComboBox = new JComboBox<>(SleeveType.values());
        jComboBox.setAlignmentX(0);
        jComboBox.setMaximumSize(new Dimension(2500, 40));
        jComboBox.setSelectedItem(SleeveType.NA);
        // initialise the field
        sleeveType = (SleeveType) jComboBox.getSelectedItem();
        // item listener updates the field if a new selection is made
        jComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                sleeveType = (SleeveType) jComboBox.getSelectedItem();
            }
        });
        // create and return the JPanel containing our dropdown list and padding
        return generateJComboBoxPanel(jComboBox);
    }
    /*--------------------------OVERALL------------*/
    /**
     * Brings together the Hoodie methods into a single JPanel object, one of our cards.
     * @return a JPanel object containing the filters used to search for a hoodie
     */
    public JPanel userInputHoodie() {
        JLabel instruction = new JLabel("Please select your preferred Hoodie style and pocket type");
        JLabel clarification = new JLabel("(optional)");
        clarification.setFont(new Font("", Font.ITALIC, 11));
        JPanel style = userInputHoodieStyle();
        JPanel pocket = userInputHoodiePocket();
        JPanel overall = new JPanel();
        overall.setLayout(new BoxLayout(overall, BoxLayout.PAGE_AXIS)); // follow the "page line"
        overall.add(instruction);
        overall.add(clarification);
        overall.add(style);
        overall.add(pocket);
        return overall;
    }
    public JPanel userInputShirt() {
        JLabel instruction = new JLabel("Please select your preferred neckline and sleeve style");
        JLabel clarification = new JLabel("(optional)");
        clarification.setFont(new Font("", Font.ITALIC, 11));
        JPanel neck = userInputShirtNeckline();
        JPanel sleeve = userInputShirtSleeve();
        JPanel overall = new JPanel();
        overall.setLayout(new BoxLayout(overall, BoxLayout.PAGE_AXIS)); // follow the "page line"
        overall.add(instruction);
        overall.add(clarification);
        overall.add(neck);
        overall.add(sleeve);
        return overall;
    }
    /**
     * Method that brings together all the JPanels with our generic criteria, GarmentType, brands, sizes, material etc.
     * @return A JPanel object containing sub-JPanels for each generic criteria
     */
    public JPanel userInputGenericPanel() {
        JPanel genericPanel = new JPanel();
        // builds the panel by calling all the methods
        genericPanel.setLayout(new BoxLayout(genericPanel, BoxLayout.Y_AXIS));
        genericPanel.add(userInputBrands());
        genericPanel.add(userInputSizes());
        genericPanel.add(userInputMaterial());
        genericPanel.add(userInputPriceRange());
        return genericPanel;
    }
    /**
     * A method to generate a panel of the images representing the types of clothing we can search for
     * @return JPanel object
     */
    public JPanel generateImagePanel() {
        // load the images as JLabels
        JLabel shirt = new JLabel(new ImageIcon("images/breakingBad_.jpg"));
        JLabel hoodie = new JLabel(new ImageIcon("images/westworld_.jpg"));
        // container panel to hold the images
        JPanel imagePanel = new JPanel();
        imagePanel.add(shirt);
        imagePanel.add(hoodie);
        return imagePanel;
    }
    /*--------------------GETTERS/SETTERS--------*/
    /**
     * Getter for our selected garmentType
     * @return our selected garmentType
     */
    public GarmentType getInputGarmentType() {
        return inputGarmentType;
    }

    /**
     * @return our selected Size Set
     */
    public Set<Size> getSizes() {
        return new HashSet<>(sizes);
    }

    /**
     * Getter for the input material
     * @return our selected Material object
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Getter for the input pocket-type
     * @return our selected PocketType object
     */
    public PocketType getPocketType() {
        return pocketType;
    }

    /**
     * Getter for the input hoodie-style
     * @return our selected HoodieStyle object
     */
    public HoodieStyle getHoodieStyle() {
        return hoodieStyle;
    }

    /**
     * Getter for the input neck-line
     * @return our selected Neckline object
     */
    public Neckline getNeckline() {
        return neckline;
    }
    /**
     * Getter for our input sleeve-type
     * @return our selected SleeveType object
     */
    public SleeveType getSleeveType() {
        return sleeveType;
    }
    /**
     * @return Double : minimum garment price
     */
    public double getMinPrice() {
        // range parameters
        return 0;
    }
    /**
     * @return Double : maximum garment price
     */
    public double getMaxPrice() {
        return maxPrice;
    }
    /**
     * @return Double : User's specified minimum price
     */
    public double getUserMinPrice() {
        return userMinPrice;
    }
    /**
     * @return Double : User's specified maximum price
     */
    public double getUserMaxPrice() {
        return userMaxPrice;
    }
    /**
     * Sets the userMinPrice field to a new value
     * @param minPrice : new minimum price
     */
    public void setUserMinPrice(Double minPrice) {
        this.userMinPrice = minPrice;
    }
    /**
     * Sets the userMaxPrice field to a new value
     * @param maxPrice : new maximum price
     */
    public void setUserMaxPrice(double maxPrice) {
        this.userMaxPrice = maxPrice;
    }
    /**
     * @return copy of HashSet of chosen brands
     */
    public Set<String> getUserBrands() {
        return new HashSet<>(userBrands);
    }
}
