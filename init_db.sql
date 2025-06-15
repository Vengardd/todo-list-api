CREATE TABLE tasks (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMPZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_user FOREIGN KEY(user_id) REFERENCES users(id)
);


CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ language 'plpgsql';


CREATE TRIGGER update_tasks_at_column
BEFORE UPDATE ON tasks
FOR EACH ROW
EXECUTE PROCEDURE update_updated_at_column();