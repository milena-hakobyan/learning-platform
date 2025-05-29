DROP TABLE IF EXISTS person_citizenship;
DROP TABLE IF EXISTS people;
DROP TABLE IF EXISTS countries;
DROP TABLE IF EXISTS continents;

CREATE TABLE IF NOT EXISTS continents (
	id SERIAL PRIMARY KEY,
	name VARCHAR(40) NOT NULL,
	code VARCHAR(2) NOT NULL UNIQUE,
	area NUMERIC(10, 2),
	population BIGINT
);

CREATE TABLE IF NOT EXISTS countries (
	id SERIAL PRIMARY KEY,
	continent_id INT,
	name VARCHAR(100) NOT NULL,
	code VARCHAR(2) NOT NULL UNIQUE,
	area NUMERIC(10, 2),
	population BIGINT,
	capital VARCHAR(100),
	official_language VARCHAR(50),
	gdp NUMERIC,
	CONSTRAINT fk_countries_on_continents FOREIGN KEY (continent_id) REFERENCES continents(id)
);


CREATE TABLE IF NOT EXISTS people (
	id SERIAL PRIMARY KEY,
	first_name VARCHAR(40) NOT NULL,
	last_name VARCHAR(40) NOT NULL,
	date_of_birth DATE,
    gender VARCHAR(10),
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS person_citizenship (
	person_id INT,
	country_id INT,
	PRIMARY KEY (person_id, country_id),
	CONSTRAINT fk_person_citizenship_on_people FOREIGN KEY (person_id) REFERENCES people(id),
	CONSTRAINT fk_person_citizenship_on_countries FOREIGN KEY (country_id) REFERENCES countries(id)
);

-- Continents
INSERT INTO continents (name, code, area, population) VALUES
('Africa', 'AF', 30370000, 1340598000),
('Europe', 'EU', 10180000, 747636026),
('Asia', 'AS', 44579000, 4641054775),
('North America', 'NA', 24709000, 592072212),
('South America', 'SA', 17840000, 430759766),
('Australia', 'AU', 8600000, 42677813),
('Antarctica', 'AN', 14000000, 1106);

-- Countries (20)
INSERT INTO countries (continent_id, name, code, area, population, capital, official_language, gdp) VALUES
(1, 'Nigeria', 'NG', 923769, 206139589, 'Abuja', 'English', 448120000000),
(2, 'Germany', 'DE', 357386, 83166711, 'Berlin', 'German', 3861123000000),
(3, 'China', 'CN', 9596961, 1439323776, 'Beijing', 'Mandarin', 14342903000000),
(4, 'United States', 'US', 9833517, 331002651, 'Washington, D.C.', 'English', 21433226000000),
(5, 'Brazil', 'BR', 8515767, 212559417, 'Brasília', 'Portuguese', 1444731000000),
(6, 'Australia', 'AU', 7692024, 25499884, 'Canberra', 'English', 1392687000000),
(5, 'Argentina', 'AR', 2780400, 45195777, 'Buenos Aires', 'Spanish', 388164000000),
(2, 'France', 'FR', 551695, 67081000, 'Paris', 'French', 2715518000000),
(3, 'India', 'IN', 3287263, 1380004385, 'New Delhi', 'Hindi', 2875140000000),
(4, 'Canada', 'CA', 9984670, 37742154, 'Ottawa', 'English/French', 1647126000000),
(1, 'Egypt', 'EG', 1002450, 102334404, 'Cairo', 'Arabic', 363070000000),
(2, 'Italy', 'IT', 301340, 60244639, 'Rome', 'Italian', 2001300000000),
(3, 'Japan', 'JP', 377975, 126476461, 'Tokyo', 'Japanese', 5081770000000),
(4, 'Mexico', 'MX', 1964375, 128932753, 'Mexico City', 'Spanish', 1260145000000),
(5, 'Chile', 'CL', 756102, 19116209, 'Santiago', 'Spanish', 282318000000),
(2, 'Spain', 'ES', 505990, 46754778, 'Madrid', 'Spanish', 1400082000000),
(3, 'Indonesia', 'ID', 1904569, 273523615, 'Jakarta', 'Indonesian', 1119190000000),
(4, 'Cuba', 'CU', 109884, 11326616, 'Havana', 'Spanish', 100000000000),
(1, 'South Africa', 'ZA', 1219090, 59308690, 'Pretoria', 'English', 351431000000),
(6, 'New Zealand', 'NZ', 268021, 4822233, 'Wellington', 'English', 206928000000);

-- People (50 entries)
INSERT INTO people (first_name, last_name, date_of_birth, gender, email, phone) VALUES
('Alice', 'Smith', '1990-01-15', 'Female', 'alice.smith@example.com', '+1234567890'),
('Bob', 'Johnson', '1985-06-30', 'Male', 'bob.johnson@example.com', '+1987654321'),
('Carlos', 'Diaz', '1978-11-20', 'Male', 'carlos.diaz@example.com', '+549876543210'),
('Diana', 'Nguyen', '1995-03-10', 'Female', 'diana.nguyen@example.com', '+61412345678'),
('Ebrahim', 'Ali', '1980-12-05', 'Male', 'ebrahim.ali@example.com', '+2348012345678'),
('Fatima', 'Zhang', '1992-08-25', 'Female', 'fatima.zhang@example.com', '+8613812345678'),
('George', 'Kim', '1989-07-14', 'Male', 'george.kim@example.com', '+821012345678'),
('Hannah', 'Brown', '1993-10-21', 'Female', 'hannah.brown@example.com', '+447912345678'),
('Ivan', 'Petrov', '1986-09-12', 'Male', 'ivan.petrov@example.com', '+74951234567'),
('Julia', 'Kowalski', '1991-04-06', 'Female', 'julia.kowalski@example.com', '+48123456789'),
('Ken', 'Yamamoto', '1975-03-15', 'Male', 'ken.yamamoto@example.com', '+81312345678'),
('Alice', 'Smith', '1984-11-30', 'Female', 'ally.smith@example.com', '+521234567890'),
('Mohammed', 'Hassan', '1988-02-18', 'Male', 'mohammed.hassan@example.com', '+201234567890'),
('Nina', 'Dubois', '1996-06-01', 'Female', 'nina.dubois@example.com', '+33123456789'),
('Oscar', 'Lopez', '1979-08-10', 'Male', 'oscar.lopez@example.com', '+34123456789'),
('Priya', 'Singh', '1994-05-22', 'Female', 'priya.singh@example.com', '+911234567890'),
('Quinn', 'Brien', '1983-12-03', 'Non-binary', 'quinn.obrien@example.com', '+353123456789'),
('Ravi', 'Chopra', '1990-09-09', 'Male', 'ravi.chopra@example.com', '+911112223333'),
('Sofia', 'Rossi', '1997-01-19', 'Female', 'sofia.rossi@example.com', '+391234567890'),
('Tom', 'Baker', '1982-07-28', 'Male', 'tom.baker@example.com', '+441234567890'),
('Uma', 'Patel', '1999-03-11', 'Female', 'uma.patel@example.com', '+911234567891'),
('Victor', 'Ng', '1987-05-05', 'Male', 'victor.ng@example.com', '+60123456789'),
('Wendy', 'Tanaka', '1992-02-27', 'Female', 'wendy.tanaka@example.com', '+81398765432'),
('Xavier', 'Garcia', '1981-06-15', 'Male', 'xavier.garcia@example.com', '+34111222333'),
('Yara', 'Mohamed', '1993-12-24', 'Female', 'yara.mohamed@example.com', '+20111222333'),
('Zane', 'Lee', '1985-04-04', 'Male', 'zane.lee@example.com', '+82111222333'),
('Aliya', 'Khan', '1998-08-08', 'Female', 'aliya.khan@example.com', '+92123456789'),
('Bruno', 'Silva', '1990-10-10', 'Male', 'bruno.silva@example.com', '+55111222333'),
('Chloe', 'White', '1991-01-01', 'Female', 'chloe.white@example.com', '+441234567891'),
('Zane', 'Lee', '1983-11-11', 'Male', 'z.leeee.com', '+491234567890'),
('Elena', 'Popov', '1995-09-09', 'Female', 'elena.popov@example.com', '+74951234568'),
('Fabio', 'Conti', '1977-06-06', 'Male', 'fabio.conti@example.com', '+39111222333'),
('Grace', 'Hughes', '1986-02-02', 'Female', 'grace.hughes@example.com', '+441234567892'),
('Hamid', 'Fadel', '1984-03-03', 'Male', 'hamid.fadel@example.com', '+201234567891'),
('Isabel', 'Navarro', '1992-12-12', 'Female', 'isabel.navarro@example.com', '+34123456788'),
('Jasper', 'Van Dijk', '1989-08-08', 'Male', 'jasper.vandijk@example.com', '+31123456789'),
('Katerina', 'Ivanova', '1987-07-07', 'Female', 'katerina.ivanova@example.com', '+74951234569'),
('Leo', 'Nguyen', '1996-06-06', 'Male', 'leo.nguyen@example.com', '+84987654321'),
('Maria', 'Costa', '1993-05-05', 'Female', 'maria.costa@example.com', '+35123456789'),
('Niko', 'Karjalainen', '1985-04-04', 'Male', 'niko.k@example.com', '+358123456789'),
('Olga', 'Nowak', '1980-03-03', 'Female', 'olga.nowak@example.com', '+48123456788'),
('Pavel', 'Horvat', '1979-02-02', 'Male', 'pavel.horvat@example.com', '+385123456789'),
('Rosa', 'Fernandez', '1994-01-01', 'Female', 'rosa.fernandez@example.com', '+34123456787'),
('Stefan', 'Keller', '1990-11-11', 'Male', 'stefan.keller@example.com', '+41123456789'),
('Tariq', 'Abbas', '1982-10-10', 'Male', 'tariq.abbas@example.com', '+97123456789'),
('Ursula', 'Meyer', '1988-09-09', 'Female', 'ursula.meyer@example.com', '+491234567891'),
('Valentina', 'Moretti', '1991-08-08', 'Female', 'valentina.moretti@example.com', '+39123456791'),
('William', 'Clark', '1995-07-07', 'Male', 'william.clark@example.com', '+12123456789'),
('Xiomara', 'Santos', '1997-06-06', 'Female', 'xiomara.santos@example.com', '+53123456789'),
('Yusuf', 'Omar', '1998-05-05', 'Male', 'yusuf.omar@example.com', '+25123456789'),
('Zofia', 'Wojcik', '1994-04-04', 'Female', 'zofia.wojcik@example.com', '+48123456787');

-- One citizenship each (person_ids: 1–35, excluding 6, 10, 15, 25, 30 — they'll have 2)
INSERT INTO person_citizenship (person_id, country_id) VALUES
(1, 4), (2, 10), (3, 5), (4, 6), (5, 1),
(7, 13), (8, 2), (9, 14), (11, 13), (12, 14),
(13, 11), (14, 8), (16, 9), (17, 2), (18, 9),
(19, 12), (20, 4), (21, 9), (22, 6), (23, 13),
(24, 14), (26, 3), (27, 1), (28, 5), (29, 8),
(31, 13), (32, 12), (33, 4), (34, 11), (35, 16),
(36, 2), (37, 3), (38, 6), (39, 17), (40, 2);

-- Two citizenships (person_ids: 6, 10, 15, 25, 30, 41, 42, 43, 44, 45)
INSERT INTO person_citizenship (person_id, country_id) VALUES
(6, 3), (6, 9),        -- Fatima: China + India
(10, 2), (10, 8),      -- Julia: Germany + France
(15, 5), (15, 11),     -- Oscar: Brazil + Egypt
(25, 11), (25, 18),    -- Yara: Egypt + Cuba
(30, 2), (30, 5),      -- David: Germany + Brazil
(41, 15), (41, 19),    -- Olga: Chile + South Africa
(42, 7), (42, 20),     -- Pavel: Argentina + New Zealand
(43, 6), (43, 14),     -- Quentin: Australia + Mexico
(44, 19), (44, 1),     -- Rina: South Africa + Nigeria
(45, 16), (45, 3);     -- Samira: Spain + China
