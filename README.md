# Beauty Marathon

> 🚧 **Актуальная версия кода находится в ветке [`development`](https://github.com/janesvami/beauty_marathon/tree/development).**

[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-brightgreen.svg)](https://spring.io/projects/spring-boot)

Бэкенд фитнес-марафона. Учёт участников, еженедельные замеры, автоматическое определение победителей по итогам месяца, email-рассылка через Kafka и FreeMarker.

## Стек

| Категория | Технология |
| :--- | :--- |
| Язык | Java 21 |
| Фреймворк | Spring Boot 3.5.3 |
| Сборка | Maven |
| БД | PostgreSQL |
| Миграции | Flyway |
| API Doc | SpringDoc OpenAPI |
| Брокер | Apache Kafka |
| Шаблоны | FreeMarker |
| Логирование | Logback + Logstash encoder |
| Контейнеризация | Docker Compose |

## Запуск

```bash
git clone https://github.com/janesvami/beauty_marathon.git
cd beauty_marathon
git checkout development
docker-compose up -d
./mvnw spring-boot:run
```

Порт: `8080`

## Профили

| Профиль | Описание |
| :--- | :--- |
| `no-docker` | Kafka не используется, уведомления — заглушка в лог |
| `docker` | Kafka активен, сообщения в топик `notifications-topic` |

## API

Swagger UI: `http://localhost:8080/swagger-ui/index.html`

| Контроллер | Путь |
| :--- | :--- |
| `UserController` | `/api/users` |
| `MeasurementController` | `/api/measurements` |
| `WinnerController` | `/api/winners` |
| `KafkaController` | `/api/kafka` |

## Структура БД (Flyway)

| Версия | Файл |
| :--- | :--- |
| V1 | `V1__create_init_tables.sql` |
| V2 | `V2__rename_column.sql` |
| V3 | `V3__add_column_month_number_to_mo_measurement.sql` |
| V4 | `V4add_total_point_column_to_user_measurement.sql` |
| V5 | `V5create_table_winner.sql` |
| V6 | `V6__add_column_email_to_user_profile.sql` |

### Таблицы

| Таблица | Поля |
| :--- | :--- |
| `user_profile` | `id`, `name`, `email`, `startWeight`, `targetWeight`, `creationDate`, `deletedState` |
| `mo_measurement` | `id`, `year`, `monthNumber`, `moDate`, `closedState` |
| `wk_measurement` | `id`, `measurementDate`, `commentary`, `closedState`, `moMeasurementId` |
| `user_measurement` | `id`, `weight`, `waterPoint`, `stepPoint`, `sleepPoint`, `diaryPoint`, `alcoholFreePoint`, `weightPoint`, `totalPoint`, `commentary`, `userId`, `wkMeasurementId` |
| `winner` | `id`, `userId`, `moMeasurementId`, `averagePoint`, `creationDate` |

## Пакеты

| Пакет | Содержимое |
| :--- | :--- |
| `controller` | REST-эндпоинты |
| `service` | Бизнес-логика |
| `repository` | Spring Data JPA |
| `entity` | JPA-сущности |
| `view` | View-объекты и фильтры |
| `exception` | Кастомные исключения, `ApiExceptionHandler` |
| `config` | Kafka, FreeMarker |

## View-объекты

| Пакет | Классы |
| :--- | :--- |
| `view.user` | `CreateUserView`, `UpdateUserView`, `GetUserView`, `UserMaxAverageView` |
| `view.measurement` | `CreateUserMeasurementView`, `UpdateUserMeasurementView`, `GetUserMeasurementView`, `GetMoMeasurementView`, `GetWkMeasurementView` |
| `view.filter.register` | `UserMeasurementFilter`, `MoMeasurementFilter`, `WinnerFilter` |

## Сервисы

| Сервис | Ответственность |
| :--- | :--- |
| `UserService` | CRUD пользователей, валидация email |
| `MeasurementService` | Замеры, расчёт `total_point`, закрытие месяца |
| `WinnerService` | Сохранение и поиск победителей |
| `TemplateService` | Загрузка FreeMarker-шаблонов |
| `KafkaNotificationProducer` | Отправка в Kafka (профиль `docker`) |
| `KafkaNotificationService` | Реализация `NotificationService` |
| `NoDockerNotificationService` | Заглушка уведомлений |

## Репозитории

| Репозиторий | Методы |
| :--- | :--- |
| `MoMeasurementRepository` | `findByYearAndMonthNumber` |
| `WkMeasurementRepository` | `findByMeasurementDate` |
| `WinnerRepository` | `findUsersWithMaxAverage(@Param("moId"))` |
| `UserMeasurementRepository` | JPA |
| `UserRepository` | JPA |

## Kafka

Топик: `notifications-topic`
Ключ: `String` (UUID)
Значение: `NotificationRequest` (JSON)

## FreeMarker

Шаблоны в `src/main/resources/templates/`:

| Файл | Назначение |
| :--- | :--- |
| `week.html` | Недельный замер |
| `monthwinner.html` | Победитель месяца |
| `monthnotwinner.html` | Остальные участники |

## Тесты

| Класс | Покрытие |
| :--- | :--- |
| `UserServiceTest` | `UserService` |
| `MeasurementServiceTest` | `MeasurementService` |
| `WinnerServiceTest` | `WinnerService` |
| `UserControllerRestTest` | `/api/users` |
| `MeasurementControllerTest` | `/api/measurements` |
| `WinnerControllerRestTest` | `/api/winners` |

## Обработка ошибок

`ApiExceptionHandler` + `ApiError`

| Исключение | Статус |
| :--- | :--- |
| `EntityNotFoundException` | 404 |
| `IllegalArgumentException` | 400 |
| `MethodArgumentNotValidException` | 400 |
| `MoClosedException` | 400 |
| `WkMeasurementClosedException` | 400 |
| `UserDeletedException` | 400 |
| `EmailAlreadyExistsException` | 400 |

## Статус проекта

Проект находится в стадии активной разработки. Основная работа ведётся в ветке [`development`](https://github.com/janesvami/beauty_marathon/tree/development). Ветка `main` может содержать нестабильный или устаревший код.