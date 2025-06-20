package dialogue.model;

public class ActionDef {
    private String type;
    private String battleId; // Có thể thêm trường khác nếu muốn

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getBattleId() { return battleId; }
    public void setBattleId(String battleId) { this.battleId = battleId; }
}