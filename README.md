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

Gateway의 루트 주소인 `http://localhost:8080`으로 접속해도 자동으로 `http://localhost:8080/member/login`으로 이동합니다.

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

### Gateway 접속 주소 유지

member-service 경로에는 `PreserveHostHeader`가 적용되어 있고, member-service는 전달된 프록시 헤더를 해석합니다. 따라서 Gateway의 로그인 주소로 접속한 경우 로그인 후에도 주소창은 `http://localhost:8080/member/members`를 유지해야 합니다. 내부 전달 대상인 `8081`은 브라우저 주소에 나타나지 않아야 합니다.

### TODO: Docker 통합 시 서비스 포트 비공개

현재는 STS에서 각 서비스를 직접 실행하므로 개발 PC에서 `http://localhost:8081/member/**`와 `http://localhost:8082/work/**`에 직접 접근할 수 있습니다. Docker 통합 단계에서는 Gateway의 `8080`만 `ports`로 호스트에 공개하고, member-service와 work-service에는 `ports`를 설정하지 않습니다. 필요한 경우 문서 목적의 `expose`만 사용합니다.

```yaml
services:
  gateway:
    ports:
      - "8080:8080"

  member-service:
    expose:
      - "8081"
    # 주의: ports: - "8081:8081"을 추가하지 않는다.

  work-service:
    expose:
      - "8082"
    # 주의: ports: - "8082:8082"를 추가하지 않는다.
```

이때 Gateway의 서비스 주소는 `localhost`가 아니라 Docker Compose 서비스 이름을 사용합니다.

```text
MEMBER_SERVICE_URL=http://member-service:8081
WORK_SERVICE_URL=http://work-service:8082
```

`expose`는 컨테이너 간 통신용 포트를 설명할 뿐 호스트 PC에 포트를 공개하지 않습니다. 같은 Compose 네트워크의 Gateway는 서비스 이름으로 접근할 수 있지만, PC 브라우저에서 `localhost:8081` 또는 `localhost:8082`로는 접속할 수 없게 됩니다. 실제 차단의 핵심은 `expose` 자체가 아니라 member-service와 work-service에 `ports`를 만들지 않는 것입니다.

상세 절차는 Git 저장소 밖의 `D:\APIM\00.Setting\java-msa-apim-study-guide.md`를 참고합니다.
