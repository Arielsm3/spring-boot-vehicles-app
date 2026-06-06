CREATE TABLE vehicle (
    id VARCHAR(36) NOT NULL,
    vin VARCHAR(17) NOT NULL UNIQUE,
    version INT,
    make SMALLINT,
    model VARCHAR(255),
    model_year VARCHAR(4),
    color VARCHAR(255),
    mileage INT,
    price DECIMAL(38, 2),
    created_date DATETIME(6),
    update_date DATETIME(6),
    PRIMARY KEY (id)
) ENGINE=InnoDB;