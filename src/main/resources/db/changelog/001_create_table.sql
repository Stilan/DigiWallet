-- liquibase formatted sql
--changeset aleksandrlitvinov:2

create table wallet (
    walletId uuid primary key,
    amount int
);

create table transaction (
    transactionId uuid primary key,
    operation varchar,
    walletId uuid REFERENCES wallet

);

