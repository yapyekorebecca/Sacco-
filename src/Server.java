import java.io.*;
import java.net.*;
import java.sql.*;
import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.*;




public class Server {

    
    private static String OTP = null;


   

    


    public static void main(String[] args) {
        ServerSocket serverSoc =null;
        Socket ss = null;
        Scanner fromclient = null;
        PrintWriter pr = null;
        String message = null;
        String command =null;
        String[] chop  = null;
        String SecureMenu = null;
       
        
        

        //db connect
        try {
            JDBC.getInstance().connect();
            //
        } catch (Exception e) {
           
            System.out.println(e.getMessage());
        } 
      



        try {
            serverSoc = new ServerSocket(5656);
            System.out.println("Server running ...");
            ss = serverSoc.accept();
            System.out.println("         New Client connection        ");
            pr = new PrintWriter(ss.getOutputStream(),true);
            
            fromclient = new Scanner(ss.getInputStream());
            
            
             
            message ="Welcome to Uprise Sacco program please login";
            pr.println(message);

            

            SecureMenu = "1. Deposit amount datedeposited receiptNumber\n" +
             "2. CheckStatement dateFrom dateTo\n" +
             "3. requestLoan amount paymentPeriodinMonths\n" +
             "4. LoanRequestStatus LoanApplicationNumber";

             
            while (true) {
                command = fromclient.nextLine();
                chop = command.split(" ");



               
            
                if (command.isEmpty()) {
                    pr.println("Please specify what you want");
                    
                
                }else if (command.startsWith("login")&& chop.length ==3 ) {
                    
                    
                    String username = chop[1];
                    String password = chop[2];
        
                    if (isValidCredentials(username, password)) {
                        pr.println("You have successfully logged in. Here is the secured menu:");
                        
                       String[] menuOptions = SecureMenu.split("\n");
                       for (String option : menuOptions) {
                           pr.println(option);
                        
                        }  pr.println("END_MENU");
                    }else {
                        pr.println("Invalid credentials");
                        pr.println("Please supply your Registered PhoneNumber And MemberNumber");

                       
                        String memberNumberInput = fromclient.next();
                        int phoneNumberInput = fromclient.nextInt();

                        //  Call the sendPassword method and capture the result
                        String gh = sendPassword(memberNumberInput, phoneNumberInput);

                        if (gh != null) {
                            pr.println("Your password is " + gh);
                        } else {
                            pr.println("Password not found for the provided user. plaese for the meantime this is your reference number : " + ReferenceNumber(memberNumberInput, phoneNumberInput)  );  
                        }
                    

                       

                    }
                        
                        
                     
                } else if (command.startsWith("deposit")&& chop.length ==5) {
                    
                    String username = chop[1];
                    String amount = chop[2];
                    String dateDeposited = chop[3];
                    String receiptNumber = chop[4];

        
                   boolean isDepositSuccesful = deposit(username, amount, dateDeposited, receiptNumber);
                    if (isDepositSuccesful) {
                        pr.println("Deposit made successfully");
                    }
                    
                    
                   
                }else{
                    pr.println("Either Command unknown or incomplete ");
                }
            }

                    
               
            
            
        }catch (Exception e) {

            System.out.println(e.getMessage());
        }


                
    }


    private static boolean isValidCredentials(String username, String password) {
        // dbUrl = "jdbc:mysql://localhost:3306/Sacco";
        // dbUsername="root";
        // dbPassword="Password1234";
        
       


        try {
           // Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);

            JDBC jdbcInstance = JDBC.getInstance();
            Connection connection = jdbcInstance.getConnection();
   
            

            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();

            boolean isValid = resultSet.next();

            resultSet.close();
            statement.close();
            connection.close();

            return isValid;
        } catch (SQLException e) {
           // e.printStackTrace();
           System.out.println(e.getMessage());
            return false;
        }
    }
    
    private static boolean deposit(String username ,String amount, String dateDeposited, String receiptNumber) {
        // dbUrl = "jdbc:mysql://localhost:3306/Sacco";
        // dbUsername="root";
        // dbPassword="Password1234";
        
        
       
        try {
            // Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);

            JDBC jdbcInstance = JDBC.getInstance();
            Connection connection = jdbcInstance.getConnection();
   

            String sql = "INSERT INTO deposits (userId, amount, dateDeposited, receiptNumber) VALUES (?, ?, ?, ?)";
            PreparedStatement insertStatement = connection.prepareStatement(sql);
            insertStatement.setInt(1, getUserIdByUsername(username));
            insertStatement.setDouble(2, Double.parseDouble(amount));
            insertStatement.setDate(3, Date.valueOf(dateDeposited));
            insertStatement.setString(4, receiptNumber);
            insertStatement.executeUpdate();



            System.out.println("New Deposit has been made!");

            return true;
            

            
             
            
            
            
            
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println(e.getMessage());
            return false;
        }
        
        
    }



    private static String sendPassword(String MemberNumber,int phoneNumber) {

        // dbUrl = "jdbc:mysql://localhost:3306/Sacco";
        // dbUsername="root";
        // dbPassword="Password1234";
        
        try {
            //Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            JDBC jdbcInstance = JDBC.getInstance();
            Connection connection = jdbcInstance.getConnection();
           
            String Sql = "SELECT password FROM users WHERE MemberNumber = ? And phoneNumber =?";
            PreparedStatement selectStatement = connection.prepareStatement(Sql);
            
            selectStatement.setString(1,MemberNumber);
            selectStatement.setInt(2,phoneNumber);
            

            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                OTP = resultSet.getString("password");
            }
            
            resultSet.close();
            selectStatement.close();
            connection.close();
            //return OTP;

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        

       return OTP;
        
        
    }

    private static int ReferenceNumber(String MemberNumber, int PhoneNumber) {
        // String dbUrl = "jdbc:mysql://localhost:3306/Sacco";
        // String dbUsername = "root";
        // String dbPassword = "Password1234";
        String DateofRequest = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss"));
        int referenceNumber = 0; // Initialize the referenceNumber variable to store the generated value.
    
        try {
            //Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);

            JDBC jdbcInstance = JDBC.getInstance();
            Connection connection = jdbcInstance.getConnection();
    
            // Use prepared statement with placeholders to insert the values
            String insertSql = "INSERT INTO issues (MemberNumber, PhoneNumber, DateofRequest) VALUES (?, ?, ?)";
            PreparedStatement insertStatement = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            insertStatement.setString(1, MemberNumber);
            insertStatement.setInt(2, PhoneNumber);
            insertStatement.setString(3, DateofRequest);
            insertStatement.executeUpdate();
    
            // Retrieve the generated ReferenceNumber
            ResultSet generatedKeys = insertStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                referenceNumber = generatedKeys.getInt(1); // Assuming the ReferenceNumber is an INT column.
            }
    
            // Close resources
            generatedKeys.close();
            insertStatement.close();
            connection.close();
    
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    
        return referenceNumber;
    }
    



    private static int getUserIdByUsername(String username) {       
        // dbUrl = "jdbc:mysql://localhost:3306/Sacco";
        // dbUsername="root";
        // dbPassword="Password1234";
        
        
       
        int userId = -5;  // this is to show that by default the user is not found (thats why we give it a negative)

        try {
            //Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);

            JDBC jdbcInstance = JDBC.getInstance();
            Connection connection = jdbcInstance.getConnection();
           
            String selectSql = "SELECT ID FROM users WHERE username = ?";
            PreparedStatement selectStatement = connection.prepareStatement(selectSql);
            selectStatement.setString(1, username);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                userId = resultSet.getInt("ID");
            }

            resultSet.close();
            selectStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userId;
    }
}
