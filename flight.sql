DROP DATABASE IF EXISTS flightbooking;
CREATE DATABASE IF NOT EXISTS flightbooking;
USE flightbooking;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    last_name VARCHAR(60) CHARACTER SET utf8mb4 NOT NULL,
    first_name VARCHAR(60) CHARACTER SET utf8mb4 NOT NULL,
    gender VARCHAR(6) NOT NULL,
    email VARCHAR(80) NOT NULL,
    date_of_birth DATE NOT NULL,
    phone_number VARCHAR(15) NOT NULL,
    address VARCHAR(100) CHARACTER SET utf8mb4,
    passport_number VARCHAR(20),
    citizen_id VARCHAR(15),
    username VARCHAR(50),
    password VARCHAR(500),
    enabled BOOLEAN
);

CREATE TABLE coflight_members (
    user_id BIGINT PRIMARY KEY,
    last_name VARCHAR(60) CHARACTER SET utf8mb4 NOT NULL,
    first_name VARCHAR(60) CHARACTER SET utf8mb4 NOT NULL,
    gender VARCHAR(6) NOT NULL,
    email VARCHAR(80) NOT NULL,
    date_of_birth DATE NOT NULL,
    phone_number NUMERIC(12) NOT NULL,
    passport_number VARCHAR(20),
    citizen_id VARCHAR(15),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE flights (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    departure VARCHAR(255) NOT NULL,
    destination VARCHAR(255) NOT NULL,
    departure_time DATETIME NOT NULL,
    return_time DATETIME,
    available_seats INT NOT NULL,
    price DOUBLE NOT NULL,
    airline VARCHAR(255) NOT NULL,
    flight_type VARCHAR(255) NOT NULL
);

CREATE TABLE ticket_classes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    free_seat_class ENUM('PREMIUM', 'STANDARD', 'EXTRA_LEGROOM', 'FRONT_ROW'),
    free_meal_quantity INT,
    free_baggage_weight DOUBLE,
    base_price_multiplier DOUBLE
);

CREATE TABLE bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reservation_code VARCHAR(255) UNIQUE NOT NULL,
    booking_date DATETIME NOT NULL,
    passenger_count INT NOT NULL,
    total_price DOUBLE NOT NULL,
    additional_cost DOUBLE NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'CANCELLED', 'PAID', 'NOTCHECKEDIN', 'CHECKEDIN', 'FLIGHTDONE'),
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE booking_flights (
    booking_id BIGINT NOT NULL,
    flight_id BIGINT NOT NULL,
    PRIMARY KEY (booking_id, flight_id),
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    FOREIGN KEY (flight_id) REFERENCES flights(id) ON DELETE CASCADE
);

CREATE TABLE seats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    flight_id BIGINT NOT NULL,
    seat_number VARCHAR(10) NOT NULL,
    row_id INT NOT NULL,
    column_letter CHAR(1) NOT NULL,
    booking_id BIGINT,
    seat_class ENUM('PREMIUM', 'STANDARD', 'EXTRA_LEGROOM', 'FRONT_ROW'),
    status ENUM('AVAILABLE', 'OCCUPIED', 'SELECTING') DEFAULT 'AVAILABLE',
    price DOUBLE NOT NULL,
    FOREIGN KEY (flight_id) REFERENCES flights(id) ON DELETE CASCADE,
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE SET NULL
);

CREATE TABLE booking_seat (
    booking_id BIGINT NOT NULL,
    seat_id BIGINT NOT NULL,
    PRIMARY KEY (booking_id, seat_id),
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    FOREIGN KEY (seat_id) REFERENCES seats(id) ON DELETE CASCADE
);

CREATE TABLE meals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DOUBLE NOT NULL,
    category VARCHAR(255),
    image VARCHAR(255)
);

CREATE TABLE booking_meal (
   id BIGINT AUTO_INCREMENT PRIMARY KEY,
   booking_id BIGINT,
   meal_id BIGINT,
   flight_id BIGINT,
   quantity INT NOT NULL DEFAULT 1,
   FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
   FOREIGN KEY (meal_id) REFERENCES meals(id) ON DELETE CASCADE);

CREATE TABLE baggage (
   id BIGINT AUTO_INCREMENT PRIMARY KEY,
   weight DOUBLE NOT NULL,
   price_per_kg DOUBLE NOT NULL,
   category VARCHAR(255),
   image VARCHAR(255),
   booking_id BIGINT,
   flight_id BIGINT,
   FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE SET NULL,
   FOREIGN KEY (flight_id) REFERENCES flights(id) ON DELETE SET NULL
);

CREATE TABLE booking_baggage (
   id BIGINT AUTO_INCREMENT PRIMARY KEY,
   booking_id BIGINT,
   baggage_id BIGINT,
   flight_id BIGINT,
   quantity INT NOT NULL DEFAULT 1,
   FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
   FOREIGN KEY (baggage_id) REFERENCES baggage(id) ON DELETE CASCADE);

CREATE TABLE faq_category (
   id INT AUTO_INCREMENT PRIMARY KEY,
   name VARCHAR(255) NOT NULL
);

CREATE TABLE faq_question (
   id BIGINT AUTO_INCREMENT PRIMARY KEY,
   category_id INT NOT NULL,
   question VARCHAR(255) NOT NULL,
   answer TEXT NOT NULL,
   FOREIGN KEY (category_id) REFERENCES faq_category(id) ON DELETE CASCADE
);

CREATE TABLE payments (
   id BIGINT AUTO_INCREMENT PRIMARY KEY,
   gender VARCHAR(10),
   last_name VARCHAR(255),
   first_name VARCHAR(255),
   dob DATE,
   phone VARCHAR(20),
   email VARCHAR(255),
   identity VARCHAR(255),
   current_location VARCHAR(255),
   payment_status VARCHAR(50),
   reservation_code VARCHAR(255)
);

-- ONE_WAY: Hà Nội → The other cities(ngày 05/04/2025, nhiều khung giờ)
INSERT INTO flights (departure, destination, departure_time, return_time, available_seats, price, airline, flight_type)
VALUES
    -- Early morning
    ('Hà Nội', 'TP. Hồ Chí Minh', '2025-04-05 06:00:00', NULL, 50, 1000000, 'Vietjet Air', 'ONE_WAY'),
    ('Hà Nội', 'Thanh Hóa', '2025-04-05 07:00:00', NULL, 40, 800000, 'Vietjet Air', 'ONE_WAY'),
    ('Hà Nội', 'Đà Nẵng', '2025-04-05 07:30:00', NULL, 45, 900000, 'Vietjet Air', 'ONE_WAY'),
    ('Hà Nội', 'Nha Trang', '2025-04-05 06:30:00', NULL, 35, 1100000, 'Vietjet Air', 'ONE_WAY'),
    -- Morning
    ('Hà Nội', 'TP. Hồ Chí Minh', '2025-04-05 09:00:00', NULL, 50, 1300000, 'Vietjet Air', 'ONE_WAY'),
    ('Hà Nội', 'Thanh Hóa', '2025-04-05 10:00:00', NULL, 40, 1000000, 'Vietjet Air', 'ONE_WAY'),
    ('Hà Nội', 'Đà Nẵng', '2025-04-05 11:00:00', NULL, 45, 1200000, 'Vietjet Air', 'ONE_WAY'),
    ('Hà Nội', 'Nha Trang', '2025-04-05 08:30:00', NULL, 35, 1400000, 'Vietjet Air', 'ONE_WAY'),
    -- Afternoon
    ('Hà Nội', 'TP. Hồ Chí Minh', '2025-04-05 14:00:00', NULL, 50, 1600000, 'Vietjet Air', 'ONE_WAY'),
    ('Hà Nội', 'Thanh Hóa', '2025-04-05 15:00:00', NULL, 40, 1200000, 'Vietjet Air', 'ONE_WAY'),
    ('Hà Nội', 'Đà Nẵng', '2025-04-05 16:00:00', NULL, 45, 1500000, 'Vietjet Air', 'ONE_WAY'),
    ('Hà Nội', 'Nha Trang', '2025-04-05 13:30:00', NULL, 35, 1700000, 'Vietjet Air', 'ONE_WAY'),
    -- Night
    ('Hà Nội', 'TP. Hồ Chí Minh', '2025-04-05 18:00:00', NULL, 50, 1800000, 'Vietjet Air', 'ONE_WAY'),
    ('Hà Nội', 'Thanh Hóa', '2025-04-05 19:00:00', NULL, 40, 1400000, 'Vietjet Air', 'ONE_WAY'),
    ('Hà Nội', 'Đà Nẵng', '2025-04-05 20:00:00', NULL, 45, 1700000, 'Vietjet Air', 'ONE_WAY'),
    ('Hà Nội', 'Nha Trang', '2025-04-05 21:00:00', NULL, 35, 1900000, 'Vietjet Air', 'ONE_WAY');

-- ONE_WAY: TP. Hồ Chí Minh → The other cities (ngày 05/04/2025, nhiều khung giờ)
INSERT INTO flights (departure, destination, departure_time, return_time, available_seats, price, airline, flight_type)
VALUES
    -- Eary morning
    ('TP. Hồ Chí Minh', 'Hà Nội', '2025-04-05 06:00:00', NULL, 50, 1100000, 'Vietjet Air', 'ONE_WAY'),
    ('TP. Hồ Chí Minh', 'Thanh Hóa', '2025-04-05 07:00:00', NULL, 40, 900000, 'Vietjet Air', 'ONE_WAY'),
    ('TP. Hồ Chí Minh', 'Đà Nẵng', '2025-04-05 07:30:00', NULL, 45, 1000000, 'Vietjet Air', 'ONE_WAY'),
    ('TP. Hồ Chí Minh', 'Nha Trang', '2025-04-05 06:30:00', NULL, 35, 800000, 'Vietjet Air', 'ONE_WAY'),
    -- Buổi sáng
    ('TP. Hồ Chí Minh', 'Hà Nội', '2025-04-05 09:00:00', NULL, 50, 1400000, 'Vietjet Air', 'ONE_WAY'),
    ('TP. Hồ Chí Minh', 'Thanh Hóa', '2025-04-05 10:00:00', NULL, 40, 1100000, 'Vietjet Air', 'ONE_WAY'),
    ('TP. Hồ Chí Minh', 'Đà Nẵng', '2025-04-05 11:00:00', NULL, 45, 1300000, 'Vietjet Air', 'ONE_WAY'),
    ('TP. Hồ Chí Minh', 'Nha Trang', '2025-04-05 08:30:00', NULL, 35, 1000000, 'Vietjet Air', 'ONE_WAY'),
    -- Buổi chiều
    ('TP. Hồ Chí Minh', 'Hà Nội', '2025-04-05 14:00:00', NULL, 50, 1700000, 'Vietjet Air', 'ONE_WAY'),
    ('TP. Hồ Chí Minh', 'Thanh Hóa', '2025-04-05 15:00:00', NULL, 40, 1300000, 'Vietjet Air', 'ONE_WAY'),
    ('TP. Hồ Chí Minh', 'Đà Nẵng', '2025-04-05 16:00:00', NULL, 45, 1600000, 'Vietjet Air', 'ONE_WAY'),
    ('TP. Hồ Chí Minh', 'Nha Trang', '2025-04-05 13:30:00', NULL, 35, 1200000, 'Vietjet Air', 'ONE_WAY'),
    -- Buổi tối
    ('TP. Hồ Chí Minh', 'Hà Nội', '2025-04-05 18:00:00', NULL, 50, 1900000, 'Vietjet Air', 'ONE_WAY'),
    ('TP. Hồ Chí Minh', 'Thanh Hóa', '2025-04-05 19:00:00', NULL, 40, 1500000, 'Vietjet Air', 'ONE_WAY'),
    ('TP. Hồ Chí Minh', 'Đà Nẵng', '2025-04-05 20:00:00', NULL, 45, 1800000, 'Vietjet Air', 'ONE_WAY'),
    ('TP. Hồ Chí Minh', 'Nha Trang', '2025-04-05 21:00:00', NULL, 35, 1400000, 'Vietjet Air', 'ONE_WAY');

-- ONE_WAY: Thanh Hóa → Các thành phố khác (ngày 05/04/2025, nhiều khung giờ)
INSERT INTO flights (departure, destination, departure_time, return_time, available_seats, price, airline, flight_type)
VALUES
    -- Sáng sớm
    ('Thanh Hóa', 'Hà Nội', '2025-04-05 06:00:00', NULL, 50, 800000, 'Vietjet Air', 'ONE_WAY'),
    ('Thanh Hóa', 'TP. Hồ Chí Minh', '2025-04-05 07:00:00', NULL, 40, 900000, 'Vietjet Air', 'ONE_WAY'),
    ('Thanh Hóa', 'Đà Nẵng', '2025-04-05 07:30:00', NULL, 45, 850000, 'Vietjet Air', 'ONE_WAY'),
    ('Thanh Hóa', 'Nha Trang', '2025-04-05 06:30:00', NULL, 35, 1000000, 'Vietjet Air', 'ONE_WAY'),
    -- Buổi sáng
    ('Thanh Hóa', 'Hà Nội', '2025-04-05 09:00:00', NULL, 50, 1000000, 'Vietjet Air', 'ONE_WAY'),
    ('Thanh Hóa', 'TP. Hồ Chí Minh', '2025-04-05 10:00:00', NULL, 40, 1100000, 'Vietjet Air', 'ONE_WAY'),
    ('Thanh Hóa', 'Đà Nẵng', '2025-04-05 11:00:00', NULL, 45, 1050000, 'Vietjet Air', 'ONE_WAY'),
    ('Thanh Hóa', 'Nha Trang', '2025-04-05 08:30:00', NULL, 35, 1200000, 'Vietjet Air', 'ONE_WAY'),
    -- Buổi chiều
    ('Thanh Hóa', 'Hà Nội', '2025-04-05 14:00:00', NULL, 50, 1200000, 'Vietjet Air', 'ONE_WAY'),
    ('Thanh Hóa', 'TP. Hồ Chí Minh', '2025-04-05 15:00:00', NULL, 40, 1300000, 'Vietjet Air', 'ONE_WAY'),
    ('Thanh Hóa', 'Đà Nẵng', '2025-04-05 16:00:00', NULL, 45, 1250000, 'Vietjet Air', 'ONE_WAY'),
    ('Thanh Hóa', 'Nha Trang', '2025-04-05 13:30:00', NULL, 35, 1400000, 'Vietjet Air', 'ONE_WAY'),
    -- Buổi tối
    ('Thanh Hóa', 'Hà Nội', '2025-04-05 18:00:00', NULL, 50, 1400000, 'Vietjet Air', 'ONE_WAY'),
    ('Thanh Hóa', 'TP. Hồ Chí Minh', '2025-04-05 19:00:00', NULL, 40, 1500000, 'Vietjet Air', 'ONE_WAY'),
    ('Thanh Hóa', 'Đà Nẵng', '2025-04-05 20:00:00', NULL, 45, 1450000, 'Vietjet Air', 'ONE_WAY'),
    ('Thanh Hóa', 'Nha Trang', '2025-04-05 21:00:00', NULL, 35, 1600000, 'Vietjet Air', 'ONE_WAY');

-- ONE_WAY: Đà Nẵng → Các thành phố khác (ngày 05/04/2025, nhiều khung giờ)
INSERT INTO flights (departure, destination, departure_time, return_time, available_seats, price, airline, flight_type)
VALUES
    -- Sáng sớm
    ('Đà Nẵng', 'Hà Nội', '2025-04-05 06:00:00', NULL, 50, 900000, 'Vietjet Air', 'ONE_WAY'),
    ('Đà Nẵng', 'TP. Hồ Chí Minh', '2025-04-05 07:00:00', NULL, 40, 1000000, 'Vietjet Air', 'ONE_WAY'),
    ('Đà Nẵng', 'Thanh Hóa', '2025-04-05 07:30:00', NULL, 45, 850000, 'Vietjet Air', 'ONE_WAY'),
    ('Đà Nẵng', 'Nha Trang', '2025-04-05 06:30:00', NULL, 35, 800000, 'Vietjet Air', 'ONE_WAY'),
    -- Buổi sáng
    ('Đà Nẵng', 'Hà Nội', '2025-04-05 09:00:00', NULL, 50, 1200000, 'Vietjet Air', 'ONE_WAY'),
    ('Đà Nẵng', 'TP. Hồ Chí Minh', '2025-04-05 10:00:00', NULL, 40, 1300000, 'Vietjet Air', 'ONE_WAY'),
    ('Đà Nẵng', 'Thanh Hóa', '2025-04-05 11:00:00', NULL, 45, 1050000, 'Vietjet Air', 'ONE_WAY'),
    ('Đà Nẵng', 'Nha Trang', '2025-04-05 08:30:00', NULL, 35, 1000000, 'Vietjet Air', 'ONE_WAY'),
    -- Buổi chiều
    ('Đà Nẵng', 'Hà Nội', '2025-04-05 14:00:00', NULL, 50, 1500000, 'Vietjet Air', 'ONE_WAY'),
    ('Đà Nẵng', 'TP. Hồ Chí Minh', '2025-04-05 15:00:00', NULL, 40, 1600000, 'Vietjet Air', 'ONE_WAY'),
    ('Đà Nẵng', 'Thanh Hóa', '2025-04-05 16:00:00', NULL, 45, 1250000, 'Vietjet Air', 'ONE_WAY'),
    ('Đà Nẵng', 'Nha Trang', '2025-04-05 13:30:00', NULL, 35, 1200000, 'Vietjet Air', 'ONE_WAY'),
    -- Buổi tối
    ('Đà Nẵng', 'Hà Nội', '2025-04-05 18:00:00', NULL, 50, 1700000, 'Vietjet Air', 'ONE_WAY'),
    ('Đà Nẵng', 'TP. Hồ Chí Minh', '2025-04-05 19:00:00', NULL, 40, 1800000, 'Vietjet Air', 'ONE_WAY'),
    ('Đà Nẵng', 'Thanh Hóa', '2025-04-05 20:00:00', NULL, 45, 1450000, 'Vietjet Air', 'ONE_WAY'),
    ('Đà Nẵng', 'Nha Trang', '2025-04-05 21:00:00', NULL, 35, 1400000, 'Vietjet Air', 'ONE_WAY');

-- ONE_WAY: Nha Trang → Các thành phố khác (ngày 05/04/2025, nhiều khung giờ)
INSERT INTO flights (departure, destination, departure_time, return_time, available_seats, price, airline, flight_type)
VALUES
    -- Sáng sớm
    ('Nha Trang', 'Hà Nội', '2025-04-05 06:00:00', NULL, 50, 1100000, 'Vietjet Air', 'ONE_WAY'),
    ('Nha Trang', 'TP. Hồ Chí Minh', '2025-04-05 07:00:00', NULL, 40, 800000, 'Vietjet Air', 'ONE_WAY'),
    ('Nha Trang', 'Thanh Hóa', '2025-04-05 07:30:00', NULL, 45, 1000000, 'Vietjet Air', 'ONE_WAY'),
    ('Nha Trang', 'Đà Nẵng', '2025-04-05 06:30:00', NULL, 35, 800000, 'Vietjet Air', 'ONE_WAY'),
    -- Buổi sáng
    ('Nha Trang', 'Hà Nội', '2025-04-05 09:00:00', NULL, 50, 1400000, 'Vietjet Air', 'ONE_WAY'),
    ('Nha Trang', 'TP. Hồ Chí Minh', '2025-04-05 10:00:00', NULL, 40, 1000000, 'Vietjet Air', 'ONE_WAY'),
    ('Nha Trang', 'Thanh Hóa', '2025-04-05 11:00:00', NULL, 45, 1200000, 'Vietjet Air', 'ONE_WAY'),
    ('Nha Trang', 'Đà Nẵng', '2025-04-05 08:30:00', NULL, 35, 1000000, 'Vietjet Air', 'ONE_WAY'),
    -- Buổi chiều
    ('Nha Trang', 'Hà Nội', '2025-04-05 14:00:00', NULL, 50, 1700000, 'Vietjet Air', 'ONE_WAY'),
    ('Nha Trang', 'TP. Hồ Chí Minh', '2025-04-05 15:00:00', NULL, 40, 1200000, 'Vietjet Air', 'ONE_WAY'),
    ('Nha Trang', 'Thanh Hóa', '2025-04-05 16:00:00', NULL, 45, 1400000, 'Vietjet Air', 'ONE_WAY'),
    ('Nha Trang', 'Đà Nẵng', '2025-04-05 13:30:00', NULL, 35, 1200000, 'Vietjet Air', 'ONE_WAY'),
    -- Buổi tối
    ('Nha Trang', 'Hà Nội', '2025-04-05 18:00:00', NULL, 50, 1900000, 'Vietjet Air', 'ONE_WAY'),
    ('Nha Trang', 'TP. Hồ Chí Minh', '2025-04-05 19:00:00', NULL, 40, 1400000, 'Vietjet Air', 'ONE_WAY'),
    ('Nha Trang', 'Thanh Hóa', '2025-04-05 20:00:00', NULL, 45, 1600000, 'Vietjet Air', 'ONE_WAY'),
    ('Nha Trang', 'Đà Nẵng', '2025-04-05 21:00:00', NULL, 35, 1400000, 'Vietjet Air', 'ONE_WAY');

-- ROUND_TRIP: Hà Nội ↔ Các thành phố khác (ngày 05/04/2025, nhiều khung giờ)
INSERT INTO flights (departure, destination, departure_time, return_time, available_seats, price, airline, flight_type)
VALUES
    -- Sáng sớm
    ('Hà Nội', 'TP. Hồ Chí Minh', '2025-04-05 06:00:00', '2025-04-08 08:00:00', 50, 1800000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Hà Nội', 'Thanh Hóa', '2025-04-05 07:00:00', '2025-04-08 09:00:00', 40, 1500000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Hà Nội', 'Đà Nẵng', '2025-04-05 07:30:00', '2025-04-08 09:30:00', 45, 1600000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Hà Nội', 'Nha Trang', '2025-04-05 06:30:00', '2025-04-08 08:30:00', 35, 2000000, 'Vietjet Air', 'ROUND_TRIP'),
    -- Buổi sáng
    ('Hà Nội', 'TP. Hồ Chí Minh', '2025-04-05 09:00:00', '2025-04-08 11:00:00', 50, 2200000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Hà Nội', 'Thanh Hóa', '2025-04-05 10:00:00', '2025-04-08 12:00:00', 40, 1800000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Hà Nội', 'Đà Nẵng', '2025-04-05 11:00:00', '2025-04-08 13:00:00', 45, 2000000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Hà Nội', 'Nha Trang', '2025-04-05 08:30:00', '2025-04-08 10:30:00', 35, 2400000, 'Vietjet Air', 'ROUND_TRIP'),
    -- Buổi chiều
    ('Hà Nội', 'TP. Hồ Chí Minh', '2025-04-05 14:00:00', '2025-04-08 16:00:00', 50, 2600000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Hà Nội', 'Thanh Hóa', '2025-04-05 15:00:00', '2025-04-08 17:00:00', 40, 2100000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Hà Nội', 'Đà Nẵng', '2025-04-05 16:00:00', '2025-04-08 18:00:00', 45, 2400000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Hà Nội', 'Nha Trang', '2025-04-05 13:30:00', '2025-04-08 15:30:00', 35, 2800000, 'Vietjet Air', 'ROUND_TRIP'),
    -- Buổi tối
    ('Hà Nội', 'TP. Hồ Chí Minh', '2025-04-05 18:00:00', '2025-04-08 20:00:00', 50, 3000000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Hà Nội', 'Thanh Hóa', '2025-04-05 19:00:00', '2025-04-08 21:00:00', 40, 2400000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Hà Nội', 'Đà Nẵng', '2025-04-05 20:00:00', '2025-04-08 22:00:00', 45, 2700000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Hà Nội', 'Nha Trang', '2025-04-05 21:00:00', '2025-04-08 23:00:00', 35, 3100000, 'Vietjet Air', 'ROUND_TRIP');

-- ROUND_TRIP: TP. Hồ Chí Minh ↔ Các thành phố khác (ngày 05/04/2025, nhiều khung giờ)
INSERT INTO flights (departure, destination, departure_time, return_time, available_seats, price, airline, flight_type)
VALUES
    -- Sáng sớm
    ('TP. Hồ Chí Minh', 'Hà Nội', '2025-04-05 06:00:00', '2025-04-08 08:00:00', 50, 1900000, 'Vietjet Air', 'ROUND_TRIP'),
    ('TP. Hồ Chí Minh', 'Thanh Hóa', '2025-04-05 07:00:00', '2025-04-08 09:00:00', 40, 1600000, 'Vietjet Air', 'ROUND_TRIP'),
    ('TP. Hồ Chí Minh', 'Đà Nẵng', '2025-04-05 07:30:00', '2025-04-08 09:30:00', 45, 1700000, 'Vietjet Air', 'ROUND_TRIP'),
    ('TP. Hồ Chí Minh', 'Nha Trang', '2025-04-05 06:30:00', '2025-04-08 08:30:00', 35, 1500000, 'Vietjet Air', 'ROUND_TRIP'),
    -- Buổi sáng
    ('TP. Hồ Chí Minh', 'Hà Nội', '2025-04-05 09:00:00', '2025-04-08 11:00:00', 50, 2300000, 'Vietjet Air', 'ROUND_TRIP'),
    ('TP. Hồ Chí Minh', 'Thanh Hóa', '2025-04-05 10:00:00', '2025-04-08 12:00:00', 40, 1900000, 'Vietjet Air', 'ROUND_TRIP'),
    ('TP. Hồ Chí Minh', 'Đà Nẵng', '2025-04-05 11:00:00', '2025-04-08 13:00:00', 45, 2100000, 'Vietjet Air', 'ROUND_TRIP'),
    ('TP. Hồ Chí Minh', 'Nha Trang', '2025-04-05 08:30:00', '2025-04-08 10:30:00', 35, 1800000, 'Vietjet Air', 'ROUND_TRIP'),
    -- Buổi chiều
    ('TP. Hồ Chí Minh', 'Hà Nội', '2025-04-05 14:00:00', '2025-04-08 16:00:00', 50, 2700000, 'Vietjet Air', 'ROUND_TRIP'),
    ('TP. Hồ Chí Minh', 'Thanh Hóa', '2025-04-05 15:00:00', '2025-04-08 17:00:00', 40, 2200000, 'Vietjet Air', 'ROUND_TRIP'),
    ('TP. Hồ Chí Minh', 'Đà Nẵng', '2025-04-05 16:00:00', '2025-04-08 18:00:00', 45, 2500000, 'Vietjet Air', 'ROUND_TRIP'),
    ('TP. Hồ Chí Minh', 'Nha Trang', '2025-04-05 13:30:00', '2025-04-08 15:30:00', 35, 2100000, 'Vietjet Air', 'ROUND_TRIP'),
    -- Buổi tối
    ('TP. Hồ Chí Minh', 'Hà Nội', '2025-04-05 18:00:00', '2025-04-08 20:00:00', 50, 3100000, 'Vietjet Air', 'ROUND_TRIP'),
    ('TP. Hồ Chí Minh', 'Thanh Hóa', '2025-04-05 19:00:00', '2025-04-08 21:00:00', 40, 2500000, 'Vietjet Air', 'ROUND_TRIP'),
    ('TP. Hồ Chí Minh', 'Đà Nẵng', '2025-04-05 20:00:00', '2025-04-08 22:00:00', 45, 2800000, 'Vietjet Air', 'ROUND_TRIP'),
    ('TP. Hồ Chí Minh', 'Nha Trang', '2025-04-05 21:00:00', '2025-04-08 23:00:00', 35, 2300000, 'Vietjet Air', 'ROUND_TRIP');

-- ROUND_TRIP: Thanh Hóa ↔ Các thành phố khác (ngày 05/04/2025, nhiều khung giờ)
INSERT INTO flights (departure, destination, departure_time, return_time, available_seats, price, airline, flight_type)
VALUES
    -- Sáng sớm
    ('Thanh Hóa', 'Hà Nội', '2025-04-05 06:00:00', '2025-04-08 08:00:00', 50, 1500000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Thanh Hóa', 'TP. Hồ Chí Minh', '2025-04-05 07:00:00', '2025-04-08 09:00:00', 40, 1600000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Thanh Hóa', 'Đà Nẵng', '2025-04-05 07:30:00', '2025-04-08 09:30:00', 45, 1550000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Thanh Hóa', 'Nha Trang', '2025-04-05 06:30:00', '2025-04-08 08:30:00', 35, 1800000, 'Vietjet Air', 'ROUND_TRIP'),
    -- Buổi sáng
    ('Thanh Hóa', 'Hà Nội', '2025-04-05 09:00:00', '2025-04-08 11:00:00', 50, 1800000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Thanh Hóa', 'TP. Hồ Chí Minh', '2025-04-05 10:00:00', '2025-04-08 12:00:00', 40, 1900000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Thanh Hóa', 'Đà Nẵng', '2025-04-05 11:00:00', '2025-04-08 13:00:00', 45, 1850000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Thanh Hóa', 'Nha Trang', '2025-04-05 08:30:00', '2025-04-08 10:30:00', 35, 2100000, 'Vietjet Air', 'ROUND_TRIP'),
    -- Buổi chiều
    ('Thanh Hóa', 'Hà Nội', '2025-04-05 14:00:00', '2025-04-08 16:00:00', 50, 2100000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Thanh Hóa', 'TP. Hồ Chí Minh', '2025-04-05 15:00:00', '2025-04-08 17:00:00', 40, 2200000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Thanh Hóa', 'Đà Nẵng', '2025-04-05 16:00:00', '2025-04-08 18:00:00', 45, 2150000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Thanh Hóa', 'Nha Trang', '2025-04-05 13:30:00', '2025-04-08 15:30:00', 35, 2400000, 'Vietjet Air', 'ROUND_TRIP'),
    -- Buổi tối
    ('Thanh Hóa', 'Hà Nội', '2025-04-05 18:00:00', '2025-04-08 20:00:00', 50, 2400000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Thanh Hóa', 'TP. Hồ Chí Minh', '2025-04-05 19:00:00', '2025-04-08 21:00:00', 40, 2500000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Thanh Hóa', 'Đà Nẵng', '2025-04-05 20:00:00', '2025-04-08 22:00:00', 45, 2450000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Thanh Hóa', 'Nha Trang', '2025-04-05 21:00:00', '2025-04-08 23:00:00', 35, 2700000, 'Vietjet Air', 'ROUND_TRIP');

-- ROUND_TRIP: Đà Nẵng ↔ Các thành phố khác (ngày 05/04/2025, nhiều khung giờ)
INSERT INTO flights (departure, destination, departure_time, return_time, available_seats, price, airline, flight_type)
VALUES
    -- Sáng sớm
    ('Đà Nẵng', 'Hà Nội', '2025-04-05 06:00:00', '2025-04-08 08:00:00', 50, 1600000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Đà Nẵng', 'TP. Hồ Chí Minh', '2025-04-05 07:00:00', '2025-04-08 09:00:00', 40, 1700000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Đà Nẵng', 'Thanh Hóa', '2025-04-05 07:30:00', '2025-04-08 09:30:00', 45, 1550000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Đà Nẵng', 'Nha Trang', '2025-04-05 06:30:00', '2025-04-08 08:30:00', 35, 1500000, 'Vietjet Air', 'ROUND_TRIP'),
    -- Buổi sáng
    ('Đà Nẵng', 'Hà Nội', '2025-04-05 09:00:00', '2025-04-08 11:00:00', 50, 2000000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Đà Nẵng', 'TP. Hồ Chí Minh', '2025-04-05 10:00:00', '2025-04-08 12:00:00', 40, 2100000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Đà Nẵng', 'Thanh Hóa', '2025-04-05 11:00:00', '2025-04-08 13:00:00', 45, 1850000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Đà Nẵng', 'Nha Trang', '2025-04-05 08:30:00', '2025-04-08 10:30:00', 35, 1800000, 'Vietjet Air', 'ROUND_TRIP'),
    -- Buổi chiều
    ('Đà Nẵng', 'Hà Nội', '2025-04-05 14:00:00', '2025-04-08 16:00:00', 50, 2400000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Đà Nẵng', 'TP. Hồ Chí Minh', '2025-04-05 15:00:00', '2025-04-08 17:00:00', 40, 2500000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Đà Nẵng', 'Thanh Hóa', '2025-04-05 16:00:00', '2025-04-08 18:00:00', 45, 2150000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Đà Nẵng', 'Nha Trang', '2025-04-05 13:30:00', '2025-04-08 15:30:00', 35, 2100000, 'Vietjet Air', 'ROUND_TRIP'),
    -- Buổi tối
    ('Đà Nẵng', 'Hà Nội', '2025-04-05 18:00:00', '2025-04-08 20:00:00', 50, 2700000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Đà Nẵng', 'TP. Hồ Chí Minh', '2025-04-05 19:00:00', '2025-04-08 21:00:00', 40, 2800000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Đà Nẵng', 'Thanh Hóa', '2025-04-05 20:00:00', '2025-04-08 22:00:00', 45, 2450000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Đà Nẵng', 'Nha Trang', '2025-04-05 21:00:00', '2025-04-08 23:00:00', 35, 2300000, 'Vietjet Air', 'ROUND_TRIP');

-- ROUND_TRIP: Nha Trang ↔ Các thành phố khác (ngày 05/04/2025, nhiều khung giờ)
INSERT INTO flights (departure, destination, departure_time, return_time, available_seats, price, airline, flight_type)
VALUES
    -- Sáng sớm
    ('Nha Trang', 'Hà Nội', '2025-04-05 06:00:00', '2025-04-08 08:00:00', 50, 2000000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Nha Trang', 'TP. Hồ Chí Minh', '2025-04-05 07:00:00', '2025-04-08 09:00:00', 40, 1500000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Nha Trang', 'Thanh Hóa', '2025-04-05 07:30:00', '2025-04-08 09:30:00', 45, 1800000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Nha Trang', 'Đà Nẵng', '2025-04-05 06:30:00', '2025-04-08 08:30:00', 35, 1500000, 'Vietjet Air', 'ROUND_TRIP'),
    -- Buổi sáng
    ('Nha Trang', 'Hà Nội', '2025-04-05 09:00:00', '2025-04-08 11:00:00', 50, 2400000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Nha Trang', 'TP. Hồ Chí Minh', '2025-04-05 10:00:00', '2025-04-08 12:00:00', 40, 1800000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Nha Trang', 'Thanh Hóa', '2025-04-05 11:00:00', '2025-04-08 13:00:00', 45, 2100000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Nha Trang', 'Đà Nẵng', '2025-04-05 08:30:00', '2025-04-08 10:30:00', 35, 1800000, 'Vietjet Air', 'ROUND_TRIP'),
    -- Buổi chiều
    ('Nha Trang', 'Hà Nội', '2025-04-05 14:00:00', '2025-04-08 16:00:00', 50, 2800000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Nha Trang', 'TP. Hồ Chí Minh', '2025-04-05 15:00:00', '2025-04-08 17:00:00', 40, 2100000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Nha Trang', 'Thanh Hóa', '2025-04-05 16:00:00', '2025-04-08 18:00:00', 45, 2400000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Nha Trang', 'Đà Nẵng', '2025-04-05 13:30:00', '2025-04-08 15:30:00', 35, 2100000, 'Vietjet Air', 'ROUND_TRIP'),
    -- Buổi tối
    ('Nha Trang', 'Hà Nội', '2025-04-05 18:00:00', '2025-04-08 20:00:00', 50, 3100000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Nha Trang', 'TP. Hồ Chí Minh', '2025-04-05 19:00:00', '2025-04-08 21:00:00', 40, 2300000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Nha Trang', 'Thanh Hóa', '2025-04-05 20:00:00', '2025-04-08 22:00:00', 45, 2700000, 'Vietjet Air', 'ROUND_TRIP'),
    ('Nha Trang', 'Đà Nẵng', '2025-04-05 21:00:00', '2025-04-08 23:00:00', 35, 2300000, 'Vietjet Air', 'ROUND_TRIP');
INSERT INTO seats (flight_id, seat_number, row_id, column_letter, seat_class, status, price) VALUES
    (1, '1A', 1, 'A', 'PREMIUM', 'AVAILABLE', 200.00),
    (1, '1B', 1, 'B', 'PREMIUM', 'AVAILABLE', 200.00),
    (1, '1C', 1, 'C', 'PREMIUM', 'AVAILABLE', 200.00),
    (1, '1D', 1, 'D', 'PREMIUM', 'AVAILABLE', 200.00),
    (1, '1E', 1, 'E', 'PREMIUM', 'AVAILABLE', 200.00),
    (1, '1F', 1, 'F', 'PREMIUM', 'AVAILABLE', 200.00),
    (1, '2A', 2, 'A', 'PREMIUM', 'AVAILABLE', 200.00),
    (1, '2B', 2, 'B', 'PREMIUM', 'AVAILABLE', 200.00),
    (1, '2C', 2, 'C', 'PREMIUM', 'AVAILABLE', 200.00),
    (1, '2D', 2, 'D', 'PREMIUM', 'AVAILABLE', 200.00),
    (1, '2E', 2, 'E', 'PREMIUM', 'AVAILABLE', 200.00),
    (1, '2F', 2, 'F', 'PREMIUM', 'AVAILABLE', 200.00),
    (1, '3A', 3, 'A', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '3B', 3, 'B', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '3C', 3, 'C', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '3D', 3, 'D', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '3E', 3, 'E', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '3F', 3, 'F', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '4A', 4, 'A', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '4B', 4, 'B', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '4C', 4, 'C', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '4D', 4, 'D', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '4E', 4, 'E', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '4F', 4, 'F', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '5A', 5, 'A', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '5B', 5, 'B', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '5C', 5, 'C', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '5D', 5, 'D', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '5E', 5, 'E', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '5F', 5, 'F', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '6A', 6, 'A', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '6B', 6, 'B', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '6C', 6, 'C', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '6D', 6, 'D', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '6E', 6, 'E', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '6F', 6, 'F', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '7A', 7, 'A', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '7B', 7, 'B', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '7C', 7, 'C', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '7D', 7, 'D', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '7E', 7, 'E', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '7F', 7, 'F', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '8A', 8, 'A', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '8B', 8, 'B', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '8C', 8, 'C', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '8D', 8, 'D', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '8E', 8, 'E', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '8F', 8, 'F', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '9A', 9, 'A', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '9B', 9, 'B', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '9C', 9, 'C', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '9D', 9, 'D', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '9E', 9, 'E', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '9F', 9, 'F', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '10A', 10, 'A', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '10B', 10, 'B', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '10C', 10, 'C', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '10D', 10, 'D', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '10E', 10, 'E', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '10F', 10, 'F', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (1, '11A', 11, 'A', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (1, '11B', 11, 'B', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (1, '11C', 11, 'C', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (1, '11D', 11, 'D', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (1, '11E', 11, 'E', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (1, '11F', 11, 'F', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (1, '12A', 12, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '12B', 12, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '12C', 12, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '12D', 12, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '12E', 12, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '12F', 12, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '13A', 13, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '13B', 13, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '13C', 13, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '13D', 13, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '13E', 13, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '13F', 13, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '14A', 14, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '14B', 14, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '14C', 14, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '14D', 14, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '14E', 14, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '14F', 14, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '15A', 15, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '15B', 15, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '15C', 15, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '15D', 15, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '15E', 15, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '15F', 15, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '16A', 16, 'A', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (1, '16B', 16, 'B', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (1, '16C', 16, 'C', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (1, '16D', 16, 'D', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (1, '16E', 16, 'E', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (1, '16F', 16, 'F', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (1, '17A', 17, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '17B', 17, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '17C', 17, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '17D', 17, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '17E', 17, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '17F', 17, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '18A', 18, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '18B', 18, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '18C', 18, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '18D', 18, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '18E', 18, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '18F', 18, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '19A', 19, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '19B', 19, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '19C', 19, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '19D', 19, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '19E', 19, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '19F', 19, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '20A', 20, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '20B', 20, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '20C', 20, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '20D', 20, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '20E', 20, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '20F', 20, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '21A', 21, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '21B', 21, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '21C', 21, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '21D', 21, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '21E', 21, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '21F', 21, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '22A', 22, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '22B', 22, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '22C', 22, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '22D', 22, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '22E', 22, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '22F', 22, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '23A', 23, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '23B', 23, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '23C', 23, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '23D', 23, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '23E', 23, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '23F', 23, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '24A', 24, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '24B', 24, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '24C', 24, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '24D', 24, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '24E', 24, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '24F', 24, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '25A', 25, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '25B', 25, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '25C', 25, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '25D', 25, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '25E', 25, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '25F', 25, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '26A', 26, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '26B', 26, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '26C', 26, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '26D', 26, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '26E', 26, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '26F', 26, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '27A', 27, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '27B', 27, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '27C', 27, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '27D', 27, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '27E', 27, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '27F', 27, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '28A', 28, 'A', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (1, '28B', 28, 'B', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (1, '28C', 28, 'C', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (1, '28D', 28, 'D', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (1, '28E', 28, 'E', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (1, '28F', 28, 'F', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (1, '29A', 29, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '29B', 29, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '29C', 29, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '29D', 29, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '29E', 29, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '29F', 29, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '30A', 30, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '30B', 30, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '30C', 30, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '30D', 30, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '30E', 30, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '30F', 30, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '31A', 31, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '31B', 31, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '31C', 31, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '31D', 31, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '31E', 31, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (1, '31F', 31, 'F', 'STANDARD', 'AVAILABLE', 100.00);

INSERT INTO seats (flight_id, seat_number, row_id, column_letter, seat_class, status, price) VALUES
    (2, '1A', 1, 'A', 'PREMIUM', 'AVAILABLE', 200.00),
    (2, '1B', 1, 'B', 'PREMIUM', 'AVAILABLE', 200.00),
    (2, '1C', 1, 'C', 'PREMIUM', 'AVAILABLE', 200.00),
    (2, '1D', 1, 'D', 'PREMIUM', 'AVAILABLE', 200.00),
    (2, '1E', 1, 'E', 'PREMIUM', 'AVAILABLE', 200.00),
    (2, '1F', 1, 'F', 'PREMIUM', 'AVAILABLE', 200.00),
    (2, '2A', 2, 'A', 'PREMIUM', 'AVAILABLE', 200.00),
    (2, '2B', 2, 'B', 'PREMIUM', 'AVAILABLE', 200.00),
    (2, '2C', 2, 'C', 'PREMIUM', 'AVAILABLE', 200.00),
    (2, '2D', 2, 'D', 'PREMIUM', 'AVAILABLE', 200.00),
    (2, '2E', 2, 'E', 'PREMIUM', 'AVAILABLE', 200.00),
    (2, '2F', 2, 'F', 'PREMIUM', 'AVAILABLE', 200.00),
    (2, '3A', 3, 'A', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '3B', 3, 'B', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '3C', 3, 'C', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '3D', 3, 'D', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '3E', 3, 'E', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '3F', 3, 'F', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '4A', 4, 'A', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '4B', 4, 'B', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '4C', 4, 'C', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '4D', 4, 'D', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '4E', 4, 'E', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '4F', 4, 'F', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '5A', 5, 'A', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '5B', 5, 'B', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '5C', 5, 'C', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '5D', 5, 'D', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '5E', 5, 'E', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '5F', 5, 'F', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '6A', 6, 'A', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '6B', 6, 'B', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '6C', 6, 'C', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '6D', 6, 'D', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '6E', 6, 'E', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '6F', 6, 'F', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '7A', 7, 'A', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '7B', 7, 'B', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '7C', 7, 'C', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '7D', 7, 'D', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '7E', 7, 'E', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '7F', 7, 'F', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '8A', 8, 'A', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '8B', 8, 'B', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '8C', 8, 'C', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '8D', 8, 'D', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '8E', 8, 'E', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '8F', 8, 'F', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '9A', 9, 'A', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '9B', 9, 'B', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '9C', 9, 'C', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '9D', 9, 'D', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '9E', 9, 'E', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '9F', 9, 'F', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '10A', 10, 'A', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '10B', 10, 'B', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '10C', 10, 'C', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '10D', 10, 'D', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '10E', 10, 'E', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '10F', 10, 'F', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (2, '11A', 11, 'A', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (2, '11B', 11, 'B', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (2, '11C', 11, 'C', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (2, '11D', 11, 'D', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (2, '11E', 11, 'E', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (2, '11F', 11, 'F', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (2, '12A', 12, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '12B', 12, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '12C', 12, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '12D', 12, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '12E', 12, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '12F', 12, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '13A', 13, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '13B', 13, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '13C', 13, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '13D', 13, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '13E', 13, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '13F', 13, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '14A', 14, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '14B', 14, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '14C', 14, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '14D', 14, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '14E', 14, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '14F', 14, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '15A', 15, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '15B', 15, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '15C', 15, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '15D', 15, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '15E', 15, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '15F', 15, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '16A', 16, 'A', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (2, '16B', 16, 'B', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (2, '16C', 16, 'C', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (2, '16D', 16, 'D', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (2, '16E', 16, 'E', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (2, '16F', 16, 'F', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (2, '17A', 17, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '17B', 17, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '17C', 17, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '17D', 17, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '17E', 17, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '17F', 17, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '18A', 18, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '18B', 18, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '18C', 18, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '18D', 18, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '18E', 18, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '18F', 18, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '19A', 19, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '19B', 19, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '19C', 19, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '19D', 19, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '19E', 19, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '19F', 19, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '20A', 20, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '20B', 20, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '20C', 20, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '20D', 20, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '20E', 20, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '20F', 20, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '21A', 21, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '21B', 21, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '21C', 21, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '21D', 21, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '21E', 21, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '21F', 21, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '22A', 22, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '22B', 22, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '22C', 22, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '22D', 22, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '22E', 22, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '22F', 22, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '23A', 23, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '23B', 23, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '23C', 23, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '23D', 23, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '23E', 23, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '23F', 23, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '24A', 24, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '24B', 24, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '24C', 24, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '24D', 24, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '24E', 24, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '24F', 24, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '25A', 25, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '25B', 25, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '25C', 25, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '25D', 25, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '25E', 25, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '25F', 25, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '26A', 26, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '26B', 26, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '26C', 26, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '26D', 26, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '26E', 26, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '26F', 26, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '27A', 27, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '27B', 27, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '27C', 27, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '27D', 27, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '27E', 27, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '27F', 27, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '28A', 28, 'A', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (2, '28B', 28, 'B', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (2, '28C', 28, 'C', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (2, '28D', 28, 'D', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (2, '28E', 28, 'E', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (2, '28F', 28, 'F', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (2, '29A', 29, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '29B', 29, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '29C', 29, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '29D', 29, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '29E', 29, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '29F', 29, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '30A', 30, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '30B', 30, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '30C', 30, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '30D', 30, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '30E', 30, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '30F', 30, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '31A', 31, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '31B', 31, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '31C', 31, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '31D', 31, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '31E', 31, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (2, '31F', 31, 'F', 'STANDARD', 'AVAILABLE', 100.00);

INSERT INTO seats (flight_id, seat_number, row_id, column_letter, seat_class, status, price) VALUES
    (3, '1A', 1, 'A', 'PREMIUM', 'AVAILABLE', 200.00),
    (3, '1B', 1, 'B', 'PREMIUM', 'AVAILABLE', 200.00),
    (3, '1C', 1, 'C', 'PREMIUM', 'AVAILABLE', 200.00),
    (3, '1D', 1, 'D', 'PREMIUM', 'AVAILABLE', 200.00),
    (3, '1E', 1, 'E', 'PREMIUM', 'AVAILABLE', 200.00),
    (3, '1F', 1, 'F', 'PREMIUM', 'AVAILABLE', 200.00),
    (3, '2A', 2, 'A', 'PREMIUM', 'AVAILABLE', 200.00),
    (3, '2B', 2, 'B', 'PREMIUM', 'AVAILABLE', 200.00),
    (3, '2C', 2, 'C', 'PREMIUM', 'AVAILABLE', 200.00),
    (3, '2D', 2, 'D', 'PREMIUM', 'AVAILABLE', 200.00),
    (3, '2E', 2, 'E', 'PREMIUM', 'AVAILABLE', 200.00),
    (3, '2F', 2, 'F', 'PREMIUM', 'AVAILABLE', 200.00),
    (3, '3A', 3, 'A', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '3B', 3, 'B', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '3C', 3, 'C', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '3D', 3, 'D', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '3E', 3, 'E', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '3F', 3, 'F', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '4A', 4, 'A', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '4B', 4, 'B', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '4C', 4, 'C', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '4D', 4, 'D', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '4E', 4, 'E', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '4F', 4, 'F', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '5A', 5, 'A', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '5B', 5, 'B', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '5C', 5, 'C', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '5D', 5, 'D', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '5E', 5, 'E', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '5F', 5, 'F', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '6A', 6, 'A', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '6B', 6, 'B', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '6C', 6, 'C', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '6D', 6, 'D', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '6E', 6, 'E', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '6F', 6, 'F', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '7A', 7, 'A', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '7B', 7, 'B', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '7C', 7, 'C', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '7D', 7, 'D', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '7E', 7, 'E', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '7F', 7, 'F', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '8A', 8, 'A', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '8B', 8, 'B', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '8C', 8, 'C', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '8D', 8, 'D', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '8E', 8, 'E', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '8F', 8, 'F', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '9A', 9, 'A', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '9B', 9, 'B', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '9C', 9, 'C', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '9D', 9, 'D', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '9E', 9, 'E', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '9F', 9, 'F', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '10A', 10, 'A', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '10B', 10, 'B', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '10C', 10, 'C', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '10D', 10, 'D', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '10E', 10, 'E', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '10F', 10, 'F', 'FRONT_ROW', 'AVAILABLE', 180.00),
    (3, '11A', 11, 'A', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (3, '11B', 11, 'B', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (3, '11C', 11, 'C', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (3, '11D', 11, 'D', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (3, '11E', 11, 'E', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (3, '11F', 11, 'F', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (3, '12A', 12, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '12B', 12, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '12C', 12, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '12D', 12, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '12E', 12, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '12F', 12, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '13A', 13, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '13B', 13, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '13C', 13, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '13D', 13, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '13E', 13, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '13F', 13, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '14A', 14, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '14B', 14, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '14C', 14, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '14D', 14, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '14E', 14, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '14F', 14, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '15A', 15, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '15B', 15, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '15C', 15, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '15D', 15, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '15E', 15, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '15F', 15, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '16A', 16, 'A', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (3, '16B', 16, 'B', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (3, '16C', 16, 'C', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (3, '16D', 16, 'D', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (3, '16E', 16, 'E', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (3, '16F', 16, 'F', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (3, '17A', 17, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '17B', 17, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '17C', 17, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '17D', 17, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '17E', 17, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '17F', 17, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '18A', 18, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '18B', 18, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '18C', 18, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '18D', 18, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '18E', 18, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '18F', 18, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '19A', 19, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '19B', 19, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '19C', 19, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '19D', 19, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '19E', 19, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '19F', 19, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '20A', 20, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '20B', 20, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '20C', 20, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '20D', 20, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '20E', 20, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '20F', 20, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '21A', 21, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '21B', 21, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '21C', 21, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '21D', 21, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '21E', 21, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '21F', 21, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '22A', 22, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '22B', 22, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '22C', 22, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '22D', 22, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '22E', 22, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '22F', 22, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '23A', 23, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '23B', 23, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '23C', 23, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '23D', 23, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '23E', 23, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '23F', 23, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '24A', 24, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '24B', 24, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '24C', 24, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '24D', 24, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '24E', 24, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '24F', 24, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '25A', 25, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '25B', 25, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '25C', 25, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '25D', 25, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '25E', 25, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '25F', 25, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '26A', 26, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '26B', 26, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '26C', 26, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '26D', 26, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '26E', 26, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '26F', 26, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '27A', 27, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '27B', 27, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '27C', 27, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '27D', 27, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '27E', 27, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '27F', 27, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '28A', 28, 'A', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (3, '28B', 28, 'B', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (3, '28C', 28, 'C', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (3, '28D', 28, 'D', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (3, '28E', 28, 'E', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (3, '28F', 28, 'F', 'EXTRA_LEGROOM', 'AVAILABLE', 150.00),
    (3, '29A', 29, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '29B', 29, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '29C', 29, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '29D', 29, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '29E', 29, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '29F', 29, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '30A', 30, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '30B', 30, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '30C', 30, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '30D', 30, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '30E', 30, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '30F', 30, 'F', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '31A', 31, 'A', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '31B', 31, 'B', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '31C', 31, 'C', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '31D', 31, 'D', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '31E', 31, 'E', 'STANDARD', 'AVAILABLE', 100.00),
    (3, '31F', 31, 'F', 'STANDARD', 'AVAILABLE', 100.00);


INSERT INTO meals (name, description, price, image) VALUES
    ('Chicken Meal', 'Grilled chicken with rice', 10.00, 'chicken-meal.jpg'),
    ('Vegetarian Meal', 'Vegetable stir-fry', 8.00, 'vegan-meal.jpg'),
    ('Beef Meal', 'Beef steak with potatoes', 12.00, 'beef-meal.jpg'),
    ('Seafood Meal', 'Shrimp and rice', 15.00, 'seafood-meal.jpg');

INSERT INTO baggage (weight, price_per_kg, image) VALUES
    (15, 15.00, 'baggage.png'),
    (20, 20.00, 'baggage.png'),
    (25, 25.00, 'baggage.png'),
    (30, 30.00, 'baggage.png');
select * from flights;

INSERT INTO ticket_classes (name, free_seat_class, free_meal_quantity, free_baggage_weight, base_price_multiplier) VALUES
    ('Economy', 'STANDARD', 1, 20.0, 1.0),

    ('Premium Economy', 'EXTRA_LEGROOM', 1, 25.0, 1.5),

    ('Business', 'PREMIUM', 2, 30.0, 2.0),

    ('First Class', 'FRONT_ROW', 3, 40.0, 3.0),

    ('Budget', NULL, 0, 0.0, 0.8),

    ('Promotional', 'STANDARD', 2, 25.0, 1.2);


INSERT INTO meals (name, description, price, category) VALUES
    ('Standard Meal', 'Chicken with rice', 50000.00, 'Standard');

INSERT INTO baggage (weight, price_per_kg, category) VALUES
    (10.0, 10000.00, 'Standard');

ALTER TABLE flights ADD COLUMN ticket_class_id BIGINT,
ADD CONSTRAINT fk_ticket_class FOREIGN KEY (ticket_class_id) REFERENCES ticket_classes(id) ON DELETE SET NULL;

INSERT INTO users (last_name, first_name, gender, email, date_of_birth, phone_number, username, password, enabled) VALUES
    ('Nguyen', 'Van', 'MALE', 'van.nguyen@example.com', '1990-01-01', '0901234567', 'vannguyen', 'password123', TRUE);

INSERT INTO flights (departure, destination, departure_time, return_time, available_seats, price, airline, flight_type, ticket_class_id) VALUES
    ('Hà Nội', 'TP. Hồ Chí Minh', '2025-04-05 06:00:00', NULL, 50, 1000000.00, 'Vietjet Air', 'ONE_WAY', 1);

INSERT INTO meals (name, description, price, category) VALUES
    ('Standard Meal', 'Chicken with rice', 50000.00, 'Standard');

INSERT INTO baggage (weight, price_per_kg, category) VALUES
    (10.0, 10000.00, 'Standard');

INSERT INTO bookings (reservation_code, booking_date, passenger_count, total_price, additional_cost, status, user_id) VALUES
    ('ABC12345', CURRENT_TIMESTAMP, 2, 2200000.00, 200000.00, 'PENDING', 1);

INSERT INTO booking_flights (booking_id, flight_id) VALUES
    (1, 1);

INSERT INTO booking_seat (booking_id, seat_id) VALUES
    (1, 1),
    (1, 2);

UPDATE seats SET status = 'OCCUPIED', booking_id = 1 WHERE id IN (1, 2);

INSERT INTO booking_meal (booking_id, meal_id, flight_id, quantity) VALUES
    (1, 1, 1, 2);

INSERT INTO booking_baggage (booking_id, baggage_id, flight_id, quantity) VALUES
    (1, 1, 1, 4);