import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class UserDetailsView {
    private String name;
    private String email;
    private final Garment choice;
    private JTextArea message;
    private final JLabel feedbackName = new JLabel("");
    private final JLabel feedbackEmail = new JLabel("");

    /**
     * Constructor for the UserDetailsView class. Only requires a garment passed in, so we can display it on the form.
     * @param choice : Garment : the user's chosen garment to order.
     */
    public UserDetailsView(Garment choice) {
        this.choice = choice;
    }

    /**
     * Method to create an overarching panel containing all the other panels contained in this class.
     * @return The JPanel described
     */
    public JPanel generateContactForm() {
        JPanel form = new JPanel(); // holds the other JPanels
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS)); // stack vertically
        JPanel confirm = this.choiceDetailsPanel(); // add each panel
        confirm.setAlignmentX(0); // centre them
        form.add(confirm);
        JPanel namePanel = this.userNamePanel();
        namePanel.setAlignmentX(0);
        form.add(namePanel);
        JPanel emailPanel = this.userEmailPanel();
        emailPanel.setAlignmentX(0);
        form.add(emailPanel);
        JPanel messagePanel = this.userInputMessage();
        messagePanel.setAlignmentX(0);
        form.add(messagePanel);
        return form; // and return the master panel
    }

/*-----------------------INPUT PANELS---------*/
    /**
     * This method displays the chosen Garment to remind the user which garment they are ordering.
     * @return JPanel object with uneditable text area containing description of Garment
     */
    public JPanel choiceDetailsPanel() {
        JTextArea description = new JTextArea(choice.getGarmentInformation()); // populate with the garment's description text
        description.setEditable(false); // don't allow for editing this
        description.setLineWrap(true); // words flow into next line
        description.setWrapStyleWord(true); // words stay together
        // create and return a new JPanel, with a border, our text field and a bit of padding.
        JPanel choicePanel = new JPanel();
        choicePanel.setBorder(BorderFactory.createTitledBorder("Your order details are below"));
        choicePanel.setLayout(new BoxLayout(choicePanel, BoxLayout.Y_AXIS)); // stack vertically
        choicePanel.add(Box.createRigidArea(new Dimension(0, 10))); // pad
        choicePanel.add(description); // place the description in
        return choicePanel;
    }

    /**
     * Method that displays the name entry, enforcing a format through its DocumentListener that calls checkName.
     * Displays feedback text to let the user know what is expected of them.
     * @return JPanel object with name entry field, padding and feedback text
     */
    public JPanel userNamePanel() {
        JLabel instruction = new JLabel("Enter your full name");
        // create our entry field
        JTextField nameEntry = new JTextField(4);
        // set the feedback text font and colour
        feedbackName.setFont(new Font("", Font.ITALIC, 11));
        feedbackName.setForeground(Color.RED);
        // add a document listener
        nameEntry.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (checkName(nameEntry)) nameEntry.requestFocusInWindow();
            }   // check the name according to checkName, if it returns true we ask the user to check their input.
            @Override
            public void removeUpdate(DocumentEvent e) {
                if (checkName(nameEntry)) nameEntry.requestFocusInWindow();
            }  // same as above
            @Override
            public void changedUpdate(DocumentEvent e) { /* pass */}
        });
        JPanel nameEntryPanel = new JPanel(); // instantiate new panel
        nameEntryPanel.setLayout(new BoxLayout(nameEntryPanel, BoxLayout.Y_AXIS)); // stack vertically
        nameEntryPanel.setAlignmentX(0); // center it
        nameEntryPanel.add(Box.createRigidArea(new Dimension(0, 10))); // pad
        nameEntryPanel.add(instruction); // add our elements
        nameEntryPanel.add(nameEntry);
        feedbackName.setAlignmentX(0); // center feedback
        nameEntryPanel.add(feedbackName);
        nameEntryPanel.add(Box.createRigidArea(new Dimension(0, 10))); // more padding
        // return the panel
        return nameEntryPanel;
    }

    /**
     * method to validate user name entry
     * @param nameEntry The JTextField used to enter name
     * @return true if invalid, false if valid
     */
    private boolean checkName(JTextField nameEntry) {
        feedbackName.setText(""); // empty feedback text
        String tempName = nameEntry.getText(); // set temp string to the text field string
        if (!tempName.contains(" ") || tempName.length() < 3) { // if it does not contain a space or is less than 3 char long
            feedbackName.setText("Please ensure you enter you full name in Firstname Lastname format"); // display feedback
            nameEntry.selectAll(); // ask for new input
            this.setName(null); // stop the user from putting in a good string, removing it, and then putting in a bad string and getting past validation
            return true; // failure!
        } else {
            this.setName(tempName); // otherwise we set the users name to this value
            feedbackName.setText(""); // remove feedback
            return false; // success!
        }
    }

    /**
     * Method to generate our email entry text field. Input is validated via checkEmail() and feedback text is
     * given to ensure the user knows what is expected of them.
     * @return JPanel with text entry, feedback text and some padding.
     */
    public JPanel userEmailPanel() {
        JLabel instruction = new JLabel("Enter your email address");
        JTextField emailEntry = new JTextField(4);
        feedbackEmail.setFont(new Font("", Font.ITALIC, 11));
        feedbackEmail.setForeground(Color.RED);
        emailEntry.getDocument().addDocumentListener(new DocumentListener() {
            @Override // as above, inverted from example
            public void insertUpdate(DocumentEvent e) {
                if(checkEmail(emailEntry)) emailEntry.requestFocusInWindow();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                if (checkEmail(emailEntry)) emailEntry.requestFocusInWindow();
            }
            @Override
            public void changedUpdate(DocumentEvent e) { /* pass */ }
        });
        JPanel emailEntryPanel = new JPanel(); // instantiate new panel
        emailEntryPanel.setLayout(new BoxLayout(emailEntryPanel, BoxLayout.Y_AXIS)); // stack vertically
        emailEntryPanel.setAlignmentX(0); // center it
        emailEntryPanel.add(Box.createRigidArea(new Dimension(0, 10))); // pad
        emailEntryPanel.add(instruction); // add our elements
        emailEntryPanel.add(emailEntry);
        feedbackEmail.setAlignmentX(0); // center feedback
        emailEntryPanel.add(feedbackEmail);
        emailEntryPanel.add(Box.createRigidArea(new Dimension(0, 10))); // more padding
        // return the panel
        return emailEntryPanel;
    }
    /**
     * method to validate user email entry
     * @param emailEntry The JTextField used to enter email
     * @return true if invalid, false if valid
     */
    private boolean checkEmail(JTextField emailEntry) {
        feedbackEmail.setText("");
        String tempEmail = emailEntry.getText();
        if (!tempEmail.contains("@")) { // if there's no '@'
            feedbackEmail.setText("Please ensure your email address contains an @.");
            emailEntry.selectAll(); // ask for better input
            this.setEmail(null); // stop the user from putting in a good string, removing it, and then putting in a bad string and getting past validation
            return true; // failure!
        } else if (tempEmail.contains("@") && !(tempEmail.contains("."))) {
            feedbackEmail.setText("Please ensure your email address is formatted properly"); // check for ' .com ' etc
            emailEntry.selectAll();
            this.setEmail(null); // stop the user from putting in a good string, removing it, and then putting in a bad string and getting past validation
            return true; // failure!
        } else { // otherwise
            feedbackEmail.setText(""); // remove feedback text
            this.setEmail(tempEmail); // set our new email
            return false; // success!
        }
    }

    /**
     * Method to generate a query field for the user to place a custom message in, doesn't require any validation
     * as the user may leave any message they like, even if it's a bit rude!
     * @return JPanel with instructional text, input field and a scroll bar if they type enough text
     */
    public JPanel userInputMessage() {
        JLabel instruction = new JLabel("If you want to leave more information, do so here");
        message = new JTextArea(6, 12); // creates a large text area, 6 rows, 12 columns
        // scroll through the message if need be
        JScrollPane jScrollPane = new JScrollPane(message);
        jScrollPane.getViewport().setPreferredSize(new Dimension(250, 100)); // 250x100 is the size of this scroll pane
        // create and return JPanel with padding, adding message, and text area / scroll pane
        JPanel userInputPanel = new JPanel();
        userInputPanel.setLayout(new BoxLayout(userInputPanel, BoxLayout.Y_AXIS)); // stack vertically
        userInputPanel.add(Box.createRigidArea(new Dimension(0, 10))); // padding
        userInputPanel.setAlignmentX(0); // center
        userInputPanel.add(instruction);
        jScrollPane.setAlignmentX(0);
        userInputPanel.add(jScrollPane);
        userInputPanel.add(Box.createRigidArea(new Dimension(0, 10))); // padding
        return userInputPanel;
    }

    /*------------GETTERS--------*/

    /**
     * @return String : name put down for order
     */
    public String getName() {
        return name;
    }

    /**
     * @return String : email put down for order
     */
    public String getEmail() {
        return email;
    }

    /**
     * @return String form obtained from the text of our JTextArea message field
     */
    public String getMessage() {
        return message.getText(); // returns the text from our message field, since it is a JTextArea field
    }
/*--------------------SETTERS-----*/
    /**
     * Setter for name field
     * @param newName : String to replace field with
     */
    public void setName(String newName) {
        this.name = newName;
    }

    /**
     * Setter for email field
     * @param newEmail : String to replace field with
     */
    public void setEmail(String newEmail) {
        this.email = newEmail;
    }
}
