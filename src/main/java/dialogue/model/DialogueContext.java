package dialogue.model;

import gamecore.DataContext;

public class DialogueContext {
    private final DataContext dataContext;

    public DialogueContext() {
        this.dataContext = new DataContext();
    }

    public Object getData(String key) {
        return dataContext.getData(key);
    }

    public void setData(String key, Object value) {
        dataContext.setData(key, value);
    }

    public boolean hasData(String key) {
        return dataContext.hasData(key);
    }

    public void removeData(String key) {
        dataContext.removeData(key);
    }

    public void clear() {
        dataContext.clear();
    }
}