package ui;
import person.model.Soldier;
import java.util.List;

public interface TeamSelectionListener {
    void onTeamSelected(List<Soldier> selectedTeam);
}