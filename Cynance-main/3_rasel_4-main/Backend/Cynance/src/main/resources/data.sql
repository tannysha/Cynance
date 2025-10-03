-- INSERT INTO admins (username, password, full_name, active)
-- VALUES ('admin1', 'adminpass123', 'Admin One', true);

DELETE FROM admins WHERE username = 'admin2';
INSERT INTO admins (username, password, full_name, active)
VALUES ('admin2', 'supersecret', 'Admin Two', true);
