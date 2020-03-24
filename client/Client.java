import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Represents a client of the Poll network application
 */
public class Client
{
  /** Socket used to connect to the server */
  private Socket socket = null;
  /** Writer stream to send data to the server */
  private PrintWriter writer = null;
  /** Reader to get data from the server */
  private BufferedReader reader = null;

  /** Specifies the port of the server */
  private final int SERVER_PORT = 7777;

  /**
   * Creates a Client object and instantiates the Socket and IO streams
   */
  public Client()
  {
    try
    {
      //Create the socket that connects to the server
      socket = new Socket("localhost", SERVER_PORT);

      //Chain a writing stream to send data to the server and enable auto flush
      writer = new PrintWriter(socket.getOutputStream(), true);

      //Chain a buffered reading stream to get data from the server
      reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
    catch (UnknownHostException e)
    {
      error("Unknown host - " + e.getMessage());
    }
    catch (IOException e)
    {
      error("Failed to set up IO streams for server communication - " + e.getMessage());
    }
  }

  /**
   * Sends the appropriate request (whether its to vote or show) to the server and prints the response
   * 
   * @param choice - the type of request to make, either choice = "show" or choice contains the poll option to vote for
   */
  public void poll(String choice)
  { 
    //Send request to the server and write response to the client
    try
    {
      //send request to the server - either the client wants to be shown the current vote breakdown, or they
      //are voting for an option
      writer.println(choice);

      //Wait for server response and collect all the lines given
      String serverResponse = reader.lines().collect(Collectors.joining("\n"));
      
      //Echo out the server response to the client
      System.out.println(serverResponse);
      
      //Close the IO streams and socket to server
      socket.close();
      writer.close();
      reader.close();
    }
    catch (IOException e)
    {
      error("IO error occurred when communicating with the server - " + e.getMessage());
    }
  }

  /**
   * Does validation checking of the command line arguments, then creates a Client object and then
   * runs poll.
   * 
   * @param args - Array of command line arguments
   */
	public static void main(String[] args)
	{
    final String usage = "USAGE: 'java Client show' or 'java Client vote <option>'";
    if (args.length < 1 || args.length > 2)
      error("An incorrect number of arguments were sent - " + usage);

    if (!(args[0].equals("show") || args[0].equals("vote")))
      error("Unexpected first argument provided (expecting 'show' or 'vote') - " + usage);
      
    Client pollClient = new Client();
    if (args[0].equals("show"))
      pollClient.poll("show");
    else
    {
      if (args.length != 2)
        error("No option provided when voting - " + usage);

      //Send in option to vote for
      pollClient.poll(args[1]);
    }
  }
  
  /**
   * Prints an error message to the error stream and closes the program
   * 
   * @param message the error message to print
   */
  private static void error(String message)
  {
    System.err.println("ERROR: " + message);
    System.exit(1);
  }
}