create table consults
(
    id              bigint primary key auto_increment,
    patient_id      bigint      not null,
    doctor_id       bigint,
    call_type       varchar(20) not null default 'VOICE',
    symptom         varchar(255),
    picture         varchar(255),
    matching_status varchar(20) not null default 'REQUESTED',
    created_at      datetime,
    updated_at      datetime,
    deleted_at      datetime,

    foreign key (patient_id) references patients (id),
    foreign key (doctor_id) references doctors (id)
);

create table consult_rejections
(
    id         bigint primary key auto_increment,
    consult_id bigint not null,
    doctor_id  bigint not null,
    created_at datetime,
    updated_at datetime,
    deleted_at datetime,

    foreign key (consult_id) references consults (id),
    foreign key (doctor_id) references doctors (id)
);