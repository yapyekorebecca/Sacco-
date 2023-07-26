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
        String userInput =null;
        String[] command  = null;
        String SecureMenu = null;
        int PhoneNumber =0;
        String MemberNumber = null;
       
        
        

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

             
            while ((userInput = fromclient.nextLine()) != null) {
                 command = userInput.split(" ");
                if (command.length > 1 && command.length < 5) {
                    System.out.println(userInput);
                    switch (command[0]) {
                        case "login":
                            if (isValidCredentials(command[1], command[2])) {
                                pr.println("You have successfully logged in. Here is the secured menu:");

                                String[] menuOptions = SecureMenu.split("\n");
                                for(String option :  menuOptions){
                                    pr.println(option);
                                }pr.println("END_MENU");
                                
                                
                            } else {
                                pr.println("Authentication failed");
                            }
                            break;
                        case "forgotPassword":
                            if (validateMemberInformation(command[1], command[2])
                                    .equals("No record found. Return after a day")) {
                                pr.println("No record found. Return after a day. Your reference number is : " + ReferenceNumber(MemberNumber,PhoneNumber));
                                        
                            } // else if(GenerateReferenceNumber())//
                            else if (validateMemberInformation(command[1], command[2]) == null) {
                                break;
                            } else {
                                pr.println(validateMemberInformation(command[1], command[2]));
                            }
                            break;
                        case "Store Reference Number":

                        case "deposit":
                            // Handle deposit command
                            break;
                        case "requestLoan":
                            // Handle requestLoan command
                            break;
                        case "checkLoanStatus":
                            // Handle checkLoanStatus command
                            break;
                        case "CheckStatement":
                            // Handle CheckStatement command
                            break;
                        default:
                            pr.println("Invalid command");
                    }

                } else {
                    pr.println("Unknown command please follow the menu ");
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
       
        try {
           
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

    private static int ReferenceNumber(String MemberNumber, int phoneNumber) {
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
            insertStatement.setInt(2, phoneNumber);
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



private static String validateMemberInformation(String memberno, String phonenumber) {

    String user_password = null;
        try {
            
            JDBC jdbcInstance = JDBC.getInstance();
            Connection connection = jdbcInstance.getConnection();
            try {
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery(
                        "SELECT * FROM users WHERE MemberNumber = '" + memberno + "' AND phoneNumber = '" + phonenumber
                                + "'");
                if (result.next()) {
                     user_password = result.getString("passWord");
                   // return user_password;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                connection.close();
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            return "No record found. Return after a day";
        }
        return user_password;
    }











}




 
