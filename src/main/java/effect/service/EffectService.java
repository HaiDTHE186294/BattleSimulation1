package effect.service;

import effect.model.Effect;
import person.model.Person;
import person.model.Soldier;

import java.util.List;

public class EffectService {

    // Áp dụng hiệu ứng vào một Soldier
    public void applyEffect(Soldier target, Effect effect) {
        target.getActiveEffects().add(effect);
        effect.apply(target);
        effect.onTurnStart(target);
        effect.reduceDuration(); // Giảm thời gian hiệu ứng ngay khi áp dụng
    }

    // Cập nhật các hiệu ứng theo lượt
    public void updateEffects(Soldier target) {
        List<Effect> effects = target.getActiveEffects();
        effects.removeIf(effect -> {
            effect.reduceDuration();
            if (effect.isExpired()) {
                effect.onExpire(target);
                System.out.println("Hiệu ứng " + effect.getName() + " đã hết hạn trên " + target.getName());
                return true; // remove
            }
            return false;
        });
    }

    // Gỡ thủ công một hiệu ứng nào đó
    public void removeEffect(Soldier target, Effect effect) {
        if (target.getActiveEffects().remove(effect)) {
            effect.onExpire(target);
        }
    }

    // Gỡ toàn bộ hiệu ứng (ví dụ khi chết hoặc reset)
    public void clearEffects(Soldier target) {
        for (Effect e : target.getActiveEffects()) {
            e.onExpire(target);
        }
        target.getActiveEffects().clear();
    }

    // Kích hoạt hiệu ứng khi bắt đầu lượt
    public void activateEffects(Soldier target) {
        if (target == null || !target.isAlive()) return;

        for (Effect effect : target.getActiveEffects()) {
            if (!effect.isExpired()) {
                effect.onTurnStart(target);
            }
        }
    }
}
