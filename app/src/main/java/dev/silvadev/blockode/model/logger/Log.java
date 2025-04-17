package dev.silvadev.blockode.model.logger;

public class Log {
    
  private String level;
  private CharSequence message;
    
  public Log(String mLevel, CharSequence mMessage) {
    level = mLevel;
    message = mMessage;
  }
  
  public String getLevel() {
  	return level;
  }
  
  public CharSequence getMessage() {
    return message;
  }
    
}
