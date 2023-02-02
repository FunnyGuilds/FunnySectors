package pl.rosehc.adapter.database;

/**
 * @author stevimeister on 22/12/2021
 **/
public abstract class DatabaseUpdatableEntry {

  private boolean whetherUpdate;

  public boolean isWhetherUpdate() {
    return this.whetherUpdate;
  }

  public void markToUpdate() {
    this.whetherUpdate = true;
  }

  public void markUpdated() {
    this.whetherUpdate = false;
  }
}
