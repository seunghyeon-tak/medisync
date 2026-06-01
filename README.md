## 프로젝트 소개
Medisync

## 기술 스택
- Java 17
- Spring Boot 3.5.14
- Spring Security + JWT
- Spring Data JPA
- Spring Data Redis
- Swagger (springdoc-openapi)
- Lombok
- Docker / Docker Compose
- Testcontainers (MySQL / PostgreSQL)

## 디렉토리 구조
```
src
├── common
│   ├── response      # ApiResponse, ErrorCode, PageResponse
│   ├── exception     # BaseException, GlobalExceptionHandler
│   ├── entity        # BaseEntity (소프트 딜리트 포함)
│   ├── jwt           # JwtProvider, JwtFilter
│   └── util          # DateUtils, StringUtils
├── config
│   ├── SwaggerConfig
│   ├── SecurityConfig
│   ├── WebConfig
│   ├── JpaConfig
│   └── RedisConfig
└── domain            # 비즈니스 로직 추가 영역
```

## 실행 방법

### 로컬 실행 (Docker)
```bash
# 1. 환경 설정
cp application-local.yml.example application-local.yml

# 2. Docker 실행
docker-compose up -d

# 3. 앱 실행
./gradlew bootRun
```

### Swagger
```
http://localhost:8080/swagger-ui/index.html
```

## 테스트
```bash
./gradlew test
```

### 테스트 환경
- Testcontainers 사용 (MySQL / PostgreSQL 선택 가능)
- `application-test.yml` 에서 DB 선택

```yaml
# MySQL 사용시 (기본값)
url: jdbc:tc:mysql:8.0:///testdb

# PostgreSQL 사용시
# url: jdbc:tc:postgresql:15:///testdb
```

### 테스트 기본 구조
```java
// Controller 테스트
class UserControllerTest extends BaseControllerTest { }

// Service 테스트
class UserServiceTest extends BaseServiceTest { }
```

## 환경변수 설명
| 변수명 | 설명 | 기본값 |
|--------|------|--------|
| JWT_SECRET | JWT 시크릿 키 | - |
| DB_URL | 데이터베이스 URL | - |
| DB_USER | 데이터베이스 유저 | user |
| DB_PASSWORD | 데이터베이스 비밀번호 | password |
| REDIS_HOST | Redis 호스트 | localhost |
| REDIS_PORT | Redis 포트 | 6379 |

## 브랜치 전략
```
main    → 안정화된 버전
develop → 개발 버전
```
