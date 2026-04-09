# ПО ЖБК — Система инспекции общежития

**dormitory-inspection** · Spring Boot 3.3.5 · Java 17 · PostgreSQL · Redis

---

## Содержание

1. [Назначение системы](#1-назначение-системы)
2. [Роли и права доступа](#2-роли-и-права-доступа)
3. [Архитектура](#3-архитектура)
4. [Схема базы данных](#4-схема-базы-данных)
5. [REST API](#5-rest-api)
6. [Безопасность (JWT)](#6-безопасность-jwt)
7. [Запуск и настройка](#7-запуск-и-настройка)
8. [Статус реализации](#8-статус-реализации)

---

## 1. Назначение системы

ПО предназначено для **автоматизации процесса оценивания, выставления и отслеживания состояния блоков в общежитии**.

Система решает следующие задачи:

- инспекторы проводят плановые обходы и фиксируют состояние блоков;
- жильцы отслеживают оценки своего блока в личном кабинете;
- администраторы управляют составом инспекторов, назначают этажи и формируют отчёты.

---

## 2. Роли и права доступа

Система поддерживает три роли с иерархическим наследованием прав.

### 2.1. USER — житель общежития

| Действие | Доступ |
|---|---|
| Просмотр оценок своего блока за всё время | + |
| Просмотр истории изменений состояния блока | + |
| Авторизация через JWT | + |

### 2.2. INSPECTOR — наследует права USER

| Действие | Доступ |
|---|---|
| Просмотр и изменение оценок блоков на закреплённых этажах | + |
| Создание обхода (дата, этаж, результаты) | + |

### 2.3. ADMIN — наследует права INSPECTOR

| Действие | Доступ |
|---|---|
| Добавление / удаление инспекторов | + |
| Назначение инспекторов на этажи | + |
| Добавление / удаление блоков | + |
| Генерация отчётов по обходам за месяц / несколько месяцев | + |

---

## 3. Архитектура

```
Клиент (Vue 3)
     │  HTTP / REST
     ▼
Spring Boot (REST API)
     │
     ├─► PostgreSQL  — основное хранилище данных
     ├─► Redis       — кэш и сессии
     └─► Swagger     — документация API (OpenAPI 3.0)
```

| Компонент | Технология |
|---|---|
| Фреймворк | Spring Boot 3.3.5 |
| Язык | Java 17 |
| БД | PostgreSQL |
| Кэш | Redis |
| Безопасность | Spring Security + JWT |
| ORM | Spring Data JPA / Hibernate |
| Маппинг | MapStruct |
| Миграции | Liquibase |
| Документация API | Swagger / OpenAPI 3.0 |
| Сборка | Maven |

Данные передаются через DTO-объекты. Итоговый продукт — бэкенд для web-клиента.

---

## 4. Схема базы данных

### 4.1. Таблица `blocks`

| Поле | Тип | Ограничения |
|---|---|---|
| id | BIGSERIAL | PK |
| number | INT | UNIQUE NOT NULL |
| floor | INT | NOT NULL |

### 4.2. Таблица `users`

| Поле | Тип | Ограничения |
|---|---|---|
| id | BIGSERIAL | PK |
| username | VARCHAR | UNIQUE NOT NULL |
| fio | VARCHAR | NOT NULL |
| password | VARCHAR | NOT NULL (bcrypt hash) |
| role | ENUM | NOT NULL (`USER` \| `INSPECTOR` \| `ADMIN`) |
| block_id | BIGINT | FK → `blocks.id`, nullable |

### 4.3. Таблица `inspector_floors`

| Поле | Тип | Ограничения |
|---|---|---|
| inspector_id | BIGINT | FK → `users.id` |
| floor_number | INT | — |

Составной PK: `(inspector_id, floor_number)`

### 4.4. Таблица `inspections`

| Поле | Тип | Ограничения |
|---|---|---|
| id | BIGSERIAL | PK |
| inspector_id | BIGINT | FK → `users.id`, NOT NULL |
| block_id | BIGINT | FK → `blocks.id`, NOT NULL |
| date | DATE | NOT NULL |
| created_at | TIMESTAMP | NOT NULL |
| shower | SMALLINT | NOT NULL (оценка 1–5) |
| toilet | SMALLINT | NOT NULL |
| hall | SMALLINT | NOT NULL |
| kitchen | SMALLINT | NOT NULL |
| room_a | SMALLINT | NOT NULL |
| room_b | SMALLINT | NOT NULL |

---

## 5. REST API

### 5.1. Аутентификация — `/api/auth`

| Метод | URL | Описание | Роль |
|---|---|---|---|
| POST | `/api/auth/signUp` | Регистрация | — |
| POST | `/api/auth/signIn` | Вход, возвращает JWT | — |

### 5.2. Пользователи — `/api/users`

| Метод | URL | Описание | Роль |
|---|---|---|---|
| GET | `/api/users` | Список всех пользователей | ADMIN |
| GET | `/api/users/{id}` | Пользователь по ID | ADMIN |
| DELETE | `/api/users/{id}` | Удаление пользователя | ADMIN |

### 5.3. Блоки — `/api/blocks`

| Метод | URL | Описание | Роль |
|---|---|---|---|
| GET | `/api/blocks` | Список всех блоков | USER, INSPECTOR, ADMIN |
| GET | `/api/blocks/{id}` | Блок по ID | USER, INSPECTOR, ADMIN |
| POST | `/api/blocks` | Создание блока | ADMIN |
| DELETE | `/api/blocks/{id}` | Удаление блока | ADMIN |

### 5.4. Обходы — `/api/inspections`

| Метод | URL | Описание | Роль |
|---|---|---|---|
| POST | `/api/inspections` | Создание обхода | INSPECTOR, ADMIN |
| GET | `/api/inspections` | Все обходы | ADMIN |
| GET | `/api/inspections/my` | Обходы текущего инспектора | INSPECTOR, ADMIN |
| GET | `/api/inspections/block/{id}` | Обходы по блоку | USER, INSPECTOR, ADMIN |

> `inspectorId` при создании обхода берётся из `Principal` (не из тела запроса).

### 5.5. Назначение этажей — `/api/users/{id}/floors`

| Метод | URL | Описание | Роль |
|---|---|---|---|
| POST | `/api/users/{id}/floors` | Назначить этажи инспектору | ADMIN |
| GET | `/api/users/{id}/floors` | Просмотр закреплённых этажей | INSPECTOR, ADMIN |

---

## 6. Безопасность (JWT)

Схема аутентификации — **stateless JWT**:

1. Клиент отправляет `POST /api/auth/signIn` с логином и паролем.
2. Сервер возвращает JWT-токен.
3. Все последующие запросы передают токен в заголовке `Authorization: Bearer <token>`.

**Архитектурное решение:** `UserDetailsService` намеренно не используется. `UserDetails` строится непосредственно в `JwtAuthenticationFilter` из claims токена (`username` + `role`) — без дополнительного запроса в БД на каждый запрос.

| Компонент | Назначение |
|---|---|
| `JwtService` | Генерация и валидация токенов |
| `JwtAuthenticationFilter` | Перехват запросов, построение `UserDetails` из claims |
| `SecurityConfig` | Режим STATELESS, permitAll для `/api/auth/**` |

---

## 7. Запуск и настройка

### 7.1. Требования

- Java 17+
- Docker + Docker Compose
- Maven 3.8+

### 7.2. Переменные окружения

Создайте файл `.env` в корне проекта (добавлен в `.gitignore`):

```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=dormitory
DB_USERNAME=postgres
DB_PASSWORD=your_password

REDIS_HOST=localhost
REDIS_PORT=6379

JWT_SECRET=your_secret_key
JWT_EXPIRATION=86400000
```

### 7.3. Запуск инфраструктуры

```bash
docker-compose up -d
```

Запускает **PostgreSQL** и **Redis**.

### 7.4. Запуск приложения

```bash
# Профиль разработки
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Сборка и запуск JAR
./mvnw clean package -DskipTests
java -jar target/dormitory-inspection-0.0.1-SNAPSHOT.jar
```

### 7.5. Доступные профили

| Профиль | Назначение |
|---|---|
| `dev` | Разработка, расширенное логирование |
| `prod` | Production-конфигурация |
| `test` | Тестовое окружение |

### 7.6. Документация API

После запуска приложения Swagger UI доступен по адресу:

```
http://localhost:8080/swagger-ui.html
```

---

## 8. Статус реализации

| Этап | Описание | Статус |
|---|---|---|
| 1 | Инициализация проекта | Готово |
| 2 | Доменный слой (сущности, миграции) | Готово |
| 3 | Security Layer (JWT) | Готово |
| 4 | User CRUD | Готово |
| 5 | Block CRUD | Готово |
| 6 | Inspection (обходы) | Готово |
| 7 | Назначение инспекторов на этажи | Готово |
| 8 | GlobalExceptionHandler | В разработке |
| 9 | Swagger / OpenAPI 3.0 | В разработке |
| 10 | Frontend (Vue 3) | В разработке |
