import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Client
{
  private Socket socket = null;
  private PrintWriter writer = null;
  private BufferedReader reader = null;

  private final int SERVER_PORT = 7777;

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

  public void poll(String choice)
  { 
    //Send request to the server and write response to the user
    try
    {
      //send request to the server - either the user wants to be shown the current vote breakdown, or they
      //are voting for an option
      writer.println(choice);

      //Wait for server response and collect all the lines given
      String serverResponse = reader.lines().collect(Collectors.joining("\n"));
      
      //Echo out the server response to the user
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
  
  private static void error(String message)
  {
    System.err.println("ERROR: " + message);
    System.exit(1);
  }
}