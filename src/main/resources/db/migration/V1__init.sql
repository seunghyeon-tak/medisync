create table users
(
    id          bigint primary key auto_increment,
    name        varchar(20) not null,
    email       varchar(100) unique,
    password    varchar(500),
    birth_day   date        not null,
    address     varchar(100),
    role        varchar(20) not null,
    phone       varchar(30) not null,
    provider_id varchar(500),
    provider    varchar(500),
    created_at  datetime,
    updated_at  datetime,
    deleted_at  datetime
);

create table hospitals
(
    id         bigint primary key auto_increment,
    name       varchar(100) not null,
    address    varchar(255) not null,
    phone      varchar(50)  not null,
    content    varchar(255),
    created_at datetime,
    updated_at datetime,
    deleted_at datetime
);

create table pharmacies
(
    id         bigint primary key auto_increment,
    name       varchar(20) not null,
    address    varchar(50) not null,
    phone      varchar(20) not null,
    content    varchar(255),
    created_at datetime,
    updated_at datetime,
    deleted_at datetime
);

create table medical_subjects
(
    id         bigint primary key auto_increment,
    name       varchar(50) not null,
    created_at datetime,
    updated_at datetime,
    deleted_at datetime
);

create table doctors
(
    id             bigint primary key,
    hospital_id    bigint       not null,
    license_number varchar(100) not null,

    foreign key (id) references users (id),
    foreign key (hospital_id) references hospitals (id)
);

create table patients
(
    id                  bigint primary key,
    blood_type          varchar(10) not null,
    medical_history     varchar(500),
    current_medications varchar(500),

    foreign key (id) references users (id)
);

create table pharmacists
(
    id             bigint primary key,
    license_number varchar(50) not null,

    foreign key (id) references users (id)
);

create table doctor_specialties
(
    id                 bigint primary key auto_increment,
    doctor_id          bigint not null,
    medical_subject_id bigint not null,

    foreign key (doctor_id) references doctors (id),
    foreign key (medical_subject_id) references medical_subjects (id)
);

create table appointment_slots
(
    id           bigint primary key auto_increment,
    doctor_id    bigint not null,
    date         date   not null,
    start_time   time   not null,
    end_time     time   not null,
    is_available tinyint(1) not null,
    created_at   datetime,
    updated_at   datetime,
    deleted_at   datetime,

    foreign key (doctor_id) references doctors (id)
);

create table appointments
(
    id                 bigint primary key auto_increment,
    patient_id         bigint not null,
    doctor_id          bigint not null,
    slot_id            bigint not null,
    symptom            varchar(255),
    picture            varchar(255),
    reservation_status varchar(20) default 'WAITING',
    created_at         datetime,
    updated_at         datetime,
    deleted_at         datetime,

    foreign key (patient_id) references patients (id),
    foreign key (doctor_id) references doctors (id),
    foreign key (slot_id) references appointment_slots (id)
);

create table treatments
(
    id                 bigint primary key auto_increment,
    appointment_id     bigint not null,
    medical_subject_id bigint not null,
    content            varchar(255),
    created_at         datetime,
    updated_at         datetime,
    deleted_at         datetime,

    foreign key (appointment_id) references appointments (id),
    foreign key (medical_subject_id) references medical_subjects (id)
);

create table prescriptions
(
    id           bigint primary key auto_increment,
    treatment_id bigint       not null,
    pharmacy_id  bigint,
    image        varchar(500) not null,
    status       varchar(255) not null default 'WAITING',
    created_at   datetime,
    updated_at   datetime,
    deleted_at   datetime,

    foreign key (treatment_id) references treatments (id),
    foreign key (pharmacy_id) references pharmacies (id)
);