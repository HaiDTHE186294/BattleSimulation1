package effect.service;

import effect.model.Effect;
import person.model.Soldier;

import java.util.ArrayList;
import java.util.List;

public class EffectService {

    // Áp dụng hiệu ứng vào một Soldier
    public void applyEffect(Soldier target, Effect effect) {
        if (target == null || effect == null) return;
        effect.apply(target);
    }

    // Cập nhật các hiệu ứng theo lượt
    public void updateEffects(Soldier target) {
        if (target == null) return;
        List<Effect> effects = target.getActiveEffects();
        if (effects == null) return;

        // Thu thập các effect hết hạn
        List<Effect> expired = new ArrayList<>();
        for (Effect effect : new ArrayList<>(effects)) {
            if (effect == null) {
                expired.add(effect);
                continue;
            }
            effect.reduceDuration();
            if (effect.isExpired()) {
                expired.add(effect);
            }
        }
        // Xóa ngoài vòng lặp và gọi onExpire an toàn
        for (Effect effect : expired) {
            effects.remove(effect);
            if (effect != null) {
                effect.onExpire(target);
                System.out.println("Effect " + effect.getName() + " was end on " + target.getName());
            }
        }
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