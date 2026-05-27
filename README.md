# Secure Vault — Android Password Manager

## Технологии
- **Язык:** Kotlin
- **UI:** Jetpack Compose (Material 3)
- **База данных:** SQLite через Room ORM
- **Шифрование:** AES-256-GCM через Android Keystore
- **Хеширование пароля:** Argon2id (OWASP параметры: 64MB, 3 итерации)
- **DI:** Hilt
- **Биометрия:** Android BiometricPrompt API

## Архитектура безопасности

1. **Мастер-пароль** хешируется Argon2id — никогда не хранится открыто
2. **Ключ AES-256** генерируется и хранится в Android Keystore (аппаратный SecurityElement / StrongBox)
3. **Каждое поле** (логин, пароль, URL, заметки) шифруется AES-256-GCM с уникальным IV
4. **Сессия** хранится только в памяти — закрыли приложение → нужен повторный вход
5. **EncryptedSharedPreferences** для хранения хеша мастер-пароля

## Структура проекта
```
app/src/main/java/com/securevault/
├── security/
│   ├── CryptoManager.kt      — AES-256-GCM + Android Keystore
│   ├── Argon2Helper.kt       — Argon2id хеширование/верификация
│   ├── BiometricHelper.kt    — Биометрическая аутентификация
│   └── SessionManager.kt     — Управление сессией и хешем пароля
├── data/
│   ├── model/VaultEntry.kt   — Модель записи (зашифрованные поля)
│   ├── db/
│   │   ├── VaultDao.kt       — SQLite запросы (Room)
│   │   └── VaultDatabase.kt  — База данных
│   └── repository/VaultRepository.kt — Шифрование/дешифрование при CRUD
├── ui/
│   ├── screens/
│   │   ├── LockScreen.kt     — Экран входа/создания мастер-пароля
│   │   ├── VaultListScreen.kt — Список паролей с поиском
│   │   └── EntryEditScreen.kt — Добавление/редактирование + генератор паролей
│   ├── theme/Theme.kt         — Тёмная тема Material 3
│   └── SecureVaultNavHost.kt  — Навигация
├── di/AppModule.kt            — Hilt DI конфигурация
├── SecureVaultApp.kt          — Application класс
└── MainActivity.kt            — Точка входа
```

## Возможности приложения
- Создание мастер-пароля при первом запуске
- Вход по паролю или биометрии (отпечаток пальца)
- Добавление/редактирование/удаление записей
- Поля: название, категория, логин, пароль, URL, заметки
- Поиск по названию
- Избранные записи (звёздочка)
- Встроенный генератор паролей (длина, регистр, цифры, символы)
- Копирование логина/пароля в буфер одной кнопкой
- Блокировка кнопкой (иконка замка в углу)
