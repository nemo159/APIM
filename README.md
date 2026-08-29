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
   docker compose up -d member-db
   ```

3. STS Run Configuration에 `DB_PASSWORD`, `APP_ENCRYPTION_KEY`를 등록합니다.
4. `member-service`를 Spring Boot App으로 실행합니다.
5. `http://localhost:8080/login`에 접속합니다.

상세 절차는 Git 저장소 밖의 `D:\APIM\00.Setting\java-msa-apim-study-guide.md`를 참고합니다.
