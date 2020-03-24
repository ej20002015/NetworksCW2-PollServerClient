import java.net.*;
import java.io.*;
import java.util.concurrent.*;

public class PollClientHandler implements Runnable
{
  private Socket clientSocket = null;
  private PrintWriter writer = null;
  private BufferedReader reader = null;

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

  @Override
  public void run()
  {
    try
    {
      PollProtocol protocol = new PollProtocol();
      String request = reader.readLine();

      //Log user request
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