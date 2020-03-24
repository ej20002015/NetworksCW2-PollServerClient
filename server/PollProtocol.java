public class PollProtocol
{
  public String processInput(String input)
  {
    //If client wants to see the poll then return this
    if (input.equals("show"))
      return Server.getPoll().toString();
    
    //attempt to increment the option specified by the client
    try
    {
      Server.getPoll().incrementOption(input);
      return input + " now has " + Server.getPoll().getOptionVotes(input) + " vote(s)";
    }
    catch (Poll.UnknownOptionException e)
    {
      return e.toString();
    }
  }  
}