package dev.silvadev.blockode.model.logger;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class LogViewModel extends ViewModel {
  private final MutableLiveData<List<Log>> logs = new MutableLiveData<>(new ArrayList<>());
  
  public LiveData<List<Log>> getLogs() {
    return logs;
  }
  
  public void add(String level, CharSequence message) {
    List<Log> currentLogs = logs.getValue();
    if(currentLogs != null) {
      List<Log> updatedLogs = new ArrayList<>(currentLogs);
      updatedLogs.add(new Log(level, message));
      logs.setValue(updatedLogs);
    }
  }
  
  public void clear() {
  	if(logs != null) {
  		logs.getValue().clear();
  	}
  }
}
