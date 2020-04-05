-- phpMyAdmin SQL Dump
-- version 4.9.2
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Server version: 10.3.22-MariaDB-log
-- PHP Version: 7.2.29

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";



--
-- Database: `1042706`
--

-- --------------------------------------------------------

--
-- Table structure for table `tempLog`
--

CREATE TABLE `tempLog` (
  `timest` timestamp NOT NULL DEFAULT current_timestamp(),
  `temperature` float NOT NULL,
  `humidity` float NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `tempLog`
--
ALTER TABLE `tempLog`
  ADD PRIMARY KEY (`timest`);
COMMIT;
