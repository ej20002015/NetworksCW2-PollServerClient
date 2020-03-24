import java.net.*;
import java.text.SimpleDateFormat;
import java.io.*;
import java.util.concurrent.*;
import java.util.Date;

/**
 * Represents a server that runs a poll
 */
public class Server
{
  /** Poll object that represents the poll being voted on */
  private static Poll poll = new Poll();
  /** Socket the server listens to to get new accept new clients */
  private ServerSocket serverSocket = null;
  /** Executor Service that maintains a pool of threads and handles submitted jobs */
  private ExecutorService executor = null;
  /** Output stream to write to the log file */
  private static PrintWriter logWriter = null;
  /** A time formatter to format the timestamps written to the log file */
  private static SimpleDateFormat timeFormatter = new SimpleDateFormat("dd-MM-YYYY : HH:mm:ss");

  /** Specifies the port the server will listen to and accept new clients */
  final private int LISTENING_PORT = 7777;
  /** Number of threads that should be in the pool */
  final private int THREAD_NUMBER = 20;

  /**
   * Gets the poll of the server
   * 
   * @return Poll object
   */
  public static Poll getPoll() { return poll; }

  /**
   * Creates a Server object. Creates the server socket on the specified port, initiates the poll
   * and then creates a new thread pool and executor
   * 
   * @param options - A string array containing the different options that are going to be in the poll
   */  
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

  /**
   * Runs the server. Listens to the server socket and accepts clients. Each client is then executed on
   * a thread that is allocated by the executor service. When the server is terminated a shutdown hook ensures
   * all sockets and IO streams are closed appropriately. 
   */
  public void run()
  { 
    //Infinite loop until the client issues a keyboard interrupt and then close the socket and log file

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
        //Only show an error message if it didn't occur because of the server socket being closed by the shutdown hook
        if (!serverSocket.isClosed())
          System.err.println("IO error occurred when attempting to accept a client - " + e.getMessage());
      }
    }
  }

  /**
   * Write a new entry to the log (thread safe)
   * 
   * @param clientIP - The IP address of the client the server is communicating with
   * @param request - The request the client has made
   */
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

  /**
   * Opens the log file for writing
   * 
   * @param fileName - Specifies the fileName of the log file to write to
   */
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

  /**
   * Does validation of the command line arguments, opens the log file, creates a new Server and
   * then runs the server
   * 
   * @param args - Array of command lin arguments
   */
	public static void main(String[] args)
	{
    String usage = "USAGE: 'java Server <option> <option> ...'";

    if (args.length < 2)
      error("Need to provide at least two options to vote for in the poll - " + usage);
    
    Server.openLogFile("log.txt");
    Server pollServer = new Server(args);
    pollServer.run();
  }
  
  /**
   * Prints an error message to the error stream and closes the program
   * 
   * @param message the error message to print
   */
  public static void error(String message)
  {
    System.err.println("ERROR: " + message);
    System.exit(1);
  }
}