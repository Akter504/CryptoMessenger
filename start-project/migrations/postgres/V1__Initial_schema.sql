-- Users
CREATE TABLE IF NOT EXISTS Users
(
    id uuid PRIMARY KEY,
    email varchar(40) NOT NULL UNIQUE,
    login varchar(20) NOT NULL UNIQUE,
    password_hash varchar(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_login ON users(login);

-- System_Roles
CREATE TABLE IF NOT EXISTS System_Roles (
    id bigserial PRIMARY KEY,
    name_system_role varchar(10) NOT NULL,
    user_id uuid NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE,
    CONSTRAINT unique_user_role UNIQUE (user_id, name_system_role)
);

-- Rooms
CREATE TABLE IF NOT EXISTS Rooms
(
    id uuid PRIMARY KEY,
    user_first_id uuid NOT NULL,
    user_second_id uuid NOT NULL,
    crypto_algorithm varchar(20) NOT NULL,
    is_active boolean,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_first_id) REFERENCES Users,
    FOREIGN KEY (user_second_id) REFERENCES Users,
    CONSTRAINT not_equal CHECK (user_first_id != user_second_id),
    UNIQUE(user_first_id, user_second_id)
);

--AUTO INJECT ROLE 'USER'
CREATE OR REPLACE FUNCTION assign_default_role()
    RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO System_Roles (name_system_role, user_id)
    VALUES ('USER', NEW.id);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER after_user_insert
    AFTER INSERT ON Users
    FOR EACH ROW
EXECUTE FUNCTION assign_default_role();
--