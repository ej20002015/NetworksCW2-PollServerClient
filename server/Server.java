import java.net.*;
import java.text.SimpleDateFormat;
import java.io.*;
import java.util.concurrent.*;
import java.util.Date;

public class Server
{
  private static Poll poll = new Poll();
  private ServerSocket serverSocket = null;
  private ExecutorService executor = null;
  //Open log file for writing
  private static PrintWriter logWriter = null;
  private static SimpleDateFormat timeFormatter = new SimpleDateFormat("dd-MM-YYYY : HH:mm:ss");

  final private int LISTENING_PORT = 7777;
  final private int THREAD_NUMBER = 20;

  public static Poll getPoll() { return poll; }

  public Server(String[] options)
  {
    //Set up the poll
    poll.setPoll(options);

    //Attempt to create a socket listening at a fixed port
    try
    {
      serverSocket = new ServerSocket(LISTENING_PORT);
    }
    catch (IOException e)
    {
      error("Could not create a server listening on port " + LISTENING_PORT + " - " + e.getMessage());
    }

    //Create the thread executor with a thread pool of the required number of threads
    executor = Executors.newFixedThreadPool(THREAD_NUMBER);
  }

  public void run()
  { 
    //Infinite loop until the user issues a keyboard interrupt and then close the socket and log file

    //Hook that runs when a keyboard interrupt is encountered and closes all the sockets and log files
    Runtime.getRuntime().addShutdownHook(new Thread() 
    {
      @Override
      public void run() 
      {
        try
        {
          serverSocket.close();
          logWriter.close();
          executor.shutdown();
        }
        catch (IOException e)
        {
          error("IO error occurred when attempting to close the server socket and log file");
        }
      }
    });

    while(true)
    {
      //For each client that connects, assign them to a handling thread and get the executor service to run it
      try
      {
        Socket clientSocket = serverSocket.accept();
        executor.submit(new PollClientHandler(clientSocket));
      }
      catch (IOException e)
      {
        System.err.println("IO error occurred when attempting to accept a client - " + e.getMessage());
      }
    }
  }

  public synchronized static void writeToLog(String clientIP, String request)
  {
    //get current date and time
    Date date = new Date();

    //Format the request correctly
    if (!request.equals("show"))
      request = new String("vote ").concat(request);

    logWriter.println(timeFormatter.format(date) + " : " + clientIP + " : " + request);
    //logWriter.flush();
  }

  private static void openLogFile(String fileName)
  {
    try
    {
      logWriter = new PrintWriter(fileName);
    }
    catch (IOException e)
    {
      error("Failed to open the log file " + fileName);
    }
  }

	public static void main(String[] args)
	{
    String usage = "USAGE: 'java Server <option> <option> ...'";

    if (args.length < 2)
      error("Need to provide at least two options to vote for in the poll - " + usage);
    
    Server.openLogFile("log.txt");
    Server pollServer = new Server(args);
    pollServer.run();
  }
  
  public static void error(String message)
  {
    System.err.println("ERROR: " + message);
    System.exit(1);
  }
}