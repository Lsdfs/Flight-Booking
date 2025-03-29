use  flightreservation;
CREATE TABLE booking (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_code VARCHAR(20) NOT NULL UNIQUE,
    passenger_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    flight_id BIGINT NOT NULL,
    return_flight_id BIGINT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'CANCELLED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (flight_id) REFERENCES flights(id) ON DELETE CASCADE,
    FOREIGN KEY (return_flight_id) REFERENCES flights(id) ON DELETE SET NULL
);

CREATE TABLE seat (
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

CREATE TABLE meal (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock INT DEFAULT 100,
    image VARCHAR(255)
);

CREATE TABLE booking_meal (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    meal_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (booking_id) REFERENCES booking(id) ON DELETE CASCADE,
    FOREIGN KEY (meal_id) REFERENCES meal(id) ON DELETE CASCADE
);

CREATE TABLE baggage (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    weight INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    image VARCHAR(255),
    booking_id BIGINT NULL,
    FOREIGN KEY (booking_id) REFERENCES booking(id) ON DELETE SET NULL
);

CREATE TABLE booking_baggage (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    baggage_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (booking_id) REFERENCES booking(id) ON DELETE CASCADE,
    FOREIGN KEY (baggage_id) REFERENCES baggage(id) ON DELETE CASCADE
);


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


select*from seat;