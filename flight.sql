create database if not exists flightbooking;
use  flightbooking;
CREATE TABLE if not exists  flights (
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
CREATE TABLE if not exists booking (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    booking_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    passenger_count INT NOT NULL,
    reservation_code INT NOT NULL,
    total_price DOUBLE NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'CANCELLED', 'PAID') NOT NULL DEFAULT 'PENDING',
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

CREATE TABLE booking_flights (
    booking_id BIGINT NOT NULL,
    flight_id BIGINT NOT NULL,
    PRIMARY KEY (booking_id, flight_id),
    FOREIGN KEY (booking_id) REFERENCES booking(id) ON DELETE CASCADE,
    FOREIGN KEY (flight_id) REFERENCES flight(id) ON DELETE CASCADE
);

CREATE TABLE if not exists seat (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    flight_id BIGINT NOT NULL,
    seat_number VARCHAR(10) NOT NULL,
    row_id INT NOT NULL,
    column_letter CHAR(1) NOT NULL,
    seat_class ENUM('PREMIUM', 'STANDARD', 'EXTRA_LEGROOM', 'FRONT_ROW') NOT NULL,
    status ENUM('AVAILABLE', 'OCCUPIED', 'SELECTING') DEFAULT 'AVAILABLE',
    price DECIMAL(10,2) NOT NULL,
    image VARCHAR(255),
    booking_id BIGINT NULL,
    FOREIGN KEY (flight_id) REFERENCES flights(id) ON DELETE CASCADE,
    FOREIGN KEY (booking_id) REFERENCES booking(id) ON DELETE SET NULL
);
CREATE TABLE if not exists meal (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock INT DEFAULT 100,
    image VARCHAR(255)
);

CREATE TABLE if not exists booking_meal (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    meal_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (booking_id) REFERENCES booking(id) ON DELETE CASCADE,
    FOREIGN KEY (meal_id) REFERENCES meal(id) ON DELETE CASCADE
);

CREATE TABLE if not exists baggage (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    weight INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    image VARCHAR(255),
    booking_id BIGINT NULL,
    FOREIGN KEY (booking_id) REFERENCES booking(id) ON DELETE SET NULL
);

CREATE TABLE if not exists booking_baggage (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    baggage_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (booking_id) REFERENCES booking(id) ON DELETE CASCADE,
    FOREIGN KEY (baggage_id) REFERENCES baggage(id) ON DELETE CASCADE
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
INSERT INTO seat (flight_id, seat_number, row_id, column_letter, seat_class, status, price, image) VALUES
(1, '1A', 1, 'A', 'PREMIUM', 'AVAILABLE', 200.00, NULL),
(1, '1B', 1, 'B', 'PREMIUM', 'AVAILABLE', 200.00, NULL),
(1, '12A', 12, 'A', 'STANDARD', 'AVAILABLE', 100.00, NULL),
(1, '12B', 12, 'B', 'STANDARD', 'AVAILABLE', 100.00, NULL);

INSERT INTO meal (name, description, price, stock, image) VALUES
('Chicken Meal', 'Grilled chicken with rice', 10.00, 50, 'chicken-meal.jpg'),
('Vegetarian Meal', 'Vegetable stir-fry', 8.00, 50, 'vegan-meal.jpg'),
('Beef Meal', 'Beef steak with potatoes', 12.00, 50, 'beef-meal.jpg'),
('Seafood Meal', 'Shrimp and rice', 15.00, 50, 'seafood-meal.jpg');

INSERT INTO baggage (weight, price, image) VALUES
(15, 15.00, 'baggage.png'),
(20, 20.00, 'baggage.png'),
(25, 25.00, 'baggage.png'),
(30, 30.00, 'baggage.png');
select * from flights;
