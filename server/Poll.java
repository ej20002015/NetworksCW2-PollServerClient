import java.util.HashMap;
import java.util.Map;

public class Poll
{
  public class UnknownOptionException extends RuntimeException
  {
    private String unknownOption;

    public UnknownOptionException(String unknownOption)
    {
      this.unknownOption = unknownOption;
    }

    public String toString()
    {
      return "ERROR: The choice " + unknownOption + " is not an option in the poll";
    }
  }

  private Map<String, Integer> pollMap = new HashMap<>();
  
  public void setPoll(String[] options)
  {
    for (String option : options)
      pollMap.put(option, 0);
  }

  public synchronized void incrementOption(String option)
  {
    //Increment total votes for the option passed in if valid. Otherwise throw an exception
    if (pollMap.containsKey(option))
      pollMap.put(option, pollMap.get(option) + 1);
    else
      throw new UnknownOptionException(option);
  }

  public int getOptionVotes(String option)
  {
    if (pollMap.containsKey(option))
      return pollMap.get(option);
    else
      throw new UnknownOptionException(option);
  }

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