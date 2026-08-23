# Notes — Приложение для заметок

Современное приложение для заметок с поддержкой текста и изображений. Построено на Clean Architecture с использованием Jetpack Compose, Room и Hilt.

---

## 1. О проекте

Приложение позволяет создавать, редактировать, удалять и искать заметки. Поддерживает добавление изображений из галереи, закрепление важных заметок и поиск по содержимому.

---

## 2. Архитектура (Clean Architecture)

**Presentation Layer (UI)**
- UI: Jetpack Compose
- ViewModels: управление состоянием
- Screens: NotesScreen, CreateNoteScreen, EditNoteScreen

**Domain Layer (Бизнес-логика)**
- Models: Note, ContentItem (Text / Image)
- UseCases: AddNote, EditNote, DeleteNote, GetNote, GetAllNotes, SearchNotes, SwitchPinnedStatus
- Repository Interfaces: контракты для работы с данными

**Data Layer (Данные)**
- Room Database: NoteDbModel, ContentItemDbModel, NotesDao
- ImageFileManager: копирование и удаление фото
- Mappers: преобразование Domain ↔ Data

---

## 3. База данных (Room)

**Таблица Notes**
- id: Int (первичный ключ, автоинкремент)
- title: String
- updateAt: Long
- isPinned: Boolean

**Таблица Content** (связь 1:N с Notes)
- noteId: Int (внешний ключ → Notes.id)
- contentType: TEXT или IMAGE
- content: String (текст или URL изображения)
- order: Int (порядок в заметке)
- Первичный ключ: noteId + order

**Особенности:**
- Каскадное удаление (CASCADE)
- Транзакции для атомарных операций
- Поиск по заголовку и контенту через JOIN

**Версия БД:** 3

**Миграции:**
- Версия 1: Создание Notes (с JSON полем content)
- Версия 2: Добавление таблицы content
- Версия 3: Оптимизация структуры

---

## 4. Управление изображениями

**ImageFileManager**
- Копирование фото из галереи во внутреннее хранилище
- Удаление фото при удалении заметки
- Проверка принадлежности файла приложению

**Путь хранения:** `/data/data/com.dron.notes/files/IMG_<UUID>.jpg`

---

## 5. Dependency Injection (Hilt)

Проект использует Hilt для внедрения зависимостей.

**Модули:**
- **DataModule:** создание Room Database и Dao
- **RepositoryModule:** связывание интерфейса NotesRepository с реализацией NotesRepositoryImpl

**Что внедряется автоматически (через @Inject constructor):**
- Все UseCase (AddNoteUseCase, EditNoteUseCase, DeleteNoteUseCase, и т.д.)
- CreateNoteViewModel, NotesViewModel

**Assisted Injection (для EditNoteViewModel):**
Используется для передачи `noteId` из навигации:

```kotlin
@HiltViewModel(assistedFactory = EditNoteViewModel.Factory::class)
class EditNoteViewModel @AssistedInject constructor(
    private val getNoteUseCase: GetNoteUseCase,
    private val editNoteUseCase: EditNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    @Assisted("noteId") private val noteId: Int
) : ViewModel()

@AssistedFactory
interface Factory {
    fun create(@Assisted("noteId") noteId: Int): EditNoteViewModel
}

Использование в Screen:

kotlin
val viewModel: EditNoteViewModel = hiltViewModel(
    creationCallback = { factory: EditNoteViewModel.Factory ->
        factory.create(noteId)
    }
)
---

## 6. Технический стек

- UI: Jetpack Compose
- DI: Hilt (Dagger)
- Database: Room
- Images: Coil (AsyncImage)
- Navigation: Jetpack Navigation Compose
- Coroutines: Kotlin Coroutines + Flow
- Testing: JUnit, Espresso

---

## 7. Структура проекта

```
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
├── di/
│   ├── DataModule.kt
│   └── RepositoryModule.kt
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
└── presentation/
    ├── MainActivity.kt
    ├── NotesApplication.kt
    ├── navigation/
    │   └── NavGraph.kt
    ├── screens/
    │   ├── common/
    │   │   └── ContentComponents.kt
    │   ├── creation/
    │   │   ├── CreateNoteScreen.kt
    │   │   └── CreateNoteViewModel.kt
    │   ├── editing/
    │   │   ├── EditNoteScreen.kt
    │   │   └── EditNoteViewModel.kt
    │   └── notes/
    │       ├── NotesScreen.kt
    │       └── NotesViewModel.kt
    ├── theme/
    │   ├── Theme.kt
    │   ├── Typography.kt
    │   ├── Colors.kt
    │   └── CustomIcons.kt
    └── utils/
        └── DateFormatter.kt
```

---

## 8. Основные функции

- **Создание заметки:** добавление текста и изображений
- **Редактирование:** изменение заголовка, текста, фото
- **Удаление:** удаление заметки и связанных фото
- **Закрепление:** закрепление важных заметок
- **Поиск:** поиск по заголовку и содержанию
- **Список заметок:** разделение на "Закрепленные" и "Остальные"

---

## 9. UI компоненты (Jetpack Compose)

- NoteCard: карточка заметки (без картинки)
- NoteCardWithImage: карточка заметки с превью картинки
- ContentList: список контента (текст + изображения)
- ImageGroup: группа изображений (до 2-х в ряд)
- ImageContent: одно изображение с кнопкой удаления
- TextContent: поле для ввода текста

---

## 10. Особенности реализации

- Transaction: атомарные операции при добавлении и редактировании
- Flow: реактивное обновление UI при изменении данных
- Sealed Classes: типобезопасные состояния и события
- Clean Architecture: разделение ответственности по слоям
- Hilt DI: автоматическое внедрение зависимостей
- Splash Screen: экран загрузки при запуске
- Assisted Injection: передача динамических параметров (noteId)

---

## 11. Запуск приложения

**Требования:**
- Android Studio Meerkat (2025.12.1) или выше
- Kotlin 2.0.21
- JDK 17
- Android API 36

**Команды:**
```bash
git clone https://github.com/yourusername/Notes.git
./gradlew build
./gradlew installDebug
```

---

## 12. Лицензия

Apache License 2.0

---

## 13. Разработчик

Slava Dronov

---

Сделано с на Kotlin и Jetpack Compose
