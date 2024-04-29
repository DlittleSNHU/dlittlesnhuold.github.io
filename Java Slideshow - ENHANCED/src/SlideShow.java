/*

============================================================================
Name        : SlideShow.java
Author      : Daniel Little
Date        : 04/24/24
Version     : 3.0
Copyright   : Copyright © 2017 SNHU COCE
Description : 5 Random Activities - with API usage

Intent      : This is a Java project that is a SlideShow that presents
              possible tasks to accomplish on any given day. This is done
              by pulling random tasks from another database using an API
              and parsing them into the proper columns for a local database.
              There is a new function called "Random" that allows the user
              to flip to a random slide to select a random activity.

============================================================================

*/

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

import java.util.Random; 

public class SlideShow extends JFrame {

	//Declare Variables
	private JPanel slidePane;
	private JPanel textPane;
	private JPanel buttonPane;
	private CardLayout card;
	private CardLayout cardText;
	private JButton btnPrev;
	private JButton btnNext;
	private JButton btnRandom;
	private JLabel lblSlide;
	private JLabel lblTextArea;

	/**
	 * Create the application.
	 */
	public SlideShow() throws HeadlessException {
		initComponent();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initComponent() {
		//Initialize variables to empty objects
		card = new CardLayout();
		cardText = new CardLayout();
		slidePane = new JPanel();
		textPane = new JPanel();
		textPane.setBackground(Color.YELLOW);
		textPane.setBounds(5, 470, 790, 50);
		textPane.setVisible(true);
		buttonPane = new JPanel();
		btnPrev = new JButton();
		btnNext = new JButton();
		btnRandom = new JButton();
		lblSlide = new JLabel();
		lblTextArea = new JLabel();

		//Setup frame attributes
		setSize(800, 600);
		setLocationRelativeTo(null);
		setTitle("Top 5 Destinations SlideShow");
		getContentPane().setLayout(new BorderLayout(10, 50));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Setting the layouts for the panels
		slidePane.setLayout(card);
		textPane.setLayout(cardText);
		Connection dbConnection = sqlConnection();
		//logic to add each of the slides and text
		for (int i = 1; i <= 5; i++) {
			lblSlide = new JLabel();
			lblTextArea = new JLabel();
			lblSlide.setText(getResizeIcon(i));
			lblTextArea.setText(getTextDescription(dbConnection));
			slidePane.add(lblSlide, "card" + i);
			textPane.add(lblTextArea, "cardText" + i);
		}

		getContentPane().add(slidePane, BorderLayout.CENTER);
		getContentPane().add(textPane, BorderLayout.SOUTH);

		buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

		btnPrev.setText("Previous");
		btnPrev.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				goPrevious();
			}
		});
		buttonPane.add(btnPrev);

		btnNext.setText("Next");
		btnNext.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				goNext();
			}
		});
		buttonPane.add(btnNext);
		
		btnRandom.setText("Random");
		btnRandom.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				randomize();
			}
		});
		buttonPane.add(btnRandom);

		getContentPane().add(buttonPane, BorderLayout.SOUTH);
	}

	/**
	 * Previous Button Functionality
	 */
	private void goPrevious() {
		card.previous(slidePane);
		cardText.previous(textPane);
	}
	
	/**
	 * Next Button Functionality
	 */
	private void goNext() {
		card.next(slidePane);
		cardText.next(textPane);
	}
	
    private void randomize() {
        Random random = new Random();
        int randomNum = random.nextInt(100)+1;
        System.out.println(randomNum);
        // For loop determines 
        for (int i = 0; i < randomNum; i++) {
            card.next(slidePane);
            cardText.next(textPane);
        }
    }

	/**
	 * Method to get the images
	 */
	private String getResizeIcon(int i) {
		String image = ""; 
		image = "<html><body><img width= '800' height='500' src='" + getClass().getResource("/resources/slide" + i + ".png") + "'</body></html>";
		return image;
	}
	
	   // Function to extract a value from JSON by key
    public static String extractValueFromJson(String jsonResponse, String key) {
        // Manually parse the JSON response to extract the value associated with the given key
        // This is a simplified approach; for complex JSON structures, consider using a dedicated JSON library

        // Find the key in the JSON response
        String keyPattern = "\"" + key + "\":";
        int keyIndex = jsonResponse.indexOf(keyPattern);

        if (keyIndex == -1) {
            // Key not found
            return null;
        }

        // Find the start index of the value (after the key and colon)
        int valueStartIndex = keyIndex + keyPattern.length();

        // Determine the type of value (number or string) and find the end index of the value
        char startChar = jsonResponse.charAt(valueStartIndex);
        int valueEndIndex;

        if (startChar == '"') {
            // The value is a string, find the closing quote
            valueStartIndex++;
            valueEndIndex = jsonResponse.indexOf('"', valueStartIndex);
        } else {
            // The value is a number or boolean, find the next comma or closing brace
            valueEndIndex = jsonResponse.indexOf(',', valueStartIndex);
            if (valueEndIndex == -1) {
                valueEndIndex = jsonResponse.indexOf('}', valueStartIndex);
            }
        }

        // Extract and return the value as a string
        return jsonResponse.substring(valueStartIndex, valueEndIndex);
    }
    
    private Connection sqlConnection() {
    	 Connection connection = null;
         try {
             // Load the SQLite JDBC driver
             Class.forName("org.sqlite.JDBC");
             // Connect to the SQLite database
             String url = "jdbc:sqlite:db/database.sqlite";
             connection = DriverManager.getConnection(url);

             System.out.println("Connected to the SQLite database.");

             // Perform database operations...
             createTable(connection);

         } catch (ClassNotFoundException e) {
             System.err.println("SQLite JDBC driver not found.");
             e.printStackTrace();
         } catch (SQLException e) {
             System.err.println("Error connecting to the database.");
             e.printStackTrace();
         } finally {
        	 System.out.println("No closing");
             // Close the connection
//             if (connection != null) {
//                 try {
//                	
//                     //connection.close();
//                 } catch (SQLException e) {
//                     e.printStackTrace();
//                 }
             //}
         }
		return connection;
    }
    
    private static void addRow(Connection connection, String activity, String type) throws SQLException {
        String sql = "INSERT INTO activities (activity, type) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, activity);
            statement.setString(2, type);
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new row has been inserted.");
            } else {
                System.out.println("Failed to insert a new row.");
            }
        }
    }
    
    // Method to create a sample table
    private static void createTable(Connection connection) throws SQLException {
        Statement statement = null;
        try {
            statement = connection.createStatement();

            // Execute an SQL statement to create a sample table
            String sql = "CREATE TABLE IF NOT EXISTS activities (" +
                    "id INTEGER PRIMARY KEY," +
                    "activity TEXT NOT NULL," +
                    "type TEXT NOT NULL" +
                    ")";
            statement.executeUpdate(sql);

            System.out.println("Table 'activities' created successfully.");

        } finally {
            // Close the statement
            if (statement != null) {
                statement.close();
            }
        }
    }
    
	
	/**
	 * Method to get the text values
	 * @throws SQLException 
	 */
	private String getTextDescription(Connection dbConnection) {
		String apiUrl = "https://www.boredapi.com/api/activity";

        try {
            
            // Create a URL object
            URL url = new URL(apiUrl);

            // Create a HttpURLConnection object
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set request method
            connection.setRequestMethod("GET");

            // Get the response code
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Reading response from input Stream
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Parse and store hotel information
                String result = response.toString();
                String activity = "Activity = " + extractValueFromJson(result,"activity");
                String type = "Type = " + extractValueFromJson(result,"type");
                try {
					addRow(dbConnection, activity, type);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                return activity + "    |   " + type;
                // Here you can parse the JSON or XML response and store it into your preferred data structure (e.g., ArrayList, HashMap, etc.)
                // For simplicity, let's print the hotel information
                //System.out.println("Hotel Information: " + hotelInfo);
            } else {
                return "" + responseCode;
            }
            // Close connection
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Close the statement

        }

        return " ";
        
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				SlideShow ss = new SlideShow();
				ss.setVisible(true);
			}
		});
	}
}