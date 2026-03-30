CREATE TABLE appointments (
    id BIGINT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    owner_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    -- other columns...
);