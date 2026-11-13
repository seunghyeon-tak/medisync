## 프로젝트 소개
**Medisync**

의사·환자·약사를 잇는 비대면진료 서비스의 백엔드입니다.

실무에서 비대면진료 서비스 백엔드를 단독으로 개발·운영했던 경험을 바탕으로, 당시 일정에 쫓겨 충분히 다루지 못했던 부분(예약 동시성, 테스트, 인증 구조 등)을 다시 설계하며 만들고 있는 개인 프로젝트입니다.

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

## 주요 기능
| 도메인     | 	기능                  | 	상태       |
|---------|----------------------|--|
| 회원      | 	의사·환자·약사 역할별 회원가입   | 	완료       
| 인증      | 	로그인, 로그아웃, 토큰 재발급   | 	완료       
| 예약 슬롯   | 	의사가 진료 가능한 시간대 생성   | 	완료       
| 진료 예약   | 	환자가 슬롯 예약, 음성·영상 통화 방식 선택 | 	완료       
| 진료 완료   | 	진료 기록 생성, 예약 상태 완료 처리 | 	완료       
| 즉시진료 매칭 | 	예약 없이 바로 진료를 요청하면 의사를 자동 배정 | 	진행 중 (엔티티·DB 설계) 
| 처방전 / 약국 | 	처방전 발행, 환자가 약국 선택   | 	예정       

## 핵심 설계

### 동시 예약 처리 : 비관적 락

같은 시간대 슬롯에 두 환자가 동시에 예약하면, 두 요청 모두 "예약 가능" 상태를 읽고 중복 예약이 생길 수 있습니다.

- 슬롯을 조회할 때 `PESSIMISTIC_WRITE` 락(`SELECT ... FOR UPDATE`)을 걸어, 먼저 들어온 요청의 트랜잭션이 끝날 때까지 다른 요청이 기다리게 했습니다.
- 뒤의 요청은 이미 예약된 슬롯을 읽게 되어 예외로 처리됩니다.
- 예약은 충돌이 실제로 자주 일어날 수 있는 작업이고, 실패 후 재시도하는 낙관적 락보다 확실하게 한 명만 성공시키는 쪽이 맞다고 판단했습니다.
- `ExecutorService`와 `CountDownLatch`로 두 요청을 동시에 실행하는 동시성 테스트를 작성해, 한 명만 성공하고 예약이 1건만 생성되는 것을 검증했습니다.

### 인증: JWT + Refresh Token Rotation + Redis

- Access Token은 짧게, Refresh Token은 길게 발급합니다.
- 토큰 재발급 시 Refresh Token도 새로 발급하고 기존 토큰은 폐기합니다(RTR). Redis에 저장된 토큰과 다른 토큰으로 재발급을 시도하면 탈취로 보고 저장된 토큰을 삭제합니다.
- Redis의 TTL로 토큰 만료를 자동 관리합니다.
- 로그아웃한 Access Token은 남은 만료 시간만큼 Redis 블랙리스트에 넣어, 만료 전이라도 사용할 수 없게 했습니다.

### 회원 구조: JPA JOINED 상속

- 의사·환자·약사는 이름, 이메일, 연락처 같은 공통 정보를 공유하고, 면허번호나 혈액형 같은 역할별 정보가 다릅니다.
- 공통 정보는 users 테이블에, 역할별 정보는 각 테이블에 두는 JOINED 전략을 사용해 중복 없이 정규화된 구조를 유지했습니다.

### 계층 구조: Controller → Facade → Service → Repository

- Facade: 여러 서비스를 조합하는 흐름을 담당합니다. (예: 로그인 시 사용자 검증 → 비밀번호 확인 → 토큰 발급)
- Service: 단일 도메인의 비즈니스 로직과 DB 작업을 담당합니다.
- 도메인별 예외 클래스가 공통 ErrorCode 인터페이스를 구현하고, GlobalExceptionHandler에서 일관된 응답 형식으로 변환합니다.

### DB 마이그레이션과 테스트

- Flyway로 스키마 변경 이력을 버전별 SQL 파일로 관리합니다.
- Testcontainers로 실제 MySQL 컨테이너 위에서 통합 테스트를 실행해, 운영 환경과 같은 DB에서 동작을 검증합니다.
- 서비스 계층은 Mockito로 단위 테스트를 작성했습니다.

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
