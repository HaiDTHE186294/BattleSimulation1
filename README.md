# Battle Simulation

Một dự án mô phỏng chiến đấu được phát triển bằng Java, cho phép người chơi trải nghiệm các tình huống chiến đấu với nhiều tính năng phong phú.

## Tính năng chính

- **Hệ thống nhân vật **:
  - Quân nhân (Soldier)
  - Thường dân (Civilian)
  - Hệ thống trạng thái nhân vật (PersonStatus)

- **Trang bị và vũ khí**:
  - Hệ thống vũ khí (Weapon)
  - Giáp (Armor)
  - Bùa chú (Charm)


- **Hệ thống hiệu ứng**:
  - Hiệu ứng bỏng (BurnEffect)
  - Khả năng mở rộng với các hiệu ứng khác

- **Quản lý trận chiến**:
  - Hệ thống quản lý combat (CombatManager)
  - Đồng hồ game (GameClock)
  - Theo dõi trận đấu (GameObserver)

## Yêu cầu hệ thống

- Java Development Kit (JDK) 11 trở lên
- Gradle 7.0 trở lên

## Cài đặt

1. Clone repository:
```bash
git clone [repository-url]
```

2. Di chuyển vào thư mục dự án:
```bash
cd BattleSimulation1
```

3. Build dự án bằng Gradle:
```bash
./gradlew build
```

## Chạy ứng dụng

Để chạy ứng dụng, sử dụng lệnh:
```bash
./gradlew run
```

## Cấu trúc dự án

- `src/main/java/combat/`: Quản lý hệ thống chiến đấu
- `src/main/java/effect/`: Các hiệu ứng trong game
- `src/main/java/equipment/`: Hệ thống trang bị và vũ khí
- `src/main/java/person/`: Quản lý nhân vật
- `src/main/java/gamecore/`: Các thành phần cốt lõi của game
- `src/main/java/ui/`: Giao diện người dùng

## Đóng góp

Tạm thời không tiếp nhận đóng góp.

## Giấy phép

null
