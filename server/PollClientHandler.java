import java.net.*;
import java.io.*;
import java.util.concurrent.*;

/**
 * Handles an individual client of the Poll server
 */
public class PollClientHandler implements Runnable
{
  /** Socket for the connected client */
  private Socket clientSocket = null;
  /** Writer stream to send data to the client */
  private PrintWriter writer = null;
  /** Reader to get data from the client */
  private BufferedReader reader = null;

  /**
   * Creates a PollClientHandler object and sets up the IO streams for communication with the client
   * 
   * @param clientSocket - The socket of the client to communicate with
   */
  PollClientHandler(Socket clientSocket)
  {
    this.clientSocket = clientSocket;

    try
    {
      //Chain a writing stream to send data to send the server response to the client and enable auto flush
      writer = new PrintWriter(clientSocket.getOutputStream(), true);
      
      //Chain a buffered reading stream to get request from the client
      reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }
    catch (IOException e)
    {
      System.err.println("Failed to set up IO streams for client communication - " + e.getMessage());
    }
  }

  /**
   * Specifies the code that will be run by this handler thread. Gets the request from the client, handles the request
   * and then sends back the correct response.
   */
  @Override
  public void run()
  {
    try
    {
      PollProtocol protocol = new PollProtocol();
      String request = reader.readLine();

      //Log client request
      Server.writeToLog(clientSocket.getInetAddress().getHostAddress(), request);

      //Process input and get the correct response
      String response = protocol.processInput(request);

      //Write the response back to the client
      writer.println(response);

      //Close the IO streams and client socket
      writer.close();
      reader.close();
      clientSocket.close();
    }
    catch (IOException e)
    {
      System.err.println("ERROR: IO error when communicating with the client - " + e.getMessage());
    }
  }
}