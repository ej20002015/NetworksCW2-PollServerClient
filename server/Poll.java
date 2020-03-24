import java.util.HashMap;
import java.util.Map;

/**
 * Represents a poll that has several options and can be voted on
 */
public class Poll
{

  /**
   * Custom exception that is thrown when the option specified is not an option in the poll
   */
  public class UnknownOptionException extends RuntimeException
  {
    /**
     * The option that was no part of the poll
     */
    private String unknownOption;

    /**
     * Creates an exception
     * 
     * @param unknownOption - The option that was unknown
     */
    public UnknownOptionException(String unknownOption)
    {
      this.unknownOption = unknownOption;
    }

    /**
     * Specifies the error message of the exception
     */
    public String toString()
    {
      return "ERROR: The choice " + unknownOption + " is not an option in the poll";
    }
  }

  /**
   * Hash map that stores each option and their corresponding number of votes
   */
  private Map<String, Integer> pollMap = new HashMap<>();
  
  /**
   * Adds the specified options to the poll and sets their number of votes to 0
   * 
   * @param options - An array of strings that specifies the options the Poll should have
   */
  public void setPoll(String[] options)
  {
    for (String option : options)
      pollMap.put(option, 0);
  }

  /**
   * Increments the vote for the specified option by 1 (thread safe)
   * 
   * @param option - The option in the poll to increment
   * @throws UnknownOptionException - Throws exception if the supplied option is not an option in the pole
   */
  public synchronized void incrementOption(String option)
  {
    //Increment total votes for the option passed in if valid. Otherwise throw an exception
    if (pollMap.containsKey(option))
      pollMap.put(option, pollMap.get(option) + 1);
    else
      throw new UnknownOptionException(option);
  }

  /**
   * Gets the current number of votes for the specified option
   * 
   * @param option - The option to get the number of votes for
   * @return Number of votes for the option specified
   */
  public int getOptionVotes(String option)
  {
    if (pollMap.containsKey(option))
      return pollMap.get(option);
    else
      throw new UnknownOptionException(option);
  }

  /**
   * Returns a string of all the options and their corresponding number of votes
   * 
   * @return String that represents the current state of the poll
   */
  public String toString()
  {
    StringBuilder pollString = new StringBuilder();
    for (String key : pollMap.keySet())
    {
      pollString.append(key);
      pollString.append(" has ");
      pollString.append(pollMap.get(key));
      pollString.append(" vote(s)\n");
    }

    return pollString.toString();
  }
}