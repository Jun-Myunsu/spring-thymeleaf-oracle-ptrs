-- Oracle용 스키마 (추후 사용)
-- 사용법: Oracle 환경에서 이 스크립트를 실행하여 테이블 생성

DROP TABLE users CASCADE CONSTRAINTS;
DROP SEQUENCE users_seq;

CREATE SEQUENCE users_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

CREATE TABLE users (
    id NUMBER(19) PRIMARY KEY,
    username VARCHAR2(50) NOT NULL,
    email VARCHAR2(100) NOT NULL UNIQUE,
    password VARCHAR2(255) NOT NULL,
    role VARCHAR2(20) DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 트리거 생성 (ID 자동 증가)
CREATE OR REPLACE TRIGGER users_trigger
    BEFORE INSERT ON users
    FOR EACH ROW
BEGIN
    IF :NEW.id IS NULL THEN
        :NEW.id := users_seq.NEXTVAL;
    END IF;
END;

-- 초기 데이터
INSERT INTO users (id, username, email, password, role, created_at, updated_at) VALUES
(users_seq.NEXTVAL, '관리자', 'admin@posco.com', 'YWRtaW4xMjM=', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO users (id, username, email, password, role, created_at, updated_at) VALUES
(users_seq.NEXTVAL, '사용자1', 'user1@posco.com', 'dXNlcjEyMw==', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO users (id, username, email, password, role, created_at, updated_at) VALUES
(users_seq.NEXTVAL, '사용자2', 'user2@posco.com', 'dXNlcjEyMw==', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

COMMIT;