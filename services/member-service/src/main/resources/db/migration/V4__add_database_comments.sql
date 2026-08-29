COMMENT ON TABLE members IS '스터디 회원 기본 정보';
COMMENT ON COLUMN members.id IS '회원 식별자';
COMMENT ON COLUMN members.name IS '회원 이름 및 로그인 아이디';
COMMENT ON COLUMN members.phone_encrypted IS 'AES-256-GCM 방식으로 암호화한 핸드폰 번호';
COMMENT ON COLUMN members.phone_hash IS '핸드폰 번호 검색 및 중복 확인용 HMAC-SHA256 해시';
COMMENT ON COLUMN members.password_hash IS 'BCrypt 방식으로 단방향 암호화한 로그인 비밀번호';
COMMENT ON COLUMN members.password_change_required IS '초기 비밀번호 변경 필요 여부';
COMMENT ON COLUMN members.del_yn IS '사용 여부(Y: 사용, N: 미사용)';
COMMENT ON COLUMN members.created_at IS '등록 일시';
COMMENT ON COLUMN members.updated_at IS '최종 수정 일시';
COMMENT ON COLUMN members.created_by IS '등록자 이름';
COMMENT ON COLUMN members.updated_by IS '최종 수정자 이름';

COMMENT ON TABLE roles IS '회원에게 부여할 수 있는 시스템 권한';
COMMENT ON COLUMN roles.id IS '권한 식별자';
COMMENT ON COLUMN roles.code IS '권한 코드(MASTER, ADMIN, MEMBER)';
COMMENT ON COLUMN roles.description IS '권한 설명';

COMMENT ON TABLE member_roles IS '회원과 권한의 매핑 정보';
COMMENT ON COLUMN member_roles.member_id IS '회원 식별자';
COMMENT ON COLUMN member_roles.role_id IS '권한 식별자';
COMMENT ON COLUMN member_roles.created_at IS '권한 매핑 등록 일시';
COMMENT ON COLUMN member_roles.created_by IS '권한 매핑 등록자 이름';
