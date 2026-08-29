package com.study.apim.memberservice.tools;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.study.apim.memberservice.security.CryptoService;
import com.study.apim.memberservice.service.MemberService;

/**
 * 최초 MASTER 계정을 DB에 직접 넣기 위한 SQL 생성 도구.
 * 실행 인수: 이름, 핸드폰번호
 * 환경 변수: APP_ENCRYPTION_KEY
 */
public final class MasterInsertSqlGenerator {
    private MasterInsertSqlGenerator() {}

    public static void main(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("사용법: MasterInsertSqlGenerator <이름> <핸드폰번호>");
        }
        String key = System.getenv("APP_ENCRYPTION_KEY");
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("APP_ENCRYPTION_KEY 환경 변수가 필요합니다.");
        }
        String name = escape(args[0].trim());
        String phone = MemberService.normalizePhone(args[1]);
        CryptoService crypto = new CryptoService(key);
        String encryptedPhone = escape(crypto.encrypt(phone));
        String phoneHash = crypto.hashForSearch(phone);
        String passwordHash = escape(new BCryptPasswordEncoder().encode(phone));

        System.out.printf("""
            WITH inserted_member AS (
                INSERT INTO members (
                    name, phone_encrypted, phone_hash, password_hash,
                    password_change_required, del_yn,
                    created_at, updated_at, created_by, updated_by
                ) VALUES (
                    '%s', '%s', '%s', '%s',
                    TRUE, 'Y', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'DB_DIRECT', 'DB_DIRECT'
                )
                RETURNING id
            )
            INSERT INTO member_roles (member_id, role_id, created_by)
            SELECT inserted_member.id, roles.id, 'DB_DIRECT'
            FROM inserted_member
            JOIN roles ON roles.code = 'MASTER';
            %n""", name, encryptedPhone, phoneHash, passwordHash);
    }

    private static String escape(String value) {
        return value.replace("'", "''");
    }
}
