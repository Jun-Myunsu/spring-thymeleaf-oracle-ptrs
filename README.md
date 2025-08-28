# POSCO PTRS System

Spring Boot 3 + MyBatis + Thymeleaf + Oracle 기반 실무 적용 가능한 CRUD 시스템

## 기술 스택

- **Framework**: Spring Boot 3.2.0
- **Template Engine**: Thymeleaf
- **ORM**: MyBatis 3.0.3
- **Validation**: Spring Boot Validation
- **Session**: HTTP Session
- **Database**: H2 (개발), Oracle (운영)
- **Build Tool**: Maven
- **Java Version**: 17

## 프로젝트 구조

```
src/
├── main/
│   ├── java/com/posco/ptrs/
│   │   ├── config/          # 설정 클래스
│   │   ├── controller/      # 컨트롤러
│   │   ├── service/         # 서비스 레이어
│   │   ├── mapper/          # MyBatis 매퍼
│   │   ├── entity/          # 엔티티 클래스
│   │   ├── dto/             # DTO 클래스
│   │   ├── util/            # 유틸리티 클래스
│   │   └── exception/       # 예외 처리
│   └── resources/
│       ├── mybatis/mapper/  # MyBatis XML 매퍼
│       ├── templates/       # Thymeleaf 템플릿
│       ├── static/          # 정적 리소스
│       ├── application.yml  # 설정 파일
│       ├── schema.sql       # H2 스키마
│       ├── data.sql         # 초기 데이터
│       └── oracle-schema.sql # Oracle 스키마
```

## 실행 방법

### 1. H2 데이터베이스로 실행 (기본)

```bash
mvn spring-boot:run
```

또는

```bash
mvn clean package
java -jar target/ptrs-1.0.0.jar
```

### 2. Oracle 데이터베이스로 실행

1. Oracle 데이터베이스 설정
2. `oracle-schema.sql` 실행하여 테이블 생성
3. 프로파일 변경하여 실행:

```bash
java -jar target/ptrs-1.0.0.jar --spring.profiles.active=oracle
```

또는 환경변수 설정:

```bash
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
java -jar target/ptrs-1.0.0.jar --spring.profiles.active=oracle
```

## 접속 정보

- **애플리케이션**: http://localhost:8080
- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa`
  - Password: (비워둠)

## 테스트 계정

- **관리자**: admin@posco.com / admin123
- **사용자**: user1@posco.com / user123

## 주요 기능

### 1. 사용자 관리 (CRUD)
- 사용자 목록 조회
- 사용자 상세 정보
- 사용자 추가/수정/삭제
- 입력 데이터 검증

### 2. 인증 및 세션
- 로그인/로그아웃
- 세션 기반 인증
- 비밀번호 암호화

### 3. 실무 적용 요소
- 계층화된 아키텍처
- 트랜잭션 관리
- 예외 처리
- 입력 검증
- 보안 (비밀번호 암호화)
- 프로파일별 설정

## 데이터베이스 전환

### H2에서 Oracle로 전환

1. `application.yml`에서 프로파일을 `oracle`로 변경
2. Oracle 데이터베이스 연결 정보 설정
3. `oracle-schema.sql` 실행
4. 애플리케이션 재시작

### 설정 변경 포인트

```yaml
spring:
  profiles:
    active: oracle  # h2에서 oracle로 변경
```

## 확장 가능한 구조

- **인터셉터**: 인증/권한 체크
- **AOP**: 로깅, 트랜잭션
- **캐시**: Redis 연동
- **API**: REST API 추가
- **보안**: Spring Security 적용
- **테스트**: 단위/통합 테스트

## 개발 도구

- **Hot Reload**: Spring Boot DevTools 적용
- **디버깅**: H2 Console 제공
- **로깅**: MyBatis SQL 로깅 활성화