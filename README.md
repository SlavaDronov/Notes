# 📝 Notes

> Современное Android-приложение для создания и управления заметками с поддержкой текста и изображений.

<p align="center">

![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?style=for-the-badge\&logo=kotlin\&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-UI-4285F4?style=for-the-badge\&logo=jetpackcompose\&logoColor=white)
![Room](https://img.shields.io/badge/Room-Database-3DDC84?style=for-the-badge\&logo=android\&logoColor=white)
![Hilt](https://img.shields.io/badge/Hilt-DI-3DDC84?style=for-the-badge\&logo=android\&logoColor=white)
![Clean Architecture](https://img.shields.io/badge/Architecture-Clean%20Architecture-orange?style=for-the-badge)

</p>

---

## ✨ О проекте

**Notes** — приложение для заметок, разработанное на **Kotlin** с использованием **Jetpack Compose**.

Приложение позволяет создавать заметки, комбинируя текст и изображения в одном документе. Важные заметки можно закреплять, а нужную информацию быстро находить с помощью поиска.

Проект построен на принципах **Clean Architecture**, использует **MVVM**, **Room**, **Hilt**, **Coroutines + Flow** и реактивное обновление интерфейса.

### 🎯 Основные возможности

* ✍️ Создание заметок
* 📝 Редактирование заметок
* 🗑️ Удаление заметок
* 🖼️ Добавление изображений из галереи
* 📌 Закрепление важных заметок
* 🔎 Поиск по заголовку и содержимому
* ↕️ Сохранение порядка элементов внутри заметки
* 🖼️ Отображение изображений в заметках
* ⚡ Реактивное обновление UI через `Flow`
* 💾 Локальное хранение данных через `Room`

---

## 📱 Возможности приложения

### 📝 Создание заметки

Заметка может содержать произвольное количество текстовых блоков и изображений.

```text
Note
 ├── Text
 ├── Image
 ├── Text
 ├── Image
 └── Text
```

Это позволяет создавать не только обычные текстовые заметки, но и небольшие визуальные документы.

### 📌 Закрепление

Важные заметки можно закрепить. Они отображаются отдельно от остальных:

```text
📌 Закрепленные

   Покупки
   Идеи для проекта

──────────────

📝 Остальные

   Заметки
   План на неделю
```

### 🔎 Поиск

Поиск выполняется по:

* заголовку заметки;
* текстовому содержимому;
* данным, связанным с заметкой.

Поиск реализован на уровне базы данных через SQL-запросы и `JOIN`.

---

# 🏗 Архитектура

Проект построен на **Clean Architecture** с разделением приложения на три основных слоя:

```text
┌─────────────────────────────────────┐
│           Presentation              │
│                                     │
│  Compose → ViewModel → UI State     │
└─────────────────┬───────────────────┘
                  │
                  ▼
┌─────────────────────────────────────┐
│              Domain                 │
│                                     │
│  Models → UseCases → Repository     │
└─────────────────┬───────────────────┘
                  │
                  ▼
┌─────────────────────────────────────┐
│               Data                  │
│                                     │
│  Room → DAO → Repository → Storage  │
└─────────────────────────────────────┘
```

Такое разделение позволяет изолировать бизнес-логику от Android Framework и конкретной реализации хранения данных.

---

## 🎨 Presentation Layer

Отвечает за UI и управление состоянием экранов.

**Используется:**

* Jetpack Compose
* ViewModel
* State / StateFlow
* Navigation Compose

### Основные экраны

* `NotesScreen` — список заметок
* `CreateNoteScreen` — создание заметки
* `EditNoteScreen` — редактирование заметки

UI получает данные из `ViewModel` и автоматически обновляется при изменении состояния.

---

## 🧠 Domain Layer

Содержит бизнес-логику приложения и не зависит от Android Framework.

### Models

```text
Note
ContentItem
 ├── Text
 └── Image
```

### Use Cases

```text
AddNoteUseCase
EditNoteUseCase
DeleteNoteUseCase
GetNoteUseCase
GetAllNotesUseCase
SearchNotesUseCase
SwitchPinnedStatusUseCase
```

Каждый Use Case отвечает за одну конкретную бизнес-операцию.

Например:

```text
UI
 ↓
ViewModel
 ↓
EditNoteUseCase
 ↓
NotesRepository
 ↓
Room
```

---

# 💾 Data Layer

Data Layer отвечает за хранение и получение данных.

### Основные компоненты

* Room Database
* DAO
* Repository implementation
* ImageFileManager
* Data ↔ Domain mappers

```text
Room Database
      │
      ▼
  NotesDao
      │
      ▼
NotesRepositoryImpl
      │
      ▼
 Domain Repository
```

---

# 🗄 Room Database

Для хранения заметок используется **Room**.

## Таблица `Notes`

| Поле       | Тип       | Описание                   |
| ---------- | --------- | -------------------------- |
| `id`       | `Int`     | Primary Key                |
| `title`    | `String`  | Заголовок заметки          |
| `updateAt` | `Long`    | Время последнего изменения |
| `isPinned` | `Boolean` | Статус закрепления         |

## Таблица `Content`

| Поле          | Тип            | Описание                     |
| ------------- | -------------- | ---------------------------- |
| `noteId`      | `Int`          | Foreign Key → `Notes.id`     |
| `contentType` | `TEXT / IMAGE` | Тип элемента                 |
| `content`     | `String`       | Текст или путь к изображению |
| `order`       | `Int`          | Порядок элемента             |

Primary Key:

```text
(noteId + order)
```

Связь:

```text
Notes 1 ─────────── N Content
```

### Особенности базы данных

* `ForeignKey` с `CASCADE`
* транзакции для атомарных операций;
* поддержка порядка элементов;
* поиск через SQL `JOIN`;
* разделение Domain и Database моделей;
* миграции Room.

### Версия базы данных

**Version 3**

```text
Version 1
   ↓
Notes + JSON content

Version 2
   ↓
Отдельная таблица Content

Version 3
   ↓
Оптимизированная структура
```

---

# 🖼 Работа с изображениями

Для работы с изображениями используется собственный компонент:

```text
ImageFileManager
```

Он отвечает за:

* копирование изображения из галереи;
* сохранение файла во внутреннем хранилище;
* удаление изображения;
* проверку принадлежности файла приложению.

Изображения не хранятся непосредственно в базе данных.

В `Room` сохраняется путь к файлу.

Пример:

```text
/data/data/com.dron.notes/files/IMG_<UUID>.jpg
```

Для отображения изображений используется **Coil / AsyncImage**.

---

# 💉 Dependency Injection

Для Dependency Injection используется **Hilt**.

### Основные модули

#### `DataModule`

Предоставляет:

* `NotesDatabase`
* `NotesDao`

#### `RepositoryModule`

Связывает интерфейс:

```kotlin
NotesRepository
```

с реализацией:

```kotlin
NotesRepositoryImpl
```

---

## Assisted Injection

`EditNoteViewModel` получает динамический параметр `noteId`, который приходит из Navigation.

Поэтому используется **Assisted Injection**:

```kotlin
@HiltViewModel(assistedFactory = EditNoteViewModel.Factory::class)
class EditNoteViewModel @AssistedInject constructor(
    private val getNoteUseCase: GetNoteUseCase,
    private val editNoteUseCase: EditNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    @Assisted("noteId") private val noteId: Int
) : ViewModel()
```

Factory:

```kotlin
@AssistedFactory
interface Factory {
    fun create(
        @Assisted("noteId") noteId: Int
    ): EditNoteViewModel
}
```

Получение ViewModel в Compose:

```kotlin
val viewModel: EditNoteViewModel = hiltViewModel(
    creationCallback = { factory: EditNoteViewModel.Factory ->
        factory.create(noteId)
    }
)
```

Это позволяет корректно передавать параметры навигации непосредственно в ViewModel.

---

# ⚡ Реактивность

Приложение использует:

* Kotlin Coroutines
* Flow
* StateFlow

Изменение данных в Room автоматически приводит к обновлению данных в `Flow`, после чего UI получает новое состояние.

```text
Room
 │
 │ Flow
 ▼
Repository
 │
 ▼
UseCase
 │
 ▼
ViewModel
 │
 │ StateFlow
 ▼
Compose UI
```

Таким образом, UI не требует ручного обновления после изменения данных.

---

# 🔄 Транзакции

Операции создания и редактирования заметок выполняются атомарно.

Например, при редактировании заметки необходимо одновременно:

1. обновить информацию о заметке;
2. обновить содержимое;
3. добавить новые изображения;
4. удалить старые изображения.

Если операция завершается ошибкой, база данных не остается в промежуточном состоянии.

---

# 🎨 UI

Интерфейс полностью реализован на **Jetpack Compose**.

### Основные компоненты

```text
NoteCard
NoteCardWithImage
ContentList
ImageGroup
ImageContent
TextContent
```

### `NoteCard`

Карточка обычной заметки без изображения.

### `NoteCardWithImage`

Карточка заметки с preview изображения.

### `ContentList`

Отображает содержимое заметки в исходном порядке:

```text
Text
Image
Text
Image
Text
```

### `ImageGroup`

Отображает изображения группами, до двух изображений в ряд.

---

# 📂 Структура проекта

```text
app/src/main/java/com/dron/notes/

├── data/
│   ├── ContentItemDbModel.kt
│   ├── ImageFileManager.kt
│   ├── Mappers.kt
│   ├── NoteDbModel.kt
│   ├── NotesDao.kt
│   ├── NotesDatabase.kt
│   ├── NotesRepositoryImpl.kt
│   └── NoteWithContentDbModel.kt
│
├── di/
│   ├── DataModule.kt
│   └── RepositoryModule.kt
│
├── domain/
│   ├── AddNoteUseCase.kt
│   ├── ContentItem.kt
│   ├── DeleteNoteUseCase.kt
│   ├── EditNoteUseCase.kt
│   ├── GetAllNotesUseCase.kt
│   ├── GetNoteUseCase.kt
│   ├── Note.kt
│   ├── NotesRepository.kt
│   ├── SearchNotesUseCase.kt
│   └── SwitchPinnedStatusUseCase.kt
│
└── presentation/
    ├── MainActivity.kt
    ├── NotesApplication.kt
    │
    ├── navigation/
    │   └── NavGraph.kt
    │
    ├── screens/
    │   ├── common/
    │   │   └── ContentComponents.kt
    │   │
    │   ├── creation/
    │   │   ├── CreateNoteScreen.kt
    │   │   └── CreateNoteViewModel.kt
    │   │
    │   ├── editing/
    │   │   ├── EditNoteScreen.kt
    │   │   └── EditNoteViewModel.kt
    │   │
    │   └── notes/
    │       ├── NotesScreen.kt
    │       └── NotesViewModel.kt
    │
    ├── theme/
    │   ├── Theme.kt
    │   ├── Typography.kt
    │   ├── Colors.kt
    │   └── CustomIcons.kt
    │
    └── utils/
        └── DateFormatter.kt
```

---

# 🛠 Технологический стек

| Технология             | Использование             |
| ---------------------- | ------------------------- |
| **Kotlin**             | Основной язык             |
| **Jetpack Compose**    | UI                        |
| **MVVM**               | Presentation architecture |
| **Clean Architecture** | Архитектура проекта       |
| **Hilt**               | Dependency Injection      |
| **Room**               | Локальная база данных     |
| **Coroutines**         | Асинхронные операции      |
| **Flow / StateFlow**   | Реактивное состояние      |
| **Navigation Compose** | Навигация                 |
| **Coil**               | Загрузка изображений      |
| **JUnit**              | Unit Testing              |
| **Espresso**           | UI Testing                |

---

# 🧪 Testing

Проект предусматривает тестирование с использованием:

* **JUnit** — unit-тесты;
* **Espresso** — UI-тестирование.

Основная бизнес-логика вынесена в `Domain Layer`, что позволяет тестировать Use Cases независимо от UI.

---

# 🚀 Запуск проекта

## Требования

* Android Studio **Meerkat (2025.12.1)** или выше
* Kotlin **2.0.21**
* JDK **17**
* Android API **36**

## Клонирование

```bash
git clone https://github.com/yourusername/Notes.git
cd Notes
```

## Сборка

```bash
./gradlew build
```

## Установка Debug версии

```bash
./gradlew installDebug
```

---

# 📌 Что демонстрирует проект

Этот проект показывает практическое применение современных подходов Android-разработки:

* Clean Architecture;
* MVVM;
* Jetpack Compose;
* Hilt Dependency Injection;
* Room Database;
* Repository Pattern;
* Use Case Pattern;
* Kotlin Coroutines;
* Flow / StateFlow;
* Assisted Injection;
* Room Transactions;
* локальное файловое хранилище;
* работу с изображениями;
* Navigation Compose;
* разделение Domain и Data моделей.

---

# 🔮 Возможные улучшения

В дальнейшем приложение можно расширить:

* ☁️ синхронизацией заметок с облаком;
* 🔐 блокировкой заметок;
* 🌙 дополнительными темами оформления;
* 🏷️ категориями и тегами;
* 📅 напоминаниями;
* 📤 экспортом заметок;
* 🔄 резервным копированием;
* 📎 поддержкой дополнительных типов файлов.

---

# 📄 Лицензия

Проект распространяется под лицензией **Apache License 2.0**.

---

# 👨‍💻 Автор

**Slava Dronov**

Разработано с использованием:

**Kotlin • Jetpack Compose • Room • Hilt • Clean Architecture**

---

<p align="center">

### ⭐ Если проект оказался полезным — поставьте Star

</p>
