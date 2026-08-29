# APIM Study Member Service

Java 17, Spring Boot 4.1.1, Gradle, Thymeleaf, PostgreSQL 17 기반의 스터디 회원 관리 서비스입니다.

## 주요 기능

- 이름/비밀번호 로그인과 비밀번호 변경
- BCrypt 비밀번호 해시
- AES-GCM 휴대폰 번호 암호화 및 HMAC 검색
- MASTER/ADMIN/MEMBER 권한 관리
- 회원 검색, 10건 페이징, 등록, 상세, 수정, 소프트 삭제
- Flyway DB 마이그레이션

## 로컬 실행

1. `.env.example`을 `.env`로 복사하고 비밀번호와 `APP_ENCRYPTION_KEY`를 설정합니다.
2. PostgreSQL을 실행합니다.

   ```powershell
   docker compose up -d member-db work-db
   ```

3. 회원 서비스의 STS Run Configuration에는 `DB_PASSWORD=MEMBER_DB_PASSWORD 값`, `APP_ENCRYPTION_KEY`를 등록합니다.
4. 업무 서비스의 STS Run Configuration에는 `DB_PASSWORD=WORK_DB_PASSWORD 값`을 등록합니다.
5. 필요한 서비스를 Spring Boot App으로 실행합니다.
6. Gateway를 마지막으로 실행하고 `http://localhost:8080/member/login`에 접속합니다.

## 로컬 포트와 Gateway 경로

| 애플리케이션 | 내부 주소 | Gateway 주소 |
|---|---|---|
| gateway | `http://localhost:8080` | `http://localhost:8080` |
| member-service | `http://localhost:8081/member` | `http://localhost:8080/member/**` |
| work-service | `http://localhost:8082/work` | `http://localhost:8080/work/**` |

Gateway의 선택 환경변수 기본값은 다음과 같습니다.

```text
MEMBER_SERVICE_URL=http://localhost:8081
WORK_SERVICE_URL=http://localhost:8082
```

상세 절차는 Git 저장소 밖의 `D:\APIM\00.Setting\java-msa-apim-study-guide.md`를 참고합니다.
