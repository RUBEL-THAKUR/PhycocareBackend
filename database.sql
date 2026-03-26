-- ============================================================
-- PsychoCare Database Schema
-- MySQL 8.0+
-- Run this file to create all tables and seed initial data
-- ============================================================

CREATE DATABASE IF NOT EXISTS psychocare_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE psychocare_db;

-- ---- ADMINS ----
CREATE TABLE IF NOT EXISTS admins (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ---- USERS ----
CREATE TABLE IF NOT EXISTS app_users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    email_id VARCHAR(255) UNIQUE,
    mobile_number VARCHAR(15) UNIQUE,
    username VARCHAR(100) UNIQUE,
    password VARCHAR(255),
    date_of_birth DATE,
    gender VARCHAR(20),
    timezone VARCHAR(100),
    profile_image VARCHAR(500),
    referral_code VARCHAR(20) UNIQUE,
    referred_by VARCHAR(20),
    wallet_balance DOUBLE DEFAULT 0.0,
    is_active BOOLEAN DEFAULT TRUE,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ---- THERAPISTS ----
CREATE TABLE IF NOT EXISTS therapists (
    id VARCHAR(36) PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email_id VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    mobile_number VARCHAR(15) NOT NULL UNIQUE,
    cv_file_name VARCHAR(500) NOT NULL,
    profile_image VARCHAR(500),
    is_above_18 BOOLEAN DEFAULT FALSE,
    accepted_terms BOOLEAN DEFAULT FALSE,
    status VARCHAR(20) DEFAULT 'PENDING',
    consultation_fee DOUBLE,
    rating DOUBLE DEFAULT 0.0,
    total_sessions INT DEFAULT 0,
    rejection_reason VARCHAR(500),
    approved_at DATETIME,
    approved_by VARCHAR(100),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ---- THERAPIST PROFILES ----
CREATE TABLE IF NOT EXISTS therapist_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    therapist_id VARCHAR(36) NOT NULL UNIQUE,
    prefix VARCHAR(20),
    first_name VARCHAR(100),
    middle_name VARCHAR(100),
    last_name VARCHAR(100),
    category VARCHAR(100),
    email_id VARCHAR(255),
    user_name VARCHAR(100),
    mobile VARCHAR(15),
    date_of_birth DATE,
    gender VARCHAR(20),
    language VARCHAR(100),
    brief_description TEXT,
    present_address VARCHAR(500),
    present_country VARCHAR(100),
    present_state VARCHAR(100),
    present_city VARCHAR(100),
    present_district VARCHAR(100),
    present_pin_code VARCHAR(20),
    clinic_address VARCHAR(500),
    clinic_country VARCHAR(100),
    clinic_state VARCHAR(100),
    clinic_city VARCHAR(100),
    clinic_district VARCHAR(100),
    clinic_pin_code VARCHAR(20),
    time_zone VARCHAR(100),
    experience INT,
    profile_image_url VARCHAR(500),
    consultation_fee_chat DOUBLE,
    consultation_fee_audio DOUBLE,
    consultation_fee_video DOUBLE,
    FOREIGN KEY (therapist_id) REFERENCES therapists(id) ON DELETE CASCADE
);

-- ---- THERAPIST QUALIFICATIONS ----
CREATE TABLE IF NOT EXISTS therapist_qualifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    therapist_id VARCHAR(36) NOT NULL,
    degree VARCHAR(200) NOT NULL,
    institution VARCHAR(300) NOT NULL,
    passing_year INT NOT NULL,
    specialization VARCHAR(200),
    FOREIGN KEY (therapist_id) REFERENCES therapists(id) ON DELETE CASCADE
);

-- ---- THERAPIST EXPERIENCE ----
CREATE TABLE IF NOT EXISTS therapist_experiences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    therapist_id VARCHAR(36) NOT NULL,
    organization VARCHAR(300) NOT NULL,
    designation VARCHAR(200) NOT NULL,
    from_year INT NOT NULL,
    to_year INT,
    is_current BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (therapist_id) REFERENCES therapists(id) ON DELETE CASCADE
);

-- ---- THERAPIST SPECIALIZATIONS ----
CREATE TABLE IF NOT EXISTS therapist_specializations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    therapist_id VARCHAR(36) NOT NULL,
    specialization_name VARCHAR(200) NOT NULL,
    FOREIGN KEY (therapist_id) REFERENCES therapists(id) ON DELETE CASCADE
);

-- ---- THERAPIST AREAS OF EXPERTISE ----
CREATE TABLE IF NOT EXISTS therapist_areas_of_expertise (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    therapist_id VARCHAR(36) NOT NULL,
    area_name VARCHAR(200) NOT NULL,
    FOREIGN KEY (therapist_id) REFERENCES therapists(id) ON DELETE CASCADE
);

-- ---- THERAPIST AWARDS ----
CREATE TABLE IF NOT EXISTS therapist_awards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    therapist_id VARCHAR(36) NOT NULL,
    award_name VARCHAR(300) NOT NULL,
    awarding_body VARCHAR(300),
    year INT,
    FOREIGN KEY (therapist_id) REFERENCES therapists(id) ON DELETE CASCADE
);

-- ---- THERAPIST PROFESSIONAL MEMBERSHIPS ----
CREATE TABLE IF NOT EXISTS therapist_professional_memberships (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    therapist_id VARCHAR(36) NOT NULL,
    organization_name VARCHAR(300) NOT NULL,
    membership_id VARCHAR(100),
    valid_till VARCHAR(20),
    FOREIGN KEY (therapist_id) REFERENCES therapists(id) ON DELETE CASCADE
);

-- ---- THERAPIST BANK DETAILS ----
CREATE TABLE IF NOT EXISTS therapist_bank_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    therapist_id VARCHAR(36) NOT NULL UNIQUE,
    account_holder_name VARCHAR(200) NOT NULL,
    account_number VARCHAR(50) NOT NULL,
    ifsc_code VARCHAR(20) NOT NULL,
    bank_name VARCHAR(200) NOT NULL,
    branch_name VARCHAR(200),
    upi_id VARCHAR(100),
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (therapist_id) REFERENCES therapists(id) ON DELETE CASCADE
);

-- ---- SESSIONS ----
CREATE TABLE IF NOT EXISTS sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    therapist_id VARCHAR(36) NOT NULL,
    mode VARCHAR(10) NOT NULL,
    status VARCHAR(30) DEFAULT 'UPCOMING',
    scheduled_at DATETIME NOT NULL,
    duration_minutes INT DEFAULT 60,
    amount_paid DOUBLE NOT NULL,
    room_id VARCHAR(100),
    notes TEXT,
    cancellation_reason VARCHAR(500),
    cancelled_at DATETIME,
    completed_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES app_users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (therapist_id) REFERENCES therapists(id) ON DELETE CASCADE
);

-- ---- WALLET TRANSACTIONS ----
CREATE TABLE IF NOT EXISTS wallet_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL,
    amount DOUBLE NOT NULL,
    balance_after DOUBLE NOT NULL,
    description VARCHAR(500),
    reference_id VARCHAR(100),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES app_users(user_id) ON DELETE CASCADE
);

-- ---- REFERRALS ----
CREATE TABLE IF NOT EXISTS referrals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    referrer_user_id BIGINT NOT NULL,
    referred_user_id BIGINT NOT NULL,
    referral_code VARCHAR(20) NOT NULL,
    session_booked BOOLEAN DEFAULT FALSE,
    credit_issued BOOLEAN DEFAULT FALSE,
    credit_amount DOUBLE,
    used_on DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (referrer_user_id) REFERENCES app_users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (referred_user_id) REFERENCES app_users(user_id) ON DELETE CASCADE
);

-- ---- FEEDBACKS ----
CREATE TABLE IF NOT EXISTS feedbacks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    quality_rating INT NOT NULL,
    helpfulness_rating INT NOT NULL,
    clarity_rating INT NOT NULL,
    source VARCHAR(200),
    comment TEXT,
    is_anonymous BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES app_users(user_id) ON DELETE CASCADE
);

-- ---- MESSAGES ----
CREATE TABLE IF NOT EXISTS messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_user_id BIGINT,
    recipient_email VARCHAR(255),
    subject VARCHAR(500) NOT NULL,
    body TEXT NOT NULL,
    folder VARCHAR(20) DEFAULT 'INBOX',
    is_read BOOLEAN DEFAULT FALSE,
    is_starred BOOLEAN DEFAULT FALSE,
    is_important BOOLEAN DEFAULT FALSE,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_user_id) REFERENCES app_users(user_id) ON DELETE CASCADE
);

-- ---- ASSESSMENTS ----
CREATE TABLE IF NOT EXISTS user_assessments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    test_name VARCHAR(200) NOT NULL,
    test_slug VARCHAR(100) NOT NULL,
    score INT,
    result_label VARCHAR(200),
    answers TEXT,
    completed BOOLEAN DEFAULT FALSE,
    taken_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES app_users(user_id) ON DELETE CASCADE
);

-- ---- PRESCRIPTIONS ----
CREATE TABLE IF NOT EXISTS prescriptions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    therapist_id VARCHAR(36) NOT NULL,
    notes TEXT,
    medications TEXT,
    follow_up_date DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES sessions(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES app_users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (therapist_id) REFERENCES therapists(id) ON DELETE CASCADE
);

-- ---- CHAT MESSAGES ----
CREATE TABLE IF NOT EXISTS chat_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id BIGINT NOT NULL,
    sender_id VARCHAR(100) NOT NULL,
    sender_role VARCHAR(20),
    content TEXT NOT NULL,
    message_type VARCHAR(20) DEFAULT 'TEXT',
    sent_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ---- OTP RECORDS ----
CREATE TABLE IF NOT EXISTS otp_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    identifier VARCHAR(255) NOT NULL,
    otp VARCHAR(10) NOT NULL,
    expires_at DATETIME NOT NULL,
    is_used BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_otp_identifier (identifier)
);

-- ---- REWARDS ----
CREATE TABLE IF NOT EXISTS rewards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    promo_code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(500),
    discount_value DOUBLE NOT NULL,
    discount_type VARCHAR(20) DEFAULT 'FLAT',
    partner_name VARCHAR(200),
    terms TEXT,
    valid_till DATE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- SEED DATA
-- ============================================================

-- Admin: admin@psychocare.com / Admin@1234 (BCrypt hash)
INSERT IGNORE INTO admins (id, name, email, password)
VALUES (
    UUID(),
    'PsychoCare Admin',
    'admin@psychocare.com',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTpHxLJMtry'
);

-- Rewards
INSERT IGNORE INTO rewards (promo_code, description, discount_value, discount_type, partner_name, terms, valid_till, is_active)
VALUES (
    'ANGEL2200',
    'Happinetz Reward - Special discount for PsychoCare users',
    200.00,
    'FLAT',
    'Happinetz',
    'Valid for first-time Happinetz users. One-time use per account. Cannot be combined with other offers. Valid till December 2025.',
    DATE_ADD(CURDATE(), INTERVAL 1 YEAR),
    TRUE
);

-- Demo therapists (status APPROVED for testing)
INSERT IGNORE INTO therapists (id, first_name, last_name, email_id, password, mobile_number, cv_file_name, is_above_18, accepted_terms, status, consultation_fee, rating, total_sessions, approved_at, approved_by)
VALUES
(
    'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
    'Sakshi', 'Kochhar',
    'sakshi.kochhar@psychocare.com',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTpHxLJMtry',
    '9876543210',
    'demo_cv_1.pdf',
    TRUE, TRUE, 'APPROVED', 1200.00, 4.7, 145,
    NOW(), 'ADMIN'
),
(
    'b2c3d4e5-f6a7-8901-bcde-f12345678901',
    'Siva', 'Tharini',
    'siva.tharini@psychocare.com',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTpHxLJMtry',
    '9876543211',
    'demo_cv_2.pdf',
    TRUE, TRUE, 'APPROVED', 800.00, 4.6, 98,
    NOW(), 'ADMIN'
),
(
    'c3d4e5f6-a7b8-9012-cdef-123456789012',
    'Sudipta', 'Das',
    'sudipta.das@psychocare.com',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTpHxLJMtry',
    '9876543212',
    'demo_cv_3.pdf',
    TRUE, TRUE, 'APPROVED', 1500.00, 4.5, 67,
    NOW(), 'ADMIN'
),
(
    'd4e5f6a7-b8c9-0123-defa-234567890123',
    'Karuna', 'Singh',
    'karuna.singh@psychocare.com',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTpHxLJMtry',
    '9876543213',
    'demo_cv_4.pdf',
    TRUE, TRUE, 'APPROVED', 3400.00, 4.9, 312,
    NOW(), 'ADMIN'
);

-- Therapist profiles
INSERT IGNORE INTO therapist_profiles (therapist_id, prefix, first_name, last_name, category, brief_description, gender, language, experience, consultation_fee_chat, consultation_fee_audio, consultation_fee_video, present_city, present_state, present_country)
VALUES
(
    'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
    'Dr.', 'Sakshi', 'Kochhar',
    'Clinical Psychologist',
    'Experienced clinical psychologist specializing in anxiety, depression, and trauma. Providing compassionate, evidence-based therapy for over 8 years.',
    'Female', 'English, Hindi', 8,
    1200.00, 1400.00, 1500.00,
    'Delhi', 'Delhi', 'India'
),
(
    'b2c3d4e5-f6a7-8901-bcde-f12345678901',
    'Ms.', 'Siva', 'Tharini',
    'Counseling Psychologist',
    'Specializes in relationship counseling, stress management, and personal growth. Warm and empathetic approach to therapy.',
    'Female', 'English, Tamil', 5,
    800.00, 1000.00, 1200.00,
    'Chennai', 'Tamil Nadu', 'India'
),
(
    'c3d4e5f6-a7b8-9012-cdef-123456789012',
    'Ms.', 'Sudipta', 'Das',
    'Child Psychologist',
    'Child and adolescent mental health specialist. Expert in behavioral therapy, ADHD, and developmental concerns.',
    'Female', 'English, Bengali', 6,
    1500.00, 1700.00, 2000.00,
    'Kolkata', 'West Bengal', 'India'
),
(
    'd4e5f6a7-b8c9-0123-defa-234567890123',
    'Dr.', 'Karuna', 'Singh',
    'Psychiatrist',
    'Senior psychiatrist with over 15 years of experience in mood disorders, OCD, and schizophrenia. Combines medication management with psychotherapy.',
    'Female', 'English, Hindi', 15,
    3400.00, 3800.00, 4000.00,
    'Mumbai', 'Maharashtra', 'India'
);

-- Therapist specializations
INSERT IGNORE INTO therapist_specializations (therapist_id, specialization_name) VALUES
('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'Anxiety Disorders'),
('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'Depression'),
('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'Trauma and PTSD'),
('b2c3d4e5-f6a7-8901-bcde-f12345678901', 'Relationship Counseling'),
('b2c3d4e5-f6a7-8901-bcde-f12345678901', 'Stress Management'),
('c3d4e5f6-a7b8-9012-cdef-123456789012', 'Child Psychology'),
('c3d4e5f6-a7b8-9012-cdef-123456789012', 'ADHD'),
('d4e5f6a7-b8c9-0123-defa-234567890123', 'Mood Disorders'),
('d4e5f6a7-b8c9-0123-defa-234567890123', 'OCD');

-- Demo user: user@psychocare.com / Admin@1234 (same BCrypt hash as admin)
-- NOTE: DataInitializer will reseed with User@1234 if this row does not exist
INSERT IGNORE INTO app_users (first_name, last_name, email_id, username, password, referral_code, wallet_balance, is_active, is_deleted)
VALUES (
    'Demo', 'User',
    'user@psychocare.com',
    'demouser',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTpHxLJMtry',
    '1WNYZOBJA0',
    500.00, TRUE, FALSE
);
