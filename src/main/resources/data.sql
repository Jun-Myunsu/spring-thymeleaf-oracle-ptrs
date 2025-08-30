-- src/main/resources/data.sql
-- BCrypt 해시: admin123, user123
INSERT INTO users (username, email, password, role) VALUES
('admin', 'admin@posco.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ADMIN'),
('user1', 'user1@posco.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'USER');

-- Level 1: 포스코
INSERT INTO categories (id, name, level, parent_id, sort_order) VALUES
('posco', '포스코', 1, NULL, 1),
('samsung', '삼성', 1, NULL, 2),
('lg', 'LG', 1, NULL, 3);

-- Level 2: 기술부분
INSERT INTO categories (id, name, level, parent_id, sort_order) VALUES
('posco-tech', '기술부분', 2, 'posco', 1),
('posco-steel', '제철부분', 2, 'posco', 2),
('samsung-elec', '전자부문', 2, 'samsung', 1),
('lg-chem', '화학부문', 2, 'lg', 1);

-- Level 3: 자동차
INSERT INTO categories (id, name, level, parent_id, sort_order) VALUES
('posco-tech-auto', '자동차', 3, 'posco-tech', 1),
('posco-tech-ship', '조선', 3, 'posco-tech', 2),
('samsung-elec-mobile', '모바일', 3, 'samsung-elec', 1),
('lg-chem-battery', '배터리', 3, 'lg-chem', 1);

-- Level 4: 기가스틸
INSERT INTO categories (id, name, level, parent_id, sort_order) VALUES
('posco-tech-auto-giga', '기가스틸', 4, 'posco-tech-auto', 1),
('posco-tech-auto-poss', '포스맥', 4, 'posco-tech-auto', 2),
('samsung-elec-mobile-galaxy', '갤럭시', 4, 'samsung-elec-mobile', 1),
('lg-chem-battery-ncm', 'NCM', 4, 'lg-chem-battery', 1);

-- Level 5: Mart
INSERT INTO categories (id, name, level, parent_id, sort_order) VALUES
('posco-tech-auto-giga-mart', 'Mart', 5, 'posco-tech-auto-giga', 1),
('posco-tech-auto-giga-shop', 'Shop', 5, 'posco-tech-auto-giga', 2),
('samsung-elec-mobile-galaxy-s', 'S시리즈', 5, 'samsung-elec-mobile-galaxy', 1),
('lg-chem-battery-ncm-811', 'NCM811', 5, 'lg-chem-battery-ncm', 1);