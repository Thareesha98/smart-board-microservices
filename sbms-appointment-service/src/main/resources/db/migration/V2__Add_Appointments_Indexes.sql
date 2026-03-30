-- Standard Single-Column Indexes
CREATE INDEX idx_appointments_student_id ON appointments(student_id);
CREATE INDEX idx_appointments_owner_id ON appointments(owner_id);
CREATE INDEX idx_appointments_status ON appointments(status);

-- Composite Index for high traffic owner/status queries
CREATE INDEX idx_appointments_owner_status ON appointments(owner_id, status);